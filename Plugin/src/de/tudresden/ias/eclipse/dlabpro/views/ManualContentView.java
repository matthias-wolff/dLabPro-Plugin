
package de.tudresden.ias.eclipse.dlabpro.views;

import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * This class is a subclass of
 * {@link de.tudresden.ias.eclipse.dlabpro.views.CommonDLabProManualView}. It extends the
 * superclass for some navigation functionality like history backwarding and home jumping. Can be
 * used from {@link de.tudresden.ias.eclipse.dlabpro.views.CommonManualToc} instances via
 * {@link #showNode(String, ISelection, CommonManualToc)} method to show help content.
 * 
 * @author Christian Feig
 * 
 */
public class ManualContentView extends CommonDLabProManualView
{

  public final static String ID                = "de.tudresden.ias.eclipse.dlabpro.manualcontentview";
  private Vector<Object>     history           = new Vector<Object>();
  private final Action       historyBackAction = new BackAction();
  private final Action       homeAction        = new HomeAction();

  // represents a pointer to current history position
  int                        index             = -1;

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl(Composite parent)
  {
    IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
    toolBarManager.add(historyBackAction);
    toolBarManager.add(homeAction);
    super.createPartControl(parent);
  }

  /**
   * this method sets the BackAction enabled or not, dependend on the available history back steps.
   * sets the BackAction enabled if at least one back step is possible, disabled else.
   * 
   */
  private void checkHistoryback()
  {
    historyBackAction.setEnabled(index > 0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.views.CommonDLabProHelpView#showContent()
   */
  protected void showContent()
  {
    if (getHomePath() != null)
    {
      String url = "file://" + getHomePath() + "home.html";
      if (getBrowser()!=null) getBrowser().setUrl(url);
      add2History(url);
    }
    else if (getBrowser()!=null) getBrowser().setUrl("");
    checkHistoryback();

  }

  /**
   * shows the given url in browser
   * 
   * @param url -
   *          the url as string
   */
  private void showURL(String url)
  {
    if (getBrowser()!=null) getBrowser().setUrl(url);
  }

  /**
   * This method shows the help content of the given node. The node is added to the history to
   * enable going backward by selecting the node in the TocView instance.
   * 
   * @param home -
   *          the validated home path
   * @param node -
   *          the TocNode of CommonTocView which contains the data to show, can be null
   * @param tocView -
   *          the CommonTocView where to select the node when going backward, can be null in history
   */
  public void showNode(String path, ISelection selection, UASRManualTocView tocView)
  {
    showURL(path);
    if (selection != null && tocView != null) add2History(new StoreObject(selection, tocView));
    else add2History(path);
  }

  /**
   * this method adds the given object to the history.
   * 
   * @param addObject -
   *          the object to add to the history
   */
  private void add2History(Object addObject)
  {
    boolean goon = true;
    if (index >= 0)
    {
      Object o = history.get(index);
      goon = !o.equals(addObject);
      if (goon && o instanceof StoreObject && addObject instanceof StoreObject)
      {
        goon = !((StoreObject)o).getSelection().equals(((StoreObject)addObject).getSelection());
      }
    }
    if (goon)
    {
      index++;
      history.add(index, addObject);
      checkHistoryback();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.views.CommonDLabProHelpView#getPreferenceName()
   */
  protected String getPreferenceName()
  {
    return IPreferenceConstants.P_PRG_DLPDOC;
  }

  /**
   * this class handles the navigation home action
   * 
   * @author Christian Feig
   * 
   */
  private class HomeAction extends Action
  {
    public HomeAction()
    {
      super("home", IAction.AS_PUSH_BUTTON);
      setImageDescriptor(DLabProPlugin.getIconImage("icons/elcl16/home_nav.png"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run()
    {
      super.run();
      showContent();
    }
  }

  /**
   * this class handles the navigation backward action
   * 
   * @author Christian Feig
   * 
   */
  private class BackAction extends Action
  {

    /**
     * Constructor
     * 
     */
    public BackAction()
    {
      super("back", IAction.AS_PUSH_BUTTON);
      setImageDescriptor(DLabProPlugin.getIconImage("icons/elcl16/back_nav.gif"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run()
    {
      super.run();
      if (index > 0) index--;
      Object o = history.get(index);
      if (o instanceof StoreObject)
      {
        StoreObject storeObject = (StoreObject)o;
        try
        {
          DLabProPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage()
              .showView(storeObject.getTocView().getID());
        }
        catch (PartInitException e)
        {
          e.printStackTrace();
        }
        storeObject.getTocView().getTreeViewer().setSelection(storeObject.getSelection());
      }
      else if (o instanceof String)
      {
        showURL(o.toString());
      }
      checkHistoryback();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IAction#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled)
    {
      super.setEnabled(enabled);
      // setImageDescriptor((enabled)?imageEnabled:imageDisabled);
    }

  }

  /**
   * This class is used to store a pair of CommonTocView and a TocNode to enable history walking.
   * 
   * @author Christian Feig
   * 
   */
  private class StoreObject
  {

    private ISelection        selection;

    private UASRManualTocView tocView;

    /**
     * 
     * @param node -
     *          the TocNode to store
     * @param tocView -
     *          the CommonTocView to store
     */
    public StoreObject(ISelection selection, UASRManualTocView tocView)
    {
      this.selection = selection;
      this.tocView = tocView;
    }

    /**
     * 
     * @return the stored ISelection
     */
    protected ISelection getSelection()
    {
      return selection;
    }

    /**
     * 
     * @return the stored CommonTocView
     */
    protected UASRManualTocView getTocView()
    {
      return tocView;
    }
  }

}
