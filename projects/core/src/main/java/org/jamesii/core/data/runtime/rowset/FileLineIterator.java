/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Pair;

/**
 * Helper class to iterate line-wise over a text file but also provides access
 * to arbitrary lines.
 * 
 * @author Johannes Rössel
 */
public class FileLineIterator implements Closeable {

  /**
   * Constant for {@link #currentLineLength}, {@link #nextLineOffsetInFile} and
   * {@link #previousLineOffsetInFile} in case they are currently not known and
   * might need to be recalculated.
   */
  private static final int UNKNOWN = -1;

  /** Initial buffer size when this class is created. 32 megabytes. */
  private static final int INITIAL_BUFFER_SIZE = 32 * 1024 * 1024;

  /**
   * The initial size of the write buffer.
   */
  private static final int INITIALWRITEBUFFERSIZE = 10000;

  /** The file to operate on. */
  private RandomAccessFile file;

  /** The {@link FileChannel} to actually manipulate the file. */
  private FileChannel channel;

  /** The current view into the file. */
  private ByteBuffer buffer;

  /**
   * Maximum number of entries. If reached (at latest) the write buffer will be
   * emptied. Set to 0 to deactivate the write buffer. Then all new lines will
   * be written to the disc directly.
   */
  private int writeBufferEntries = INITIALWRITEBUFFERSIZE;

  /**
   * Default write buffer with writeBufferEntry slots. Will be emptied to the
   * file channel if full or if a read / relocated occurs (whatever will happen
   * first). In fact this is a double buffering here as the file channel is
   * combined with a memory map of the file. However, this buffer speeds up
   * writing as long as reads happen less frequent than writes.
   */
  private List<String> writeBuffer = new ArrayList<>(writeBufferEntries);

  /**
   * The number of bytes that are mapped into memory. Essentially the window
   * size into the file.
   */
  private int bufferSize;

  /** The byte position of the first byte of the buffer within the file. */
  private long bufferOffset;

  /**
   * The current byte position within the file. Not necessarily linked to the
   * buffer's own position since it proved to be easier to handle this on my own
   * here.
   */
  private long offset;

  /** Represents the line break as bytes in the selected encoding. */
  private byte[] lineBreak = new byte[] { 0xD, 0xA };

  /**
   * Represents the originally set line break as a string. This is needed when
   * the encoding changes and the {@link #lineBreak} array must be re-created.
   */
  private String lineBreakString = "\r\n";

  /**
   * The character encoding of the file. Java for some reason calls this
   * {@link Charset} which is a related, but different concept.
   */
  private Charset encoding = Charset.forName("UTF-8");

  /** A {@link CharsetDecoder} for decoding parts of the file. */
  private CharsetDecoder decoder = encoding.newDecoder()
      .onMalformedInput(CodingErrorAction.REPLACE)
      .onUnmappableCharacter(CodingErrorAction.REPLACE);

  /** Holds the current line. */
  private CharSequence currentLine;

  /** Current length in bytes of the current line */
  private int currentLineLength;

  /** The offset cache to quickly find certain indexes again. */
  private IOffsetCache cache;

  /**
   * Offset within the file of the current line. Initially {@link #UNKNOWN} but
   * only as long there is no current line.
   */
  private long currentLineOffsetInFile = UNKNOWN;

  /**
   * Offset within the file of the next line. May be {@link #UNKNOWN} if this
   * isn't known currently.
   */
  private long nextLineOffsetInFile = UNKNOWN;

  /**
   * Offset within the file of the previous line. May be {@link #UNKNOWN} if
   * this isn't known currently.
   */
  private long previousLineOffsetInFile = UNKNOWN;

  /** The line index within the file. */
  private int currentIndex;

  /**
   * Indicates whether this {@link FileLineIterator} has been closed already or
   * not.
   */
  private boolean closed = false;

  /**
   * Initialises a new instance of the {@link FileLineIterator} class operating
   * on the given file.
   * 
   * @param fileName
   *          The name and path of the file to operate on.
   * @throws FileNotFoundException
   *           if creating or opening the file failed.
   * @throws IOException
   *           if an error occurred during memory-mapping the file.
   */
  public FileLineIterator(String fileName) throws IOException {
    this(new File(fileName));
  }

  /**
   * Initialises a new instance of the {@link FileLineIterator} class operating
   * on the given file.
   * 
   * @param f
   *          The file to operate on.
   * @throws FileNotFoundException
   *           if creating or opening the file failed.
   * @throws IOException
   *           if an error occurred during memory-mapping the file.
   */
  public FileLineIterator(File f) throws FileNotFoundException {
    // r - read
    // w - write
    // d - every update to the file contents is written immediately

    // Note to the Java developers: readable constants or actual flags would
    // have been too much to ask?
    this.file = new RandomAccessFile(f, "rw");
    channel = file.getChannel();
    cache = new DefaultOffsetCache();
    offset = 0;
    bufferOffset = 0;
    currentLineOffsetInFile = 0;
    previousLineOffsetInFile = UNKNOWN;
    nextLineOffsetInFile = UNKNOWN;
    currentLineLength = UNKNOWN;
    currentLine = null;
    currentIndex = 0;
    setBufferSize(INITIAL_BUFFER_SIZE);
    cache.clear();
    cache.put(0, 0L);
  }

  @Override
  public synchronized void close() throws IOException {

    if (closed) {
      return;
    }

    flush();

    closed = true;

    if (channel != null) {
      channel.close();
    }
    if (file != null) {
      file.close();
    }
    channel = null;
    file = null;
    buffer = null;
    cache = null;
    encoding = null;
  }

  /**
   * Retrieves a single byte from the file at the specified offset. The memory
   * mapping will be updated if necessary.
   * 
   * @param byteOffset
   *          The offset in the file to read a byte from.
   * @return The byte read at the offset.
   * @throws IOException
   *           if the offset is beyond the end of the file.
   */
  private byte getByte(long byteOffset) throws IOException {
    try {
      return buffer.get((int) (byteOffset - bufferOffset));
    } catch (IndexOutOfBoundsException e) {
      reMap(byteOffset);
      if (buffer.limit() == 0) {
        throw new IOException("Tried reading past end of file.", e);
      }
      return buffer.get(0);
    }
  }

  /**
   * Retrieves a range of bytes from the file at a specified offset with a
   * specified length. The memory mapping and buffer size will be updated if
   * necessary.
   * 
   * @param byteOffset
   *          The offset in the file to start reading from.
   * @param length
   *          The length of the requested byte range.
   * @return A {@link ByteBuffer} containing the requested range of bytes.
   */
  private ByteBuffer getBytes(long byteOffset, int length) {
    // Grow buffer if needed
    if (length > bufferSize) {
      while (length > bufferSize) {
        bufferSize *= 2;
      }
      reMap(byteOffset);
    }

    ByteBuffer retval;
    int oldLimit = buffer.limit();

    if (byteOffset >= bufferOffset
        && byteOffset + length < bufferOffset + buffer.limit()) {
      int location = (int) (byteOffset - bufferOffset);
      buffer.position(location);
      buffer.limit(location + length);
    } else {
      reMap(byteOffset);
      oldLimit = buffer.limit();
      buffer.limit(length);
    }

    retval = buffer.slice();
    buffer.limit(oldLimit);
    return retval;
  }

  /**
   * Updates the memory mapping of the file with a new offset.
   * {@link #bufferOffset} is set to {@code newOffset}.
   * 
   * @param newOffset
   *          The new offset within the file where the buffer starts.
   * @throws FileRowSetException
   *           wrapping an {@link IOException} if an error occurs when
   *           memory-mapping the file.
   */
  private void reMap(long newOffset) {
    try {
      flush();

      bufferOffset = newOffset;

      // restrict the buffer's length to the file size to avoid mapping more
      // than there is in the file.
      long length = Math.min(getFileSize() - bufferOffset, bufferSize);

      buffer = channel.map(MapMode.READ_ONLY, bufferOffset, length);
    } catch (IOException e) {
      throw new FileRowSetException(e);
    }
  }

  /**
   * Flush the write buffer.
   */
  private synchronized void flush() {
    if (writeBuffer.isEmpty()) {
      return;
    }

    StringBuilder tw = new StringBuilder();
    for (String line : writeBuffer) {
      tw.append(line);
    }

    ByteBuffer toWrite = encoding.encode(tw.toString());

    byte[] bytes = new byte[toWrite.limit()];
    toWrite.get(bytes);

    try {
      file.seek(file.length());
      file.write(bytes);
      writeBuffer.clear();
    } catch (IOException e) {
      SimSystem.report(e);
    }
  }

  /**
   * Moves the offset to the start of the next line in the file (or to the very
   * end of the file if there is no next line). Please only call this method
   * from synchronized methods! The buffer should have been flushed before as
   * well.
   */
  private void moveToNextLine() {
    if (nextLineOffsetInFile == UNKNOWN) {
      // find next line break; the next line should come after that
      nextLineOffsetInFile = findNextLineBreak();

      if (nextLineOffsetInFile == -1) {
        // unless there isn't any further line break - in which case we just set
        // the offset to the end of the file
        nextLineOffsetInFile = getFileSize();
      } else {
        // otherwise we still need to add the length of the line break to get to
        // the start of the next line
        nextLineOffsetInFile += lineBreak.length;
      }
    }

    previousLineOffsetInFile = currentLineOffsetInFile;
    currentLineOffsetInFile = nextLineOffsetInFile;
    nextLineOffsetInFile = UNKNOWN;
    offset = currentLineOffsetInFile;

    currentLine = null;
    currentLineLength = UNKNOWN;

    ++currentIndex;

    if (!iisLast()) {
      cache.putTentatively(currentIndex, offset);
    }
  }

  /**
   * Moves the offset to the start of the previous line in the file (or to
   * {@code -1} if there is no previous line).
   */
  private void moveToPreviousLine() {
    if (isFirst()) {
      offset = -1;
      return;
    }

    if (previousLineOffsetInFile == UNKNOWN) {
      previousLineOffsetInFile =
          findPreviousLineBreak(findPreviousLineBreak() - 1);

      if (previousLineOffsetInFile == -1) {
        // we went past the start of the file, which may be considered an
        // implicit line break
        previousLineOffsetInFile = 0;
      } else {
        // account for the line break
        previousLineOffsetInFile += lineBreak.length;
      }
    }

    nextLineOffsetInFile = currentLineOffsetInFile;
    currentLineOffsetInFile = previousLineOffsetInFile;
    previousLineOffsetInFile = UNKNOWN;
    offset = currentLineOffsetInFile;

    currentLine = null;
    currentLineLength = UNKNOWN;

    --currentIndex;

    cache.putTentatively(currentIndex, offset);
  }

  /**
   * Retrieves the current line from the file.
   * 
   * @return A {@link CharSequence} containing the current line in the file or
   *         {@code null} if the file is empty.
   */
  public synchronized CharSequence current() {
    if (offset < 0 || offset >= getFileSize()
        || currentLineOffsetInFile == UNKNOWN) {
      return null;
    }

    if (currentLine == null) {
      // current line not yet cached
      if (currentLineLength == UNKNOWN) {
        // current line length not yet known
        long nextCRLF = findNextLineBreak();
        currentLineLength = (int) (nextCRLF - currentLineOffsetInFile);

        if (nextCRLF == -1) {
          // Went past EOF in searching - the line extends until the end of the
          // file.
          currentLineLength = (int) (getFileSize() - currentLineOffsetInFile);

          if (currentLineLength == 0) {
            return null;
          }
        }
      }

      ByteBuffer line = getBytes(currentLineOffsetInFile, currentLineLength);

      try {
        currentLine = decoder.decode(line);
      } catch (CharacterCodingException e) {
        throw new FileRowSetException(e);
      }
    }

    cache.put(currentIndex, currentLineOffsetInFile);

    return currentLine;
  }

  /**
   * Finds the next occurrence of the given sequence of bytes from the given
   * offset. The offset isn't changed.
   * <p>
   * <strong>Note:</strong> This method returns the <em>start</em> of the given
   * sequence. So if the desired result is the start of the first byte
   * <em>after</em> the needle, then {@code needle.length} needs to be added.
   * 
   * @param needle
   *          The byte sequence to find.
   * @param startingOffset
   *          The offset in the file to start searching from.
   * @return The starting offset of the next occurrence of the searched byte
   *         sequence in the file or {@code -1} if it couldn't be found.
   */
  private long findNext(byte[] needle, long startingOffset) {
    long myOffset = startingOffset;
    try {
      loop: while (true) {
        for (int i = 0; i < needle.length; i++) {
          byte current = getByte(myOffset);
          ++myOffset;

          if (needle[i] != current) {
            continue loop;
          }
        }
        break;
      }
    } catch (IOException e) {
      // We went right past the end of the file.
      return -1;
    }

    // subtract needle length since we already point one byte too far
    return myOffset - needle.length;
  }

  /**
   * Finds the next line break starting from the given offset. The offset isn't
   * changed.
   * <p>
   * <strong>Note:</strong> This method returns the <em>start</em> of the next
   * line break. So if the desired result is the start of the first byte
   * <em>after</em> the line break, then {@code lineBreak.length} needs to be
   * added.
   * 
   * @param startingOffset
   *          The offset in the file to start searching from.
   * 
   * @return The starting offset of the next line break in the file or
   *         {@code -1} if it couldn't be found.
   */
  private long findNextLineBreak(long startingOffset) {
    return findNext(lineBreak, startingOffset);
  }

  /**
   * Finds the next line break. The offset isn't changed.
   * <p>
   * <strong>Note:</strong> This method returns the <em>start</em> of the next
   * line break. So if the desired result is the start of the first byte
   * <em>after</em> the line break, then {@code lineBreak.length} needs to be
   * added.
   * 
   * @return The starting offset of the next line break in the file or
   *         {@code -1} if it couldn't be found.
   */
  private long findNextLineBreak() {
    if (currentLineLength != UNKNOWN) {
      return offset + currentLineLength;
    }
    return findNextLineBreak(offset);
  }

  /**
   * Finds the previous occurrence of the given sequence of bytes from the given
   * offset. The offset isn't changed.
   * <p>
   * <strong>Note:</strong> Even though this method searches <em>backwards</em>
   * it returns the <em>start</em> of the given sequence. So if the desired
   * result is the start of the first byte <em>after</em> the needle, then
   * {@code needle.length} needs to be added.
   * 
   * @param needle
   *          The byte sequence to find.
   * @param startingOffset
   *          The offset in the file to start searching from.
   * @return The starting offset of the previous occurrence of the searched byte
   *         sequence in the file or {@code -1} if it couldn't be found.
   */
  private long findPrevious(byte[] needle, long startingOffset) {
    long myOffset = startingOffset;
    try {
      loop: while (true) {
        for (int i = needle.length - 1; i >= 0; i--) {
          if (myOffset < 0) {
            return -1;
          }

          byte current = getByte(myOffset);
          --myOffset;
          if (needle[i] != current) {
            continue loop;
          }
        }
        break;
      }
    } catch (IOException e) {
      // won't happen since we are searching backwards, but what the heck
      return -1;
    }

    // Add 1 since we already point one byte too far back
    return myOffset + 1;
  }

  /**
   * Finds the previous line break from the given offset. The offset isn't
   * changed.
   * <p>
   * <strong>Note:</strong> Even though this method searches <em>backwards</em>
   * it returns the <em>start</em> of the line break. So if the desired result
   * is the start of the first byte <em>after</em> the line break, then
   * {@code lineBreak.length} needs to be added.
   * 
   * @param startingOffset
   *          The offset in the file to start searching from.
   * 
   * @return The starting offset of the previous line break in the file or
   *         {@code -1} if it couldn't be found.
   */
  private long findPreviousLineBreak(long startingOffset) {
    return findPrevious(lineBreak, startingOffset);
  }

  /**
   * Finds the previous line break from the current offset. The offset isn't
   * changed.
   * <p>
   * <strong>Note:</strong> Even though this method searches <em>backwards</em>
   * it returns the <em>start</em> of the line break. So if the desired result
   * is the start of the first byte <em>after</em> the line break, then
   * {@code lineBreak.length} needs to be added.
   * 
   * @return The starting offset of the previous line break in the file or
   *         {@code -1} if it couldn't be found.
   */
  private long findPreviousLineBreak() {
    return findPreviousLineBreak(offset);
  }

  /**
   * Retrieves the current line index.
   * 
   * @return The current line index within the file.
   */
  public synchronized int index() {
    return currentIndex;
  }

  /**
   * Rewind to the first line and return it.
   * 
   * @return A {@link CharSequence} containing the first line of the file or
   *         {@code null} if the file is empty.
   */
  public synchronized CharSequence first() {
    return absolute(0);
  }

  /**
   * Retrieves a value determining whether this currently is the first line.
   * <p>
   * This is exactly equivalent to
   * 
   * <pre>
   * index() == 0
   * </pre>
   * 
   * @return {@code true} if the current line is the first, {@code false} if
   *         not.
   */
  public synchronized boolean isFirst() {
    return index() == 0;
  }

  /**
   * Moves the position to the last line in the file and retrieves it.
   * 
   * @return A {@link CharSequence} containing the last line of the file, or
   *         {@code null} if there are no lines in the file.
   */
  public synchronized CharSequence last() {
    flush();
    // try moving closest to the file end
    Pair<Integer, Long> closest = cache.findClosest(Integer.MAX_VALUE);
    currentIndex = closest.getFirstValue();
    offset = closest.getSecondValue();
    currentLineOffsetInFile = offset;

    currentLineLength = UNKNOWN;
    currentLine = null;
    previousLineOffsetInFile = UNKNOWN;
    nextLineOffsetInFile = UNKNOWN;

    // slow, but ensures the index is updated and the cache filled
    while (!iisLast()) {
      moveToNextLine();
    }
    return current();

    // below is still code to seek to the end of the file and then work
    // backwards until the last line is found. This has the problem that we do
    // not have a line index there and therefore can neither cache any offsets
    // nor return a meaningful index when someone calls index().

    /**
     * <pre>
     * // ugly hack around Eclipse mangling commented-out code when formatting.
     * try {
     *   long fileSize = channel.size();
     * 
     *   if (fileSize == 0)
     *     return null;
     * 
     *   reMap(fileSize - bufferSize);
     *   // start at the last byte
     *   offset = fileSize - 1;
     *   // find last line break
     *   offset = findPrevious(lineBreak) + lineBreak.length;
     *   // We might just have found the line break following the last line
     *   // (therefore still marking the end of the file)
     *   if (current().length() == 0) {
     *     offset = fileSize - 1;
     *     offset = findPrevious(lineBreak);
     *     offset = findPrevious(lineBreak) + lineBreak.length;
     *   }
     * 
     *   index = -1;
     * 
     *   return current();
     * } catch (IOException e) {
     *   throw new FileRowSetException(e);
     * }
     * </pre>
     */
  }

  /**
   * Determines whether the current position represents the last line in the
   * file.
   * 
   * @return A value representing whether the current position is the last line
   *         in the file.
   */
  public synchronized boolean isLast() {
    flush();
    return iisLast();
  }

  /**
   * Internal {@link #isLast()} method which can be used instead of the
   * {@link #isLast()} method in this class if no synchronisation is needed.
   * 
   * @return
   */
  private boolean iisLast() {
    long nextLineBreak = findNextLineBreak();
    // if there is no next line break we're at the end of the file
    if (nextLineBreak == -1) {
      return true;
    }
    // maybe that was the last line break and no line follows after that
    nextLineBreak = findNextLineBreak(nextLineBreak + lineBreak.length);
    return nextLineBreak == -1;
    // Seems there is still a line after this
  }

  /**
   * Moves to the next line in the file and returns it.
   * 
   * @return A {@link CharSequence} containing the next line in the file or
   *         {@code null} if there is no next line.
   */
  public synchronized CharSequence next() {
    flush();
    moveToNextLine();
    return current();
  }

  /**
   * Moves to the previous line in the file and returns it.
   * 
   * @return A {@link CharSequence} containing the previous line in the file or
   *         {@code null} if there is no previous line.
   */
  public synchronized CharSequence previous() {
    moveToPreviousLine();
    return current();
  }

  /**
   * Moves to a line relative to the current one. A positive index indicates a
   * move forward, negative moves backwards. An invocation of
   * {@code relative(0)} is equivalent to {@code current()},
   * {@code relative(-1)} is equivalent to {@code previous()} and
   * {@code relative(1)} is equivalent to {@code next()}.
   * <p>
   * This method returns {@code null} if the requested index would be beyond the
   * start or end of the file. In this case, the current line will be the first
   * or last line, respectively.
   * 
   * @param target
   *          The relative line index to fetch.
   * @return A {@link CharSequence} containing the requested line or
   *         {@code null} if either the end or the start of the file was reached
   *         before reaching the requested line.
   */
  public synchronized CharSequence relative(int target) {
    switch (target) {
    case 0:
      return current();
    case -1:
      return previous();
    case 1:
      return next();
    }

    return absolute(currentIndex + target);
  }

  /**
   * Moves to a specific line in the file.
   * <p>
   * If this method is called with a negative line index or an index beyond the
   * end of the file, then the current line will be the first or last line,
   * respectively and this method returns {@code null}.
   * 
   * @param targetIndex
   *          The line index to move to.
   * @return The line at the requested index or {@code null} if it was beyond
   *         the start or end of the file.
   */
  public synchronized CharSequence absolute(int targetIndex) {
    if (targetIndex < 0) {
      first();
      return null;
    }

    flush();

    currentLine = null;
    currentLineLength = UNKNOWN;
    nextLineOffsetInFile = UNKNOWN;
    previousLineOffsetInFile = UNKNOWN;

    Pair<Integer, Long> closest = cache.findClosest(targetIndex);
    currentIndex = closest.getFirstValue();
    offset = closest.getSecondValue();
    currentLineOffsetInFile = offset;

    int delta = targetIndex - currentIndex;

    if (delta > 0) {
      while (delta > 0) {
        moveToNextLine();
        --delta;
        if (offset >= getFileSize()) {
          moveToPreviousLine();
          return null;
        }
      }
    } else {
      while (delta < 0) {
        moveToPreviousLine();
        ++delta;
        if (offset == -1) {
          first();
          return null;
        }
      }
    }

    return current();
  }

  /**
   * Updates the byte array used to hold the line break in its current character
   * encoding.
   */
  private void updateLineBreakCharacterBuffer() {
    ByteBuffer lineBreakBuffer = encoding.encode(lineBreakString);
    lineBreak = new byte[lineBreakBuffer.limit()];
    lineBreakBuffer.get(lineBreak);
  }

  /**
   * Sets the character encoding used by this instance.
   * <p>
   * This should not be changed on the fly once the file was partially read.
   * 
   * @param newEncoding
   *          The new character encoding to use.
   */
  public void setEncoding(Charset newEncoding) {
    encoding = newEncoding;
    // the decoder shouldn't throw any exceptions
    decoder =
        encoding.newDecoder().onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE);
    updateLineBreakCharacterBuffer();
  }

  /**
   * Retrieves the character encoding used by this instance.
   * 
   * @return The character encoding to use to read text from the file.
   */
  public Charset getEncoding() {
    return encoding;
  }

  /**
   * Sets the string that is used as a line break. By default this is so U+000D
   * (Carriage Return) U+000A (Line Feed), ({@code "\r\n"}). Other possibilities
   * might be just {@code "\n"} or even U+2028 (Line Separator).
   * <p>
   * This should not be changed on the fly once the file was partially read.
   * 
   * @param newLineBreak
   *          The new line break to use.
   */
  public void setLineBreak(String newLineBreak) {
    lineBreakString = newLineBreak;
    updateLineBreakCharacterBuffer();
  }

  /**
   * Retrieves the line break to be used when reading the file.
   * 
   * @return A {@link String} containing the sequence of characters that will be
   *         treated as a line break.
   */
  public String getLineBreak() {
    return lineBreakString;
  }

  /**
   * Sets the buffer size for the “window” into the file.
   * <p>
   * The buffer size can change even without an explicit call to this method,
   * e.g. if a requested line was longer than the current buffer size. This
   * buffer size are the number of bytes cached in the internally used file
   * channel memory map. The {@link #INITIAL_BUFFER_SIZE} is
   * {@value #INITIAL_BUFFER_SIZE} bytes.
   * 
   * @param bufferSize
   *          The new buffer size.
   */
  public final synchronized void setBufferSize(int bufferSize) {
    if (bufferSize <= 0) {
      throw new IllegalArgumentException("bufferSize must be positive.");
    }

    this.bufferSize = bufferSize;
    reMap(bufferOffset);
  }

  /**
   * Retrieves the current buffer size.
   * 
   * @return The current buffer size.
   */
  public synchronized int getBufferSize() {
    return bufferSize;
  }

  /**
   * Retrieves the file this instance operates on.
   * 
   * @return The file in use.
   */
  public synchronized RandomAccessFile getFile() {
    return file;
  }

  /**
   * Retrieves the file size.
   * 
   * @return The underlying file's size.
   */
  private long getFileSize() {
    try {
      flush();
      return file.length();
    } catch (IOException e) {
      throw new FileRowSetException(e);
    }
  }

  /**
   * Append the line to the file we are working on.
   * 
   * @param line
   *          (CSV separated)
   */
  public void write(String line) throws SQLException {

    writeBuffer.add(line + lineBreakString);

    if (writeBuffer.size() >= writeBufferEntries) {
      flush();
    }

    // RandomAccessFile file = getFile();
    //
    // try {
    // ByteBuffer toWrite = encoding.encode(line);
    //
    // byte[] bytes = new byte[toWrite.limit()];
    // toWrite.get(bytes);
    // file.seek(file.length());
    // file.write(bytes);
    // } catch (IOException e) {
    // throw new SQLException(e);
    // }
  }
}
