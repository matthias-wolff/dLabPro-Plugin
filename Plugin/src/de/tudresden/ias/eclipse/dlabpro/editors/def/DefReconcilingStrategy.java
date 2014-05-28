
package de.tudresden.ias.eclipse.dlabpro.editors.def;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonFoldingStructureProvider;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonParser;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonReconcilingStrategy;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.DefParser;

/**
 * This class represents the impementation of
 * {@link de.tudresden.ias.eclipse.dlabpro.editors.CommonReconcilingStrategy} for def-files/-editor
 * 
 * @author Christian Feig
 * 
 */
public class DefReconcilingStrategy extends CommonReconcilingStrategy
{

  /**
   * Constructor
   * 
   * @param editor
   */
  public DefReconcilingStrategy(AbstractEditor editor)
  {
    super(editor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonReconcilingStrategy#getParser()
   */
  protected CommonParser getParser()
  {
    return new DefParser();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonReconcilingStrategy#getFoldingStructureProvider(de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor)
   */
  protected CommonFoldingStructureProvider getFoldingStructureProvider(AbstractEditor editor)
  {
    return new DefFoldingStructureProvider(editor);
  }

}
