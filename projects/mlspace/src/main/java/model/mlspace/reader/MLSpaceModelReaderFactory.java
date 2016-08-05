/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.reader;

import java.net.URI;

import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.data.model.ModelFileReaderFactory;
import org.jamesii.core.data.model.read.plugintype.IMIMEType;
import org.jamesii.core.factories.Context;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

import model.mlspace.IMLSpaceModel;

/**
 * Factory for {@link MLSpaceModelReader}
 *
 * @author Arne Bittig
 */
public class MLSpaceModelReaderFactory extends ModelFileReaderFactory {

  public static final String ML_SPACE_STRING = "MLSpaceString";

  /** Parameter block identifier for model reader flag */
  public static final String OLD_SYNTAX_FALLBACK = "OldSyntaxFallback";

  private static final long serialVersionUID = 562977799219319726L;

  @Override
  public IModelReader create(ParameterBlock params, Context context) {
    return new MLSpaceModelReader(ParameterBlocks
        .getSubBlockValueOrDefault(params, OLD_SYNTAX_FALLBACK, false));
  }

  @Override
  public boolean supportsMIMEType(IMIMEType mime) {
    return false;
  }

  @Override
  public boolean supportsModel(IModel model) {
    return model instanceof IMLSpaceModel;
  }

  @Override
  public boolean supportsModel(ISymbolicModel<?> model) {
    return false; // TODO
  }

  @Override
  public String getDescription() {
    return "Files for MLSpace";
  }

  @Override
  public String getFileEnding() {
    return "mls";
  }

  @Override
  public boolean supportsURI(URI uri) {
    if (uri.getScheme().equals(ML_SPACE_STRING)) {
      return true;
    }
    return super.supportsURI(uri);
  }

}
