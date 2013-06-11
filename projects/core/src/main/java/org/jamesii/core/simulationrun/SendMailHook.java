/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun;

import java.io.Serializable;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.tasks.ComputationTaskHook;
import org.jamesii.core.util.EMail;

/**
 * The Class SendMailHook.
 * 
 * @author Jan Himmelspach
 * @param <I>
 */
public class SendMailHook<I extends Serializable> extends
    ComputationTaskHook<I> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4399364663644742787L;

  /** The mail. */
  private EMail mail = null;

  /** The mail adresses. */
  private String[] mailAdresses = null;

  /** The sender. */
  private String sender;

  /**
   * The Constructor.
   * 
   * @param iMailHost
   *          the i mail host
   * @param iPort
   *          the i port
   * @param oldHook
   *          the old hook
   * @param from
   *          the from
   * @param mailAdresses
   *          the mail adresses
   */
  public SendMailHook(String iMailHost, int iPort,
      ComputationTaskHook<I> oldHook, String from, String[] mailAdresses) {
    super(oldHook);
    mail = new EMail(iMailHost, iPort);
    this.sender = from;
    this.mailAdresses = mailAdresses;
  }

  /**
   * The Constructor.
   * 
   * @param iMailHost
   *          the i mail host
   * @param iPort
   *          the i port
   * @param from
   *          the from
   * @param mailAdresses
   *          the mail adresses
   */
  public SendMailHook(String iMailHost, int iPort, String from,
      String[] mailAdresses) {
    super(null);
    mail = new EMail(iMailHost, iPort);
    this.sender = from;
    this.mailAdresses = mailAdresses;
  }

  /**
   * Send E-Mail containing toString value of the passed information.
   * 
   * @param information
   *          the information
   */
  @Override
  protected void executeHook(I information) {
    mail.sendMail(SimSystem.SIMSYSTEM + " " + SimSystem.VERSION
        + " sends some information", information.toString(), sender,
        mailAdresses);
  }

}
