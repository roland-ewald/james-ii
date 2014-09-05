/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.offline.plugintype;

import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.visualization.offline.IOfflineVisualizer;

/**
 * Super class for all factories of off-line visualisers.
 * 
 * @author Roland Ewald
 */
public abstract class OfflineVisFactory extends Factory<IOfflineVisualizer>
    implements IParameterFilterFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 5957929009370571836L;

  /**
   * Creates off-line visualiser.
   * 
   * @param dataStorage
   *          the data storage to be read from
   * @param params
   *          the parameter block to configure visualisation
   * @return newly created off-line visualiser
   */
  public abstract IOfflineVisualizer create(IDataStorage dataStorage,
      ParameterBlock params);

  @Override
  public IOfflineVisualizer create(ParameterBlock params, Context context) {
    return create((IDataStorage) params.getValue(), params);
  }

}
