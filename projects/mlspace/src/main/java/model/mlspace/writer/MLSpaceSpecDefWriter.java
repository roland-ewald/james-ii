/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.writer;

import java.io.IOException;
import java.util.Map;

import model.mlspace.IMLSpaceModel;
import model.mlspace.entities.Species;
import model.mlspace.entities.binding.BindingSites;
import model.mlspace.entities.values.AbstractValueRange;

import org.jamesii.core.util.misc.Triple;

/**
 * @author Arne Bittig
 * @date 19.10.2012
 */
public class MLSpaceSpecDefWriter implements IModelWriterModule<IMLSpaceModel> {

  @Override
  public void writeModelComponent(IMLSpaceModel model, Appendable output)
      throws IOException {
    for (Triple<Species, Map<String, AbstractValueRange<?>>, BindingSites> def : model
        .getModelEntityFactory().getFullSpeciesDefinitions()) {
      output.append(def.getA().toString());
      output.append('(');
      boolean first = true;
      for (Map.Entry<String, AbstractValueRange<?>> e : def.getB().entrySet()) {
        if (!first) {
          output.append(',');
        } else {
          first = false;
        }
        output.append(e.getKey());
        output.append(':');
        AbstractValueRange<?> value = e.getValue();
        if (value.size() == 1) {
          output.append(value.iterator().next().toString());
        } else {
          output.append(value.toString());
        }

      }
      output.append(')');
      if (def.getC() != null) {
        // output.append('<');
        output.append(def.getC().toString());
        // output.append('>');
      }
      output.append('\n');

    }

  }

  @Override
  public String getComponentName() {
    return "Species Definitions";
  }

}
