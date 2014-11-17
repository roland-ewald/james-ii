/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.writer.antlr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.jamesii.SimSystem;
import org.jamesii.core.data.model.IModelWriter;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.util.misc.Files;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.model.carules.CARulesAntlrDocument;

/**
 * The Class CAModelWriter.
 * 
 * @author Stefan Rybacki
 */
class CAModelFileWriter implements IModelWriter {

  @Override
  public void write(IModel model, URI target) {
    throw new UnsupportedOperationException("not implemented!");
  }

  @Override
  public void write(ISymbolicModel<?> model, URI ident) {
    if (model == null || ident == null) {
      return;
    }

    File f = Files.getFileFromURI(ident);
    BufferedWriter writer = null;
    try {
      String text =
          (String) model.getAsDocument(CARulesAntlrDocument.class).getContent();

      writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),
              "UTF8"));

      writer.write(text);
    } catch (UnsupportedEncodingException e) {
      SimSystem.report(e);
    } catch (IOException e) {
      SimSystem.report(e);
    } finally {
      org.jamesii.core.util.BasicUtilities.close(writer);
    }
  }

}
