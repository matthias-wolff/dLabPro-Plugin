package de.tudresden.ias.eclipse.dlabpro.editors.vis.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

public class AutoRefreshAction extends AbstractVisEditorActionDelegate
{

  public void run(IAction action)
  {
    mEditor.setAutoRefresh(!mEditor.getAutoRefresh());
  }

  public void setActiveEditor(IAction action, IEditorPart targetEditor)
  {
    super.setActiveEditor(action,targetEditor);
    if (mEditor!=null)
      action.setChecked(mEditor.getAutoRefresh());
  }  
  
  public void selectionChanged(IAction action, ISelection selection)
  {
  }

}
