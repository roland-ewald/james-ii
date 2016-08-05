package org.jamesii.core.math.match;

import java.util.Map;

/**
 * @author Arne Bittig
 * @date 07.08.2014
 */
public interface ValueMatch {
  boolean matches(Object value, Map<String, ?> variables);
}
