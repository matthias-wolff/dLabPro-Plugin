
package de.tudresden.ias.eclipse.dlabpro.editors.vis.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import de.tucottbus.kt.jlab.datadisplays.utils.PlayActionUtil;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.VisEditor;

public class PlayAction extends AbstractVisEditorActionDelegate
{

  public PlayAction()
  {
    super();
  }
  
  public void run(IAction action)
  {
    if (mEditor!=null) mEditor.performPlay();
  }
  
  public void selectionChanged(IAction action, ISelection selection)
  {
    setActiveEditor(action,mEditor);
  }

  public void setActiveEditor(IAction action, IEditorPart targetEditor)
  {
    super.setActiveEditor(action,targetEditor);
    try
    {
      VisEditor iVis = (VisEditor)targetEditor;
      iVis.updatePlayAction(action);
    }
    catch (Exception e)
    {
      PlayActionUtil.setDisabled(action,"the current editor is not capable of playing");
    }
  }
  
}

// EOF
