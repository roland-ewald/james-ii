/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.jamesii.core.base.INamedEntity;

/**
 * Class containing methods which operate on strings.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald, Susanne Biermann
 */
public final class Strings {

  /**
   * Hidden constructor.
   */
  private Strings() {
  }

  /**
   * Similar to generic definition.
   * 
   * @param array
   *          the array
   * @return string representation
   */
  public static String dispArray(double[] array) {
    if (array == null) {
      return "null";
    }
    StringBuilder s = new StringBuilder("[");
    for (int i = 0; i < array.length; i++) {
      s.append(" " + array[i]);
    }
    s.append(" ]");
    return s.toString();
  }

  /**
   * Similar to generic definition.
   * 
   * @param array
   *          the array
   * @return string representation
   */
  public static String dispArray(long[] array) {
    if (array == null) {
      return "null";
    }
    StringBuilder s = new StringBuilder("[");
    for (int i = 0; i < array.length; i++) {
      s.append(" " + array[i]);
    }
    s.append(" ]");
    return s.toString();
  }

  /**
   * Similar to generic definition.
   * 
   * @param array
   *          the array
   * @return string representation
   */
  public static String dispArray(int[] array) {
    if (array == null) {
      return "null";
    }
    StringBuilder s = new StringBuilder("[");
    for (int i = 0; i < array.length; i++) {
      s.append(" " + array[i]);
    }
    s.append(" ]");
    return s.toString();
  }

  /**
   * Similar to generic definition.
   * 
   * @param array
   *          the array
   * @return string representation
   */
  public static String dispArray(boolean[] array) {
    if (array == null) {
      return "null";
    }
    StringBuilder s = new StringBuilder("[");
    for (int i = 0; i < array.length; i++) {
      s.append(" " + array[i]);
    }
    s.append(" ]");
    return s.toString();
  }

  /**
   * Displays array of type X as a row vector.
   * 
   * @param array
   *          the array
   * @param <X>
   *          the type of the array elements
   * @return string representation
   */
  public static <X> String dispArray(X[] array) {
    if (array == null) {
      return "null";
    }
    StringBuilder s = new StringBuilder("[");
    for (int i = 0; i < array.length; i++) {
      s.append(" ");
      s.append(array[i] == null ? "null" : array[i].toString());
    }
    s.append(" ]");
    return s.toString();
  }

  /**
   * Generates matrix representation for display. {@link Object#toString()} is
   * used.
   * 
   * @param matrix
   *          the matrix containing the objects
   * @param delim
   *          the delimiter between elements in the same line
   * @param <X>
   *          the type of the matrix elements
   * @return string representation
   */
  public static <X> String displayMatrix(X[][] matrix, char delim) {
    if (matrix == null) {
      return "null";
    }
    StringBuilder matrixString = new StringBuilder("");
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        matrixString.append((matrix[i][j] == null ? "null" : matrix[i][j]
            .toString()) + delim);
      }
      matrixString.append("\n");
    }
    return matrixString.toString();
  }

  /**
   * Generates matrix representation for display.
   * 
   * @param matrix
   *          matrix to be displayed
   * @return string representation
   */
  public static String displayMatrix(int[][] matrix) {
    return displayMatrix(matrix, ' ');
  }

  /**
   * Generates matrix representation for display.
   * 
   * @param matrix
   *          matrix to be displayed
   * @param delim
   *          delimiter between different elements in one line
   * @return string representation
   */
  public static String displayMatrix(int[][] matrix, char delim) {
    if (matrix == null) {
      return "null";
    }
    StringBuilder matrixString = new StringBuilder("");
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        matrixString.append(Integer.toString(matrix[i][j]) + delim);
      }
      matrixString.append("\n");
    }
    return matrixString.toString();
  }

  /**
   * Generates matrix representation for display.
   * 
   * @param matrix
   *          matrix to be displayed
   * @return string representation
   */
  public static String displayMatrix(double[][] matrix) {
    return displayMatrix(matrix, ' ');
  }

  /**
   * Generates matrix representation for display.
   * 
   * @param matrix
   *          the matrix to be displayed
   * @param delim
   *          the delimiter between elements in the same line
   * @return string representation
   */
  public static String displayMatrix(double[][] matrix, char delim) {
    if (matrix == null) {
      return "null";
    }
    StringBuilder matrixString = new StringBuilder("");
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        matrixString.append(Double.toString(matrix[i][j]) + delim);
      }
      matrixString.append("\n");
    }
    return matrixString.toString();
  }

  /**
   * Displays a mapping.
   * 
   * @param map
   *          map to be displayed
   * @param <X>
   *          the types of the keys in the map
   * @param <Y>
   *          the types of the values in the map
   * @return string representation
   */
  public static <X, Y> String dispMap(Map<X, Y> map) {
    if (map == null) {
      return "null";
    }
    return dispMap(map, new ArrayList<>(map.keySet()), " => ", " ; ", true);
  }

  /**
   * Displays map.
   * 
   * @param map
   *          the map to be displayed
   * @param keys
   *          list of keys that shall be displayed
   * @param keyValDelim
   *          the delimiter between key and value
   * @param entryDelim
   *          the delimiter between entries
   * @param dispKeys
   *          if true keys are displayed
   * @param <X>
   *          type of the keys
   * @param <Y>
   *          type of the values
   * @return the string representation of the map
   */
  public static <X, Y> String dispMap(Map<X, Y> map, List<X> keys,
      String keyValDelim, String entryDelim, boolean dispKeys) {
    if (map == null) {
      return "null";
    }
    StringBuilder result = new StringBuilder("");
    for (X key : keys) {
      result.append((dispKeys ? key + keyValDelim : "") + map.get(key)
          + entryDelim);
    }
    return result.toString();
  }

  /**
   * Displays the contents of a map in the order of the given list of keys.
   * Values associated with keys that are not in the list will be ignored.
   * 
   * @param map
   *          the mapping
   * @param keys
   *          the list of keys for which the values shall be displayed
   * @param <X>
   *          the types of the keys in the map
   * @return the string containing a tab-separated list with the string
   *         representation of the values
   */
  public static <X> String dispMap(Map<X, ?> map, List<X> keys) {
    return dispMap(map, keys, "", "\t", false);
  }

  /**
   * Displays all values of a mapping.
   * 
   * @param map
   *          map to be displayed
   * @param <X>
   *          the types of the keys in the map
   * @param <Y>
   *          the types of the values in the map
   * @return string representation
   */
  public static <X, Y> String dispMapValues(Map<X, Y> map) {
    if (map == null) {
      return "null";
    }
    StringBuilder result = new StringBuilder("");
    Set<Entry<X, Y>> entries = map.entrySet();
    for (Entry<X, Y> entry : entries) {
      result.append(entry.getValue().toString() + " ; ");
    }
    return result.toString();
  }

  /**
   * Displays the elements e_1, ..., e_n of an iterable entity as a tuple (e_1,
   * ..., e_n). The order is not defined.
   * 
   * @param iterable
   *          the iterable entity to be displayed
   * @return string with tuple
   */
  public static String dispIterable(Iterable<?> iterable) {
    return "(" + iterableAsString(iterable) + ")";
  }

  /**
   * Builds a string of elements passed as iterable. The string will look like
   * a1, a2, a3, ..., an.
   * 
   * @param iterable
   * @return
   */
  public static String iterableAsString(Iterable<?> iterable) {
    StringBuilder dispSet = new StringBuilder("");
    for (Object element : iterable) {
      if (dispSet.length() != 0) {
        dispSet.append(", ");
      }
      dispSet.append((element == null ? "NULL" : element.toString()));
    }
    return dispSet.toString();
  }

  /**
   * Displays the name of the given class (without packages).
   * 
   * @param cl
   *          the class
   * @return the name of the class
   */
  public static String dispClassName(Class<?> cl) {
    String className = cl.getName();
    return className.substring(className.lastIndexOf('.') + 1);
  }

  /**
   * Converts a double-Value to a String and formats the latter in such a way,
   * that: it is not in exponential notation, it is restricted to a certain
   * number of decimal places.
   * 
   * @param originalNumber
   *          double-Value which has to be converted to a formated String
   * @param numberOfFractionDigits
   *          prescribed number of decimal places for the string representation
   * @return string representation of the passed number in non-exponential
   *         notation and with the prescribed number of decimal places
   */

  public static String formatNumberForDisplay(double originalNumber,
      int numberOfFractionDigits) {
    return String.format("%." + numberOfFractionDigits + "f", originalNumber);
  }

  /**
   * Returns the number of characters appearing before a certain character in
   * the given string.
   * 
   * @param string
   *          the given string
   * @param c
   *          the character until which shall be counted
   * @return the number of characters before the given character occurs. if it
   *         not occurs, the length of the given string is returned
   */
  public static int getCharacterCount(String string, char c) {
    // If c does not appear in this string, return length of the
    // string,
    // otherwise, the index is the number of characters before c
    return string.indexOf(c) == -1 ? string.length() : string.indexOf(c);
  }

  /**
   * Counts number of substrings in a given string.
   * 
   * @param string
   *          the given string
   * @param subString
   *          the substring to be counted
   * @return number of substring occurrences in the string, 0 if string or
   *         substring are empty
   */
  public static int countSubstrings(String string, String subString) {
    if (string.isEmpty() || subString.isEmpty()) {
      return 0;
    }
    int counter = 0;
    int currentIndex = string.indexOf(subString);
    while (currentIndex >= 0) {
      counter++;
      currentIndex = string.indexOf(subString, currentIndex + 1);
    }
    return counter;
  }

  /**
   * Returns the names of named entities. If name is null or empty, the class
   * name will be used.
   * 
   * @param entities
   *          the list of entities
   * @return list of the entities' names
   */
  public static List<String> getEntityNames(
      List<? extends INamedEntity> entities) {
    if (entities == null) {
      return new ArrayList<>();
    }

    ArrayList<String> al = new ArrayList<>();
    for (INamedEntity f : entities) {
      String s = f.getName();
      if ((s == null) || (s.compareTo("") == 0)) {
        al.add(f.getClass().getName());
      } else {
        al.add(s);
      }
    }
    return al;
  }

  /**
   * Please use this method with care! It is only meant for formating numbers
   * before output. It is not a mathematically sound calculation! It returns the
   * number of significant decimal places of a double value: Zeros, that are
   * localized at the end of a double value are not significant, so 0.010 has 2
   * significant decimal places Secondly a significant decimal place is one that
   * is not behind a zero, eg. 2.340005 has 2 significant decimal places -> 34.
   * Some examples: 0.10 would become 0.1 (method returns 1) 2.340005 -> 2.34
   * (method returns 2) 0.3401 -> 0.34 (method returns 2) 1.05 -> 1 (method
   * returns 0) 0.505 -> 0.5 (method returns 1)
   * 
   * @param d
   *          whose significant decimal place are calculated
   * @return number of significant decimal places
   */
  public static int getNumberOfSignificantFractionDigits(double d) {

    DecimalFormat df = new DecimalFormat();

    // avoid scientific notation
    df.setMaximumFractionDigits(Integer.MAX_VALUE);

    // convert the double to a string
    String doubleAsString = df.format(d);

    // get the substring with the decimal places
    String decimalPlaces = "";

    int commmaPos = doubleAsString.indexOf(',');
    int pointPos = doubleAsString.indexOf('.');
    if (commmaPos != -1) {
      decimalPlaces = doubleAsString.substring(commmaPos + 1);
    } else if (pointPos != -1) {
      decimalPlaces = doubleAsString.substring(pointPos + 1);
    }

    // if d is in (-1,1), we have to search for first significant
    // digit
    int indexFirstSignificantDec = 0;
    if (Math.abs(d) < 1) {
      for (int i = 0; i < decimalPlaces.length(); i++) {
        if (decimalPlaces.charAt(i) != '0') {
          indexFirstSignificantDec = i;
          break;
        }
      }
    }

    return indexFirstSignificantDec
        + getCharacterCount(decimalPlaces.substring(indexFirstSignificantDec),
            '0');

  }

  /**
   * Get separated list of array content.
   * 
   * @param array
   *          the array
   * @param separator
   *          the characters to be inserted in-between
   * @param <X>
   *          the types of the elements in the array
   * @return comma-separated list
   */
  public static <X> String getSeparatedList(X[] array, String separator) {
    if (array == null) {
      return "";
    }
    StringBuilder s = new StringBuilder("");
    for (int i = 0; i < array.length; i++) {
      s.append(array[i].toString());
      if (i < array.length - 1) {
        s.append(separator);
      }
    }
    return s.toString();
  }

  /**
   * Indent the given string. I.e., place the indentBy string in front of each
   * line in the string. Lines must be separated by an \n character
   * 
   * @param string
   *          strings to be indented
   * @param indentBy
   *          string with which the lines of the string parameter shall be
   *          indented
   * @return indented string
   */
  public static String indent(String string, String indentBy) {

    StringTokenizer st = new StringTokenizer(string, "\n");

    StringBuilder result = new StringBuilder("");

    while (st.hasMoreTokens()) {
      String s = st.nextToken();
      result.append(indentBy);
      result.append(s);
      if (st.hasMoreTokens()) {
        result.append("\n");
      }
    }

    return result.toString();
  }

  /**
   * Returns true if the first character of the given string is one of the
   * characters given in the array.
   * 
   * @param str
   *          string to be tested
   * @param chars
   *          array with characters
   * @return true if the first character of the given string is one of the
   *         characters given in the array
   */
  public static boolean startsWith(String str, char[] chars) {
    if (str.isEmpty() || chars.length == 0) {
      return false;
    }
    char first = str.charAt(0);
    for (char c : chars) {
      if (c == first) {
        return true;
      }
    }
    return false;
  }

  /**
   * Converts URI to ASCII string.
   * 
   * @param uri
   *          the URI
   * @return the ASCII string, empty if URI is null
   */
  public static String uriToString(URI uri) {
    return uri == null ? "" : uri.toASCIIString();
  }

  /**
   * Converts the given string to a list of characters.
   * 
   * @param string
   *          the string that shall be converted
   * @return the list of characters
   */
  public static List<Character> stringToList(String string) {
    char[] charArray = string.toCharArray();
    ArrayList<Character> charList = new ArrayList<>();
    for (char ch : charArray) {
      charList.add(ch);
    }
    return charList;
  }

  /**
   * Converts string to URI.
   * 
   * @param string
   *          the string
   * @return the URI, null if the string is empty
   * @throws URISyntaxException
   *           if the string does not represent a valid URI
   */
  public static URI stringToURI(String string) throws URISyntaxException {
    return string.length() == 0 ? null : new URI(string);
  }

  /**
   * Get MAC-Address as a (hex-) string.
   * 
   * @param macAddress
   *          the MAC address
   * @return MAC-Address as a (hex-) string
   */
  public static String getMACAddressString(byte[] macAddress) {
    Formatter formatter = new Formatter();
    if (macAddress != null) {
      for (int i = 0; i < macAddress.length; i++) {
        formatter.format("%02X" + (i < macAddress.length - 1 ? "-" : ""),
            macAddress[i]);
      }
    }
    return formatter.toString();
  }

  /**
   * Converts a string containing several comma separated values into an
   * ArrayList.
   * 
   * @param <T>
   *          type of the list content
   * 
   * @param value
   *          The string containing comma separated values
   * @param typeInfo
   *          the type info
   * @return An instance of array list containing the values
   */
  public static <T> List<T> getListValues(Class<T> typeInfo, String value) {
    ArrayList<T> result = new ArrayList<>();
    // result.getClass().getTypeParameters()
    StringTokenizer st = new StringTokenizer(value, ","); // better
    // search
    // for ()
    // ???
    // Then we could even allow
    // to use lists of lists of
    // ...
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      result.add(stringToValue(typeInfo, token.trim()));
    }
    return result;
  }

  /**
   * Converts a string into a value. This method can handle the default data
   * types and lists of those.
   * 
   * @param typeInfo
   *          The class of the data type
   * @param value
   *          The value
   * @param <V>
   *          the type of the resulting value
   * @return An instance of data type set to the value of value
   */
  @SuppressWarnings({ "unchecked" })
  public static <V> V stringToValue(Class<V> typeInfo, String value) {
    if ((value == null) || (value.isEmpty())) {
      return null;
    } else if (typeInfo == Double.class) {
      return (V) Double.valueOf(value);
    } else if (typeInfo == Float.class) {
      return (V) Float.valueOf(value);
    } else if (typeInfo == Long.class) {
      return (V) Long.valueOf(value);
    } else if (typeInfo == Integer.class) {
      return (V) Integer.valueOf(value);
    } else if (typeInfo == Byte.class) {
      return (V) Byte.valueOf(value);
    } else if (typeInfo == Short.class) {
      return (V) Short.valueOf(value);
    } else if (typeInfo == Boolean.class) {
      return (V) Boolean.valueOf(value);
    } else if (typeInfo == String.class) {
      return (V) value;
    }
    return null;
  }

  /**
   * Returns Levenshtein distance for two strings. This is the number of
   * insertions deletions or substitutions needed to transform one string into
   * another (case-sensitive).
   * 
   * @param s1
   *          first string
   * @param s2
   *          second string
   * @return Levenshtein distance
   */
  public static int getLevenshteinDistance(String s1, String s2) {

    int s1Length = s1.length();
    int s2Length = s2.length();
    int[][] table = new int[s1Length + 1][s2Length + 1];

    for (int i = 1; i <= s1Length; i++) {
      table[i][0] = i;
    }
    for (int i = 1; i <= s2Length; i++) {
      table[0][i] = i;
    }

    int cost = 0;
    for (int i = 1; i <= s1Length; i++) {
      for (int j = 1; j <= s2Length; j++) {
        cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
        table[i][j] =
            Math.min(table[i - 1][j] + 1,
                Math.min(table[i][j - 1] + 1, table[i - 1][j - 1] + cost));
      }
    }

    return table[s1Length][s2Length];
  }

  /**
   * Returns the Hamming distance for the two given strings of equal length.
   * This is the number of different characters (case-sensitive) in both
   * strings.
   * 
   * @param s1
   *          the first string
   * @param s2
   *          the second string
   * @return Hamming distance that is not less than <code>0</code> if both
   *         strings are of equal length, <code>-1<code> otherwise
   */
  public static int getHammingDistance(String s1, String s2) {

    int distance;
    if (s1.equals(s2)) {
      distance = 0;
    } else {
      int s1Length = s1.length();
      int s2Length = s2.length();

      if (s1Length == s2Length) {
        distance = 0;
        for (int i = 0; i < s1Length; i++) {
          if (s1.charAt(i) != s2.charAt(i)) {
            distance++;
          }
        }
      } else {
        // Hamming distance is not defined for strings of unequal length
        distance = -1;
      }
    }

    return distance;
  }

  /**
   * Generates a random string.
   * 
   * @param length
   *          the length of the string to generate
   * @param rnd
   *          the random number generator to use
   * @return the generated string
   */
  public static String generateRandomString(int length, Random rnd) {
    if (length <= 0) {
      return "";
    }

    char[] characters =
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZöäüßÖÄÜ#+-.,;:_'*\"!<>|@€^ô°Ô´`éèÉÈáàÁÀóòÓÒ§$%&/()=?}][{]\r\n\t "
            .toCharArray();

    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(characters[rnd.nextInt(characters.length)]);
    }
    return builder.toString();
  }

  /**
   * Gets a formatted string with the current time and/or date. The format is
   * defined as for {@link SimpleDateFormat}, e.g. 'dd. MM. yyyy (HH:mm:ss)'.
   * 
   * @see SimpleDateFormat
   * 
   * @param format
   *          the format (as used by {@link SimpleDateFormat})
   * @return a string containing current time and date in the prescribed format
   */
  public static String getCurrentTimeAndDate(String format) {
    DateFormat dateFormat = new SimpleDateFormat(format);
    return dateFormat.format(Calendar.getInstance().getTime());
  }

  /**
   * Copies a character 'c' n times.
   * 
   * @param c
   *          the character
   * @param n
   *          the number of desired copies
   * @return the string containing the copies, or an empty string if n <= 0
   */
  public static String copyChar(char c, int n) {
    StringBuilder copies = new StringBuilder(n);
    for (int i = 0; i < n; i++) {
      copies.append(c);
    }
    return copies.toString();
  }

  /**
   * The simple helper class to compare objects by their string representation.
   * 
   * @see String#valueOf(Object)
   * 
   * @param <T>
   *          the type of the objects to be sorted
   */
  public static final class RepresentationComparator<T> implements
      Comparator<T>, Serializable {
    /**
     * The constant serial version uid.
     */
    private static final long serialVersionUID = -8314006737875935686L;

    @Override
    public int compare(T o1, T o2) {
      return String.valueOf(o1).compareTo(String.valueOf(o2));
    }
  }
}
