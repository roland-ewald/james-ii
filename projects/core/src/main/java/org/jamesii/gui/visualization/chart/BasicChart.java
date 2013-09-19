/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.visualization.chart.axes.LinearAxis;
import org.jamesii.gui.visualization.chart.axes.SteppingAxis;
import org.jamesii.gui.visualization.chart.coordinatesystem.CoordinateSystemXY;
import org.jamesii.gui.visualization.chart.coordinatesystem.ICoordinateSystem;
import org.jamesii.gui.visualization.chart.model.BasicXYChartModel;
import org.jamesii.gui.visualization.chart.model.DefaultFunctionalXYSeries;
import org.jamesii.gui.visualization.chart.model.DefaultXYSeries;
import org.jamesii.gui.visualization.chart.model.IChartModel;
import org.jamesii.gui.visualization.chart.model.IChartModelListener;
import org.jamesii.gui.visualization.chart.model.ISeries;
import org.jamesii.gui.visualization.chart.model.InvalidModelException;
import org.jamesii.gui.visualization.chart.plot.IPlot;
import org.jamesii.gui.visualization.chart.plot.LinePlot;
import org.jamesii.gui.visualization.chart.plot.NullPlot;
import org.jamesii.gui.visualization.chart.plot.TwoSeriesAreaLinePlot;

/**
 * <p>
 * <b><font color="red">NOTE: This class is very likely to change or vanish in
 * future releases so use with care.</font></b>
 * 
 * @author Stefan Rybacki
 */
public class BasicChart extends JComponent implements IChartModelListener {

  private static final int DEFAULT_DELAY = 500;

  private static final int BORDER = 5;

  private static final int TWOBORDERS = 2 * BORDER;

  /**
   * This class is used by the main method available herein to show a frame with
   * a chart.
   * 
   * @author Stefan Rybacki
   * 
   */
  private static final class MainFrame implements Runnable {
    private static final int DEFAULT_HEIGHT = 480;

    private static final int DEFAULT_WIDTH = 640;

    @Override
    public void run() {
      final JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      frame.setLayout(new BorderLayout());
      final BasicXYChartModel model = new BasicXYChartModel();

      /*
       * final AggregatedXYSeries minAggrSeries = new AggregatedXYSeries(
       * "Min Series", new MinAggregator()); final AggregatedXYSeries
       * maxAggrSeries = new AggregatedXYSeries( "Max Series", new
       * MaxAggregator()); final AggregatedXYSeries avgAggrSeries = new
       * AggregatedXYSeries( "Avg Series", new AvgAggregator()); final
       * AggregatedXYSeries medianAggrSeries = new AggregatedXYSeries(
       * "Median Series", new MedianAggregator());
       * 
       * model.addSeries(minAggrSeries); model.addSeries(maxAggrSeries);
       * model.addSeries(avgAggrSeries); model.addSeries(medianAggrSeries);
       */

      for (int i = 0; i < BORDER; i++) {
        final DefaultXYSeries s =
            new DefaultFunctionalXYSeries(String.format("Series %d", i));
        model.addSeries(s);

        /*
         * minAggrSeries.addSeries(s); maxAggrSeries.addSeries(s);
         * avgAggrSeries.addSeries(s); medianAggrSeries.addSeries(s);
         */

        Timer timer = new Timer(100, new ActionListener() {
          private double lastValueX = 0;

          private double lastValueY = 0; // Math.random() * DEFAULT_DELAY -

          // 250

          @Override
          public void actionPerformed(ActionEvent e) {
            model.startUpdating();
            try {
              lastValueX += 0.01;
              lastValueY += Math.random() / 10000 - 0.5 / 10000;
              s.addValuePair(lastValueX, lastValueY);
            } finally {
              model.finishedUpdating();
            }
          }
        });
        timer.setRepeats(true);
        timer.start();

      }

      CoordinateSystemXY system = new CoordinateSystemXY(model);
      // system.setRenderer(new SimpleAxisRenderer());

      BasicChart chart = new BasicChart(system.getModel(), system);
      chart.setShowLegend(false);
      chart.setPlotterForSeries(system.getModel().getSeries(1), new NullPlot());
      system.setAxis(0, new SteppingAxis(new LinearAxis(), 0, 0.1));

      Point2D start = new Point2D.Float(0, 0);
      Point2D end = new Point2D.Float(50, 50);
      float[] dist = { 0.0f, 0.2f, 0.7f, 1.0f };
      Color[] c = { Color.RED, Color.WHITE, Color.BLUE, Color.green };
      LinearGradientPaint p =
          new LinearGradientPaint(start, end, dist, c, CycleMethod.REPEAT);

      chart.setPlotterForSeries(system.getModel().getSeries(2),
          new TwoSeriesAreaLinePlot(system.getModel().getSeries(1), p));
      frame.add(new JScrollPane(chart), BorderLayout.CENTER);

      frame.setVisible(true);
    }
  }

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 3039861291758643382L;

  /**
   * The colors.
   */
  private static final Color[] COLORS = new Color[] { Color.red, Color.blue,
      Color.green, Color.orange.darker(), Color.magenta.darker(),
      Color.red.darker().darker(), Color.blue.darker().darker(),
      Color.green.darker().darker(), Color.yellow.darker().darker(),
      Color.orange.darker().darker(), Color.magenta.darker().darker() };

  /**
   * The Constant standardPlotters used if no custom plotter is registered for a
   * given series.
   */
  private static final IPlot[] STANDARDPLOTTERS = new IPlot[COLORS.length];

  private static final IPlot NULL_PLOT = new NullPlot();
  {
    for (int i = 0; i < COLORS.length; i++) {
      STANDARDPLOTTERS[i] = new LinePlot(COLORS[i % COLORS.length]);
    }
  }

  /**
   * The screen buffer.
   */
  private transient BufferedImage buffer;

  /**
   * The coordinate system to use.
   */
  private final transient ICoordinateSystem coordinateSystem;

  /**
   * The chart model to use.
   */
  private transient IChartModel chartModel;

  /**
   * Holds a set of hidden series
   */
  private final transient Set<ISeries> hidden = new HashSet<ISeries>();

  /**
   * The plotters for each series of the {@link IChartModel}.
   */
  private final Map<ISeries, IPlot> plotters = new HashMap<>();

  /**
   * Flag indicating whether legend should be shown.
   */
  private boolean showLegend = false;

  /**
   * Flag indicating whether data changed since last repaint.
   */
  private boolean dataChanged = true;

  /**
   * Instantiates a new basic chart.
   * 
   * @param model
   *          the chart model to use
   * @param system
   *          the coordinate system to use
   */
  public BasicChart(IChartModel model, ICoordinateSystem system) {
    super();
    setModel(model);
    coordinateSystem = system;
  }

  /**
   * Sets a custom plotter for the specified series.
   * 
   * @param series
   *          the series the plotter is registered for
   * @param plotter
   *          the plotter to register
   * @return the plotter that previously was attached to that series if any
   */
  public IPlot setPlotterForSeries(ISeries series, IPlot plotter) {
    IPlot put = plotters.put(series, plotter);
    dataChanged = true;
    repaint();
    return put;
  }

  /**
   * Removes the plotter for the specified series.
   * 
   * @param series
   *          the series
   */
  public void removePlotterForSeries(ISeries series) {
    plotters.remove(series);
    dataChanged = true;
    repaint();
  }

  /**
   * Removes all registered custom plotters.
   */
  public void removePlottersForAllSeries() {
    plotters.clear();
    dataChanged = true;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (chartModel.repaintLock()) {
      try {
        // check buffer size
        if (buffer == null || buffer.getWidth() != getWidth()
            || buffer.getHeight() != getHeight()) {
          buffer =
              BasicUtilities.createCompatibleImage(getWidth(), getHeight(),
                  Transparency.TRANSLUCENT);
          dataChanged = true;
        }

        if (dataChanged) {
          dataChanged = false;

          Graphics gBuffer = buffer.getGraphics();
          gBuffer.setFont(getFont());

          // clear image to completely transparent
          DataBuffer dataBuffer = buffer.getRaster().getDataBuffer();
          for (int i = 0; i < dataBuffer.getSize(); i++) {
            dataBuffer.setElem(i, 0);
          }

          super.paintComponent(gBuffer);

          // calculate legend width and line count needed to draw the
          // legend
          List<Integer> legendLines = new ArrayList<>();
          int width = 0;
          int maxWidth = 0;
          if (showLegend) {
            int namesInLine = 0;
            for (int i = 0; i < chartModel.getSeriesCount(); i++) {
              maxWidth = Math.max(maxWidth, width);
              String name = chartModel.getSeriesName(chartModel.getSeries(i));
              width +=
                  20 + SwingUtilities.computeStringWidth(
                      gBuffer.getFontMetrics(), name);

              namesInLine++;
              if (width > getWidth() - 20 && namesInLine > 1) {
                i--;
                width -=
                    20 + SwingUtilities.computeStringWidth(
                        gBuffer.getFontMetrics(), name);
                legendLines.add(width);
                width = 0;
                namesInLine = 0;
              }
            }
            legendLines.add(width);
            maxWidth = Math.max(maxWidth, width);
          }

          coordinateSystem.drawCoordinateSystem((Graphics2D) gBuffer, BORDER,
              BORDER, getWidth() - TWOBORDERS, getHeight() - TWOBORDERS
                  - legendLines.size() * gBuffer.getFontMetrics().getHeight()
                  - BORDER);

          for (int i = 0; i < chartModel.getSeriesCount(); i++) {
            ISeries s = chartModel.getSeries(i);
            IPlot plot = hidden.contains(s) ? NULL_PLOT : getPlotForSeries(s);
            Point p =
                coordinateSystem.getPlotOrigin((Graphics2D) gBuffer, BORDER,
                    BORDER, getWidth() - TWOBORDERS, getHeight() - TWOBORDERS
                        - legendLines.size()
                        * gBuffer.getFontMetrics().getHeight() - BORDER);
            Dimension d =
                coordinateSystem.getPlotDimension((Graphics2D) gBuffer, BORDER,
                    BORDER, getWidth() - TWOBORDERS, getHeight() - TWOBORDERS
                        - legendLines.size()
                        * gBuffer.getFontMetrics().getHeight() - BORDER);
            plot.drawPlot(this, chartModel.getSeries(i), (Graphics2D) gBuffer,
                (int) p.getX(), (int) p.getY(), d.width, d.height);

          }

          if (showLegend) {
            // draw legend TODO sr137: make this more abstract aka
            // IAdditional
            width = legendLines.get(0);
            int x = (getWidth() - width) / 2;
            int y =
                getHeight() - 7 - (legendLines.size() - 1)
                    * gBuffer.getFontMetrics().getHeight();

            gBuffer.setColor(Color.white);
            gBuffer.fillRect((getWidth() - maxWidth) / 2 - BORDER, y
                - gBuffer.getFontMetrics().getHeight(), maxWidth + TWOBORDERS,
                gBuffer.getFontMetrics().getHeight() * legendLines.size()
                    + BORDER);
            gBuffer.setColor(Color.darkGray);
            gBuffer.drawRect((getWidth() - maxWidth) / 2 - BORDER, y
                - gBuffer.getFontMetrics().getHeight(), maxWidth + TWOBORDERS,
                gBuffer.getFontMetrics().getHeight() * legendLines.size()
                    + BORDER);

            for (int i = 0; i < chartModel.getSeriesCount(); i++) {
              IPlot plot = getPlotForSeries(chartModel.getSeries(i));
              String name = chartModel.getSeriesName(chartModel.getSeries(i));
              plot.drawPlotInLegend((Graphics2D) gBuffer, x, y
                  - gBuffer.getFontMetrics().getAscent(), TWOBORDERS, gBuffer
                  .getFontMetrics().getAscent());

              gBuffer.setColor(Color.darkGray);
              gBuffer.drawString(name, x + 12, y);

              x +=
                  20 + SwingUtilities.computeStringWidth(
                      gBuffer.getFontMetrics(), name);
              if (x >= width && legendLines.size() > 1) {
                legendLines.remove(0);
                width = legendLines.get(0);
                x = (getWidth() - width) / 2;
                y += gBuffer.getFontMetrics().getHeight();
              }
            }
          }
        }
        g.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
      } finally {
        chartModel.repaintUnlock();
      }
    } else {
      // draw buffer and schedule another repaint
      g.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
      BasicUtilities.repaintOnEDT(this, 1000);
    }
  }

  /**
   * Helper method that gets the plotter for the specified series.
   * 
   * @param s
   *          the series the plotter is requested for
   * @return the plotter for series
   */
  private IPlot getPlotForSeries(ISeries s) {
    IPlot plot = plotters.get(s);
    if (plot == null) {
      int i = 0;
      for (i = 0; i < getModel().getSeriesCount(); i++) {
        if (getModel().getSeries(i) == s) {
          break;
        }
      }
      plot = STANDARDPLOTTERS[i % COLORS.length];
      plotters.put(s, plot);
    }
    return plot;
  }

  /**
   * Gets the default color for series. This would be the color that is assigned
   * from the chart to the series. This can be completely different from the
   * actual color used by a assigned plot, e.g. if
   * {@link #setPlotterForSeries(ISeries, IPlot)} was used.
   * 
   * 
   * @param s
   *          the series
   * @return the default color for given series
   */
  public Color getDefaultColorForSeries(ISeries s) {
    int i = 0;
    for (i = 0; i < getModel().getSeriesCount(); i++) {
      if (getModel().getSeries(i) == s) {
        break;
      }
    }
    return COLORS[i % COLORS.length];
  }

  @Override
  public Dimension getMinimumSize() {
    return coordinateSystem.getMinimumSize((Graphics2D) getGraphics());
  }

  @Override
  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  /**
   * Gets the currently used chart model.
   * 
   * @return the model currently used
   */
  public IChartModel getModel() {
    return chartModel;
  }

  /**
   * Gets the coordinate system currently in use.
   * 
   * @return the coordinate system currently used
   */
  public ICoordinateSystem getCoordinateSystem() {
    return coordinateSystem;
  }

  /**
   * Sets the chart model to use.
   * 
   * @param model
   *          the new chart model
   */
  public final void setModel(IChartModel model) {
    if (model == null) {
      throw new InvalidModelException("model can't be null");
    }

    dataChanged = true;
    IChartModel old = chartModel;
    if (chartModel != null) {
      chartModel.removeListener(this);
    }
    this.chartModel = model;
    model.addListener(this);
    if (coordinateSystem != null) {
      coordinateSystem.setModel(model);
    }
    firePropertyChange("model", old, model);
    repaint();
  }

  @Override
  public synchronized void dataChanged() {
    dataChanged = true;
    BasicUtilities.repaintOnEDT(this, DEFAULT_DELAY);
  }

  @Override
  public synchronized void groupChanged(int seriesIndex, int dimension) {
    dataChanged = true;
    BasicUtilities.repaintOnEDT(this, DEFAULT_DELAY);
  }

  @Override
  public synchronized void seriesAdded(ISeries series) {
    dataChanged = true;
    BasicUtilities.repaintOnEDT(this, DEFAULT_DELAY);
  }

  @Override
  public synchronized void seriesRemoved(ISeries series) {
    dataChanged = true;
    hidden.remove(series);
    plotters.remove(series);
    BasicUtilities.repaintOnEDT(this, DEFAULT_DELAY);
  }

  @Override
  public synchronized void valueAdded(ISeries series, long valueIndex) {
    dataChanged = true;
    BasicUtilities.repaintOnEDT(this, DEFAULT_DELAY);
  }

  @Override
  public synchronized void valueRemoved(ISeries series, long valueIndex) {
    dataChanged = true;
    BasicUtilities.repaintOnEDT(this, DEFAULT_DELAY);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    BasicUtilities.invokeLaterOnEDT(new MainFrame());
  }

  /**
   * Gets the plotter for the specified series.
   * 
   * @param series
   *          the series the plotter is requested for
   * @return the plotter for series
   */
  public IPlot getPlotterForSeries(ISeries series) {
    return getPlotForSeries(series);
  }

  /**
   * @param show
   *          if true legend will be shown
   */
  public void setShowLegend(boolean show) {
    this.showLegend = show;
    dataChanged = true;
    repaint();
  }

  /**
   * @param series
   * @param b
   */
  public void setSeriesHidden(ISeries series, boolean b) {
    if (b) {
      hidden.add(series);
    } else {
      hidden.remove(series);
    }
    dataChanged = true;
    repaint();
  }
}
