
package de.tudresden.ias.eclipse.dlabpro.editors.def;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonFoldingStructureProvider;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.Segment;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.SnippetSegment;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * this class inherits from {@link de.tudresden.ias.eclipse.dlabpro.editors.CommonFoldingStructureProvider} and handles
 * folding for def-files
 * 
 * @author Christian Feig
 * 
 */
public class DefFoldingStructureProvider extends CommonFoldingStructureProvider
{

  /**
   * Constructor
   * 
   * @param editor
   */
  public DefFoldingStructureProvider(AbstractEditor editor)
  {
    super(editor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.tudresden.ias.eclipse.dlabpro.editors.CommonFoldingStructureProvider#isInitiallyFolded(de.tudresden.ias.eclipse
   * .dlabpro.editors.CommonElement)
   */
  protected boolean isInitiallyFolded(CommonElement element)
  {
    IPreferenceStore store = DLabProPlugin.getDefault().getPreferenceStore();

    if (element instanceof Segment)
    {
      if (element.getName().endsWith("MAN")
          && store.getBoolean(IPreferenceConstants.P_FLD_DEFMAN)) { return true; }
      if (element instanceof Segment
          && element.getName().endsWith("CODE")
          && store.getBoolean(IPreferenceConstants.P_FLD_DEFCODE)) { return true; }
    }
    return false;
  }

}
