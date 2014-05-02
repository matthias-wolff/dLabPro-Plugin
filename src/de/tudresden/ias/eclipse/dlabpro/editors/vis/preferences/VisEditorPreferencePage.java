
package de.tudresden.ias.eclipse.dlabpro.editors.vis.preferences;

import javax.sound.sampled.Mixer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VisColorManager;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.VisEditor;
import de.tudresden.ias.eclipse.dlabpro.preferences.DLabProPreferencePage;
import de.tudresden.ias.eclipse.dlabpro.utils.SoundUtil;
import de.tudresden.ias.eclipse.dlabpro.utils.WorkbenchUtil;

public class VisEditorPreferencePage extends DLabProPreferencePage implements
    IVisEditorPreferenceConstants
{

  Combo  iCompColorTab;
  Combo  iValueColorTab;
  Combo  i3DPerspective;
  Text   i3DYaw;
  Label  i3DYawLab;
  Text   i3DPitch;
  Label  i3DPitchLab;
  Label  i3DPitchLab2;
  Combo  i3DSurface;
  Text   i3DTransparency;
  Button iSpecLab;
  Combo  iSndPlayDev;
  
  @Override
  protected void createControls(Composite iParent)
  {
    Group    iGrp;
    Label    iLab;
    GridData iGd;
    int      i;
    String[] asColorTables;
    
    setDescription("Data display settings.");

    iGrp = (Group)createControl(Group.class,iParent,SWT.NONE,1,2);
    iGrp.setText("Default Color Tables");
    iLab = (Label)createControl(Label.class,iGrp,SWT.NULL,1);
    iLab.setText("Oscillogram / Bar Diagram");
    iCompColorTab = (Combo)createControl(Combo.class,iGrp,SWT.READ_ONLY,1);
    asColorTables = VisColorManager.getCompColorTables();
    for (i=0; i<asColorTables.length; i++) iCompColorTab.add(asColorTables[i]);
    iLab = (Label)createControl(Label.class,iGrp,SWT.NULL,1);
    iLab.setText("Spectrogram / 3D-View");
    iValueColorTab = (Combo)createControl(Combo.class,iGrp,SWT.READ_ONLY,1);
    asColorTables = VisColorManager.getValueColorTables();
    for (i=0; i<asColorTables.length; i++) iValueColorTab.add(asColorTables[i]);

    iGrp = (Group)createControl(Group.class,iParent,SWT.NONE,1,2);
    iGrp.setText("Spectrogram Settings");
    iSpecLab = (Button)createControl(Button.class,iGrp,SWT.CHECK,2);
    iSpecLab.setText(" Show data values (space permitting)");

    iGrp = (Group)createControl(Group.class,iParent,SWT.NONE,1,7);
    iGrp.setText("3D-View Settings");
    iLab = (Label)createControl(Label.class,iGrp,SWT.NULL,1);
    iLab.setText("Perspective");
    i3DPerspective = new Combo(iGrp,SWT.READ_ONLY);
    i3DPerspective.setLayoutData(new GridData(SWT.DEFAULT,SWT.DEFAULT));
    i3DPerspective.add("Parallel"                      ); 
    i3DPerspective.add("Bird's-eye view (user defined)"); 
    i3DPerspective.add("Top"                           ); 
    i3DPerspective.add("Front"                         ); 
    i3DPerspective.add("Left"                          ); 
    i3DPerspective.add("Rear"                          ); 
    i3DPerspective.add("Right"                         ); 
    i3DPerspective.add("Bird's-eye view N"             ); 
    i3DPerspective.add("Bird's-eye view NE"            ); 
    i3DPerspective.add("Bird's-eye view E"             ); 
    i3DPerspective.add("Bird's-eye view SE"            ); 
    i3DPerspective.add("Bird's-eye view S"             ); 
    i3DPerspective.add("Bird's-eye view SW"            ); 
    i3DPerspective.add("Bird's-eye view W"             ); 
    i3DPerspective.add("Bird's-eye view NW"            );
    i3DPerspective.setVisibleItemCount(15);
    i3DPerspective.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        boolean bEnable = i3DPerspective.getSelectionIndex()==1;
        i3DYawLab   .setEnabled(bEnable);
        i3DYaw      .setEnabled(bEnable);
        i3DPitchLab .setEnabled(bEnable);
        i3DPitch    .setEnabled(bEnable);
        i3DPitchLab2.setEnabled(bEnable);
      }
    });
    i3DYawLab = (Label)createControl(Label.class,iGrp,SWT.NULL,1);
    i3DYawLab.setText("   Yaw");
    i3DYaw = new Text(iGrp,SWT.BORDER|SWT.SINGLE|SWT.RESIZE);
    i3DYaw.setLayoutData(new GridData(30,SWT.DEFAULT));
    i3DPitchLab = (Label)createControl(Label.class,iGrp,SWT.NULL,1);
    i3DPitchLab.setText("° Pitch");
    i3DPitch = new Text(iGrp,SWT.BORDER|SWT.SINGLE|SWT.RESIZE);
    i3DPitch.setLayoutData(new GridData(30,SWT.DEFAULT));
    i3DPitchLab2 = (Label)createControl(Label.class,iGrp,SWT.NULL,1);
    i3DPitchLab2.setText("°");
    iLab = (Label)createControl(Label.class,iGrp,SWT.NULL,1);
    iLab.setText("Surface options");
    i3DSurface = new Combo(iGrp,SWT.READ_ONLY);
    iGd = new GridData(SWT.DEFAULT,SWT.DEFAULT); iGd.horizontalSpan = 6;
    i3DSurface.setLayoutData(iGd);
    i3DSurface.add("Lines only");
    i3DSurface.add("Colors only");
    i3DSurface.add("Colors and black lines");
    i3DSurface.add("Colors and colored lines");
    iLab = (Label)createControl(Label.class,iGrp,SWT.NULL,1);
    iLab.setText("Transparency [%]");
    i3DTransparency = new Text(iGrp,SWT.SINGLE|SWT.BORDER|SWT.RIGHT);
    iGd = new GridData(20,SWT.DEFAULT); iGd.horizontalSpan = 6;
    i3DTransparency.setLayoutData(iGd);

    iGrp = (Group)createControl(Group.class,iParent,SWT.NONE,1,2);
    iGrp.setText("Audio settings");
    iLab = (Label)createControl(Label.class,iGrp,SWT.NULL,1);
    iLab.setText("Playback device");
    iSndPlayDev = new Combo(iGrp,SWT.READ_ONLY);
    iSndPlayDev.setLayoutData(new GridData(SWT.DEFAULT,SWT.DEFAULT));

    Mixer[] liPlayMixers = SoundUtil.getPlaybackMixers();
    for (i=0; i<liPlayMixers.length; i++)
      iSndPlayDev.add(liPlayMixers[i].getMixerInfo().getName());
  }

  @Override
  protected void initializeDefaults(IPreferenceStore iStore)
  {
    iCompColorTab.select(iStore.getDefaultInt(P_VIS_COMPCOLORTAB));
    iValueColorTab.select(iStore.getDefaultInt(P_VIS_VALUECOLORTAB));
    iSpecLab.setSelection(iStore.getDefaultBoolean(P_VIS_SPECLABEL));
    i3DPerspective.select(iStore.getDefaultInt(P_VIS_3DPERSPECTIVE));
    i3DYaw.setText(String.valueOf(iStore.getDefaultFloat(P_VIS_3DYAW)));
    i3DPitch.setText(String.valueOf(iStore.getDefaultFloat(P_VIS_3DPITCH)));
    i3DSurface.select(iStore.getDefaultInt(P_VIS_3DSURFACE));
    i3DTransparency.setText(String.valueOf(iStore.getDefaultInt(P_VIS_3DTRANSPARENCY)));
    iSndPlayDev.select(0);
  }

  @Override
  protected void initializeValues(IPreferenceStore iStore)
  {
    iCompColorTab.select(iStore.getInt(P_VIS_COMPCOLORTAB));
    iValueColorTab.select(iStore.getInt(P_VIS_VALUECOLORTAB));
    iSpecLab.setSelection(iStore.getBoolean(P_VIS_SPECLABEL));
    i3DPerspective.select(iStore.getInt(P_VIS_3DPERSPECTIVE));
    i3DYaw.setText(String.valueOf(iStore.getFloat(P_VIS_3DYAW)));
    i3DPitch.setText(String.valueOf(iStore.getFloat(P_VIS_3DPITCH)));
    i3DSurface.select(iStore.getInt(P_VIS_3DSURFACE));
    i3DTransparency.setText(String.valueOf(iStore.getInt(P_VIS_3DTRANSPARENCY)));
    
    int nMixer = iSndPlayDev.indexOf(iStore.getString(P_VIS_SNDPLAYDEV));
    iSndPlayDev.select(nMixer<0?0:nMixer);
  }

  @Override
  protected void storeValues(IPreferenceStore iStore)
  {
    iStore.setValue(P_VIS_COMPCOLORTAB ,iCompColorTab .getSelectionIndex());
    iStore.setValue(P_VIS_VALUECOLORTAB,iValueColorTab.getSelectionIndex());
    iStore.setValue(P_VIS_SPECLABEL    ,iSpecLab      .getSelection()     );
    iStore.setValue(P_VIS_3DPERSPECTIVE,i3DPerspective.getSelectionIndex());
    iStore.setValue(P_VIS_3DSURFACE    ,i3DSurface    .getSelectionIndex());
    iStore.setValue(P_VIS_SNDPLAYDEV   ,iSndPlayDev .getText()          );
    
    int nTransparency = 0;
    try
    {
      nTransparency = Integer.valueOf(i3DTransparency.getText());
      if (nTransparency<0  ) nTransparency = 0;
      if (nTransparency>100) nTransparency = 100;
    }
    catch (NumberFormatException e)
    {
    }
    iStore.setValue(P_VIS_3DTRANSPARENCY,nTransparency);

    float nYaw = iStore.getDefaultFloat(P_VIS_3DYAW);
    try
    {
      nYaw = Float.valueOf(i3DYaw.getText());
      if (nYaw<-180) nYaw=-180;
      if (nYaw> 180) nYaw= 180;
    }
    catch (NumberFormatException e)
    {
    }
    iStore.setValue(P_VIS_3DYAW,nYaw);

    float nPitch = iStore.getDefaultFloat(P_VIS_3DPITCH);
    try
    {
      nPitch = Float.valueOf(i3DPitch.getText());
      if (nPitch<-90) nPitch=-90;
      if (nPitch> 90) nPitch= 90;
    }
    catch (NumberFormatException e)
    {
    }
    iStore.setValue(P_VIS_3DPITCH,nPitch);
    
    // Apply to editors
    VisColorManager iCm = new VisColorManager();
    iCm.switchCompColors (iCompColorTab .getSelectionIndex());
    iCm.switchValueColors(iValueColorTab.getSelectionIndex());
    IEditorPart iEditor = WorkbenchUtil.getActiveEditor();
    
    if (iEditor instanceof VisEditor)
      ((VisEditor)iEditor).reload(((VisEditor)iEditor).getTransposed());
  }

  public static void defineDefaults(IPreferenceStore iStore)
  {
    iStore.setDefault(P_VIS_COMPCOLORTAB  , 0    );
    iStore.setDefault(P_VIS_VALUECOLORTAB , 0    );
    iStore.setDefault(P_VIS_SPECLABEL     , false);
    iStore.setDefault(P_VIS_3DPERSPECTIVE , 1    );
    iStore.setDefault(P_VIS_3DSURFACE     , 2    );
    iStore.setDefault(P_VIS_3DTRANSPARENCY, 15   );
    iStore.setDefault(P_VIS_3DYAW         ,-50.f );
    iStore.setDefault(P_VIS_3DPITCH       , 60.f );
    iStore.setDefault(P_VIS_SNDPLAYDEV    ,""    );
  }

}
