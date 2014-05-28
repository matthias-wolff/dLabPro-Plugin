package de.tudresden.ias.eclipse.dlabpro.actions.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor;

public class FoldingCollapseAllAction implements IEditorActionDelegate
{
  public AbstractEditor m_iEditor = null;
  
  public void setActiveEditor(IAction action, IEditorPart targetEditor)
  {
    try
    {
      m_iEditor = (AbstractEditor)targetEditor;
    }
    catch (ClassCastException e) {}
  }
  
  public void run(IAction action)
  {
    m_iEditor.doFolding(0);
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
  }

}
