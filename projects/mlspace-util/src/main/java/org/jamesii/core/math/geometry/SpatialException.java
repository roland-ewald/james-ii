/**
 *
 */
package org.jamesii.core.math.geometry;

/**
 * Runtime exception for anything arising in geometry-related calculations (not
 * so much for {@link IllegalArgumentException illegal arguments} or
 * {@link UnsupportedOperationException unsupported operations}).
 *
 * @author Arne Bittig
 */
public class SpatialException extends RuntimeException {

  private static final long serialVersionUID = 4437595217652623974L;

  /**
   * @param message
   *          detail message (passed to
   *          {@link RuntimeException#RuntimeException(String)})
   */
  public SpatialException(String message) {
    super(message);
  }

}
