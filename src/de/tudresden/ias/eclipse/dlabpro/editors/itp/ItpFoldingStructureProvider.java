
package de.tudresden.ias.eclipse.dlabpro.editors.itp;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonFoldingStructureProvider;
import de.tudresden.ias.eclipse.dlabpro.editors.itp.model.ItpElement;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * this class handles folding for xtp/?tp-files
 * 
 * @author Christian Feig
 * 
 */
public class ItpFoldingStructureProvider extends CommonFoldingStructureProvider
{

  public ItpFoldingStructureProvider(AbstractEditor editor)
  {
    super(editor);
  }

  /*
   * (non-Javadoc)
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonFoldingStructureProvider#isInitiallyFolded(de.tudresden.ias.eclipse.dlabpro.editors.CommonElement)
   */
  protected boolean isInitiallyFolded(CommonElement element)
  {
    IPreferenceStore store = DLabProPlugin.getDefault().getPreferenceStore();
    if (element instanceof ItpElement)
    {
      if (((ItpElement)element).getType() == ItpElement.TYPE_JAVADOC
          && store.getBoolean(IPreferenceConstants.P_FLD_XTPJAVADOC))
      {
        return true;
      }
      if (((ItpElement)element).getType() == ItpElement.TYPE_FUNCTION
          && store.getBoolean(IPreferenceConstants.P_FLD_XTPFUNCTION))
      {
        return true;
      }
    }
    return false;
  }

}
