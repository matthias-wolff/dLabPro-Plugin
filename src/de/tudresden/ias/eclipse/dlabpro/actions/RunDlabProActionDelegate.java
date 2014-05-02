package de.tudresden.ias.eclipse.dlabpro.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.tudresden.ias.eclipse.dlabpro.utils.LaunchUtil;

public class RunDlabProActionDelegate implements IWorkbenchWindowActionDelegate
{

  public void dispose()
  {
  }

  public void init(IWorkbenchWindow window)
  {
  }

  public void run(IAction action)
  {
    LaunchUtil.launchDlabpro(LaunchUtil.getDlabproExe(true),null,(String)null,null);
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
  }

}
