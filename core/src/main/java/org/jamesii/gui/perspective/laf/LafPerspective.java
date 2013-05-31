/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.laf;

import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager.LookAndFeelInfo;

import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.action.ToggleAction;
import org.jamesii.gui.application.preferences.IPreferencesPage;
import org.jamesii.gui.perspective.AbstractPerspective;

/**
 * Perspective for JAMES II GUI that provides access to different look and
 * feels.
 * 
 * @author Stefan Rybacki
 */
class LafPerspective extends AbstractPerspective {

  /**
   * Creates a new perspective for look and feel support
   */
  public LafPerspective() {
    super();
  }

  @Override
  public String getDescription() {
    return getName();
  }

  @Override
  public String getName() {
    return "Look and Feel Perspective";
  }

  @Override
  protected List<IAction> generateActions() {
    List<IAction> actions = new ArrayList<>();

    actions.add(new ActionSet("org.jamesii.laf", "Change Look and Feel",
        "org.jamesii.menu.main/org.jamesii.edit?after=org.jamesii.preferences",
        null));

    for (final LookAndFeelInfo info : LafManager.getLookAndFeels()) {
      ToggleAction action =
          new LafChooseAction(
              "org.jamesii.laf." + info.getClassName(),
              null,
              new String[] { "org.jamesii.menu.main/org.jamesii.edit/org.jamesii.laf" },
              info, null);
      actions.add(action);
    }

    return actions;
  }

  @Override
  public List<IPreferencesPage> getPreferencesPages() {
    List<IPreferencesPage> pages = new ArrayList<>();
    pages.add(new LafPreferences());
    return pages;
  }

  @Override
  public boolean isMandatory() {
    return true;
  }

}
