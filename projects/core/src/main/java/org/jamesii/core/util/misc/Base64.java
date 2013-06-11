/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

/**
 * Static class providing access to base64 encoding and decoding methods as
 * valid to RFC4648
 * <p/>
 * http://tools.ietf.org/html/rfc4648#page-5
 * 
 * @author Stefan Rybacki
 */
public final class Base64 {
  /**
   * The Base64 alphabet, which is *NOT* URL or filename safe
   */
  private static final char[] alphabet =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
          .toCharArray();

  /**
   * The Base64 alphabet, which *IS* URL or filename safe
   */
  @SuppressWarnings("unused")
  private static final char[] alphabetSafe =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
          .toCharArray();

  /**
   * The alphabet lookup structure to speed up index lookup of character in
   * alphabet
   */
  private static final int[] alphabetLookup = new int[255];

  static {
    createAlphabetLookup();
  }

  /**
   * Hidden constructor.
   */
  private Base64() {
  }

  /**
   * Encodes a byte array to a Base64 encoded {@link String} using a Base64
   * alphabet that is *NOT* URL and filename safe. It follows the rules
   * described in RFC 4648 in the section 4 Base 64 Encoding.
   * <p>
   * Note: result is a continuous base64 string without any line break.
   * 
   * @param data
   *          the data to be encoded
   * @return the data represented as base64 encoded string
   */
  public static String encode(byte[] data) {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < data.length; i += 3) {
      int a = data[i] & 255;
      int b = i + 1 < data.length ? data[i + 1] & 255 : 0;
      int c = i + 2 < data.length ? data[i + 2] & 255 : 0;
      builder.append(alphabet[a >> 2]);
      builder.append(alphabet[((a & 3) << 4) + (b >> 4)]);
      if (i + 1 < data.length) {
        builder.append(alphabet[((b & 15) << 2) + (c >> 6)]);
      } else {
        builder.append("=");
      }
      if (i + 2 < data.length) {
        builder.append(alphabet[(c & 63)]);
      } else {
        builder.append("=");
      }
    }

    return builder.toString();
  }

  /**
   * Decodes a base64 encoded {@link String} where the String must be continuous
   * with no line breaks and can only contain base64 alphabet characters and its
   * length must be a multiple of 4 characters including padding ='s
   * 
   * @param encodedData
   *          the encoded data to decode
   * @return the decoded byte[] array
   */
  public static byte[] decode(String encodedData) {
    if (encodedData == null) {
      throw new NullPointerException("encoded data can't be null");
    }

    // check length
    if (encodedData.length() / 4 * 4 != encodedData.length()) {
      throw new IllegalArgumentException(
          "length of encoded data must be a multiple of 4");
    }

    char[] data = encodedData.toCharArray();

    // get padding ='s at end of data and adjust byte array
    // accordingly
    int j = 0;
    if (encodedData.endsWith("===")) {
      j = 3;
    } else if (encodedData.endsWith("==")) {
      j = 2;
    } else if (encodedData.endsWith("=")) {
      j = 1;
    }

    byte[] result = new byte[data.length / 4 * 3 - j];

    for (int i = 0; i < data.length; i += 4) {
      int a = indexInAlphabet(data[i]);
      int b = indexInAlphabet(data[i + 1]);
      int c = indexInAlphabet(data[i + 2]);
      int d = indexInAlphabet(data[i + 3]);

      if (a == -1 || b == -1 || c == -1 || d == -1) {
        throw new IllegalArgumentException(
            "encoded data contains unsupported characters");
      }

      if (data[i + 1] != '=') {
        result[i / 4 * 3] = (byte) ((a << 2) + (b >> 4));
      }
      if (data[i + 2] != '=') {
        result[i / 4 * 3 + 1] = (byte) (((b & 15) << 4) + ((c >> 2) & 15));
      }
      if (data[i + 3] != '=') {
        result[i / 4 * 3 + 2] = (byte) (((c & 3) << 6) + (d & 63));
      }
    }

    return result;
  }

  /**
   * Initializes a alphabet lookup structure that is reused for one decoding run <br/>
   * TODO sr137: create it just once for all available alphabets
   */
  private static void createAlphabetLookup() {
    for (int i = 0; i < alphabetLookup.length; i++) {
      alphabetLookup[i] = -1;
    }

    // fill alphabet
    for (int i = 0; i < alphabet.length; i++) {
      alphabetLookup[alphabet[i]] = i;
    }
  }

  /**
   * Returns the index of a character in the alphabet.
   * 
   * @param c
   *          the c
   * @return the index of the character if in the alphabet or -1 if not (padding
   *         character = is indexed with 256
   */
  private static int indexInAlphabet(char c) {
    if (c == '=') {
      return 256;
    }
    if (c > alphabetLookup.length || c < 0) {
      return -1;
    }
    return alphabetLookup[c];
  }
}
