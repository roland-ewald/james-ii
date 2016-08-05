/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import model.mlspace.IMLSpaceModel;
import model.mlspace.MLSpaceModel;
import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.NSMEntity;
import model.mlspace.subvols.Subvol;
import simulator.mlspace.eventrecord.IContSpaceEventRecord;
import simulator.mlspace.eventrecord.IEventRecord;
import simulator.mlspace.eventrecord.SubvolEventRecord;
import simulator.mlspace.observation.graphic.GraphicalOutputObserver.GraphicalOutputParameters;
import simulator.mlspace.observation.graphic.output.IImageProcessor;
import simulator.mlspace.observation.graphic.output.IImageProcessorFactory;

/**
 * Class containing factory method for {@link GraphicalOutputObserver},
 * automatically creating the necessary {@link IGraphicsProvider graphic
 * providers} based on which drawable components are present in the model. Also
 * creates and adds {@link IImageProcessor image processors} specified via the
 * constructor.
 * 
 * @author Arne Bittig
 * @date 13.06.2012
 */
public class GraphicalObserverFactory {

  private final Collection<IImageProcessorFactory> imgProcFacs;

  private final GraphicalOutputObserver.GraphicalOutputParameters goParams;

  private final IColorProvider<AbstractModelEntity> colorProvider;

  private final int[] subvolSubdivisions;

  private final Integer amountForMaxIntensity;

  private final boolean hideSubvolBoundaries;

  /**
   * @param imgProcFacs
   *          {@link IImageProcessorFactory Image processor factories}
   * @param goParams
   *          Graphical output parameters (height, width, fading factor)
   */
  public GraphicalObserverFactory(
      Collection<IImageProcessorFactory> imgProcFacs,
      GraphicalOutputObserver.GraphicalOutputParameters goParams) {
    this(imgProcFacs, goParams, new DefaultColorProvider(), null, false, null);
  }

  /**
   * @param imgProcFacs
   *          {@link IImageProcessorFactory Image processor factories}
   * @param goParams
   *          Graphical output parameters (height, width, fading factor)
   * @param colorProvider
   *          Method to assign color to entities
   * @param subvolSubdivisions
   *          Subdivisions (x,y) for each subvol viz (null for automatic or if
   *          not relevant)
   * @param hideSubvolBoundaries
   *          Flag whether to display no or thick lines for subvol boundaries
   * @param amountForMaxIntensity
   *          Particle amount for max intensity of the respective color in the
   *          subvol viz (null for automatic or if not relevant)
   */
  public GraphicalObserverFactory(
      Collection<IImageProcessorFactory> imgProcFacs,
      GraphicalOutputParameters goParams,
      IColorProvider<AbstractModelEntity> colorProvider,
      int[] subvolSubdivisions, boolean hideSubvolBoundaries,
      Integer amountForMaxIntensity) {
    this.imgProcFacs = imgProcFacs;
    this.goParams = goParams;
    this.colorProvider = colorProvider;
    this.subvolSubdivisions = subvolSubdivisions;
    this.amountForMaxIntensity = amountForMaxIntensity;
    this.hideSubvolBoundaries = hideSubvolBoundaries;
  }

  /**
   * @param model
   *          ML-Space model
   * @param filePathAndPrefix
   *          File name to incorporate into params' file name specifications if
   *          non-null
   * @return Graphical observer
   */
  public GraphicalOutputObserver<?> create(IMLSpaceModel model,
      String filePathAndPrefix) {
    GraphicalOutputParameters localParams;
    if (goParams == null) {
      localParams = new GraphicalOutputParameters();
    } else {
      localParams = new GraphicalOutputParameters(goParams);
    }
    List<IImageProcessor> imgProcs = new LinkedList<>();
    for (IImageProcessorFactory ipf : imgProcFacs) {
      imgProcs.add(ipf.create(model, filePathAndPrefix));
    }
    if (MLSpaceModel.hasNSMPart(model)) {
      if (model.getCompartments().isEmpty()
          || model.getVectorFactory() == null) {
        return createNSMOnlyObserver(imgProcs, localParams, model);
      } else {
        return createHybridObserver(imgProcs, localParams, model);
      }
    } else if (MLSpaceModel.hasActiveCompartments(model)) {
      return createContOnlyObserver(imgProcs, localParams, model);
    }
    return null;
  }

  private static final float MAJOR_WIDTH = 2.0f;

  private static final float MEDIUM_WIDTH = 2.4f;

  private static final float MINOR_WIDTH = 1.2f;

  private static final float[] MEDIUM_DASH = new float[] { 6f };

  private static final float[] MINOR_DASH = new float[] { 3f };

  private static final BasicStroke MAJOR_STROKE = new BasicStroke(MAJOR_WIDTH);

  private static final BasicStroke MEDIUM_STROKE = new BasicStroke(MEDIUM_WIDTH,
      BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, MEDIUM_DASH, 0.0f);

  private static final BasicStroke MINOR_STROKE = new BasicStroke(MINOR_WIDTH,
      BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, MINOR_DASH, 0.0f);

  private static final BasicStroke HIDDEN_STROKE =
      new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f,
          new float[] { 0f, 10f }, 5f);

  private static final double DEFAULT_MAX_INTENSITY_FACTOR = 1.5;

  private static final int DEFAULT_MIN_MAX_INTENSITY_LEVEL = 4;

  private GraphicalOutputObserver<IEventRecord> createHybridObserver(
      List<IImageProcessor> imgProcs, GraphicalOutputParameters params,
      IMLSpaceModel model) {
    List<IGraphicsProvider<? extends IEventRecord>> gps = new ArrayList<>(2);
    if (hideSubvolBoundaries) {
      GraphicsForComps gfc =
          new GraphicsForComps(MAJOR_STROKE, MEDIUM_STROKE, colorProvider);
      gps.add(new GraphicsForSubvols(HIDDEN_STROKE, colorProvider,
          getSubvolSubdivision(model), getAmountForMaxIntensity(model)));
      gps.add(gfc);
    } else {
      GraphicsForComps gfc =
          new GraphicsForComps(MEDIUM_STROKE, MINOR_STROKE, colorProvider);
      gps.add(new GraphicsForSubvols(MAJOR_STROKE, colorProvider,
          getSubvolSubdivision(model), getAmountForMaxIntensity(model)));
      gps.add(gfc);
    }
    if (model.getVectorFactory().getDimension() == 2) {
      return new GraphicalOutputObserver<>(gps, imgProcs,
          params != null ? params : new GraphicalOutputParameters());
    } else {
      return new GraphicalOutputObserver3D<>(gps, imgProcs,
          params != null ? params : new GraphicalOutputParameters());
    }
  }

  private int[] getSubvolSubdivision(IMLSpaceModel model) {
    if (subvolSubdivisions != null) {
      return subvolSubdivisions;
    }
    Set<NSMEntity> ents = new HashSet<>();
    for (Subvol sv : model.getSubvolumes()) {
      ents.addAll(sv.getState().keySet());
    }
    int numEnts = ents.size();
    if (model.getModelEntityFactory() != null) {
      numEnts = Math.max(numEnts,
          model.getModelEntityFactory().getNumberOfDefinedSpecies() + 1);
    }
    int x = (int) Math.round(Math.sqrt(numEnts));
    int y = (int) Math.round((double) numEnts / x);
    return new int[] { x, y };
  }

  private int getAmountForMaxIntensity(IMLSpaceModel model) {
    if (amountForMaxIntensity != null) {
      return amountForMaxIntensity;
    }
    int maxAmount = 0;
    for (Subvol sv : model.getSubvolumes()) {
      int sum = 0;
      for (Integer amount : sv.getState().values()) {
        sum += amount;
      }
      if (maxAmount < sum) {
        maxAmount = sum;
      }
    }
    int rv = (int) (DEFAULT_MAX_INTENSITY_FACTOR * maxAmount);
    if (rv >= DEFAULT_MIN_MAX_INTENSITY_LEVEL) {
      return rv;
    } else {
      return DEFAULT_MIN_MAX_INTENSITY_LEVEL;
    }
  }

  /**
   * @param imgProcs
   * @param params
   * @return
   */
  private GraphicalOutputObserver<IContSpaceEventRecord> createContOnlyObserver(
      List<IImageProcessor> imgProcs, GraphicalOutputParameters params,
      IMLSpaceModel model) {
    List<GraphicsForComps> gps = Collections.singletonList(
        new GraphicsForComps(MAJOR_STROKE, MEDIUM_STROKE, colorProvider));
    if (model.getVectorFactory().getDimension() == 2) {
      return new GraphicalOutputObserver<>(gps, imgProcs,
          params != null ? params : new GraphicalOutputParameters());
    } else {
      return new GraphicalOutputObserver3D<>(gps, imgProcs,
          params != null ? params : new GraphicalOutputParameters());
    }
  }

  private GraphicalOutputObserver<SubvolEventRecord> createNSMOnlyObserver(
      List<IImageProcessor> imgProcs, GraphicalOutputParameters params,
      IMLSpaceModel model) {
    List<GraphicsForSubvols> gps = Collections.singletonList(
        new GraphicsForSubvols(MAJOR_STROKE, new DefaultColorProvider(),
            getSubvolSubdivision(model), getAmountForMaxIntensity(model)));
    return new GraphicalOutputObserver<>(gps, imgProcs,
        params != null ? params : new GraphicalOutputParameters());
  }
}
