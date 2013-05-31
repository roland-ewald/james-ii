/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * @author Stefan Rybacki
 */
public class ParameterAddAction extends AbstractAction {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7816447145125205273L;

  private AtomicInteger counter = new AtomicInteger(0);

  private JPopupMenu popupMenu;

  private DefaultListModel model;

  private Map<String, ExperimentVariable<?>> variables;

  private Set<String> names = new HashSet<>();

  @SuppressWarnings("unchecked")
  public ParameterAddAction(final DefaultListModel model,
      Map<String, ExperimentVariable<?>> vars) {
    super("+");
    variables = vars;
    popupMenu = new JPopupMenu("Select Type");
    this.model = model;
    for (Entry<String, ExperimentVariable<?>> e : vars.entrySet()) {
      JMenuItem item = new JMenuItem(e.getKey());
      item.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          // find unique var name
          String name = String.format("VAR%08d", counter.incrementAndGet());
          try {
            while (names.contains(name)) {
              name = String.format("VAR%08d", counter.incrementAndGet());
            }
          } catch (Exception e1) {
          }

          names.add(name);
          ExperimentVariable<?> expV = variables.get(e.getActionCommand());

          // STUB copy by serialization
          try {
            byte[] s = SerialisationUtils.serialize(expV);
            expV = (ExperimentVariable<?>) SerialisationUtils.deserialize(s);
            expV.setName(name);
          } catch (IOException | ClassNotFoundException e1) {
            SimSystem.report(e1);
          }

          ParameterAddAction.this.model
              .addElement(new SimpleEntry<String, ExperimentVariable<?>>(e
                  .getActionCommand(), expV));
        }
      });
      item.setActionCommand(e.getKey());
      popupMenu.add(item);
    }

    for (int i = 0; i < model.getSize(); i++) {
      Object object = model.get(i);
      if (object instanceof Entry<?, ?>) {
        Entry<String, ExperimentVariable<?>> e =
            (Entry<String, ExperimentVariable<?>>) object;
        names.add(e.getValue().getName());
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    popupMenu.show((Component) e.getSource(), 0,
        ((Component) e.getSource()).getHeight());
  }

}
