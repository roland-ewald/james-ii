/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

import org.jamesii.SimSystem;

/**
 * Simple class for sending EMails. This here seems to work for the EMails we
 * currently need to send. <br/>
 * Here we still miss sending of attachments. See
 * http://tools.ietf.org/html/rfc821 for further information.
 * 
 * @author Jan Himmelspach
 */
public class EMail {

  /** The date format. */
  private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL,
      Locale.US);

  /** The port number to be used for sending the mail. */
  private int smtpHostPort;

  /** The data input stream used for talking to the smtp server. */
  private BufferedReader smtpServerInputStream = null;

  /** The data output stream used for talking to the smtp server. */
  private DataOutputStream smtpServerOutputStream = null;

  /** The host name to be used for sending the mail. */
  private String smtpHostName;

  /**
   * Instantiates a new e-mail sending object.
   * 
   * @param smtpHostName
   *          the host name to be used
   * @param smtpHostPortNumber
   *          the port number to be used
   */
  public EMail(String smtpHostName, int smtpHostPortNumber) {
    super();

    this.smtpHostName = smtpHostName;
    this.smtpHostPort = smtpHostPortNumber;

  }

  /**
   * Connect to a smtp server.
   * 
   * @return true, if connection is established, false otherwise
   */
  private boolean connect() {

    /** The smtp socket to be used for sending the mail. */
    Socket smtpSocket = null;

    // try to connect to server
    try {
      // create the socket to talk through
      smtpSocket = new Socket(smtpHostName, smtpHostPort);

      // create a reader to read from (server responses)
      smtpServerInputStream =
          new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));

      // create stream to write through
      smtpServerOutputStream =
          new DataOutputStream(smtpSocket.getOutputStream());

      // if none if the above is null we should have a connection to the smtp
      // server
      if (smtpSocket == null || smtpServerOutputStream == null
          || smtpServerInputStream == null) {
        sendingFailed(null);
        return false;
      }
    } catch (IOException e) {
      sendingFailed(e);
      return false;
    }

    return true;
  }

  private void sendingFailed(Throwable cause) {
    SimSystem.report(Level.SEVERE, "Sending of the e-mail failed (Host: "
        + smtpHostName + " Port: " + smtpHostPort + ")", cause);
  }

  /**
   * Clear server replies (empty the input stream).
   * 
   * @throws IOException
   */
  private void clearServerReplies() throws IOException {
    while (smtpServerInputStream.readLine() != null) {
    }
  }

  /**
   * Check server reply.
   * 
   * @param s
   *          the answer expected
   * 
   * @throws IOException
   */
  private void checkServerReply(String s) throws IOException {
    String response = null;
    while (response == null) {
      response = smtpServerInputStream.readLine();
    }
    // check response, if we start with the given string we are fine and can get
    // out here
    if (response.indexOf(s) == 0) {
      return;
    }

    throw new RuntimeException("Server reported " + response
        + " and the code expected was " + s);
  }

  /**
   * Send an EMail without cc and bcc receivers.
   * 
   * @param subject
   *          the subject
   * @param text
   *          the message
   * @param from
   *          the from
   * @param receiverAdresses
   *          the receivers
   */
  public void sendMail(String subject, String text, String from,
      String[] receiverAdresses) {
    sendMail(subject, text, from, receiverAdresses, null, null);
  }

  private void send(String msg, String reply) throws IOException {
    smtpServerOutputStream.writeBytes(msg);
    checkServerReply(reply); // OK
  }

  /**
   * Send an EMail.
   * 
   * @param subject
   *          the subject
   * @param text
   *          the message
   * @param from
   *          the from
   * @param receiverAddresses
   *          the receivers
   * @param ccReceiverAddresses
   *          the cc receiver addresses
   * @param bccReceiverAddresses
   *          the bcc receiver addresses
   */
  public void sendMail(String subject, String text, String from,
      String[] receiverAddresses, String[] ccReceiverAddresses,
      String[] bccReceiverAddresses) {

    // try to create connection to server, if fails then exit
    if (!connect()) {
      return;
    }

    Date date = new Date();
    try {

      clearServerReplies();

      // start sending protocol
      send("HELO\r\n", "250");

      // send sender's name/address
      send("MAIL From: <" + from + ">\r\n", "250"); // Sender ok

      // send receivers names/adresses
      for (int i = 0; i < receiverAddresses.length; i++) {
        smtpServerOutputStream.writeBytes("RCPT To: <" + receiverAddresses[i]
            + ">\r\n");
      }

      checkServerReply("250"); // Receiver ok

      // if cc receivers add them
      if (ccReceiverAddresses != null) {
        for (int i = 0; i < ccReceiverAddresses.length; i++) {
          smtpServerOutputStream.writeBytes("RCPT Cc: <"
              + ccReceiverAddresses[i] + ">\r\n");
        }
      }

      clearServerReplies();

      // send data (content of the mail)
      send("DATA\r\n", "354"); // server should tell us to end data with with a
                               // dot .

      // *************** let's write the header

      // sending application
      smtpServerOutputStream.writeBytes("X-Mailer: " + SimSystem.SYSTEMNAME
          + "\r\n");
      // local date
      smtpServerOutputStream.writeBytes("DATE: " + dateFormat.format(date)
          + "\r\n");
      // sender
      smtpServerOutputStream.writeBytes("From: " + SimSystem.SYSTEMNAME
          + "-Mailer <" + from + ">\r\n");
      // receiver
      for (int i = 0; i < receiverAddresses.length; i++) {
        smtpServerOutputStream.writeBytes("To:  <" + receiverAddresses[i]
            + ">\r\n");
      }

      // if cc receivers add them here again
      if (ccReceiverAddresses != null) {
        for (int i = 0; i < ccReceiverAddresses.length; i++) {
          smtpServerOutputStream.writeBytes("Cc: <" + ccReceiverAddresses[i]
              + ">\r\n");
        }
      }

      // if bcc receivers add them here again
      if (ccReceiverAddresses != null) {
        for (int i = 0; i < bccReceiverAddresses.length; i++) {
          smtpServerOutputStream.writeBytes("bcc: <" + bccReceiverAddresses[i]
              + ">\r\n");
        }
      }

      // subject and text
      smtpServerOutputStream.writeBytes("Subject: " + subject + "\r\n");
      smtpServerOutputStream.writeBytes(text + "\r\n");
      // mail has to be terminated by a line containing a single "."
      smtpServerOutputStream.writeBytes("\r\n.\r\n");

      checkServerReply("250");

      // quit
      smtpServerOutputStream.writeBytes("QUIT\r\n");

      // server will say bye, ignoring

    } catch (IOException e) {
      sendingFailed(e);
    }
  }

}
