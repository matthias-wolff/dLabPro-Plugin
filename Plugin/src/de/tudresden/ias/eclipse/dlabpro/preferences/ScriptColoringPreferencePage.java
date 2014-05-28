
package de.tudresden.ias.eclipse.dlabpro.preferences;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into
 * JFace that allows us to create a page that is small and knows how to save, restore and apply
 * itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 */

public class ScriptColoringPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage, IPreferenceConstants
{

  public ScriptColoringPreferencePage()
  {
    super(GRID);
    setPreferenceStore(DLabProPlugin.getDefault().getPreferenceStore());
    initializeDefaults(getPreferenceStore());
  }

  /**
   * Sets the default values of the preferences.
   */
  public static void initializeDefaults(IPreferenceStore store)
  {
    PreferenceConverter.setDefault(store, P_CLR_COMMENT, new RGB(63,127,95));
    store.setDefault(P_CLR_COMMENT + P_SUFFIX_BOLD, false);
    store.setDefault(P_CLR_COMMENT + P_SUFFIX_ITALIC, false);

    PreferenceConverter.setDefault(store, P_CLR_STRING, new RGB(42,0,255));
    store.setDefault(P_CLR_STRING + P_SUFFIX_BOLD, false);
    store.setDefault(P_CLR_STRING + P_SUFFIX_ITALIC, false);

    PreferenceConverter.setDefault(store, P_CLR_CONSTANTS,  new RGB(32,0,255));
    store.setDefault(P_CLR_CONSTANTS + P_SUFFIX_BOLD, false);
    store.setDefault(P_CLR_CONSTANTS + P_SUFFIX_ITALIC, false);

    PreferenceConverter.setDefault(store, P_CLR_PREPROC, new RGB(0,150,0));
    store.setDefault(P_CLR_PREPROC + P_SUFFIX_BOLD, true);
    store.setDefault(P_CLR_PREPROC + P_SUFFIX_ITALIC, false);

    PreferenceConverter.setDefault(store, P_CLR_KEYWORD, new RGB(127,0,85));
    store.setDefault(P_CLR_KEYWORD + P_SUFFIX_BOLD, true);
    store.setDefault(P_CLR_KEYWORD + P_SUFFIX_ITALIC, false);

    PreferenceConverter.setDefault(store, P_CLR_FORMULA, new RGB(100,0,200));
    store.setDefault(P_CLR_FORMULA + P_SUFFIX_BOLD, false);
    store.setDefault(P_CLR_FORMULA + P_SUFFIX_ITALIC, true);
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
   * manipulate various types of preferences. Each field editor knows how to save and restore
   * itself.
   */
  public void createFieldEditors()
  {
    // The head line
    Link iDescr = new Link(getFieldEditorParent(),SWT.NULL);
    GridData iGd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    iGd.horizontalSpan = 4;
    iGd.widthHint = 150;
    iDescr.setLayoutData(iGd);
    iDescr.setText("Some colors and the font can be configured on the <A "
        + "href=\"org.eclipse.ui.preferencePages.GeneralTextEditor\">Text "
        +" Editors</A> preference page.\n");
    iDescr.addListener(SWT.Selection, new Listener()
    {
      public void handleEvent(Event event)
      {
        PreferencesUtil.createPreferenceDialogOn(getShell(),event.text,null,null);
      }
    });
    
    // The field editors
    addField(new DLabProColorFieldEditor(P_CLR_COMMENT  , "Comments" , getFieldEditorParent()));
    addField(new DLabProColorFieldEditor(P_CLR_STRING   , "Strings"  , getFieldEditorParent()));
    addField(new DLabProColorFieldEditor(P_CLR_CONSTANTS, "Constants", getFieldEditorParent()));
    addField(new PreprocessorColorFieldEditor(P_CLR_PREPROC, "Preprocessor Instructions",
        getFieldEditorParent()));
    addField(new DLabProColorFieldEditor(P_CLR_KEYWORD  , "Keywords" , getFieldEditorParent()));
    addField(new DLabProColorFieldEditor(P_CLR_FORMULA  , "Formulas" , getFieldEditorParent()));
  }

  public void init(IWorkbench workbench)
  {
  }

  private class DLabProColorFieldEditor extends ColorFieldEditor
  {
    @SuppressWarnings("unused")
    String name;
    Button boldButton;
    Button italicButton;

    public DLabProColorFieldEditor(String name, String labelText, Composite parent)
    {
      super(name, labelText, parent);
      this.name = name;
    }

    public int getNumberOfControls()
    {
      return 4;
    }

    protected void doFillIntoGrid(Composite parent, int numColumns)
    {
      Control control = getLabelControl(parent);
      GridData gd = new GridData();
      // gd.horizontalSpan = numColumns - 1;
      control.setLayoutData(gd);

      Button colorButton = getChangeControl(parent);
      gd = new GridData();
      int widthHint = convertHorizontalDLUsToPixels(colorButton, IDialogConstants.BUTTON_WIDTH);
      gd.widthHint = Math.max(widthHint, colorButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
      colorButton.setLayoutData(gd);
      italicButton = new Button(parent, SWT.CHECK);
      italicButton.setLayoutData(new GridData());
      italicButton.setText("italic");
      boldButton = new Button(parent, SWT.CHECK);
      boldButton.setLayoutData(new GridData());
      boldButton.setText("bold");
    }

    protected void adjustForNumColumns(int numColumns)
    {
    }

    protected void doLoad()
    {
      if (getColorSelector() == null || boldButton == null || italicButton == null) return;
      getColorSelector().setColorValue(
          PreferenceConverter.getColor(getPreferenceStore(), getPreferenceName()));
      boldButton.setSelection(getPreferenceStore().getBoolean(getPreferenceName() + P_SUFFIX_BOLD));
      italicButton.setSelection(getPreferenceStore()
          .getBoolean(getPreferenceName() + P_SUFFIX_ITALIC));
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected void doLoadDefault()
    {
      if (getColorSelector() == null) return;
      getColorSelector().setColorValue(
          PreferenceConverter.getDefaultColor(getPreferenceStore(), getPreferenceName()));
      boldButton.setSelection(getPreferenceStore().getDefaultBoolean(
          getPreferenceName() + P_SUFFIX_BOLD));
      italicButton.setSelection(getPreferenceStore().getDefaultBoolean(
          getPreferenceName() + P_SUFFIX_ITALIC));
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected void doStore()
    {
      super.doStore();
      getPreferenceStore().setValue(getPreferenceName() + P_SUFFIX_BOLD, boldButton.getSelection());
      getPreferenceStore().setValue(getPreferenceName() + P_SUFFIX_ITALIC,
          italicButton.getSelection());
    }

    public void store()
    {
      if (getPreferenceStore() == null) return;

      if (presentsDefaultValue())
      {
        getPreferenceStore().setToDefault(getPreferenceName() + P_SUFFIX_BOLD);
        getPreferenceStore().setToDefault(getPreferenceName() + P_SUFFIX_ITALIC);
        super.store();
      }
      else doStore();
    }

  }

  private class PreprocessorColorFieldEditor extends DLabProColorFieldEditor
  {

    /**
     * @param name
     * @param labelText
     * @param parent
     */
    public PreprocessorColorFieldEditor(String name, String labelText, Composite parent)
    {
      super(name, labelText, parent);
    }

    protected void doStore()
    {
      PreferenceConverter.setValue(getPreferenceStore(), PREPROCESSOR_WITH_LEADING$,
          getColorSelector().getColorValue());
      getPreferenceStore().setValue(PREPROCESSOR_WITH_LEADING$ + P_SUFFIX_BOLD,
          boldButton.getSelection());
      getPreferenceStore().setValue(PREPROCESSOR_WITH_LEADING$ + P_SUFFIX_ITALIC,
          italicButton.getSelection());
      PreferenceConverter.setValue(getPreferenceStore(), PREPROCESSOR_WITH_BRACKETS,
          getColorSelector().getColorValue());
      getPreferenceStore().setValue(PREPROCESSOR_WITH_BRACKETS + P_SUFFIX_BOLD,
          boldButton.getSelection());
      getPreferenceStore().setValue(PREPROCESSOR_WITH_BRACKETS + P_SUFFIX_ITALIC,
          italicButton.getSelection());
      super.doStore();
    }
  }
}