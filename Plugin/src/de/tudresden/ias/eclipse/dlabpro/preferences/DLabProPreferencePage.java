
package de.tudresden.ias.eclipse.dlabpro.preferences;

import java.lang.reflect.Constructor;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;

/**
 * Abstract preference page for the dLabPro plug-in providing a basic life cycle.
 * @author Matthias Wolff
 */
public abstract class DLabProPreferencePage extends PreferencePage implements
    IWorkbenchPreferencePage, IPreferenceConstants
{

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  public void init(IWorkbench workbench)
  {
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
   */
  protected Control createContents(Composite iParent)
  {
    Composite iBody = new Composite(iParent, SWT.NULL);
    GridLayout iGl = new GridLayout(1,false);
    iGl.marginHeight = 0;
    iGl.marginWidth = 0;
    iBody.setLayout(iGl);
    Link iDescr = new Link(iBody,SWT.NULL);
    GridData iGd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    iGd.widthHint = 150;
    iDescr.setLayoutData(iGd);
    iDescr.addListener(SWT.Selection, new Listener()
    {
      public void handleEvent(Event event)
      {
        PreferencesUtil.createPreferenceDialogOn(getShell(),event.text,null,null);
      }
    });
    Label iSep = (Label)createControl(Label.class,iBody,SWT.NULL,1);
    createControls(iBody);
    String sDescr = getDescription();
    if (sDescr!=null && sDescr.length()>0)
      iDescr.setText(sDescr);
    else
    {
      iDescr.dispose();
      iSep.dispose();
    }
    initializeValues(doGetPreferenceStore());
    return iBody;
  }

  /**
   * Creates controls representing and editing preference values.
   * @param iParent the parent of the controls
   */
  protected abstract void createControls(Composite iParent);
  
  // -- Controls --
  
  /**
   * Creates a control an sets the default grid layout data.
   * 
   * @param cControl the class of the new control
   * @param iParent  the parent of the new control
   * @param nStyle   the control style
   * @param nColSpan the number of columns to span
   * @param nCols    the number of columns if the control is a composite
   */
  @SuppressWarnings("unchecked")
  protected final Control createControl(Class cControl, Composite iParent, int nStyle, int nColSpan, int nCols)
  {
    try
    {
      Constructor mConstr = cControl
          .getDeclaredConstructor(new Class[]{Composite.class,int.class});
      Control iCtl = (Control)mConstr.newInstance(new Object[]{iParent,nStyle});
      if (iCtl instanceof Composite)
      {
        GridLayout iGl = new GridLayout();
        iGl.numColumns = nCols;
        ((Composite)iCtl).setLayout(iGl);
        GridData iGd = new GridData();
        iGd.verticalAlignment = GridData.FILL;
        iGd.horizontalAlignment = GridData.FILL;
        iGd.grabExcessHorizontalSpace = true;
        iCtl.setLayoutData(iGd);
      }
      else
      {
        GridData iGd = new GridData();
        iGd.horizontalSpan = nColSpan;
        iGd.horizontalAlignment = GridData.FILL;
        iCtl.setLayoutData(iGd);
      }
      return iCtl;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Creates a control an sets the default grid layout data.
   * 
   * @param cControl the class of the new control
   * @param iParent  the parent of the new control
   * @param nStyle   the control style
   * @param nColSpan the number of columns to span
   */
@SuppressWarnings("unchecked")
  protected final Control createControl(Class cControl, Composite iParent, int nStyle, int nColSpan)
  {
    return createControl(cControl,iParent,nStyle,nColSpan,1);
  }
   
  // -- User Interaction --
  
  /* 
   * (non-Javadoc)
   * @see org.eclipse.jface.preference.PreferencePage#performOk()
   */
  public boolean performOk()
  {
    storeValues(doGetPreferenceStore());
    DLabProPlugin.getDefault().savePluginPreferences();
    return super.performOk();
  }

  /* 
   * (non-Javadoc)
   * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
   */
  protected void performDefaults()
  {
    super.performDefaults();
    initializeDefaults(doGetPreferenceStore());
  }

  // -- Preference Store -- 
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
   */
  protected IPreferenceStore doGetPreferenceStore()
  {
    return DLabProPlugin.getDefault().getPreferenceStore();
  }

  /** 
   * These settings will show up when Preference dialog opens up for the first
   * time. Derived classes should override this method in order to implement
   * their default preferences.
   * 
   * @param iStore the preference store 
   */
  static void defineDefaults(IPreferenceStore iStore)
  {
  }
  
  /**
   * Initializes the controls page with the values in the preference store.
   * 
   * @param iStore the preference store 
   */
  protected abstract void initializeValues(IPreferenceStore iStore);

  /**
   * Initializes the controls page with the default values in the preference
   * store.
   * 
   * @param iStore the preference store 
   */
  protected abstract void initializeDefaults(IPreferenceStore iStore);
  
  /**
   * Stores the contents of the controls into the preference store.
   * 
   * @param iStore the preference store 
   */
  protected abstract void storeValues(IPreferenceStore iStore);
  
}
