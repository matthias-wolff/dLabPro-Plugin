
package de.tudresden.ias.eclipse.dlabpro.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class GeneralPreferencePage extends DLabProPreferencePage
{

  private Button iBtnCnsRecycle;
  private Button iBtnCnsShow;
  private Button iBtnDlgRun;
  private Button iBtnDlgRerun;
  
  protected void createControls(Composite iParent)
  {
    setDescription("Settings for dLabPro / UASR development.");

    Group iGrp = (Group)createControl(Group.class,iParent,SWT.NONE,1);
    iGrp.setText("Console");
    iBtnCnsRecycle = (Button)createControl(Button.class,iGrp,SWT.CHECK,1);
    iBtnCnsRecycle.setText("Recycle console");
    iBtnCnsShow = (Button)createControl(Button.class,iGrp,SWT.CHECK,1);
    iBtnCnsShow.setText("Show console when a process is started");

    iGrp = (Group)createControl(Group.class,iParent,SWT.NONE,1);
    iGrp.setText("Prompt for arguments when ...");
    iBtnDlgRun = (Button)createControl(Button.class,iGrp,SWT.CHECK,1);
    iBtnDlgRun.setText("running a script");
    iBtnDlgRerun = (Button)createControl(Button.class,iGrp,SWT.CHECK,1);
    iBtnDlgRerun.setText("re-running a script");
  }

  protected void initializeValues(IPreferenceStore iStore)
  {
    iBtnCnsRecycle.setSelection(iStore.getBoolean(P_CNS_RECYCLE));
    iBtnCnsShow   .setSelection(iStore.getBoolean(P_CNS_SHOW   ));
    iBtnDlgRun    .setSelection(iStore.getBoolean(P_DLG_RUN    ));
    iBtnDlgRerun  .setSelection(iStore.getBoolean(P_DLG_RERUN  ));
  }
  
  protected void storeValues(IPreferenceStore iStore)
  {
    iStore.setValue(P_CNS_RECYCLE,iBtnCnsRecycle.getSelection());
    iStore.setValue(P_CNS_SHOW   ,iBtnCnsShow   .getSelection());
    iStore.setValue(P_DLG_RUN    ,iBtnDlgRun    .getSelection());
    iStore.setValue(P_DLG_RERUN  ,iBtnDlgRerun  .getSelection());
  }

  protected void initializeDefaults(IPreferenceStore iStore)
  {
    iBtnCnsRecycle.setSelection(iStore.getDefaultBoolean(P_CNS_RECYCLE));
    iBtnCnsShow   .setSelection(iStore.getDefaultBoolean(P_CNS_SHOW   ));
    iBtnDlgRun    .setSelection(iStore.getDefaultBoolean(P_DLG_RUN    ));
    iBtnDlgRerun  .setSelection(iStore.getDefaultBoolean(P_DLG_RERUN  ));
  }
  
  public static void defineDefaults(IPreferenceStore iStore)
  {
    iStore.setDefault(P_CNS_RECYCLE,true );
    iStore.setDefault(P_CNS_SHOW   ,true );
    iStore.setDefault(P_DLG_RUN    ,true );
    iStore.setDefault(P_DLG_RERUN  ,false);
  }
  
}
