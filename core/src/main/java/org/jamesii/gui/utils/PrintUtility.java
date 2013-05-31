package org.jamesii.gui.utils;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.RepaintManager;

/**
 * Utility class for printing a Component with any content
 * 
 * ORIGIN: 7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/ May be freely
 * used or adapted.
 * 
 * - Fully commented - Changed Permissions - Added multi - page printing
 * 
 * @author Roland Ewald
 */
@Deprecated
public class PrintUtility implements Printable {

  // ORIGINAL COMMENT BY MARTY HALL
  /**
   * The speed and quality of printing suffers dramatically if any of the
   * containers have double buffering turned on. So this turns if off globally.
   * 
   * @param c
   *          the c
   */
  private static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
    return;
  }

  /**
   * Re-enables double buffering globally.
   * 
   * @param c
   *          the c
   */
  private static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
    return;
  }

  /**
   * This function has to be called to print a component.
   * 
   * @param c
   *          the c
   * 
   * @throws PrinterException
   *           the printer exception
   */
  public static void printComponent(Component c) throws PrinterException {

    new PrintUtility(c).print();

    return;
  }

  // The component which is printed
  /** The component to be printed. */
  private Component componentToBePrinted;

  /**
   * The constructor.
   * 
   * @param component
   *          the component
   */
  public PrintUtility(Component component) {
    componentToBePrinted = component;
    return;
  }

  /**
   * Function that activates the printer.
   * 
   * @throws PrinterException
   *           the printer exception
   */
  public void print() throws PrinterException {

    PrinterJob printJob = PrinterJob.getPrinterJob();

    printJob.setPrintable(this);

    if (printJob.printDialog()) {
      printJob.print(); // print if user said 'ok'
    }

    return;
  }

  /**
   * Core Function. Is called for every page the printer prints, and has to
   * print into the Graphics context it gets as a parameter
   * 
   * @param g
   *          the graphics context of the printer
   * @param pageFormat
   *          the Format the printer will print
   * @param pageIndex
   *          int number of the page to be printed
   * 
   * @return int status - either NO_SUCH_PAGE or PAGE_EXISTS
   */
  @Override
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {

    if (!(g instanceof Graphics2D)) {
      throw new IllegalArgumentException(
          "Provided Graphics is not of type Graphics2D");
    }
    Graphics2D g2d = (Graphics2D) g;

    // Calculating the actual height
    double height = pageFormat.getImageableHeight();
    double maxHeight = height * pageIndex;

    // If this component isn't long enough to also fill THIS Page
    if (componentToBePrinted.getHeight() < maxHeight) {
      return (NO_SUCH_PAGE); // end printing
    }

    // Else translate the context insofar that it fits to the component
    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY()
        - maxHeight);

    disableDoubleBuffering(componentToBePrinted);
    componentToBePrinted.paint(g2d);
    enableDoubleBuffering(componentToBePrinted);

    return (PAGE_EXISTS);

  }

}
// EOF
