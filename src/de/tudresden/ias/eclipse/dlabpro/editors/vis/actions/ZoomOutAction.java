package de.tudresden.ias.eclipse.dlabpro.editors.vis.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;

public class ZoomOutAction extends AbstractVisEditorActionDelegate {

	public void run(IAction action) {
		if (mEditor != null) {
			mEditor.zoomOut();
		} else
			VIS.MSG("Fehler in ZoomOutAction");
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
