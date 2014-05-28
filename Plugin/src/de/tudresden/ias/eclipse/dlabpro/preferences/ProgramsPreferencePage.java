
package de.tudresden.ias.eclipse.dlabpro.preferences;

import java.util.Vector;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.utils.LaunchUtil;

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

public class ProgramsPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage, IPreferenceConstants
{

  Vector<FieldEditor> m_iFields = null;

  public ProgramsPreferencePage()
  {
    super(GRID);
    setPreferenceStore(DLabProPlugin.getDefault().getPreferenceStore());
    DLabProPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    m_iFields = new Vector<FieldEditor>();
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
   * manipulate various types of preferences. Each field editor knows how to save and restore
   * itself.
   * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
   */
  public void createFieldEditors()
  {
    FieldEditor iFe;
    GridData    iGd;
    Composite   iTop = getFieldEditorParent();
    Label       iLab;

    // dLabPro Field Editor Group
    iLab = new Label(getFieldEditorParent(),SWT.NULL);
    iGd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    iGd.horizontalSpan = 3;
    iGd.widthHint = 150;
    iLab.setLayoutData(iGd);
    iLab.setText("\ndLabPro:");
      
    iFe = new DirectoryFieldEditor(P_PRG_DLPHOME,"     Home directory",iTop);
    addField(iFe);
    m_iFields.add(iFe);

    iFe = new DirectoryFieldEditor(P_PRG_DLPDOC,"     Manual directory",iTop);
    addField(iFe);
    m_iFields.add(iFe);

    iFe = new FileFieldEditor(P_PRG_DLPEXE,"     Executable",iTop);
    addField(iFe);
    m_iFields.add(iFe);

    iFe = new StringFieldEditor(P_PRG_DLPARGS,"     - Default arguments",iTop);
    addField(iFe);
    m_iFields.add(iFe);

    // CGen Field Editor Group
    iLab = new Label(getFieldEditorParent(),SWT.NULL);
    iGd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    iGd.horizontalSpan = 3;
    iGd.widthHint = 150;
    iLab.setLayoutData(iGd);
    iLab.setText("\nCGen:");

    iFe = new FileFieldEditor(P_PRG_CGENEXE,"     Executable",
        getFieldEditorParent());
    addField(iFe);
    m_iFields.add(iFe);

    iFe = new StringFieldEditor(P_PRG_CGENARGS,"     - Default arguments",
        getFieldEditorParent());
    addField(iFe);
    m_iFields.add(iFe);

    // UASR Field Editor Group
    iLab = new Label(getFieldEditorParent(),SWT.NULL);
    iGd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    iGd.horizontalSpan = 3;
    iGd.widthHint = 150;
    iLab.setLayoutData(iGd);
    iLab.setText("\nUASR:");

    iFe = new DirectoryFieldEditor(P_PRG_UASRHOME,"     Home directory",
        getFieldEditorParent());
    addField(iFe);
    m_iFields.add(iFe);

    iFe = new DirectoryFieldEditor(P_PRG_UASRDOC,"     Manual directory",
        getFieldEditorParent());
    addField(iFe);
    m_iFields.add(iFe);

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  public void init(IWorkbench workbench)
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent event)
  {
    super.propertyChange(event);
  }

  
  /* (non-Javadoc)
   * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performDefaults()
   */
  protected void performDefaults()
  {
    super.performDefaults();
    
    LaunchUtil.getDlabproExe(true).getAbsolutePath();
    LaunchUtil.getCgenExe(true).getAbsolutePath();
    LaunchUtil.getUasrHome(true).getAbsolutePath();
    
    for (int i=0; i<m_iFields.size(); i++)
      ((FieldEditor)m_iFields.get(i)).load();
  }

}