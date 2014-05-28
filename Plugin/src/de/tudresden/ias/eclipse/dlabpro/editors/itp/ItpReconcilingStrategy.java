
package de.tudresden.ias.eclipse.dlabpro.editors.itp;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonFoldingStructureProvider;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonParser;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonReconcilingStrategy;
import de.tudresden.ias.eclipse.dlabpro.editors.itp.model.ItpParser;

public class ItpReconcilingStrategy extends CommonReconcilingStrategy
{

  public ItpReconcilingStrategy(AbstractEditor editor)
  {
    super(editor);
  }

  protected CommonParser getParser()
  {
    return new ItpParser();
  }

  protected CommonFoldingStructureProvider getFoldingStructureProvider(AbstractEditor editor)
  {
    return new ItpFoldingStructureProvider(editor);
  }

}
