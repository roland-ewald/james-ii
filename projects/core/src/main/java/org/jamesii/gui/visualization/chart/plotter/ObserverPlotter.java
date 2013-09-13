/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.plotter;

import java.awt.BorderLayout;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JComponent;
import javax.swing.JPanel;

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
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.visualization.chart.BasicChart;
import org.jamesii.gui.visualization.chart.coordinatesystem.CoordinateSystemXY;
import org.jamesii.gui.visualization.chart.model.BasicXYChartModel;
import org.jamesii.gui.visualization.chart.model.DefaultXYSeries;

/**
 * Incorporated observer plotter that uses the new chart component but still
 * supporting the old {@link IPlotableObserver} interface as data source for the
 * chart. This is achieved by wrapping the data of {@link IPlotableObserver} in
 * an {@link org.jamesii.gui.visualization.chart.model.IChartModel}.
 * Unfortunately it is that the {@link IPlotableObserver} interface always
 * provides the entire data so the data in the chart model must be updated
 * completely as it is not clear which data actually changed or was added or
 * removed. This might lead into a slow diagram.
 * 
 * @author Stefan Rybacki
 */
@Deprecated
class ObserverPlotter extends AbstractWindow implements IObserverListener {

  /**
   * The updating lock.
   */
  private final Lock updatingLock = new ReentrantLock();

  /**
   * The chart model that is used to wrap the {@link IPlotableObserver} data.
   */
  private BasicXYChartModel model;

  /**
   * The chart.
   */
  private BasicChart chart;

  /**
   * Instantiates a new observer plotter.
   */
  public ObserverPlotter() {
    super("Line Chart Output", IconManager.getIcon(IconIdentifier.INFO_SMALL),
        Contribution.EDITOR);
    model = new BasicXYChartModel();
  }

  @Override
  public void init(ComputationTaskRuntimeInformation srti) {
  }

  @Override
  public void updateOccurred(
      final INotifyingObserver<? extends IObservable> observer) {

    if (!(observer instanceof IPlotableObserver)) {
      return;
    }

    if (updatingLock.tryLock()) {
      try {
        BasicUtilities.invokeAndWaitOnEDT(new Runnable() {

          @Override
          public void run() {
            // only update if not currently updating
            // update chart model
            IPlotableObserver<IObservable> ob =
                (IPlotableObserver<IObservable>) observer;
            model.startUpdating();
            try {
              model.removeAllSeries();
              for (String name : ob.getVariableNames()) {
                DefaultXYSeries s = new DefaultXYSeries(name);
                List<Pair<? extends Number, ? extends Number>> variableData =
                    ob.getVariableData(name);
                for (Pair<? extends Number, ? extends Number> data : variableData) {
                  s.addValuePair(data.getFirstValue(), data.getSecondValue());
                }
                model.addSeries(s);
              }
            } finally {
              model.finishedUpdating();
            }
          }
        });
      } catch (Exception e) {
      } finally {
        updatingLock.unlock();
      }
    }

  }

  @Override
  protected JComponent createContent() {
    JPanel panel = new JPanel();
    // DefaultListSelectionModel selectionModel = new
    // DefaultListSelectionModel();
    // selectionModel
    // .setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    //
    // CheckBoxGroup<ISeries> legend =
    // new CheckBoxGroup<>(new PlotterLegendGroupModel(model),
    // selectionModel);
    chart = new BasicChart(model, new CoordinateSystemXY(model));
    chart.setShowLegend(false);

    panel.setLayout(new BorderLayout());
    panel.add(chart, BorderLayout.CENTER);
    // panel.add(new JScrollPane(legend), BorderLayout.LINE_END);

    return panel;
  }

  @Override
  public void windowClosed() {
    // TODO sr137: remove observer plotter as listener
  }

  @Override
  protected IAction[] generateActions() {
    return null;
  }

}
