/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plotter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.observe.INotifyingObserver;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.listener.IObserverListener;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.visualization.chart.BasicChart;
import org.jamesii.gui.visualization.chart.coordinatesystem.CoordinateSystemXY;
import org.jamesii.gui.visualization.chart.model.BasicXYChartModel;
import org.jamesii.gui.visualization.chart.model.DefaultFunctionalXYSeries;
import org.jamesii.gui.visualization.chart.model.DefaultXYSeries;
import org.jamesii.gui.visualization.chart.model.ISeries;
import org.jamesii.gui.visualization.chart.plot.IColoredPlot;
import org.jamesii.gui.visualization.chart.plot.IPlot;

// TODO sr137: maybe provide data export extension point in plotter
// gui to export collected data to
// be usable in other statistical applications
/**
 * Incorporated observer plotter that uses the new chart supporting the
 * {@link IIncrementalPlotableObserver} interface as data source for the chart.
 * This is achieved by wrapping the data of {@link IIncrementalPlotableObserver}
 * in an {@link org.jamesii.gui.visualization.chart.model.IChartModel}. Only the
 * current data is collected from the interface and stored in the chart model
 * for later use. This avoids the copy process of the entire data every time
 * {@link #updateOccurred(INotifyingObserver)} is called and gives new
 * opportunities to support data windows directly in the plotter rather than in
 * the data provider.
 * 
 * @author Stefan Rybacki
 */
class ObserverPlotterIncremental extends AbstractWindow implements
    IObserverListener {

  /**
   * The Constant MAX_VALUES used in combination with {@link #slidingWindow}
   */
  private static final int MAX_VALUES = 500;

  /**
   * The chart model that is used.
   */
  private final BasicXYChartModel model;

  /**
   * The chart.
   */
  private BasicChart chart;

  /**
   * maps series' to variable names
   */
  private final Map<String, DefaultXYSeries> serieses = new HashMap<>();

  /**
   * The currently visualized observer (only one is allowed).
   */
  private INotifyingObserver visualizedObserver;

  /**
   * The simulation runners counter.
   */
  private AtomicInteger runCounter = new AtomicInteger(0);

  // FIXME sr137: when reusing this plotter in a parallel executed experiment
  // the runCounter value for each init has to be mapped to srti and observer
  // because they might come and update simultaneously

  /**
   * A flag indicating whether the plot should hold only a specified amount of
   * data
   */
  private boolean slidingWindow = false;

  /** The default plot type. */
  private IColoredPlot type;

  /**
   * Instantiates a new observer plotter.
   * 
   * @param type
   */
  public ObserverPlotterIncremental(IColoredPlot type) {
    super("Line Chart Output", IconManager.getIcon(IconIdentifier.INFO_SMALL),
        Contribution.EDITOR);
    model = new BasicXYChartModel();
    this.type = type;
  }

  @Override
  public void init(ComputationTaskRuntimeInformation srti) {
    visualizedObserver = null;
    // clear mapping between existing series' and names
    serieses.clear();
    runCounter.incrementAndGet();
  }

  @Override
  public void updateOccurred(final INotifyingObserver observer) {
    if (visualizedObserver == null) {
      visualizedObserver = observer;
    }

    if (visualizedObserver != observer) { // NOSONAR: want identity equality
      SimSystem.report(Level.WARNING,
          "Visual Data Plotter: Can only handle one observer!");
      return;
    }

    if (!(observer instanceof IIncrementalPlotableObserver)) {
      return;
    }

    final IIncrementalPlotableObserver ob =
        (IIncrementalPlotableObserver) observer;

    checkVariableNames(ob);

    try {
      model.startUpdating();
      for (String name : serieses.keySet()) {
        DefaultXYSeries s = serieses.get(name);

        Pair<? extends Number, ? extends Number> data = ob.getCurrentData(name);

        Pair<? extends Number, ? extends Number> min = ob.getMin();
        Pair<? extends Number, ? extends Number> max = ob.getMax();

        if (min != null) {
          s.setMin(0, min.getFirstValue());
          s.setMin(1, min.getSecondValue());
        }

        if (max != null) {
          s.setMax(0, max.getFirstValue());
          s.setMax(1, max.getSecondValue());
        }

        if (data != null) {
          s.addValuePair(data.getFirstValue(), data.getSecondValue());
        }

        // now check for sliding window flag
        if (slidingWindow) {
          // cut old values
          s.cutToValueCount(MAX_VALUES);
        }
      }
    } catch (Exception e) {
      SimSystem.report(e);
    } finally {
      model.finishedUpdating();
    }

  }

  /**
   * Check variable names.
   * 
   * @param ob
   *          the ob
   */
  private void checkVariableNames(
      IIncrementalPlotableObserver<? extends IObservable> ob) {
    for (String name : ob.getVariableNames()) {
      if (!serieses.containsKey(name)) {
        DefaultXYSeries s = null;
        if (ob.isMonotonIncreasingX(name)) {
          s =
              new DefaultFunctionalXYSeries(name
                  + (runCounter.get() >= 1 ? " (run: " + runCounter.get() + ")"
                      : ""));

        } else {
          s =
              new DefaultXYSeries(name
                  + (runCounter.get() >= 1 ? " (run: " + runCounter.get() + ")"
                      : ""));
        }
        serieses.put(name, s);
        model.addSeries(s);
      }

      // also set plotter for series if any
      ISeries s = serieses.get(name);
      IPlot p = ob.getPlotFor(name, chart.getDefaultColorForSeries(s));
      if (p != null) {
        chart.setPlotterForSeries(s, p);
      } else {
        // FIXME sr137: use plot stored in type here (must be cloned and colore
        // set though)
      }
    }
  }

  @Override
  protected JComponent createContent() {
    JPanel panel = new JPanel(new BorderLayout());

    // final DefaultListSelectionModel selectionModel =
    // new DefaultListSelectionModel();
    // selectionModel
    // .setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    // selectionModel.addSelectionInterval(0, model.getSeriesCount());
    //
    // final CheckBoxGroup<ISeries> legend =
    // new CheckBoxGroup<ISeries>(new PlotterLegendGroupModel(model),
    // selectionModel) {
    // private static final long serialVersionUID = 1L;
    //
    // @Override
    // public void intervalAdded(ListDataEvent e) {
    // super.intervalAdded(e);
    // selectionModel.addSelectionInterval(e.getIndex0(), e.getIndex1());
    // }
    // };
    //
    // legend.setBorder(new EmptyBorder(5, 5, 5, 5));
    chart = new BasicChart(model, new CoordinateSystemXY(model));
    chart.setShowLegend(true);
    //
    // JScrollPane sLegend = new JScrollPane(legend);
    // sLegend.setBorder(new EmptyBorder(0, 0, 0, 0));
    //
    panel.add(chart, BorderLayout.CENTER);
    // panel.add(sLegend, BorderLayout.LINE_END);
    //
    // selectionModel.addListSelectionListener(new ListSelectionListener() {
    //
    // @Override
    // public void valueChanged(ListSelectionEvent e) {
    // for (int i = 0; i < legend.getModel().getSize(); i++) {
    // chart.setSeriesHidden(model.getSeries(i),
    // !selectionModel.isSelectedIndex(i));
    // }
    // }
    // });

    return panel;
  }

  @Override
  public void windowClosed() {
    if (visualizedObserver != null) {
      visualizedObserver.removeListener(this);
    }
  }

  @Override
  protected IAction[] generateActions() {
    return new IAction[0];
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(640, 480);
  }

}
