package de.tudresden.ias.eclipse.dlabpro.editors.vis.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

public class ResetAction extends AbstractVisEditorActionDelegate
{

  public void run(IAction action)
  {
    if (mEditor != null)
      mEditor.layout(0);
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
  }

}
