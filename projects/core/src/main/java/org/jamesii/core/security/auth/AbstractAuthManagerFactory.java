/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.security.auth;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.security.auth.simpleauthmanager.SimpleAuthManagerFactory;

/**
 * Abstract factory for the authentication and authorization manager plugin.
 * 
 * @author Simon Bartels
 * 
 */
public class AbstractAuthManagerFactory extends
    AbstractFactory<AuthManagerFactory> {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = -173767813142028782L;

  /**
   * Parameter block identifier for the configuration file of the auth* manager.
   */
  public static final String FILE_PARAM = "file";

  /**
   * Class that asks all auth* manager factories whether they can handle the
   * given configuration.
   * 
   * @author Simon Bartels
   * 
   */
  private class ConfigurationFileCriterion extends
      FactoryCriterion<AuthManagerFactory> {

    @Override
    public List<AuthManagerFactory> filter(List<AuthManagerFactory> factories,
        ParameterBlock parameter) {
      if (parameter == null || !parameter.hasSubBlock(FILE_PARAM)) {
        SimSystem
            .report(Level.SEVERE,
                "Can not determine an auth*manager factory without configuration parameters!");
        return new ArrayList<>();
      }

      File f = new File((String) parameter.getSubBlock(FILE_PARAM).getValue());
      if (!f.exists()) {
        String message =
            "Specified file: " + f.getAbsolutePath() + " does not exist.\n"
                + "Working directory is " + System.getProperty("user.dir");

        // lets make sure that this is definitely noticed
        SimSystem.report(Level.SEVERE, message);
        // when there's no config then there MUST NOT be a proper security
        // system
        return new ArrayList<>();
      }
      for (int i = factories.size() - 1; i >= 0; i--) {
        if (!factories.get(i).supportsConfigurationFile(f)) {
          factories.remove(i);
        }
      }
      return factories;
    }
  }

  /**
   * Criterion evaluating all factories regarding the quality of the underlying
   * security system. Basically this criterion just serves to dismiss the
   * {@link SimpleAuthManagerFactory}.
   * 
   * @author Simon Bartels
   * @author Jan Himmelspach
   */
  private class QualityCriterion extends FactoryCriterion<AuthManagerFactory> {
    @Override
    public List<AuthManagerFactory> filter(List<AuthManagerFactory> factories,
        ParameterBlock parameter) {
      Collections.sort(factories, new Comparator<AuthManagerFactory>() {
        @Override
        public int compare(AuthManagerFactory o1, AuthManagerFactory o2) {
          return Integer.valueOf(o2.getQualityFeedback()).compareTo(
              o1.getQualityFeedback());
        }
      });
      return factories;
    }
  }

  /**
   * Default constructor adding the criteria.
   */
  public AbstractAuthManagerFactory() {
    addCriteria(new ConfigurationFileCriterion());
    addCriteria(new QualityCriterion());
  }

}
