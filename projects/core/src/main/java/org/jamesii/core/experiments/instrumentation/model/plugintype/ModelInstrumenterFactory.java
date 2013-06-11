/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.model.plugintype;

import java.net.URI;

import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Super class of all model instrumenter factories.
 * 
 * @author Roland Ewald
 */
public abstract class ModelInstrumenterFactory extends
    Factory<IModelInstrumenter> implements IParameterFilterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4155370976453153158L;

  /**
   * Checks the URI in the required parameter block for supported schemes. A
   * supported scheme is a string like "file-ca" where the "ca" part must be
   * EXACTLY matched by one of the supported formalism-specific (sub-)schemes.
   * 
   * @param params
   *          the parameter block
   * @param schemes
   *          the list of supported (sub-)schemes
   * @return 1 if there is one parameter scheme that is contained in the URI
   *         scheme, otherwise 0
   */
  public static int supportsSchemes(ParameterBlock params, String... schemes) {
    URI uri =
        (URI) params
            .getSubBlockValue(AbstractModelInstrumenterFactory.MODELURI);
    return uri == null ? 0 : matchScheme(uri.getScheme(), schemes);
  }

  /**
   * Checks the URI in the required parameter block for supported string
   * patterns.
   * 
   * @param params
   *          the parameter block
   * @param schemes
   *          the list of supported strings
   * @return 1 if there is one parameter string that is contained in the URI,
   *         otherwise 0
   */
  public static int supportsURIStrings(ParameterBlock params, String... strings) {
    URI uri =
        (URI) params
            .getSubBlockValue(AbstractModelInstrumenterFactory.MODELURI);
    return uri == null ? 0 : matchStrings(uri.toString(), strings);
  }

  /**
   * Matches strings.
   * 
   * @param target
   *          the target string
   * @param matches
   *          the list of potential matches
   * @return 1 if one of the matches is contained in the target, otherwise 0
   */
  private static int matchStrings(String target, String... matches) {
    for (String match : matches) {
      if (target.contains(match)) {
        return 1;
      }
    }
    return 0;
  }

  /**
   * Match scheme by a number of (sub-) schemes.
   * 
   * @param scheme
   *          the scheme
   * @param subSchemes
   *          the sub schemes
   * @return one if a sub-scheme matches the given scheme, zero otherwise
   */
  protected static int matchScheme(String scheme, String[] subSchemes) {
    String schemeToBeChecked = extractSubSchemeToBeChecked(scheme);
    for (String subScheme : subSchemes) {
      if (subScheme.equals(schemeToBeChecked)) {
        return 1;
      }
    }
    return 0;
  }

  /**
   * Extract the sub-scheme to be checked.
   * 
   * @param scheme
   *          the URI scheme
   * @return the string that contains the part after a '-', if such a character
   *         exists
   */
  private static String extractSubSchemeToBeChecked(String scheme) {
    int subSchemeStart = scheme.indexOf('-');
    if (subSchemeStart < 0) {
      return scheme;
    }
    if (subSchemeStart == scheme.length() - 1) {
      throw new IllegalArgumentException("Scheme '" + scheme
          + "' seems to be malformed.");
    }
    return scheme.substring(subSchemeStart + 1);
  }

}
