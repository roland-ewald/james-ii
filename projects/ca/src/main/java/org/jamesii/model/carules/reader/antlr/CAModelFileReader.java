/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.util.misc.Files;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.model.carules.CARulesAntlrDocument;
import org.jamesii.model.carules.CARulesModel;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.converter.CARulesConverter;
import org.jamesii.model.carules.grid.ICARulesGrid;
import org.jamesii.model.carules.grid.object.Grid2D;
import org.jamesii.model.carules.grid.plugintype.AbstractGridFactory;
import org.jamesii.model.carules.grid.plugintype.BaseGridFactory;
import org.jamesii.model.carules.reader.antlr.parser.CAProblemToken;
import org.jamesii.model.carules.symbolic.ISymbolicCAModel;
import org.jamesii.model.carules.symbolic.SymbolicCAModel;

/**
 * This class provides an {@link IModelReader} implementation for
 * {@link CARulesModel} that is able to load {@link CARulesModel}s as
 * {@link SymbolicCAModel} or {@link CARulesModel} from files using the
 * following specified grammar for source files:
 * <p/>
 * <code>
 * <table>
 * <tr>
 *  <td>model</td><td>::=</td><td>grammarversion (wolframrule | dimensions neighborhood? state+ rule+)</td>
 * </tr>
 * <tr>
 *  <td>grammarversion</td><td>::=</td><td><u>@caversion</u> (integer | double) <u>;</u></td>
 * </tr>
 * <tr>
 *  <td>wolframrule</td><td>::=</td><td><u>wolframrule</u> <u>:</u>? integer <u>;</u></td>
 * </tr>
 * <tr>
 *  <td>dimensions</td><td>::=</td><td><u>dimensions</u> <u>:</u>? integer <u>;</u></td>
 * </tr>
 * <tr>
 *  <td>neighborhood</td><td>::=</td><td><u>neighborhood</u> (<u>moore</u> | <u>neumann</u> | <u>free</u>? freeneighborhood) <u>;</u></td>
 * </tr>
 * <tr>
 *  <td>freeneighborhood</td><td>::=</td><td><u>{</u> (<u>[</u> integer (<u>,</u>? integer)* <u>]</u>)+ <u>}</u></td>
 * </tr>
 * <tr>
 *  <td>state</td><td>::=</td><td><u>state</u> <u>:</u>? id (<u>,</u>? id)* <u>;</u></td>
 * </tr>
 * <tr>
 *  <td>rule</td><td>::=</td><td><u>rule</u> (<u>{</u> id (<u>,</u>? id)*<u>}</u>)? (<u>[</u> double <u>]</u>)? <u>:</u> condition? <u>-></u> id <u>;</u></td>
 * </tr>
 * <tr>
 *  <td>id</td><td>::=</td><td>(<u>A</u>..<u>Z</u>)+ (<u>A</u>..<u>Z</u>| <u>0</u>..<u>9</u> | <u>_</u>)*</td>
 * </tr>
 * <tr>
 *  <td>condition</td><td>::=</td><td><u>(</u> condition <u>)</u> | not? condition | condition (and | or) condition | weightedid </td>
 * </tr>
 * <tr>
 *  <td>weightedid</td><td>::=</td><td>id (<u>{</u> (integer? <u>,</u> integer | integer) <u>}</u>)?</td>
 * </tr>
 * <tr>
 *  <td>not</td><td>::=</td><td><u>!</u> | <u>not</u> | <u>~</u></td>
 * </tr>
 * <tr>
 *  <td>and</td><td>::=</td><td><u>&</u> | <u>&&</u> | <u>and</u> | <u>^</u></td>
 * </tr>
 * <tr>
 *  <td>or</td><td>::=</td><td><u>|</u> | <u>or</u> | <u>||</u> | <u>+</u> | <u>v</u></td>
 * </tr>
 * <tr>
 *  <td>integer</td><td>::=</td><td></td>
 * </tr>
 * <tr>
 *  <td>double</td><td>::=</td><td></td>
 * </tr>
 * </table>
 * </code>
 * <p/>
 * 
 * Comments can also appear anywhere and have the following syntax:
 * <p/>
 * <table>
 * <tr>
 * <td>comment</td>
 * <td>::=</td>
 * <td><u>//</u> arbitrary* EOL | <u>/&#42;</u> arbitrary* <u>&#42;/</u></td>
 * </tr>
 * </table>
 * 
 * <p/>
 * This means for instance to create a wolfram ca model with wolfram rule 128
 * the following code would do it:
 * <p/>
 * 
 * <code>
 * <pre>
 * &#064;caversion 1;
 * wolframrule 128;
 * </pre>
 * </code>
 * 
 * To create a 2D game of life ca model the following would be sufficient:
 * <p/>
 * 
 * <code>
 * <pre>
 * &#064;caversion 1;
 * dimensions 2;
 * neighborhood moore;
 * state DEAD,ALIVE;
 * rule{ALIVE}:!ALIVE{2,3}->DEAD;
 * rule{DEAD}:ALIVE{3}->ALIVE;
 * </pre>
 * </code>
 * <p/>
 * 
 * So what does all this means. First we specifiy the grammar version we are
 * about to use, this way it is possible for the parser to recognize supported
 * grammars or not. Then using <code>dimensions 2;</code> sets the model
 * dimensions to 2D. Afterwards the <code>moore</code> neighborhood is set as
 * neighborhood to use when simulating. <code>state DEAD, ALIVE</code> is used
 * to define the available states which could also look like this
 * <code>state DEAD; state ALIVE;</code>. The following rules are to interpret
 * as follows. <code>rule{ALIVE}:!ALIVE{2,3}->DEAD;</code> is only to use if the
 * current state is <code>ALIVE</code> because it is specified after
 * <code>rule</code>. Then it says if <code>NOT</code> the neighborhood contains
 * between 2 and 3 <code>ALIVE</code> cells this cell changes to state
 * <code>DEAD</code>.<br/>
 * The second rule <code>rule{DEAD}:ALIVE{3}->ALIVE;</code> is only applied if
 * current state is <code>DEAD</code> and the neighborhood contains exactly 3
 * <code>ALIVE</code> states and will then change to <code>ALIVE</code>
 * 
 * 
 * @author Stefan Rybacki
 */
class CAModelFileReader implements IModelReader {

  /** The grid factory parameters */
  private ParameterBlock gridParameters = null;

  /**
   * Instantiates a new CA model file reader.
   * 
   * @param params
   *          the params
   */
  public CAModelFileReader(ParameterBlock params) {
    super();

    if (ParameterBlocks.hasSubBlock(params, CAModelReaderFactory.CGRID)) {
      gridParameters = params.getSubBlock(CAModelReaderFactory.CGRID);
    }
  }

  @Override
  public IModel read(URI source, Map<String, ?> parameters) {
    if (source == null) {
      return null;
    }

    File f = Files.getFileFromURI(source);
    if (!f.exists()) {
      return null;
    }

    BufferedReader reader = null;
    try {
      reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(f),
              "UTF8"));

      StringBuilder builder = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null) {
        builder.append(line);
        builder.append(System.getProperty("line.separator"));
      }

      CARulesConverter converter = new CARulesConverter();

      ISymbolicModel<?> sm =
          converter.fromDocument(new CARulesAntlrDocument(builder.toString()));

      SymbolicCAModelInformation info =
          (SymbolicCAModelInformation) sm.getAsDataStructure();

      List<CAProblemToken> problems = info.getProblems();

      if (problems.size() > 0) {
        SimSystem
            .report(
                Level.SEVERE,
                null,
                "%s contains Problems either of syntactical or semantical nature. Loading aborted.",
                new Object[] { source });
        return null;
      }

      int[] standardSize = new int[info.getDimensions()];
      for (int i = 0; i < standardSize.length; i++) {
        standardSize[i] = 10;
      }

      List<ICACell> cellList = new ArrayList<>();

      Object t = parameters.get("initialState");

      t = t instanceof String ? loadInitialState((String) t) : t;

      if (t instanceof ICARulesGrid) {
        ICARulesGrid grid = (ICARulesGrid) t;
        // set size from parameters
        standardSize = grid.getSize();

        // set cells from parameters
        cellList = grid.getCellList();
      }

      CARulesModel model = null;
      if (gridParameters == null) {
        model =
            new CARulesModel(f.getName(), info.getDimensions(),
                info.getRules(), info.getStates(), info.getNeighborhood(),
                cellList, standardSize, false);
      } else {

        gridParameters.addSubBl(AbstractGridFactory.DIMENSION,
            info.getDimensions());

        BaseGridFactory gridFactory =
            SimSystem.getRegistry().getFactory(AbstractGridFactory.class,
                gridParameters);

        model =
            new CARulesModel(f.getName(), info.getDimensions(),
                info.getRules(), info.getStates(), info.getNeighborhood(),
                cellList, standardSize, false, gridFactory);
      }

      return model;
    } catch (IOException e) {
      SimSystem.report(e);
    } finally {
      org.jamesii.core.util.BasicUtilities.close(reader);
    }

    return null;
  }

  @Override
  public ISymbolicModel<?> read(URI ident) {
    if (ident == null) {
      return null;
    }

    File f = Files.getFileFromURI(ident);
    if (!f.exists()) {
      return null;
    }

    BufferedReader reader = null;
    try {
      reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(f),
              "UTF8"));
      StringBuilder builder = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null) {
        builder.append(line);
        builder.append(System.getProperty("line.separator"));
      }

      ISymbolicCAModel model = new SymbolicCAModel();
      model.setFromDocument(new CARulesAntlrDocument(builder.toString()));
      return model;
    } catch (IOException e) {
      SimSystem.report(e);
    } finally {
      org.jamesii.core.util.BasicUtilities.close(reader);
    }

    return null;
  }

  /**
   * Load initial state from a file name in very custom format.
   * 
   * @param fileName
   *          the file name to load initial state from
   * 
   * @return the initial grid or {@code null} if grid couldn't be loaded
   */
  private ICARulesGrid loadInitialState(String fileName) {
    BufferedReader reader = null;
    int skipChars;
    int height;
    int width;
    InputStream stream;
    try {
      stream = new FileInputStream(fileName);
      reader = new BufferedReader(new InputStreamReader(stream, "UTF8"));

      String strWidth = reader.readLine();
      String strHeight = reader.readLine();

      if (strWidth == null || strHeight == null) {
        return null;
      }

      width = Integer.parseInt(strWidth);
      height = Integer.parseInt(strHeight);
      skipChars = strWidth.length() + strHeight.length() + 2;

    } catch (IOException e) {
      return null;
    } finally {
      org.jamesii.core.util.BasicUtilities.close(reader);
    }

    try {

      stream = new FileInputStream(fileName);
      if (stream.skip(skipChars) != skipChars) {
        return null;
      }

      ICARulesGrid grid = new Grid2D(new int[] { width, height }, 0);

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int s = stream.read();
          grid.setState(s, new int[] { x, y });
        }
        stream.read();
      }

      return grid;
    } catch (IOException e) {
      return null;
    } finally {
      org.jamesii.core.util.BasicUtilities.close(stream);
    }
  }

}
