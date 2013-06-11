/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james.dnd;

import java.awt.Rectangle;
import java.awt.dnd.DropTargetListener;

/**
 * Interface used for drag and drop. Simply specifies the region where something
 * can be dropped. It also extends the {@link DropTargetListener} interface so
 * drop events can directly be forwarded to the drop region.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change or to vanish in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
interface IDropRegion extends DropTargetListener {
  /**
   * Returns the region where something can be dropped at.
   * 
   * @return the region
   */
  Rectangle getBounds();
}
