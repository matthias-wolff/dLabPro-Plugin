package de.tudresden.ias.eclipse.dlabpro.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ScriptEditorsPreferencePage extends DLabProPreferencePage
    implements IWorkbenchPreferencePage
{

  @Override
  protected void createControls(Composite parent)
  {
    setDescription("dLabPro editor preferences. Note that some preferences "
        + "may be set on the <A href=\"org.eclipse.ui.preferencePages."
        + "GeneralTextEditor\">Text Editors</A> preference page.");
  }

  @Override
  protected void initializeDefaults(IPreferenceStore store)
  {
  }

  @Override
  protected void initializeValues(IPreferenceStore store)
  {
  }

  @Override
  protected void storeValues(IPreferenceStore store)
  {
  }

  public static void defineDefaults(IPreferenceStore iStore)
  {
  }  
  
}
