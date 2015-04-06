package de.tudresden.ias.eclipse.dlabpro.editors.vis.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import de.tucottbus.kt.jlab.datadisplays.utils.DdUtils;

public class ThreeDAction extends AbstractVisEditorActionDelegate
{

  public void run(IAction action)
  {
    if (mEditor != null) mEditor.layout(DdUtils.CP_STYLE_3DVIEW);
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
  }

}
