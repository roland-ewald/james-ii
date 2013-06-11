/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.validation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * This class can be used to implement custom validators that implement the
 * {@link IValidator} interface and validate text components. The main method to
 * implement is the {@link #validate(String)} method that determines whether the
 * text component's text is valid or not in the implemented context.
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class AbstractTextComponentValidator extends AbstractValidator
    implements PropertyChangeListener, DocumentListener {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -4326336772715014102L;

  /**
   * the text component
   */
  private transient JTextComponent textComponent;

  /**
   * flag specifying whether the text component's context is valid
   */
  private transient boolean valid = false;

  /**
   * Create a text component validator for the specified component
   * 
   * @param c
   *          the text component the validator is for
   */
  public AbstractTextComponentValidator(JTextComponent c) {
    super();
    setComponent(c);
  }

  /**
   * Sets the component that should be validated. This must be swing text
   * component extending {@link JTextComponent}.
   * 
   * @param c
   *          the component
   */
  public final void setComponent(JTextComponent c) {
    if (textComponent != null) {
      textComponent.removePropertyChangeListener(this);
      textComponent.getDocument().removeDocumentListener(this);
    }
    if (c != null) {
      // keep track of document changes
      c.addPropertyChangeListener(this);
      // keep track of text changes within the document
      c.getDocument().addDocumentListener(this);
    }
    textComponent = c;
  }

  @Override
  public final boolean isValid() {
    return valid;
  }

  @Override
  public final void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() == textComponent
        && evt.getPropertyName().equals("document")) {
      ((Document) evt.getOldValue()).removeDocumentListener(this);
      ((Document) evt.getNewValue()).addDocumentListener(this);
    }
  }

  @Override
  public final void changedUpdate(DocumentEvent e) {
    if (e.getDocument() == textComponent.getDocument()) {
      valid = validate(textComponent.getText());
      fireValidityChanged();
    }
  }

  /**
   * Implement this method to provide custom validation for the specified text.
   * 
   * @param text
   *          the text to validate
   * @return true if provided text is valid false else
   */
  public abstract boolean validate(String text);

  @Override
  public final void insertUpdate(DocumentEvent e) {
    changedUpdate(e);
  }

  @Override
  public final void removeUpdate(DocumentEvent e) {
    changedUpdate(e);
  }

}
