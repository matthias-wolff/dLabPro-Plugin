package de.tudresden.ias.eclipse.dlabpro.editors.vis.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import de.tucottbus.kt.jlab.datadisplays.utils.DdUtils;

public class ZoomOutAction extends AbstractVisEditorActionDelegate {

	public void run(IAction action) {
		if (mEditor != null) {
			mEditor.zoomOut();
		} else
			DdUtils.MSG("Fehler in ZoomOutAction");
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
