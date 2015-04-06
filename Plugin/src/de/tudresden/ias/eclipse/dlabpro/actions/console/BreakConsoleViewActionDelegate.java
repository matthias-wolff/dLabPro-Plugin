package de.tudresden.ias.eclipse.dlabpro.actions.console;

import java.io.IOException;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.action.IAction;

import de.tucottbus.kt.jlab.datadisplays.utils.DdUtils;

public class BreakConsoleViewActionDelegate extends
    AbstractConsoleViewActionDelegate
{

  public String getId()
  {
    return "dLabPro Plugin.BreakConsoleViewAction";
  }

  public void run(IAction action)
  {
    if (getProcess()==null) return;
    try
    {
      getProcess().getStreamsProxy().write("\n");
    }
    catch (IOException e)
    {
      DdUtils.EXCEPTION(e);
    }
  }

  public boolean getEnabled()
  {
    if (getAction()==null) return false;
    getAction().setToolTipText("Break");
    
    IProcess iProc = getProcess();
    if (iProc==null) return false;
    String sCommand = iProc.getAttribute(IProcess.ATTR_CMDLINE);
    if (sCommand==null) return false;
    if (sCommand.indexOf("--in-IDE")<0)
    {
      getAction().setToolTipText("Break (Disabled because dLabPro was " +
          "started without option --in-IDE)");
      return false;
    }
    return iProc.canTerminate();
  }

}
