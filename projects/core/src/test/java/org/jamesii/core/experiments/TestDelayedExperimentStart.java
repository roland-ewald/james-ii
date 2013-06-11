package org.jamesii.core.experiments;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.logging.TestingLogListener;

/**
 * Tests whether {@link BaseExperiment#stop(boolean)} can be safely called even
 * though {@link BaseExperiment#execute()} takes 'unusually' long for
 * initialization (for example, the registry could need to be initialized, which
 * is the case in this test).
 * <p/>
 * <b>NOTE:</b> This test assumes it takes the registry *more* than
 * {@link TestDelayedExperimentStart#EXP_STOP_DELAY} milliseconds to be
 * initialized. If not, this test will fail *erroneously*, because the specified
 * experiment is not valid.
 * 
 * @author Roland Ewald
 * 
 */
public class TestDelayedExperimentStart extends ChattyTestCase {

  /** The time delay until {@link BaseExperiment#stop(boolean)} is called. */
  private static final long EXP_STOP_DELAY = 500L;

  public void testDelayedStart() throws URISyntaxException {

    TestingLogListener testLog = new TestingLogListener(Level.SEVERE);
    ApplicationLogger.addLogListener(testLog);

    final BaseExperiment exp = new BaseExperiment();
    exp.setModelLocation(new URI("java://no.such.model"));

    new Thread(new Runnable() {
      @Override
      public void run() {
        exp.execute();
      }
    }).start();

    try {
      Thread.sleep(EXP_STOP_DELAY);
      exp.stop(true);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception encountered");
    }

    if (testLog.getLastLogRecord() != null) {
      addInformation("Log Record: " + testLog.getLastLogRecord().getThrown());
    }
    assertNull(testLog.getLastLogRecord());
  }
}
