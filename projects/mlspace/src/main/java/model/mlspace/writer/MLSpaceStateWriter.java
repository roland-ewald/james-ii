/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.writer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.util.hierarchy.IHierarchy;

import model.mlspace.IMLSpaceModel;
import model.mlspace.entities.IEntity;
import model.mlspace.entities.ModelEntityFactory;
import model.mlspace.entities.binding.IEntityWithBindings;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.entities.spatial.SpatialEntity;

/**
 * @author Arne Bittig
 * @date 19.10.2012
 */
public class MLSpaceStateWriter implements IModelWriterModule<IMLSpaceModel> {

  private static final String INDENT = "    ";

  @Override
  public void writeModelComponent(IMLSpaceModel model, Appendable output)
      throws IOException {
    IHierarchy<SpatialEntity> compTree = model.getCompartments();
    Collection<SpatialEntity> roots = compTree.getRoots();
    writeCompSubTree(roots, compTree, 0, output);

  }

  private static void writeCompSubTree(Collection<SpatialEntity> thisLevelComps,
      IHierarchy<SpatialEntity> compTree, int level, Appendable output)
          throws IOException {
    boolean first = true;
    for (SpatialEntity comp : thisLevelComps) {
      addIndent(level, output);
      if (!first) {
        output.append("+ 1 ");
      } else {
        first = false;
        output.append("  1 ");
      }
      writeSpatialEntity(comp, level, output);
      Collection<SpatialEntity> children = compTree.getChildren(comp);
      if (!children.isEmpty()) {
        output.append("[\n");
        writeCompSubTree(children, compTree, level + 1, output);
        addIndent(level, output);
        output.append(']');
      }
      output.append('\n');
    }
  }

  private static void addIndent(int level, Appendable output)
      throws IOException {
    for (int l = level; l > 0; l--) {
      output.append(INDENT);
    }
  }

  private static void writeSpatialEntity(SpatialEntity comp, int level,
      Appendable output) throws IOException {

    output.append(comp.getSpecies().toString());
    output.append('(');
    Set<String> attributesWritten = writeSpatialAttributes(comp, output);
    writeOtherAttributes(comp, output, attributesWritten);
    output.append(')');
    if (comp instanceof IEntityWithBindings<?>) {
      writeBindings((IEntityWithBindings<?>) comp, level, output);
    }
  }

  private static Set<String> writeSpatialAttributes(SpatialEntity comp,
      Appendable output) throws IOException {
    Set<String> attributesWritten = new HashSet<>();
    boolean first = true;
    for (SpatialAttribute spAtt : SpatialAttribute.values()) {
      Object attVal = comp.getAttribute(spAtt.toString());
      if (isNonEmptyAndNonZero(attVal)) {
        if (!first) {
          output.append(",");
        } else {
          first = false;
        }
        output.append(spAtt.toString());
        output.append(':');
        output.append(attVal.toString());
        attributesWritten.add(spAtt.toString());
        appendAspectRatioIfShape(spAtt, comp, output);
      }
    }
    return attributesWritten;
  }

  private static boolean isNonEmptyAndNonZero(Object attVal) {
    boolean nonEmptyAndNonZero = true;
    if (attVal == null) {
      nonEmptyAndNonZero = false;
    }
    if (attVal instanceof String && ((String) attVal).isEmpty()) {
      nonEmptyAndNonZero = false;
    }
    if (attVal instanceof Number && ((Number) attVal).doubleValue() == 0.) {
      nonEmptyAndNonZero = false;
    }
    return nonEmptyAndNonZero;
  }

  private static void appendAspectRatioIfShape(SpatialAttribute spAtt,
      SpatialEntity comp, Appendable output) throws IOException {
    if (spAtt.equals(SpatialAttribute.SHAPE)) {
      IDisplacementVector maxExtVector = comp.getShape().getMaxExtVector();
      double[] maxExtArr = maxExtVector.toArray();
      Arrays.sort(maxExtArr);
      if (maxExtArr[0] < maxExtArr[maxExtArr.length - 1]) {
        output.append(',');
        output.append(ModelEntityFactory.ASPECT_RATIO);
        output.append(':');
        output.append(maxExtVector.toString());
      }
    }
  }

  private static void writeOtherAttributes(SpatialEntity comp,
      Appendable output, Set<String> attributesWritten) throws IOException {
    boolean first = attributesWritten.isEmpty();
    for (String att : comp.getAttributeNames()) {
      if (!attributesWritten.contains(att)) {
        if (!first) {
          output.append(",");
        } else {
          first = false;
        }
        output.append(att);
        output.append(':');
        output.append(comp.getAttribute(att).toString());
      }
    }
  }

  private static void writeBindings(IEntityWithBindings<?> comp, int level,
      Appendable output) throws IOException {
    Map<String, ? extends IEntityWithBindings<?>> bindingEntries =
        comp.bindingEntries();
    if (bindingEntries.isEmpty()) {
      return;
    }
    output.append('\n');
    addIndent(level, output);
    for (int i = comp.getSpecies().toString().length() + 1; i >= -1; i--) {
      output.append(' ');
    }
    boolean first = true;
    output.append('<');
    for (Map.Entry<String, ? extends IEntity<?>> e : bindingEntries
        .entrySet()) {
      if (!first) {
        output.append(",");
      } else {
        first = false;
      }
      output.append(e.getKey());
      output.append(':');
      output.append(e.getValue().getSpecies().toString());
    }
    output.append('>');
  }

  @Override
  public String getComponentName() {
    return "State";
  }

}
