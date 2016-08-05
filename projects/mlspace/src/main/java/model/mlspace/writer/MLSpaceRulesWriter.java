/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.writer;

import java.io.IOException;
import java.util.Collection;

import model.mlspace.IMLSpaceModel;
import model.mlspace.rules.MLSpaceRule;

/**
 * @author Arne Bittig
 * @date 19.10.2012
 */
public class MLSpaceRulesWriter implements IModelWriterModule<IMLSpaceModel> {

  @Override
  public void writeModelComponent(IMLSpaceModel model, Appendable output)
      throws IOException {
    writeRules(model.getNSMReactionRules(), output);
    writeRules(model.getTimedReactionRules(), output);
    writeRules(model.getCollisionTriggeredRules(), output);
    writeRules(model.getTransferInRules(), output);
    writeRules(model.getTransferOutRules(), output);
  }

  /**
   * @param collection
   * @param output
   * @throws IOException
   */
  private static void writeRules(Collection<? extends MLSpaceRule> collection,
      Appendable output) throws IOException {
    for (MLSpaceRule rule : collection) {
      output.append(rule.toString());
      output.append('\n');
    }
  }

  @Override
  public String getComponentName() {
    return "Rules";
  }

}
