/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.io.Reader;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Triple;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.ListenerSupport;

/**
 * Abstract class for {@link IInfoProvider} implementing basic functionality
 * like listener handling and providing protected fire methods.
 *
 * @author Stefan Rybacki
 */
public abstract class AbstractInfoProvider implements IInfoProvider {

  /**
   * The registered listeners.
   */
  private final ListenerSupport<IInfoProviderListener> listeners
          = new ListenerSupport<>();

  /**
   * The global queue.
   */
  private final BlockingQueue<Triple<AbstractInfoProvider, Reader, Integer>> queue
          = new LinkedBlockingQueue<>();

  /**
   * The last recognized cursor/caret pos.
   */
  private volatile int lastPos = -1;
  private final Semaphore s = new Semaphore(0);

  @Override
  public final synchronized void addInfoProviderListener(IInfoProviderListener l) {
    listeners.addListener(l);
  }

  @Override
  public final synchronized void removeInfoProviderListener(
          IInfoProviderListener l) {
    listeners.removeListener(l);
  }

  @Override
  public final void waitForParsingResult() {
    try {
      s.acquire();
    } catch (InterruptedException ex) {
    }
  }

  /**
   * Notifies all registered {@link IInfoProviderListener}s that a token was
   * inserted at the given position in the token list.
   *
   * @param tokenIndex the token index where a token was inserted into the token
   * list
   */
  protected final synchronized void fireTokenInserted(int tokenIndex) {
    for (IInfoProviderListener l : listeners) {
      if (l != null) {
        l.tokenInserted(this, tokenIndex);
      }
    }
  }

  /**
   * Notifies all registered {@link IInfoProviderListener}s that a token was
   * removed from the list of tokens.
   *
   * @param tokenIndex the token index of the token that was removed from the
   * list of tokens
   */
  protected final synchronized void fireTokenRemoved(int tokenIndex) {
    for (IInfoProviderListener l : listeners) {
      if (l != null) {
        l.tokenRemoved(this, tokenIndex);
      }
    }
  }

  /**
   * Notifies all registered {@link IInfoProviderListener}s that a couple or all
   * tokens changed.
   */
  protected final synchronized void fireTokensChanged() {
    for (IInfoProviderListener l : listeners) {
      if (l != null) {
        l.tokensChanged(this);
      }
    }
  }

  /**
   * a async parsing thread
   */
  private final Thread parsingThread = new Thread(new Runnable() {

    @Override
    public void run() {
      while (true) {
        try {
          Triple<AbstractInfoProvider, Reader, Integer> first = queue.take();
          try {
            if (first != null) {
              first.getA().run(first.getB(), first.getC());
              s.release();
            }
          } finally {
            BasicUtilities.close(first.getB());
          }
          Thread.yield();
        } catch (InterruptedException e) {
          SimSystem.report(e);
        }

      }
    }
  }, "InfoProvider Parsing Thread");

  {
    parsingThread.setDaemon(true);
    parsingThread.start(); // NOSONAR: background thread for each instance is
    // correct
  }

  @Override
  public final void contentChanged(final Reader content, int cPos) {
    // since async update of info is permitted do this in another
    // thread and
    // queue subsequent calls where the queue is emptied before adding
    // any new
    // entry
    lastPos = cPos;
    queue.clear();
    //clean up semaphore
    if (s.hasQueuedThreads()) {
      int l = s.getQueueLength();
      s.release(l);
      s.drainPermits();
    }
    queue.add(new Triple<>(this, content, cPos));
  }

  @Override
  public void cursorPosChanged(int newPos, Reader content) {
    if (isCursorChangeSensitive() && newPos != lastPos) {
      contentChanged(content, newPos);
    }
  }

  /**
   * Override and return true if the info provider should be aware of cursor
   * position changes. Default return is false.
   *
   * @return true, if this provider is cursor change sensitive
   */
  protected boolean isCursorChangeSensitive() {
    return false;
  }

  /**
   * Implement this method to do the content parsing/analyzing or whatsoever and
   * notify registered listeners after finishing.
   *
   * @param input the content as reader stream
   * @param cPos the current cursor position in case this info provider is
   * cursor position sensitive
   */
  protected abstract void run(Reader input, int cPos);

  @Override
  public List<ITokenAction> getActionsForToken(ILexerToken token) {
    return null;
  }

  @Override
  public boolean isOnlyStyleToken(ILexerToken token) {
    return false;
  }

}
