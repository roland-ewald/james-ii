/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.base;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.model.ISymbolicModelWindowManager;
import org.jamesii.gui.model.base.plugintype.AbstractModelInfoProviderFactory;
import org.jamesii.gui.model.base.plugintype.ModelInfoProviderFactory;
import org.jamesii.gui.model.windows.plugintype.AbstractModelWindowFactory;
import org.jamesii.gui.model.windows.plugintype.ModelWindow;
import org.jamesii.gui.model.windows.plugintype.ModelWindowFactory;
import org.jamesii.gui.syntaxeditor.highlighting.HighlightingManager;
import org.jamesii.gui.syntaxeditor.highlighting.IHighlighter;

/**
 * Factory for providing a syntax highlighting text editor for supported models.
 * Supported models are those that provide a highlighter and appropriate model
 * reader and writer.
 * 
 * @author Stefan Rybacki
 * @see org.jamesii.gui.syntaxeditor.highlighting.plugintype.HighlightingFactory
 * @see IHighlighter
 */
public class ModelTextEditorFactory extends ModelWindowFactory {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 4697119696490411999L;

  /**
   * Instantiates a new model text editor factory.
   */
  public ModelTextEditorFactory() {
    super();
  }

  @Override
  public ModelWindow<? extends ISymbolicModel<?>> create(ParameterBlock params,
      ISymbolicModelWindowManager mlManager) {

    ISymbolicModel<?> model =
            params
                .getSubBlockValue(AbstractModelWindowFactory.MODEL);

    if (model == null) {
      return null;
    }

    List<IHighlighter> highlighters =
        HighlightingManager.getAvailableHighlightersFor(model);

    ParameterBlock par =
        new ParameterBlock(highlighters.get(0).getDocumentClass(),
            AbstractModelInfoProviderFactory.DOCUMENT_CLASS);

    List<IModelInfoProvider> infoProviders = new ArrayList<>();

    List<ModelInfoProviderFactory> list = null;
    try {
      list =
          SimSystem.getRegistry().getFactoryList(
              AbstractModelInfoProviderFactory.class, par);
      for (ModelInfoProviderFactory f : list) {
        infoProviders.add(f.create(par));
      }
    } catch (Exception e) {
    }

    return new ModelTextEditor(String.format("Model '%s'", model.getName()),
        model, mlManager, highlighters, infoProviders);
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    ISymbolicModel<?> model =
        (ISymbolicModel<?>) params
            .getSubBlockValue(AbstractModelWindowFactory.MODEL);

    if (model == null) {
      return 0;
    }

    List<IHighlighter> highlighters =
        HighlightingManager.getAvailableHighlightersFor(model);

    if (highlighters.size() > 0) {
      return 1;
    }

    return 0;
  }

}
