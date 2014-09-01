/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jamesii.SimSystem;
import org.jamesii.gui.decoration.Decorator;
import org.jamesii.gui.decoration.MirrorDecoration;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.FlatButton;
import org.jamesii.gui.utils.GradientPanel;

/**
 * Main class to provide wizard functionality. Basically the wizard class forms
 * a container that manages wizard pages {@link IWizardPage} provided by a
 * wizard controller {@link IWizardController}. It provides main functionality
 * like displaying a specific wizard page and providing options to move forward,
 * backward or cancel the wizarding process.<br/>
 * The wizard controller provides the wizard pages through its
 * {@link IWizardController#init(IWizard)} method, as well as the reactions on
 * canceling or successfully finishing the wizard.<br/>
 * The {@link IWizardController} provides the validation of their data as well
 * as information about the content and the possible continue options (back,
 * next, cancel and finish).<br/>
 * The provided {@link IWizard} object should be used to persist data between
 * pages and to obtain collected data throughout the wizarding process via the
 * {@link IWizardController}.
 * 
 * @author Stefan Rybacki
 * @see WelcomePage
 * @see LicensePage
 */
public class Wizard implements WindowListener {
  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class WizardImplementation implements IWizard {
    @Override
    public String getPreviousPage() {
      return model.getPreviousPageId();
    }

    @Override
    public <B> B getValue(final String id) {
      return model.getValue(id);
    }

    @Override
    public boolean isValue(final String id) {
      return model.isValue(id);
    }

    @Override
    public void putValue(final String id, final Object value) {
      model.putValue(id, value);
    }

    @Override
    public void removeValue(final String id) {
      model.removeValue(id);
    }

    @Override
    public void registerPage(final IWizardPage page, final String id) {
      model.addPage(page, id);

      final Dimension s = page.getPreferredSize();
      if (s != null) {
        preferredPageSize.width = Math.max(s.width, preferredPageSize.width);
        preferredPageSize.height = Math.max(s.height, preferredPageSize.height);
      }
    }
  }

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class WizardControllerListenerImplementation implements
      IWizardControllerListener {
    @Override
    public void backChanged(final IWizardPage page) {
      checkButtons(page);
    }

    @Override
    public void cancelChanged(final IWizardPage page) {
      checkButtons(page);
    }

    @Override
    public void finishChanged(final IWizardPage page) {
      checkButtons(page);
    }

    @Override
    public void helpChanged(final IWizardPage page) {
      checkButtons(page);
    }

    @Override
    public void nextChanged(final IWizardPage page) {
      checkButtons(page);
    }

    @Override
    public void statesChanged(final IWizardPage page) {
      checkButtons(page);
    }

    @Override
    public void next(final IWizardPage page) {
      doNext(page);
    }
  }

  /**
   * Internally used {@link IWizard} object to obfuscate internal functions.
   * This forces the user to implement the {@link IWizardController} interface
   * and to provide the wizard pages from there.
   */
  private IWizard iWizard = new WizardImplementation();

  /**
   * the wizard window
   */
  private JDialog wizard;

  /**
   * the main panel where the header and the actual content of the current page
   * is displayed
   */
  private JPanel mainPanel;

  /**
   * the back button
   */
  private JButton backwardButton;

  /**
   * the next button
   */
  private JButton forwardButton;

  /**
   * the cancel button
   */
  private JButton cancelButton;

  /**
   * the header
   */
  private JPanel header;

  /**
   * the help button
   */
  private JButton helpButton;

  /**
   * the finish button
   */
  private JButton finishButton;

  /**
   * the icon label used to display the icon for the current page
   */
  private JLabel wizardIcon;

  /**
   * the label displaying the current page title
   */
  private JLabel titleLabel;

  /**
   * the label displaying the current sub title
   */
  private JLabel subTitleLabel;

  /**
   * used to determine the proper wizard window size by determining the max
   * preferred page size of all registered pages
   */
  private final Dimension preferredPageSize = new Dimension(0, 0);

  /**
   * wizard model managing pages and current selected page
   */
  private final WizardModel model = new WizardModel(iWizard);

  /**
   * the controller used
   */
  private IWizardController controller;

  /**
   * The ran flag.
   */
  private boolean ran;

  /**
   * The wizard controller listener.
   */
  private IWizardControllerListener listener =
      new WizardControllerListenerImplementation();

  /**
   * The action listener.
   */
  private ActionListener actionListener;

  /**
   * Helper method simulating a click on next button if next is enabled.
   * 
   * @param page
   *          the page
   */
  private void doNext(final IWizardPage page) {
    if (page == model.getCurrentPage()
        && controller.canNext(model.getCurrentPageId(), iWizard)
        && forwardButton.isEnabled()) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          forwardButton.doClick();
        }
      });
      return;
    }

    // if next button is not enabled because its the last page but you
    // can finish use finish instead
    if (page == model.getCurrentPage()
        && controller.canFinish(model.getCurrentPageId(), iWizard)
        && finishButton.isEnabled()) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          finishButton.doClick();
        }
      });
    }

  }

  /**
   * Instantiates a new wizard. The given controller is used to provide
   * {@link IWizardPage}s and the owner frame is used to center the resulting
   * wizard over it.
   * 
   * @param owner
   *          the owner frame (can be null)
   * @param controller
   *          the wizard controller that provides {@link IWizardPage}s and
   *          reacts on finished wizard
   */
  public Wizard(final JFrame owner, final IWizardController controller) {
    wizard = new JDialog(owner);
    this.controller = controller;
    controller.addWizardControllerListener(listener);

    JPanel contentPanel = new JPanel(new BorderLayout());
    initComponents(contentPanel);

    wizard.getContentPane().add(contentPanel);

    wizard.addWindowListener(this);
    wizard.setMinimumSize(new Dimension(400, 300));
    wizard.setModal(true);
    wizard.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    controller.init(iWizard);
  }

  /**
   * helper method to initialize the wizard components like buttons, header and
   * so on
   * 
   * @param contentPane
   *          the panel that is used to put all wizarding components on
   */
  private void initComponents(final JPanel contentPane) {
    final JPanel buttonPanel = new JPanel();
    final Box buttonBox = Box.createHorizontalBox();

    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());

    actionListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Wizard.this.actionPerformed(e);
      }
    };

    backwardButton = new JButton(new BackwardAction(actionListener));

    forwardButton = new JButton(new ForwardAction(actionListener));
    forwardButton.setHorizontalTextPosition(SwingConstants.LEFT);
    helpButton = new FlatButton(new HelpAction(actionListener));
    cancelButton = new JButton(new CancelAction(actionListener));
    finishButton = new JButton(new FinishAction(actionListener));

    buttonPanel.setLayout(new BorderLayout());
    buttonPanel.add(new JSeparator(), BorderLayout.NORTH);

    buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
    buttonBox.add(helpButton);
    buttonBox.add(Box.createHorizontalGlue());
    buttonBox.add(backwardButton);
    buttonBox.add(Box.createHorizontalStrut(10));
    buttonBox.add(forwardButton);
    buttonBox.add(Box.createHorizontalStrut(30));
    buttonBox.add(finishButton);
    buttonBox.add(Box.createHorizontalStrut(10));
    buttonBox.add(cancelButton);
    buttonPanel.add(buttonBox, BorderLayout.CENTER);

    contentPane.add(buttonPanel, java.awt.BorderLayout.SOUTH);
    contentPane.add(mainPanel, java.awt.BorderLayout.CENTER);

    final Box westBox = Box.createVerticalBox();

    final Box titleBox = Box.createHorizontalBox();
    final Box subTitleBox = Box.createHorizontalBox();

    titleLabel = new JLabel("TITLE GOES HERE");
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
    titleBox.add(Box.createHorizontalStrut(10));
    titleBox.add(titleLabel);
    titleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

    subTitleLabel = new JLabel("SUBTITLE GOES HERE");
    subTitleBox.add(Box.createHorizontalStrut(15));
    subTitleBox.add(subTitleLabel);
    subTitleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

    westBox.add(titleBox);
    westBox.add(subTitleBox);

    header =
        new GradientPanel(new BorderLayout(), Color.white, new Color(250, 250,
            250));
    header.setPreferredSize(new Dimension(10, 64));

    header.add(westBox, BorderLayout.LINE_START);

    wizardIcon = new JLabel();

    final Box iconBox = Box.createHorizontalBox();
    iconBox.add(wizardIcon);

    header.add(new Decorator(iconBox, new MirrorDecoration(20, 1)),
        BorderLayout.LINE_END);

    header.add(new JSeparator(), BorderLayout.PAGE_END);

    mainPanel.add(header, BorderLayout.NORTH);
  }

  /**
   * helper method disabling all wizard buttons at once
   */
  private void makeDefault() {
    try {
      BasicUtilities.invokeAndWaitOnEDT(new Runnable() {

        @Override
        public void run() {
          // disable all buttons
          forwardButton.setEnabled(false);
          backwardButton.setEnabled(false);
          finishButton.setEnabled(false);
          helpButton.setEnabled(false);
          cancelButton.setEnabled(false);
        }

      });
    } catch (final Exception e) {
      SimSystem.report(e);
    }
  }

  /**
   * Changes current wizard page to the one specified. The backwards parameter
   * is needed in case the page change occurs because of a backward action. In
   * this case the visited pages queue needs to be updated differently.
   * 
   * @param page
   *          the page to change to
   * @param backwards
   *          if true the page change occurs because of a backward action
   */
  private void changePage(final String page, final boolean backwards) {
    IWizardPage currentPage = model.getCurrentPage();
    // deco.markTransitionStart();

    try {
      currentPage = model.setCurrentPage(page, backwards);
      if (currentPage == null) {
        SimSystem.report(Level.WARNING, null,
            String.format("Page not found! (%s)", page), null);
        SimSystem
            .report(
                Level.INFO,
                null,
                "The wizard encountered an error in one of its pages and is being closed!",
                null);
        cancel();
        return;
      }

      // disable everything (means set to default)
      makeDefault();

      titleLabel.setText(currentPage.getTitle());
      subTitleLabel.setText(currentPage.getSubTitle());
      wizardIcon.setIcon(currentPage.getPageIcon());

      mainPanel.removeAll();
      mainPanel.add(header, BorderLayout.NORTH);
      final JComponent wpage = currentPage.getPage();
      if (wpage != null) {
        mainPanel.add(currentPage.getPage(), BorderLayout.CENTER);
      }
      mainPanel.revalidate();

      SwingUtilities.updateComponentTreeUI(mainPanel);
    } catch (RuntimeException e) {
      SimSystem.report(e);
      SimSystem
          .report(Level.INFO,
              "The wizard encountered an error in one of its pages and is being closed!");
      cancel();
    }

    // deco.markTransitionEnd();

    checkButtons(currentPage);
    currentPage.shown(iWizard);
  }

  /**
   * Button clicked reaction.
   * 
   * @param e
   *          the event
   */
  protected final void actionPerformed(final ActionEvent e) {
    if (e.getSource() == backwardButton) {
      model.getCurrentPage().hiding(iWizard);
      final String newPage =
          controller.getBackPage(model.getCurrentPageId(), iWizard);
      if (newPage != null) {
        changePage(newPage, true);
      }
    }
    if (e.getSource() == forwardButton) {
      model.getCurrentPage().hiding(iWizard);
      final String newPage =
          controller.getNextPage(model.getCurrentPageId(), iWizard);
      if (newPage != null) {
        changePage(newPage, false);
      }
    }

    if (e.getSource() == cancelButton) {
      cancel();
    }

    if (e.getSource() == finishButton) {
      finish();
    }

    if (e.getSource() == helpButton) {
      final IWizardHelpProvider help = model.getCurrentPage().getHelp();
      if (help != null) {
        help.showHelp(iWizard, model.getCurrentPage());
      }
    }

  }

  /**
   * Shows the wizard with the initial page shown. The initial page is set using
   * {@link IWizardController#getStartPage()}.
   */
  public final void showWizard() {
    if (ran) {
      throw new IllegalStateException("Wizard was already running.");
    }
    ran = true;

    // use the max preferred size of registered pages to
    // calculate wizard size
    mainPanel.setPreferredSize(new Dimension(preferredPageSize.width,
        preferredPageSize.height + header.getPreferredSize().height));
    wizard.pack();

    changePage(controller.getStartPage(), false);

    wizard.setLocationRelativeTo(wizard.getOwner());
    wizard.setVisible(true);
  }

  /**
   * Helper method that checks the return values of
   * {@link IWizardController#canBack(String, IWizard)},
   * {@link IWizardController#canFinish(String, IWizard)},
   * {@link IWizardController#canCancel(String, IWizard)},
   * {@link IWizardPage#canHelp(IWizard)} and
   * {@link IWizardController#canNext(String, IWizard)} and enables/disables the
   * wizard buttons accordingly. (The check is only performed if the supplied
   * page is the current active page)
   * 
   * @param page
   *          the page that invoked a check on its can... methods because of
   *          internal changes
   */
  private void checkButtons(final IWizardPage page) {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {

      @Override
      public void run() {
        if (page == model.getCurrentPage()) {
          forwardButton.setEnabled(controller.canNext(model.getCurrentPageId(),
              iWizard));
          backwardButton.setEnabled(controller.canBack(
              model.getCurrentPageId(), iWizard));
          finishButton.setEnabled(controller.canFinish(
              model.getCurrentPageId(), iWizard));
          helpButton.setEnabled(page.canHelp(iWizard));
          cancelButton.setEnabled(controller.canCancel(
              model.getCurrentPageId(), iWizard));
        }
      }

    });
  }

  /**
   * Helper method that closes and disposes the wizard dialog
   */
  private void close() {
    controller.removeWizardControllerListener(listener);
    model.closePages();
    mainPanel.removeAll();
    wizard.removeAll();
    wizard.setVisible(false);
    wizard.dispose();
  }

  /**
   * Helper method that is invoked when the cancel or close button of the wizard
   * window is clicked. It performs necessary notifications as well as closes
   * the wizard afterwards.
   */
  private void cancel() {
    try {
      if (model.getCurrentPage() != null
          && controller.canCancel(model.getCurrentPageId(), iWizard)) {
        controller.cancel(iWizard, model.getCurrentPage());
        close();
        model.cleanUp();
      }
    } catch (final RuntimeException e) {
      SimSystem.report(e);
    }
  }

  /**
   * Similar to {@link #cancel()} this method is called if the finish button is
   * clicked on the wizard and it performs necessary notifications to listeners
   * as well as closes the wizard dialog afterwards.
   */
  private void finish() {
    try {
      if (model.getCurrentPage() != null
          && controller.canFinish(model.getCurrentPageId(), iWizard)) {
        model.getCurrentPage().hiding(iWizard);
        close();
        controller.finish(iWizard);
      }
    } catch (RuntimeException e) {
      SimSystem.report(e);
    } finally {
      model.cleanUp();
    }
  }

  @Override
  public void windowActivated(final WindowEvent e) {
  }

  @Override
  public void windowClosed(final WindowEvent e) {
  }

  @Override
  public void windowClosing(final WindowEvent e) {
    cancel();
  }

  @Override
  public void windowDeactivated(final WindowEvent e) {
  }

  @Override
  public void windowDeiconified(final WindowEvent e) {
  }

  @Override
  public void windowIconified(final WindowEvent e) {
  }

  @Override
  public void windowOpened(final WindowEvent e) {
  }

}
