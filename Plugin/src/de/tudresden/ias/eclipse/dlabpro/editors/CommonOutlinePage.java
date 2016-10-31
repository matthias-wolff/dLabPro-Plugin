
package de.tudresden.ias.eclipse.dlabpro.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.ClassSection;
import de.tudresden.ias.eclipse.dlabpro.editors.itp.model.ItpElement;
import de.tudresden.ias.eclipse.dlabpro.utils.WorkbenchUtil;

abstract public class CommonOutlinePage extends ContentOutlinePage
{
  protected AbstractEditor fEditor;

  /**
   * sets the model for the outline page
   * 
   * @param model -
   *          must not be null
   */
  public void setModel(CommonElement model)
  {
    Object[] expandedElements = getTreeViewer().getExpandedElements();
    boolean bFirstTime = (getTreeViewer().getInput()==null);

    getTreeViewer().getControl().setRedraw(false); // Flicker fixing 
    getTreeViewer().setInput(model);
    if (model!=null)
    {
      getTreeViewer().collapseAll();
      for (Object expandedElement : expandedElements)
      {
        CommonElement e1 = (CommonElement)expandedElement;
        for (CommonElement e2 : model.getAllChildren(true))
          if (e1.equalsByQualifiedName(e2))
            getTreeViewer().setExpandedState(e2,true);
      }
      if (expandedElements.length==0 && bFirstTime)
        for (CommonElement element : model.getChildren())
        {
          if (element instanceof ItpElement)
            if (element.getType()==ItpElement.TYPE_FUNCTION)
              getTreeViewer().setExpandedState(element,true);
          if (element instanceof ClassSection)
            getTreeViewer().setExpandedState(element,true);
        }
    }
    getTreeViewer().getControl().setRedraw(true);
  }
  
  private Tree tree;

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  public void createControl(Composite parent)
  {
    super.createControl(parent);
    tree = getTreeViewer().getTree();
    tree.addListener(SWT.MouseDown, new Listener()
    {
      public void handleEvent(Event event)
      {
        Point point = new Point(event.x, event.y);
        if (tree != null && event.button == 3)
        {
          TreeItem item = tree.getItem(point);
          if (item != null && item.getData() instanceof CommonElement && item.getData() != null)
          {
            if (showObject((CommonElement)item.getData()))
            {
              String path = null;
              String filePath = item.getText();
              IFile openFile = null;
              if (filePath.startsWith("$"))
              {
                String preferenceName = filePath.substring(1, filePath.indexOf("/"));
                path = DLabProPlugin.getDefault().getPreferenceStore().getString(preferenceName);
                filePath = filePath.substring(filePath.indexOf("/"), filePath.length());
                openFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
                    new Path(path + filePath));
              }
              else
              {
                IFile iFile = WorkbenchUtil.getSelectedIFile();
                path = iFile.getFullPath().toFile().getParentFile().getPath();
                openFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
                    new Path(path + filePath));
              }

              if (openFile != null && openFile.exists())
              {
                try
                {
                  IDE.openEditor(DLabProPlugin.getDefault().getWorkbench()
                      .getActiveWorkbenchWindow().getActivePage(), openFile);
                }
                catch (PartInitException e)
                {
                  // TODO nothing to do, maybe we could show a
                  // message ..
                }
              }
            }
          }
        }
      }
    });
  }

  /**
   * This method asks subclasses if the given element of type CommonElement should be openend or
   * not. Is used to enable opening include files etc. via rightclick.
   * 
   * @param element -
   *          the CommonElement to handle or not, must not be null
   * @return true if the linked file should be opened, false else
   */
  protected abstract boolean showObject(CommonElement element);

  /**
   * Loads an object icon image.
   * @param sName the name of the image
   * @return the image
   */
  public static final Image loadLoresObjIcon(String sName)
  {
    return DLabProPlugin.loadIconImage("icons/obj16/"+sName);
  }

  /**
   * Loads a high resolution object icon image.
   * @param sName the name of the image
   * @return the image
   */
  public static final Image loadObjIcon(String sName)
  {
    return DLabProPlugin.loadIconImage("icons/hires/obj/"+sName);
  }

  /**
   * This class represents a implementation of {@link ITreeContentProvider}. Is used from
   * subclasses to set the content provider.
   * 
   * @author Christian Feig
   * 
   */
  public class CommonContentProvider implements ITreeContentProvider
  {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement)
    {
      return ((CommonElement)parentElement).getChildren();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element)
    {
      return ((CommonElement)element).getParent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element)
    {
      Object[] children = getChildren(element);
      return children != null && children.length != 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement)
    {
      return getChildren(inputElement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
      // do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      // do nothing
    }
  }
}
