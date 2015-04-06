package de.tudresden.ias.eclipse.dlabpro.actions.console;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.internal.console.ConsoleView;

import de.tucottbus.kt.jlab.datadisplays.utils.DdUtils;
import de.tudresden.ias.eclipse.dlabpro.utils.LaunchUtil;

public abstract class AbstractConsoleViewActionDelegate
implements IViewActionDelegate, IPropertyListener 
{
  private IViewPart m_iView   = null; 
  private IAction   m_iAction = null;
  private IProcess  m_iProc   = null;

  public AbstractConsoleViewActionDelegate()
  {
    //DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
    //ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(this);
  }
  
  public abstract String getId();
  
  public abstract boolean getEnabled(); 
  
  public IProcess getProcess()
  {
    return m_iProc;
  }
  
  public IAction getAction()
  {
    return m_iAction;
  }
  
  public void init(IViewPart view)
  {
    try
    {
      m_iView = view;
      IConsoleView iCv = (IConsoleView)view;
      iCv.addPropertyListener(this);
    }
    catch (Exception e)
    {
      DdUtils.EXCEPTION(e);
    }
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    m_iAction = action;
    update();
  }

  public void propertyChanged(Object source, int propId)
  {
    if (source instanceof ConsoleView)
      try
      {
        ConsoleView iCv = (ConsoleView)source;
        ProcessConsole iPc = (ProcessConsole)iCv.getConsole();
        if (iPc!=null)
          m_iProc = iPc.getProcess();
        else
          m_iProc = null;
        update();
      }
      catch (ClassCastException e1)
      {
      }
      catch (Exception e)
      {
        DdUtils.EXCEPTION(e);
      }
  }

  public void update()
  {
    // Set enabled state
    m_iAction.setEnabled(getEnabled());
    
    // Add or removed action to/from the local console view tool bar
    if (m_iProc==null)
    {
      removeFromToolBar();
      return;
    }

    String sProcType = m_iProc.getAttribute(IProcess.ATTR_PROCESS_TYPE);
    if (sProcType.equals(LaunchUtil.PT_DLABPRO)) addToToolBar();
    else removeFromToolBar();
  }

  private void addToToolBar()
  {
    IToolBarManager iTbm = m_iView.getViewSite().getActionBars()
    .getToolBarManager();
    if (iTbm.find(getId())!=null) return;
    iTbm.prependToGroup(IConsoleConstants.LAUNCH_GROUP,m_iAction);
    iTbm.update(true);
  }
  
  private void removeFromToolBar()
  {
    IToolBarManager iTbm = m_iView.getViewSite().getActionBars()
        .getToolBarManager();
    iTbm.remove(getId());
    iTbm.update(true);
  }
}
