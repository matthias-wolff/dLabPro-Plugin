
package de.tudresden.ias.eclipse.dlabpro.editors.vis.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.VisEditor;

public class PlayAction extends AbstractVisEditorActionDelegate
{

  static ImageDescriptor m_iImgPla = null;                                      // Play icon
  static ImageDescriptor m_iImgPlw = null;                                      // Play with warning icon
  static ImageDescriptor m_iImgStp = null;                                      // Stop icon
  
  public PlayAction()
  {
    super();

    if (m_iImgPla==null)
      m_iImgPla = DLabProPlugin.getIconImage("icons/etool16/play_edit.gif");
    if (m_iImgPlw==null)
      m_iImgPlw = DLabProPlugin.getIconImage("icons/etool16/playw_edit.gif");
    if (m_iImgStp==null)
      m_iImgStp = DLabProPlugin.getIconImage("icons/etool16/stop_edit.gif");
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
      setDisabled(action,"the current editor is not capable of playing");
    }
  }

  public static void setPlay(IAction action)
  {
    action.setImageDescriptor(m_iImgPla);
    action.setEnabled(true);
    action.setToolTipText("Play");
  }

  public static void setPlayWarning(IAction action, String sMsg)
  {
    action.setImageDescriptor(m_iImgPlw);
    action.setEnabled(true);
    if (sMsg!=null && sMsg.length()>0) 
      action.setToolTipText("Play (Warning: "+sMsg+")");
    else
      action.setToolTipText("Play (Warning)");
  }
  
  public static void setStop(IAction action)
  {
    action.setImageDescriptor(m_iImgStp);
    action.setEnabled(true);
    action.setToolTipText("Stop");
  }
  
  public static void setDisabled(IAction action, String sMsg)
  {
    action.setImageDescriptor(m_iImgPla);
    action.setEnabled(false);
    if (sMsg!=null && sMsg.length()>0) 
      action.setToolTipText("Play (Disabled because "+sMsg+")");
    else
      action.setToolTipText("Play");
  }
  
}

// EOF
