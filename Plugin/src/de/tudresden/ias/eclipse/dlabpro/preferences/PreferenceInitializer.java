/*
 * Created on 12.04.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

package de.tudresden.ias.eclipse.dlabpro.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.VisPrintDialog;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.preferences.VisEditorPreferencePage;

/**
 * this class inherits abstract class <code>AbstractPreferenceInitializer</code> and initializes
 * the default preferences
 * 
 * @author Xian
 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{

  public void initializeDefaultPreferences()
  {
    final IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();
    
    GeneralPreferencePage       .defineDefaults(iStore);
    ScriptEditorsPreferencePage .defineDefaults(iStore);
    ScriptFoldingPreferencePage .defineDefaults(iStore);
    ScriptColoringPreferencePage.initializeDefaults(iStore);
    VisEditorPreferencePage     .defineDefaults(iStore);
    VisPrintDialog              .defineDefaults(iStore);

  }

}
