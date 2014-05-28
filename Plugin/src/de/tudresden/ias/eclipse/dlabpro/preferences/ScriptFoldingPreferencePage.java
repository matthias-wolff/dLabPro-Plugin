package de.tudresden.ias.eclipse.dlabpro.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class ScriptFoldingPreferencePage extends DLabProPreferencePage
{

  private Button iBtnEnable;
  private Button iBtnJavadoc;
  private Button iBtnFunction;
  private Button iBtnManual;
  private Button iBtnCode;
  
  @Override
  protected void createControls(Composite iParent)
  {
    iBtnEnable = (Button)createControl(Button.class,iParent,SWT.CHECK,1);
    iBtnEnable.setText("Enable folding");
    iBtnEnable.setSelection(true);
    iBtnEnable.setEnabled(false);
    createControl(Label.class,iParent,SWT.NULL,1);
    Label iLabel = (Label)createControl(Label.class,iParent,SWT.NULL,1);
    iLabel.setText("Initially fold these elements:");
    iBtnJavadoc = (Button)createControl(Button.class,iParent,SWT.CHECK,1);
    iBtnJavadoc.setText("Javadoc");
    iBtnFunction = (Button)createControl(Button.class,iParent,SWT.CHECK,1);
    iBtnFunction.setText("Functions");
    iBtnManual = (Button)createControl(Button.class,iParent,SWT.CHECK,1);
    iBtnManual.setText("Manual snippets");
    iBtnCode = (Button)createControl(Button.class,iParent,SWT.CHECK,1);
    iBtnCode.setText("Code snippets");
  }

  @Override
  protected void initializeDefaults(IPreferenceStore iStore)
  {
    iBtnJavadoc .setSelection(iStore.getDefaultBoolean(P_FLD_XTPJAVADOC ));
    iBtnFunction.setSelection(iStore.getDefaultBoolean(P_FLD_XTPFUNCTION));
    iBtnManual  .setSelection(iStore.getDefaultBoolean(P_FLD_DEFMAN     ));
    iBtnCode    .setSelection(iStore.getDefaultBoolean(P_FLD_DEFCODE    ));
  }

  @Override
  protected void initializeValues(IPreferenceStore iStore)
  {
    iBtnJavadoc .setSelection(iStore.getBoolean(P_FLD_XTPJAVADOC ));
    iBtnFunction.setSelection(iStore.getBoolean(P_FLD_XTPFUNCTION));
    iBtnManual  .setSelection(iStore.getBoolean(P_FLD_DEFMAN     ));
    iBtnCode    .setSelection(iStore.getBoolean(P_FLD_DEFCODE    ));
  }

  @Override
  protected void storeValues(IPreferenceStore iStore)
  {
    iStore.setValue(P_FLD_XTPJAVADOC ,iBtnJavadoc .getSelection());
    iStore.setValue(P_FLD_XTPFUNCTION,iBtnFunction.getSelection());
    iStore.setValue(P_FLD_DEFMAN     ,iBtnManual  .getSelection());
    iStore.setValue(P_FLD_DEFCODE    ,iBtnCode    .getSelection());
  }

  public static void defineDefaults(IPreferenceStore iStore)
  {
    iStore.setDefault(P_FLD_XTPJAVADOC , true );
    iStore.setDefault(P_FLD_XTPFUNCTION, false);
    iStore.setDefault(P_FLD_DEFMAN     , true );
    iStore.setDefault(P_FLD_DEFCODE    , true );
  }
}
