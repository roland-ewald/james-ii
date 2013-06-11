/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * An {@link TestCase} implementation that is able to provide additional
 * information to the test configuration that has currently failed or has
 * omitted an error. E.g., it is possible to provide a parameter called seed as
 * part of the test configuration that is needed to reproduce the test.
 * 
 * @author Stefan Rybacki
 */
public abstract class ChattyTestCase extends TestCase {

  /**
   * The key value list.
   */
  private final List<Entry<String, String>> keyValue = new ArrayList<>();

  /**
   * Additional map that allows to override previous set parameters
   */
  private final Map<String, String> fieldParams = new HashMap<>();

  public ChattyTestCase(String name) {
    super(name);
  }

  public ChattyTestCase() {
    super();
  }

  /**
   * Run.
   * 
   * @param res
   *          the result
   */
  @Override
  public void run(final TestResult res) {
    try {
      super.run(new TestResult() {
        @Override
        public synchronized void addError(Test test, Throwable t) {
          res.addError(test,
              new RuntimeException((t.getMessage() != null ? t.getMessage()
                  : "") + createMessageProlog(), t));
        }

        @Override
        public synchronized void addFailure(Test test, AssertionFailedError t) {
          AssertionFailedError failure =
              new AssertionFailedError((t.getMessage() != null ? t.getMessage()
                  : "") + createMessageProlog());
          failure.setStackTrace(t.getStackTrace());
          res.addFailure(test, failure);
        }

        @Override
        public void endTest(Test test) {
          res.endTest(test);
        }

        @Override
        public void startTest(Test test) {
          res.startTest(test);
        }

      });
    } catch (Throwable e) {
    } finally {
    }
  }

  /**
   * Adds the specified information to the end of the information list and is
   * provided when a test fails or omits an error. Also for parameter providing
   * see {@link #addParameter(String, Object)}.
   * 
   * @param information
   *          the information
   */
  public void addInformation(String information) {
    addParameter("INFO", information);
  }

  /**
   * Adds the parameter to the list of test parameters that are might needed to
   * reproduce failing tests. Parameters will be added up front to the list of
   * parameters. Where information passed through
   * {@link #addInformation(String)} are added to the end of the information
   * list.
   * 
   * @param key
   *          the parameter key
   * @param value
   *          the parameter value
   */
  public void addParameter(String key, Object value) {
    if (key != null) {
      if (value != null) {
        keyValue.add(0, new SimpleImmutableEntry<>(key, String.valueOf(value)));
      } else {
        keyValue.add(new SimpleImmutableEntry<>(key, String.valueOf(value)));
      }
    }

  }

  /**
   * Adds the parameter to the list of test parameters that are might needed to
   * reproduce failing tests. Parameters will be added or overridden in case the
   * specified key already exists as updatable parameter. If value is
   * <code>null</code> the updatable parameter identified by the given key will
   * be removed.
   * 
   * @param key
   *          the parameter key
   * @param value
   *          the parameter value, if <code>null</code> a previous updateable
   *          parameter with the given key will be removed
   */
  public void addUpdateableParameter(String key, Object value) {
    if (key != null) {
      if (value != null) {
        fieldParams.put(key, String.valueOf(value));
      } else {
        fieldParams.remove(key);
      }
    }

  }

  /**
   * Helper method that creates the failure/error message prolog containing all
   * provided parameters and information.
   * 
   * @return the prolog message
   */
  protected String createMessageProlog() {
    StringBuilder message = new StringBuilder();
    message.append("\n");

    // find widest key
    int w = 0;
    for (Entry<String, String> e : keyValue) {
      if (e.getKey().length() > w && e.getValue() != null) {
        w = e.getKey().length();
      }
    }

    for (Entry<String, String> e : fieldParams.entrySet()) {
      if (e.getKey().length() > w && e.getValue() != null) {
        w = e.getKey().length();
      }
    }

    for (Entry<String, String> e : fieldParams.entrySet()) {
      // now show key plus padding according to w
      message.append(padding(e.getKey(), w));
      if (e.getValue() != null) {
        message.append("=");
        message.append(e.getValue());
      }
      message.append("\n");
    }

    for (Entry<String, String> e : keyValue) {
      // now show key plus padding according to w
      message.append(padding(e.getKey(), w));
      if (e.getValue() != null) {
        message.append("=");
        message.append(e.getValue());
      }
      message.append("\n");
    }

    return message.toString();
  }

  /**
   * Takes the given key and adds as many spaces as needed to fit the specified
   * width w.
   * 
   * @param key
   *          the key
   * @param w
   *          the width to fit
   * @return the padded string
   */
  private String padding(String key, int w) {
    StringBuilder b = new StringBuilder();
    b.append(key);
    for (int i = 0; i < w - key.length(); i++) {
      b.append(" ");
    }

    return b.toString();
  }

}
