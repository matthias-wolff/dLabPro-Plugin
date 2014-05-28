package de.tudresden.ias.eclipse.dlabpro.actions.console;

import java.io.IOException;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.action.IAction;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;

public class StepConsoleViewActionDelegate extends
    AbstractConsoleViewActionDelegate
{

  public boolean getEnabled()
  {
    if (getAction()==null) return false;
    getAction().setToolTipText("Step");

    IProcess iProc = getProcess();
    if (iProc==null) return false;
    String sCommand = iProc.getAttribute(IProcess.ATTR_CMDLINE);
    if (sCommand==null) return false;
    if (sCommand.indexOf("--in-IDE")<0)
    {
      getAction().setToolTipText("Step (Disabled because dLabPro was " +
          "started without option --in-IDE)");
      return false;
    }
    return iProc.canTerminate();
  }

  public String getId()
  {
    return "dLabPro Plugin.StepConsoleViewAction";
  }

  public void run(IAction action)
  {
    if (getProcess()==null) return;
    try
    {
      getProcess().getStreamsProxy().write("\nstep\n");
    }
    catch (IOException e)
    {
      VIS.EXCEPTION(e);
    }
  }

}
