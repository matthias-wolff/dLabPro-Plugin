
package de.tudresden.ias.eclipse.dlabpro.views;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.views.CommonManualToc.ReturnObject;
import de.tudresden.ias.eclipse.dlabpro.views.toctree.TocContentProvider;
import de.tudresden.ias.eclipse.dlabpro.views.toctree.TocNode;

public class UASRManualTocView extends ViewPart implements ISelectionChangedListener, TreeListener,
    IPropertyChangeListener
{

  public final static String ID         = "de.tudresden.ias.eclipse.dlabpro.uasrmanualtocview";

  private TreeViewer         treeViewer;

  private CommonManualToc    uasrToc    = new UasrManualToc();

  private CommonManualToc    dlabProToc = new DLabProManualToc();

  TocNode                    root       = new TocNode(null, null);

  public UASRManualTocView()
  {
    DLabProPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl(Composite parent)
  {
    treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    treeViewer.addSelectionChangedListener(this);
    treeViewer.setLabelProvider(new CommonTocLabelProvider());
    treeViewer.setContentProvider(new TocContentProvider());
    updateTree();
  }

  private void updateTree()
  {
    treeViewer.getTree().removeTreeListener(this);
    root = new TocNode(null, null);
    ReturnObject uasrObject = uasrToc.getInput(root);
    ReturnObject dlabProObject = dlabProToc.getInput(root);

    treeViewer.setInput(root);
    ArrayList expandedElements = new ArrayList();

    if (uasrObject != null)
    {
      if (uasrObject.getExpandedElements() != null) expandedElements.addAll(uasrObject
          .getExpandedElements());
    }
    if (dlabProObject != null)
    {
      if (dlabProObject.getExpandedElements() != null) expandedElements.addAll(dlabProObject
          .getExpandedElements());
    }
    treeViewer.setExpandedElements(expandedElements.toArray());
    treeViewer.getTree().addTreeListener(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  public void setFocus()
  {
    treeViewer.getControl().setFocus();

  }

  /**
   * 
   * @return treeViewer
   */
  protected TreeViewer getTreeViewer()
  {
    return treeViewer;
  }

  /**
   * 
   * @return the ID used inner eclipse to identify the view component
   */
  protected String getID()
  {
    return ID;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
   */
  public void selectionChanged(SelectionChangedEvent event)
  {

    if (!(event.getSelection() instanceof IStructuredSelection)) return;

    IStructuredSelection selection = (IStructuredSelection)event.getSelection();
    if (selection.size() != 1) return;
    Object element = selection.getFirstElement();
    if (element instanceof TocNode)
    {
      TocNode node = (TocNode)element;
      if (node.getFLink() != null)
      {
        IWorkbenchPage page = DLabProPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
            .getActivePage();
        String path = null;
        try
        {
          ManualContentView part = (ManualContentView)page.showView(ManualContentView.ID);
          path = node.getFLink();
          part.showNode(path, selection, this);
        }
        catch (PartInitException e)
        {
          e.printStackTrace();
        }

      }

    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.events.TreeListener#treeCollapsed(org.eclipse.swt.events.TreeEvent)
   */
  public void treeCollapsed(TreeEvent e)
  {
    // System.out.println(e.data);
    // TreeItem[]items = ((Tree)e.getSource()).getSelection();
    // for(int i = 0; i < items.length; i++)
    // treeViewer.refresh(treeViewer.getInput(), true);
    treeViewer.update(treeViewer.getInput(), new String[]
    { "changelabel" });

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.events.TreeListener#treeExpanded(org.eclipse.swt.events.TreeEvent)
   */
  public void treeExpanded(TreeEvent e)
  {

    // TreeItem[]items = ((Tree)e.getSource()).getSelection();
    // for(int i = 0; i < items.length; i++)
    // treeViewer.refresh(treeViewer.getInput(), true);
    // treeViewer.refresh(items[i].getData(), true);
    // treeViewer.update(items[i].getData(), new String[]{"changelabel"});
    treeViewer.update(treeViewer.getInput(), new String[]
    { "changelabel" });

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent event)
  {
    // boolean found = false;
    if (event.getProperty().equals(uasrToc.getPreferenceName()))
    {
      uasrToc.propertyChange(event);
      // found = true;

    }
    if (event.getProperty().equals(dlabProToc.getPreferenceName()))
    {
      dlabProToc.propertyChange(event);
      // found = true;

    }

    updateTree();

  }

  private class CommonTocLabelProvider extends LabelProvider
  {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element)
    {
      String text = ((TocNode)element).getName();
      return (text != null) ? text : "no title";
    }

    /**
     * 
     * @param node -
     *          the {@link TocNode} to create the image for
     * @return {@link Image}
     */
    private Image createImage(TocNode node)
    {
      File file = null;
      if (treeViewer.getExpandedState(node))
      {
        if (node.getFIconExp() != null) file = new File(node.getFIconExp());
      }
      else if (node.getFIconCol() != null) file = new File(node.getFIconCol());

      if (file != null)

      try
      {

        if (file.exists()) return ImageDescriptor.createFromURL(file.toURL()).createImage();
      }
      catch (MalformedURLException e)
      {
        // no icon ...
      }

      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object element)
    {
      if (element instanceof TocNode) return createImage((TocNode)element);
      else return super.getImage(element);
    }

  }

}
