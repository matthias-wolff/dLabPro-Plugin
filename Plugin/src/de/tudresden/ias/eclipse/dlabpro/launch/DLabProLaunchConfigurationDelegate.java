package de.tudresden.ias.eclipse.dlabpro.launch;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import de.tudresden.ias.eclipse.dlabpro.utils.LaunchUtil;

public class DLabProLaunchConfigurationDelegate implements
    ILaunchConfigurationDelegate, IDlabProLaunchConfigurationConstants
{

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration,
   * java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
   */
  public void launch(ILaunchConfiguration configuration, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException
  {
    // Get parameters form configuration
    File              iExe      = getExe    (configuration);
    File              iScr      = getScript (configuration);
    ArrayList<String> lsCmdLine = getArgs   (configuration);
    File              iHome     = getWorkDir(configuration);
    
    // Do launch
    try
    {
      LaunchUtil.launchDlabpro(iExe,iScr,lsCmdLine,iHome,launch);
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
  }

  public static File getExe(ILaunchConfiguration iCfg)
  {
    try
    {
      return new File(iCfg.getAttribute(LA_EXE,""));
    }
    catch (CoreException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  public static File getScript(ILaunchConfiguration iCfg)
  {
    try
    {
      String sScr = iCfg.getAttribute(LA_SCRIPT,""); 
      IFile iScr = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(sScr));
      return iScr.getLocation().toFile();
    }
    catch (IllegalArgumentException e)
    {
      return null;
    }
    catch (CoreException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  public static File getWorkDir(ILaunchConfiguration iCfg)
  {
    try
    {
      String sWorkDir = iCfg.getAttribute(LA_WORKDIR,""); 
      return new File(sWorkDir);
    }
    catch (CoreException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public static ArrayList<String> getArgs(ILaunchConfiguration iCfg)
  {
    try
    {
      String sExeArgs = iCfg.getAttribute(LA_EXE_ARGS,"");
      ArrayList<String> lsArgs = LaunchUtil.parseCommandLine(sExeArgs);
      lsArgs.addAll(iCfg.getAttribute(LA_SCRIPT_ARGS,new ArrayList<String>()));
      return lsArgs;
    }
    catch (CoreException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
}
