package de.tudresden.ias.eclipse.dlabpro.editors.vis.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.VisEditor;

public abstract class AbstractVisEditorActionDelegate implements
		IEditorActionDelegate {

	protected VisEditor mEditor = null;

	public void setActiveEditor(IAction action, IEditorPart targetEditor)
	{
	  if (targetEditor instanceof VisEditor)
	    mEditor = (VisEditor) targetEditor;
	  else
	    mEditor = null;
	}

	public VisEditor getActiveEditor()
	{
	  return mEditor;
	}
}
