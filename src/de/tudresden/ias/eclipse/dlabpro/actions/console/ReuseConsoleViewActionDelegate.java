
package de.tudresden.ias.eclipse.dlabpro.actions.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * This class represents an view action delegate implementation to handle "reuse console" button in console view.
 * 
 * @author Christian Feig
 * 
 */
public class ReuseConsoleViewActionDelegate implements IViewActionDelegate
{

  private static int             isEnabled = -1;
  private static List<IViewPart> views     = null;
  private static List<IAction>   actions   = null;

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
   */
  public void init(IViewPart view)
  {
    if (isEnabled<0)
    {
      isEnabled = (DLabProPlugin.getDefault().getPreferenceStore()
          .getBoolean(IPreferenceConstants.P_CNS_RECYCLE)) ? 1 : 0;
    }
    getViews().add(view);
    ReuseConsoleViewActionDelegate.setEnabled(isEnabled!=0);
  }

  private static List<IViewPart> getViews()
  {
    if (views == null) views = new ArrayList<IViewPart>();
    return views;
  }

  private static List<IAction> getActions()
  {
    if (actions == null) actions = new ArrayList<IAction>();
    return actions;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  public void run(IAction action)
  {
    if (!getActions().contains(action)) getActions().add(action);
    isEnabled = action.isChecked()?1:0;
    setEnabled(isEnabled!=0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
   * org.eclipse.jface.viewers.ISelection)
   */
  public void selectionChanged(IAction action, ISelection selection)
  {
    // seems to be called after initialization, so set value of action to "global" selected :)
    if (!getActions().contains(action))
    {
      getActions().add(action);
      action.setChecked(isEnabled!=0);
    }
  }

  /**
   * 
   * @return true if "Recycle Console" button is selected, false else
   */
  public static boolean isEnabled()
  {
    return isEnabled!=0;
  }

  /**
   * Sets the selected state of the "Recycle Console" button.
   * 
   * @param enabled
   *          - true if button set to selected, false else
   */
  public static void setEnabled(boolean enabled)
  {
    // for(int i = 0; i < getViews().size(); i++){
    // IAction action = ((IViewPart)getViews().get(i)).getViewSite().getActionBars().getGlobalActionHandler(actionId);
    // if(action != null)
    // action.setChecked(enabled);
    // System.out.println(((IViewPart)getViews().get(i)).getViewSite().getActionBars().getGlobalActionHandler(actionId));
    // }
    // isEnabled = enabled;
    for (int i = 0; i < getActions().size(); i++)
    {
      IAction action = (IAction)getActions().get(i);
      if (action != null) action.setChecked(enabled);
    }

  }

}
