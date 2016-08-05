package model.mlspace;

import org.jamesii.core.model.symbolic.SymbolicModel;

/**
 * @author Arne Bittig
 * @date 17.09.2012
 */
public class SymbolicMLSpaceModel extends SymbolicModel<String> {

  private static final long serialVersionUID = 8758843133207730340L;

  /**
   *
   */
  public SymbolicMLSpaceModel() {
  }

  /**
   * @param model
   */
  public SymbolicMLSpaceModel(String model) {
    setFromDataStructure(model);
  }

}
