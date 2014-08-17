package de.tudresden.ias.eclipse.dlabpro.editors.vis.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;

public class SpectrogramAction extends AbstractVisEditorActionDelegate {	

  public void run(IAction action)
  {
    if (mEditor != null) mEditor
        .layout(VIS.CP_STYLE_SPECTROGRAM);
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
  }

}