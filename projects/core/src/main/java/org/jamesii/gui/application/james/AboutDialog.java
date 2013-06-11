/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.ApplicationDialog;
import org.jamesii.gui.application.resource.ApplicationResourceManager;
import org.jamesii.gui.application.resource.BasicResources;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.ResourceLoadingException;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.decoration.Decorator;
import org.jamesii.gui.decoration.MirrorDecoration;

/**
 * The Class AboutDialog.
 * 
 * @author Enrico Seib
 */
public final class AboutDialog extends ApplicationDialog {

  private static final int VGAP = 10;

  private static final int HGAP = 20;

  private static final int INSET10 = 10;

  private static final float FONTSIZE20 = 20f;

  private static final int MIRRORSIZE = 35;

  private static final int INSET5 = 5;

  private static final int HEIGHT = 600;

  private static final int WIDTH = 600;

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1079962700852281433L;

  /**
   * Field for the license
   */
  private JEditorPane licenseField;

  /**
   * Instantiates a new about dialog.
   * 
   * @param owner
   *          the owner
   */
  public AboutDialog(Window owner) {
    super(owner);

    setModal(true);
    setTitle("About");
    setSize(WIDTH, HEIGHT);

    init();

  }

  /**
   * Inits the.
   */
  public void init() {
    JPanel jp = new JPanel(new BorderLayout(HGAP, VGAP));
    jp.setBorder(BorderFactory.createEmptyBorder(INSET5, INSET10, INSET5,
        INSET10));

    Box jpNorth = Box.createVerticalBox();

    JLabel jII =
        new JLabel(SimSystem.SIMSYSTEM + " Version " + SimSystem.VERSION);
    jII.setFont(jII.getFont().deriveFont(FONTSIZE20));
    jII.setAlignmentX(CENTER_ALIGNMENT);

    JLabel jIII = new JLabel("Click here to visit JAMES II on the web");

    // setting up the text attributes for the link label (not bold,
    // italic plus underlined)
    Map<TextAttribute, Object> attributes = new HashMap<>();
    attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
    attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT);

    jIII.setFont(jIII.getFont().deriveFont(attributes));
    jIII.addMouseListener(createMA());
    jIII.setAlignmentX(CENTER_ALIGNMENT);

    JTextArea credits = new JTextArea(SimSystem.getCredits());
    credits.setWrapStyleWord(true);
    credits.setEditable(false);
    credits.setAlignmentX(LEFT_ALIGNMENT);

    Box vBox = Box.createVerticalBox();
    vBox.add(jII);
    vBox.add(Box.createVerticalStrut(INSET5));
    vBox.add(jIII);

    jpNorth.add(new Decorator(vBox, new MirrorDecoration(MIRRORSIZE, 1)));
    jpNorth.add(new JScrollPane(credits));

    jp.add(jpNorth, BorderLayout.NORTH);

    JPanel jpSouth = new JPanel(new BorderLayout());

    JTabbedPane tp = new JTabbedPane();

    String licenseText = null;
    try {
      licenseText =
          ApplicationResourceManager
              .getResource(BasicResources.TEXTFILE_LICENSE_TEXT);
    } catch (ResourceLoadingException e) {
      SimSystem.report(e);
    }

    if (licenseText != null) {
      licenseText =
          licenseText.replaceAll(
              "(\r?\n\\s*)+(\\d)[.][\\s*\t]([^\r\n]*)(\r?\n\\s*)+",
              "<h2>$2. $3</h2>");
      licenseText = licenseText.replaceAll("(\r?\n){2,}", "<p/>");
      licenseText = licenseText.replaceAll("(\r?\n)+", "<br/>");
      licenseText =
          licenseText.replaceAll("(?i)org.jamesii ii", "<b><i>$0</i></b>");
      licenseText = "<html>" + licenseText + "</html>";
    }

    licenseField = new JEditorPane();
    licenseField.setContentType("text/html");
    licenseField.setEditable(false);

    licenseField.setText(licenseText);
    licenseField.setCaretPosition(0);

    JTextArea vm = new JTextArea(SimSystem.getVMInfo());
    vm.setWrapStyleWord(true);
    vm.setEditable(false);

    JPanel licensePanel = new JPanel();
    licensePanel.setLayout(new BorderLayout());
    licensePanel.add(new JScrollPane(licenseField), BorderLayout.CENTER);

    Box subLicensePanel;

    subLicensePanel = Box.createVerticalBox();
    subLicensePanel.add(Box.createVerticalStrut(INSET5));

    JButton printButton = new JButton("Print license");
    printButton.setIcon(IconManager.getIcon(IconIdentifier.PRINT_SMALL));
    printButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        openPrintDialog(licenseField);
      }
    });

    printButton.setAlignmentX(RIGHT_ALIGNMENT);
    subLicensePanel.add(printButton);

    licensePanel.add(subLicensePanel, BorderLayout.SOUTH);

    tp.addTab("License Information", licensePanel);
    tp.addTab("System Information", new JScrollPane(vm));

    jpSouth.add(tp, BorderLayout.CENTER);

    JPanel sp = new JPanel();
    sp.setLayout(new FlowLayout());

    JButton closeButton = new JButton("Close");
    closeButton.setIcon(IconManager.getIcon(IconIdentifier.CLOSE_SMALL));
    closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });

    sp.add(closeButton);

    jpSouth.add(sp, BorderLayout.SOUTH);

    jp.add(jpSouth, BorderLayout.CENTER);

    this.setContentPane(jp);
  }

  /**
   * Creates the mouse adapter.
   * 
   * @return the mouse adapter
   */
  private MouseAdapter createMA() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        openBrowserDialog();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
    };
  }

  /**
   * Opens the standard browser to open org.jamesii II in the web
   */
  private void openBrowserDialog() {
    // test if java desktop is supported
    if (Desktop.isDesktopSupported()) {
      Desktop desk = Desktop.getDesktop();
      try {
        desk.browse(new URI("http://www.jamesii.org"));
      } catch (IOException | URISyntaxException e) {
        SimSystem.report(e);
      }
    } else {
      JOptionPane.showMessageDialog(this,
          "Your your operating system does not support this option ");
    }
  }

  /**
   * Opens dialog to print the license file
   * 
   * @param ePane
   *          JEditorPane which content should be printed
   */
  private void openPrintDialog(JEditorPane ePane) {

    try {
      ePane.print();
    } catch (PrinterException e) {
      SimSystem.report(e);
    }
  }
}
