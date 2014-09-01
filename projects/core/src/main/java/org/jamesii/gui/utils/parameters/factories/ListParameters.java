/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.list.Entry;

/**
 * @author Stefan Rybacki
 * 
 */
public class ListParameters extends PluginTypeParameters {

  private List<Entry> list = new LinkedList<>();

  @SuppressWarnings("unchecked")
  public ListParameters(String baseFactory, ParameterBlock block) {
    super(baseFactory, null, block);
    // extract list from block if any
    if (block.getValue() instanceof List<?>) {

      List<?> newList = block.getValue();

      if (newList.isEmpty()) {
        return;
      }

      // accepts a list of entries
      if (newList.get(0) instanceof Entry) {
        list = block.getValue();
      } else { // or a list of parameter blocks

        // otherwise we assume that we get a list of parameter blocks
        List<ParameterBlock> pbList = (List<ParameterBlock>) newList;

        for (ParameterBlock pblock : pbList) {
          list.add(new Entry(pblock.getValue().toString(), pblock));
        }

      }
    }

  }

  public void setList(List<Entry> entries) {
    list.clear();
    list.addAll(entries);
  }

  public List<Entry> getList() {
    return Collections.unmodifiableList(list);
  }

  @Override
  public ParameterBlock getAsParameterBlock() {
    ParameterBlock block = super.getAsParameterBlock();

    // build a return type which meets the expectancy of the outside world
    List<ParameterBlock> pbList = new ArrayList<>();
    for (Entry entry : list) {
      pbList.add(new ParameterBlock(entry.getFactoryName(), entry
          .getParameters().getSubBlocks()));
    }

    // set pbList to return a list of parameter blocks
    block.setValue(list);

    return block;
  }

}
