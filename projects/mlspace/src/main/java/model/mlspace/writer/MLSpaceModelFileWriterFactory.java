/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.writer;

import java.util.ArrayList;
import java.util.List;

import model.mlspace.IMLSpaceModel;

import org.jamesii.core.data.model.ModelFileWriterFactory;
import org.jamesii.core.data.model.read.plugintype.IMIMEType;
import org.jamesii.core.factories.Context;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * @author Arne Bittig
 * @date 19.10.2012
 */
public class MLSpaceModelFileWriterFactory extends ModelFileWriterFactory {

  private static final long serialVersionUID = 4165785621930719905L;

  @Override
  public ModelFileWriter<IMLSpaceModel> create(ParameterBlock parameters, Context context) {
    List<IModelWriterModule<IMLSpaceModel>> writerModules = new ArrayList<>();
    writerModules.add(new MLSpaceSpecDefWriter());
    writerModules.add(new MLSpaceRulesWriter());
    writerModules.add(new MLSpaceStateWriter());
    return new ModelFileWriter<>(new ModularModelWriter<>(writerModules, "\n",
        "///* ", " *///"));
  }

  @Override
  public String getDescription() {
    return "ML-Space model file";
  }

  @Override
  public String getFileEnding() {
    return "mls";
  }

  @Override
  public boolean supportsModel(IModel model) {
    return model instanceof IMLSpaceModel;
  }

  @Override
  public boolean supportsModel(ISymbolicModel<?> model) {
    return false;
  }

  @Override
  public boolean supportsMIMEType(IMIMEType mime) {
    return MLSpaceMimeType.getInstance().equals(mime);
  }

}
