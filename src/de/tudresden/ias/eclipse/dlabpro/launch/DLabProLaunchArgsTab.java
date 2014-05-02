package de.tudresden.ias.eclipse.dlabpro.launch;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.properties.XtpFileProperties;
import de.tudresden.ias.eclipse.dlabpro.properties.XtpFilePropertiesEditor;
import de.tudresden.ias.eclipse.dlabpro.utils.WorkbenchUtil;

public class DLabProLaunchArgsTab extends AbstractLaunchConfigurationTab
  implements IDlabProLaunchConfigurationConstants
{

  private XtpFilePropertiesEditor iEdi;
  private XtpFileProperties       iPrp;
  private Image                   iImage;

  /**
   * Constructs a new "Arguments" tab for configuring a dLabPro launch
   * configuration.
   */
  public DLabProLaunchArgsTab()
  {
    super();
    iImage = DLabProPlugin.loadIconImage("icons/obj16/args_tab.gif");
    iPrp = new XtpFileProperties(null,false);
  }

  /* (non-Javadoc)
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#dispose()
   */
  @Override
  public void dispose()
  {
    if (iImage!=null) iImage.dispose();
    super.dispose();
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
   */
  public void createControl(Composite parent)
  {
    try
    {
      iEdi = new XtpFilePropertiesEditor(parent,SWT.NONE,iPrp);
      iEdi.addModifyListener(new ModifyListener()
      {
        public void modifyText(ModifyEvent e)
        {
          updateLaunchConfigurationDialog();
        }
      });
      setControl(iEdi);
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
   */
  public String getName()
  {
    return "Arguments";
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
   */
  @Override
  public Image getImage()
  {
    return iImage;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getMessage()
   */
  @Override
  public String getMessage()
  {
    return "Run a dLabPro script";
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
   */
  public boolean isValid(ILaunchConfiguration launchConfig)
  {
    // TODO: do something useful!
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getErrorMessage()
   */
  public String getErrorMessage()
  {
    // TODO: do something useful!
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
   */
  public void initializeFrom(ILaunchConfiguration iCfg)
  {
    try
    {
      String sScript = iCfg.getAttribute(LA_SCRIPT,"");
      if (sScript==null) sScript = "";
      IFile  fScript = WorkbenchUtil.getIFileFromFilename(sScript); 
      iPrp = new XtpFileProperties(fScript,true);
      iPrp.setWorkDir(iCfg.getAttribute(LA_WORKDIR,""));
      iPrp.setAutoWorkDir(iCfg.getAttribute(LA_WORKDIR_USEDEF,true));
    }
    catch (CoreException e)
    {
      e.printStackTrace();
    }
    iEdi.setProperties(iPrp);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
   */
  public void performApply(ILaunchConfigurationWorkingCopy iCfg)
  {
    iPrp = iEdi.getProperties();
    iPrp.store();
    iCfg.setAttribute(LA_WORKDIR       ,iPrp.getWorkDir()   );
    iCfg.setAttribute(LA_WORKDIR_USEDEF,iPrp.isAutoWorkDir());
    iCfg.setAttribute(LA_SCRIPT_ARGS   ,iPrp.getArgs()      );
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
   */
  public void setDefaults(ILaunchConfigurationWorkingCopy iCfg)
  {
    iCfg.setAttribute(LA_WORKDIR       ,""                     );
    iCfg.setAttribute(LA_WORKDIR_USEDEF,true                   );
    iCfg.setAttribute(LA_SCRIPT_ARGS   ,new ArrayList<String>());
  }

  /**
   * Returns the main tab in this launch configuration tab group.
   */
  protected DLabProLaunchMainTab getMainTab()
  {
    for (ILaunchConfigurationTab iTab : getLaunchConfigurationDialog().getTabs())
      if (iTab instanceof DLabProLaunchMainTab)
        return (DLabProLaunchMainTab)iTab;
    return null;
  }
  
}
