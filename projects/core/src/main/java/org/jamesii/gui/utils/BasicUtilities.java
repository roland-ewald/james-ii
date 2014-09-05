/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeModel;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.NoFactoryFoundException;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.AbstractFactoryParameterDialogFactory;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogParameter;

/**
 * Just a container for all (static) utility function regarding the GUI.
 * 
 * @author Roland Ewald
 * @author Stefan Rybacki
 */

public final class BasicUtilities {

  /**
   * The a list of repaints that are still in progress with an optional delay to
   * avoid multiple repaint calls for the same component
   */
  private static final Set<Component> repaints = Collections
      .synchronizedSet(new HashSet<Component>());

  public static final Dimension RESOLUTION_VGA = new Dimension(640, 480);

  /**
   * Hidden constructor.
   */
  private BasicUtilities() {
  }

  /**
   * Center a given window on the screen.
   * 
   * @param window
   *          JFrame
   */
  @Deprecated
  public static void centerOnScreen(Window window) {

    int locationX =
        (int) Math.round((Toolkit.getDefaultToolkit().getScreenSize()
            .getWidth() - window.getWidth()) / 2);

    int locationY =
        (int) Math.round((Toolkit.getDefaultToolkit().getScreenSize()
            .getHeight() - window.getHeight()) / 2);

    window.setLocation(locationX, locationY);
  }

  /**
   * Displays a {@link URI} using {@link URLDecoder} with encoding from
   * {@link SimSystem#getEncoding()}. Useful for displaying save/load location
   * in the status bar.
   * 
   * @param uri
   *          the URI to be decoded
   * @return decoded String
   */
  public static String displayURI(URI uri) {
    String decodedURL = "";
    try {
      decodedURL = URLDecoder.decode(uri.toString(), SimSystem.getEncoding());
    } catch (UnsupportedEncodingException e) {
      SimSystem.report(e);
    }
    return decodedURL;
  }

  /**
   * Retrieves a map Factory => FactoryParameterDialog for GUI-Tasks that may be
   * accomplished by various factories. This utility method is aimed at
   * automated dialog look-up and creation. This allows to easily add dialogs
   * for input/output parameters of newly created model reader/writers,
   * experiment reader/writers, etc.
   * 
   * @param factoryClass
   *          the super class of the factories
   * @param abstractFactoryParameter
   *          the abstract factory parameter
   * @param parentWindow
   *          the component that owns the dialogs
   * @param <F>
   *          type of base factory
   * @return a map Factory => FactoryParameterDialog for GUI-Tasks that may be
   *         accomplished by various factories
   */
  // Cast is ensured by semantics of
  // getAbstractFactoryForBaseFactory()
  @SuppressWarnings("unchecked")
  public static <F extends Factory<?>> Map<F, IFactoryParameterDialog<?>> getGUIFactories(
      Class<F> factoryClass, ParameterBlock abstractFactoryParameter,
      Component parentWindow) {

    List<F> factories;

    try {
      if (abstractFactoryParameter != null) {
        factories =
            SimSystem.getRegistry().getFactoryList(
                (Class<? extends AbstractFactory<F>>) SimSystem.getRegistry()
                    .getAbstractFactoryForBaseFactory(factoryClass),
                abstractFactoryParameter);
      } else {
        factories = SimSystem.getRegistry().getFactories(factoryClass);
      }
    } catch (RuntimeException rte) {
      SimSystem
          .report(
              Level.SEVERE,
              "Error while looking for suitable factories. There are no factories for type ' "
                  + factoryClass.getName() + "'.");
      factories = null;
    }

    if (factories == null) {
      return new HashMap<>();
    }

    Map<F, IFactoryParameterDialog<?>> result = new HashMap<>();

    // Filter those factories that do not have a factory parameter
    // dialog that
    // supports them
    for (F factory : factories) {
      try {
        IFactoryParameterDialog<?> dialog = getDialog(factoryClass, factory);
        result.put(factory, dialog);
      } catch (NoFactoryFoundException ex) {
        SimSystem.report(ex);
        // Don't do anything if no GUI dialog was found
      }
    }

    return result;
  }

  /**
   * Get dialog for given parameters.
   * 
   * @param factoryClass
   *          the factory class
   * @param concreteFactory
   *          the concrete factory
   * @param <F>
   *          type of the base factory
   * @param <CF>
   *          type of the actual factory
   * 
   *          TODO: Generalise for lists of concrete factories
   * @return dialog if available, null otherwise
   */
  private static <F extends Factory<?>, CF extends F> IFactoryParameterDialog<?> getDialog(
      Class<F> factoryClass, CF concreteFactory) {
    FactoryParameterDialogFactory<F, CF, ? extends AbstractFactory<F>> fpdFactory =
        BasicUtilities.getDialogFactory(factoryClass, concreteFactory);
    if (fpdFactory == null) {
      return null;
    }
    ArrayList<F> facList = new ArrayList<>();
    facList.add(concreteFactory);

    return fpdFactory.create(FactoryParameterDialogParameter.getParameterBlock(
        factoryClass, facList), SimSystem.getRegistry().createContext());
  }

  /**
   * Gets the dialog factory.
   * 
   * @param factoryClass
   *          the factory class
   * @param concreteFactory
   *          the concrete factory
   * @param <AF>
   *          type of the abstract factory
   * @param <F>
   *          type of the base factory
   * @param <CF>
   *          type of the actual factory
   * @return the dialog factory
   */
  @SuppressWarnings("unchecked")
  private static <F extends Factory<?>, CF extends F, AF extends AbstractFactory<F>> FactoryParameterDialogFactory<F, CF, AF> getDialogFactory(
      Class<F> factoryClass, CF concreteFactory) {
    // This cast is ensured by the semantics of the factory
    // (FactoryClassCriteria in AbstractFactParamDialogFactory)
    List<Factory> concrfFClasses = new ArrayList<>();
    concrfFClasses.add(concreteFactory);
    return (FactoryParameterDialogFactory<F, CF, AF>) SimSystem.getRegistry()
        .getFactory(
            AbstractFactoryParameterDialogFactory.class,
            (new ParameterBlock()).addSubBl(
                AbstractFactoryParameterDialogFactory.ABSTRACT_FACTORY_CLASS,
                SimSystem.getRegistry().getAbstractFactoryForBaseFactory(
                    factoryClass)).addSubBl(
                AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES,
                concrfFClasses));
  }

  /**
   * Loads an image icon for a given class, from a given source path.
   * 
   * @param resourceClass
   *          the class that uses the resource
   * @param source
   *          the source path
   * @return the image icon, otherwise an empty image icon
   */
  @Deprecated
  // Use new resource system from org.jamesii.gui.application.resources
  // instead
  public static ImageIcon loadIcon(Class<?> resourceClass, String source) {
    InputStream iconSource = null;
    try {
      iconSource = resourceClass.getResourceAsStream(source);
      ImageIcon icon = null;

      byte[] iconData = new byte[iconSource.available()];

      icon = new ImageIcon(iconData);

      return icon;
    } catch (Exception e) {
      SimSystem.report(e);
      return new ImageIcon();
    } finally {
      close(iconSource);
    }
  }

  /**
   * Prints a question message.
   * 
   * @param parent
   *          parent component for the dialog
   * @param title
   *          title of the dialog
   * @param message
   *          question to be displayed
   * @return decision, as defined in {@link JOptionPane}
   */
  public static int printQuestion(Component parent, String title, Object message) {
    return JOptionPane.showConfirmDialog(parent, message, title,
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
  }

  /**
   * Redraws a single component.
   * 
   * @param component
   *          component to be drawn new
   */
  public static void redrawComp(Component component) {
    SwingUtilities.updateComponentTreeUI(component);
    component.invalidate();
    component.validate();
    component.repaint();
  }

  /**
   * Code Snippet from "Java ist auch eine Insel" to update all windows for the
   * UI. (@see http://www.galileocomputing.de/openbook/
   * javainsel6/javainsel_14_021.htm)
   */
  public static void updateUI() {
    try {
      invokeAndWaitOnEDT(new Runnable() {
        @Override
        public void run() {
          for (Frame f : Frame.getFrames()) {
            redrawComp(f);
            for (Window w : f.getOwnedWindows()) {
              redrawComp(w);
            }
          }
        }
      });
    } catch (Exception ex) {
      SimSystem.report(ex);
    }
  }

  /**
   * Runs the given runnable object on the EDT and waits for its completion. In
   * contrast to {@link SwingUtilities#invokeAndWait(Runnable)} this method
   * checks whether it was called from EDT and avoids invokeAndWait if possible.
   * 
   * @param r
   *          the runnable object
   * @author Stefan Rybacki
   * @throws InvocationTargetException
   * @throws InterruptedException
   */
  public static void invokeAndWaitOnEDT(Runnable r)
      throws InterruptedException, InvocationTargetException {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeAndWait(r);
    } else {
      r.run();
    }
  }

  /**
   * Runs the given runnable object on the EDT without waiting for its
   * completion. In contrast to {@link SwingUtilities#invokeLater(Runnable)}
   * this method check whether it was called from EDT and avoids invokeLater if
   * possible.
   * 
   * @param r
   *          the runnable object
   * @author Stefan Rybacki
   */
  public static void invokeLaterOnEDT(Runnable r) {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(r);
    } else {
      r.run();
    }
  }

  /**
   * Invokes {@link Component#repaint()} from within the EDT.
   * 
   * @param c
   *          the component that should be repainted
   */
  public static void repaintOnEDT(final Component c) {
    repaintOnEDT(c, 50);
  }

  /**
   * Invokes {@link Component#repaint()} from within the EDT.
   * 
   * @param c
   *          the component that should be repainted
   * @param delay
   *          the maximum time before this component is repainted
   */
  public static void repaintOnEDT(final Component c, final long delay) {
    // add a little repaint call merging
    if (repaints.contains(c)) {
      return;
    }

    repaints.add(c);

    try {
      invokeLaterOnEDT(new Runnable() {

        @Override
        public void run() {
          try {
            Thread.sleep(delay);
          } catch (InterruptedException e) {
          }
          c.repaint();
          repaints.remove(c);
        }

      });
    } catch (Exception e) {
    }
  }

  /**
   * Invokes {@link Component#invalidate()} followed by a
   * {@link Component#repaint()} from within the EDT.
   * 
   * @param c
   *          the component that should be invalidated
   * @param delay
   *          the maximum time before this component is repainted
   */
  public static void revalidateOnEDT(final Component c, final long delay) {
    if (repaints.contains(c)) {
      return;
    }
    try {
      invokeLaterOnEDT(new Runnable() {

        @Override
        public void run() {
          try {
            Thread.sleep(delay);
          } catch (InterruptedException e) {
          }
          c.invalidate();
          c.validate();
          c.repaint();
          repaints.remove(c);
        }

      });
    } catch (Exception e) {
    }
  }

  /**
   * Invokes {@link Component#invalidate()} followed by a
   * {@link Component#repaint()} from within the EDT.
   * 
   * @param c
   *          the component that should be invalidated
   */
  public static void revalidateOnEDT(final Component c) {
    revalidateOnEDT(c, 50);
  }

  /**
   * Creates the compatible image.
   * 
   * @param width
   *          the width
   * @param height
   *          the height
   * @return the buffered image
   * @see java.awt.GraphicsConfiguration
   */
  public static BufferedImage createCompatibleImage(int width, int height) {
    return GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice().getDefaultConfiguration()
        .createCompatibleImage(width, height);
  }

  /**
   * Creates the compatible image.
   * 
   * @param width
   *          the width
   * @param height
   *          the height
   * @param transparency
   *          the transparency
   * @return the buffered image
   * @see java.awt.GraphicsConfiguration#createCompatibleImage(int, int, int)
   */
  public static BufferedImage createCompatibleImage(int width, int height,
      int transparency) {
    return GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice().getDefaultConfiguration()
        .createCompatibleImage(width, height, transparency);
  }

  /**
   * Make factory class name readable.
   * 
   * @param fName
   *          the factory name
   * @return the readable version of the factory name
   */
  public static String makeFactoryClassNameReadable(String fName) {
    if (fName == null) {
      return null;
    }

    String result = fName.replaceAll("Factory", "");
    result = result.replaceAll("_", " ");

    // now only use the simple class name version in case its fully
    // qualified
    int a = result.lastIndexOf('.');
    if (a >= 0) {
      result = result.substring(a + 1);
    }

    return makeCamelCaseReadable(result);
  }

  /**
   * Make factory class name readable.
   * 
   * @param f
   *          the factory
   * @return the readable version of the factory name
   */
  public static String makeFactoryClassNameReadable(Factory f) {
    return makeFactoryClassNameReadable(f.getClass().getName());
  }

  /**
   * Make camel case readable.
   * 
   * @param camelCase
   *          the camel case string
   * @return the readable version of the camel case string
   */
  public static String makeCamelCaseReadable(String camelCase) {
    if (camelCase == null) {
      return null;
    }
    // acronyms
    String result =
        camelCase.replaceAll("([A-Z0-9]+)([A-Z0-9]|\\z|\\s)", "$1 $2");
    // camelcase splitting
    result = result.replaceAll("([a-z])([A-Z])", "$1 $2");

    return result.trim();
  }

  /**
   * Helper method that converts an {@link Icon} to an {@link Image}
   * 
   * @param icon
   *          the icon to convert
   * @return the converted icon as image
   */
  public static Image iconToImage(Icon icon) {
    if (icon instanceof ImageIcon) {
      return ((ImageIcon) icon).getImage();
    }
    int w = icon.getIconWidth();
    int h = icon.getIconHeight();
    BufferedImage image =
        BasicUtilities.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
    Graphics2D g = image.createGraphics();
    icon.paintIcon(new CellRendererPane(), g, 0, 0);
    g.dispose();
    return image;
  }

  /**
   * Prints a tree structure to a writer output. This can for instance be used
   * to print a tree to the console.
   * 
   * @param tree
   *          the tree structure represented by a {@link TreeModel}
   * @param output
   *          the output to write to e.g., new OutputStreamWriter( System.out)
   * @param formatter
   *          a custom formatter used instead of {@link #toString()} if
   *          specified (can be null)
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void printTree(TreeModel tree, Writer output,
      IFormatter<?> formatter) throws IOException {
    printTreeHelper(tree, tree.getRoot(), output, "", true, formatter);
  }

  /**
   * Print tree helper method.
   * 
   * @param tree
   *          the tree to print
   * @param node
   *          the current node
   * @param output
   *          the output to write to
   * @param indent
   *          the indent to use
   * @param last
   *          if true the given node is the last node of the current sub tree
   * @param formatter
   *          the formatter to be used for custom object output (is used instead
   *          of {@link #toString()}
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("unchecked")
  private static void printTreeHelper(TreeModel tree, Object node,
      Writer output, String indent, boolean last, IFormatter formatter)
      throws IOException {
    output.write(indent + (last ? "└──" : "├──")
        + (formatter != null ? formatter.format(node) : node) + "\n");

    for (int i = 0; i < tree.getChildCount(node); i++) {
      Object child = tree.getChild(node, i);
      boolean l = !(i < tree.getChildCount(node) - 1);
      printTreeHelper(tree, child, output, indent + (!last ? "│   " : "    "),
          l, formatter);
    }

    output.flush();
  }

  /**
   * Helper method that checks the java version.
   * 
   * @param requiredVersion
   *          specifies the required version e.g. "1.6"
   * 
   * @return true, if successful
   */
  public static boolean checkJavaVersion(final String requiredVersion) {
    // check java version first
    String usedVersion = System.getProperty("java.specification.version");
    boolean sufficient = false;
    try {
      sufficient =
          !(usedVersion == null || Double.valueOf(usedVersion).doubleValue() < Double
              .valueOf(requiredVersion).doubleValue());
    } catch (Exception e) {
    }
    return sufficient;
  }

  /**
   * Helper method that updates the ui of all components that are currently on
   * the screen. That includes all windows and frames and all subcomponents,
   * e.g. after a L&F change.
   */
  public static void updateTreeUI() {
    try {
      for (Frame f : Frame.getFrames()) {
        SwingUtilities.updateComponentTreeUI(f);
      }
      for (Window w : Window.getWindows()) {
        SwingUtilities.updateComponentTreeUI(w);
      }
    } catch (Exception e) {
    }
  }

  /**
   * Helper method that should be used in a finally block when closing
   * {@link Closeable} objects such as {@link InputStream},
   * {@link java.io.OutputStream} and so on. It checks for <code>null</code> and
   * catches execeptions that might be thrown when closing silently.
   * 
   * @param closeable
   *          the object to close
   */
  public static void close(Closeable closeable) {
    if (closeable == null) {
      return;
    }

    try {
      closeable.close();
    } catch (IOException e) {
      SimSystem.report(e);
    }
  }

  /**
   * Simple helper to make a 1:1 copy of an array.
   * 
   * @param src
   *          the array to be copied
   * @return an copy of the array
   */
  public static <T> T[] copyArray(T[] src) {
    return Arrays.copyOf(src, src.length);
  }

  public static void close(Statement closeable) {
    if (closeable == null) {
      return;
    }

    try {
      closeable.close();
    } catch (SQLException e) {
      SimSystem.report(e);
    }
  }

  public static void close(ResultSet closeable) {
    if (closeable == null) {
      return;
    }

    try {
      closeable.close();
    } catch (SQLException e) {
      SimSystem.report(e);
    }
  }

}
