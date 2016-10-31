
package de.tudresden.ias.eclipse.dlabpro.editors.itp;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonOutlinePage;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.SegmentGroup;
import de.tudresden.ias.eclipse.dlabpro.editors.itp.model.ItpElement;

public class ItpOutlinePage extends CommonOutlinePage
{

  private static class ItpLabelProvider extends LabelProvider
  {
    private final Image iIconFunctionList = loadLoresObjIcon("function_list.gif");
    private final Image iIconFunctionObj  = loadLoresObjIcon("function_obj.gif" );
    private final Image iIconFunctionsObj = loadLoresObjIcon("functions_obj.gif");
    private final Image iIconIncludeList  = loadLoresObjIcon("include_list.gif" );
    private final Image iIconIncludeObj   = loadLoresObjIcon("include_obj.gif"  );

    public String getText(Object element)
    {

      return ((CommonElement)element).getName();
    }

    public Image getImage(Object object)
    {
      CommonElement element = null;
      if (object instanceof CommonElement) element = (CommonElement)object;

      if (element != null && element instanceof ItpElement)
      {
        if (element.getType() == ItpElement.TYPE_INCLUDES)
        {
          if (element.getChildren().length > 0)
            return iIconIncludeList;
          else
            return iIconIncludeObj;
        }
        else if (element.getType() == ItpElement.TYPE_FUNCTION)
        {
          if (element.getParent().getType() != ItpElement.TYPE_FUNCTION) return iIconFunctionList;
          if (element.getChildren().length > 0) return iIconFunctionsObj;
          else return iIconFunctionObj;
        }
      }
      return super.getImage(object);

    }
    
    public void dispose()
    {
      super.dispose();
      if (iIconFunctionList != null) iIconFunctionList.dispose();
      if (iIconFunctionObj  != null) iIconFunctionObj .dispose();
      if (iIconFunctionsObj != null) iIconFunctionsObj.dispose();
      if (iIconIncludeList  != null) iIconIncludeList .dispose();
      if (iIconIncludeObj   != null) iIconIncludeObj  .dispose();
    }
  }

  public ItpOutlinePage(ITPEditor editor)
  {
    super.fEditor = editor;

  }

  public void createControl(Composite parent)
  {
    super.createControl(parent);
    TreeViewer treeViewer = getTreeViewer();
    treeViewer.setLabelProvider(new ItpLabelProvider());
    treeViewer.setContentProvider(new CommonContentProvider());
    treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
    treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        if (!(event.getSelection() instanceof IStructuredSelection)) return;
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        if (selection.size() != 1) return;
        Object element = selection.getFirstElement();
        if (element instanceof SegmentGroup) return;
        if (!(element instanceof CommonElement)) return;
        CommonElement commonElement = (CommonElement)element;
        if (commonElement.getLength() == -1) return;

        fEditor.selectAndReveal(commonElement.getOffset(), commonElement.getLength());
      }
    });

    setModel(fEditor.getModel());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonOutlinePage#handleObject(java.lang.Object)
   */
  protected boolean showObject(CommonElement element)
  {
    return element instanceof ItpElement && (element.getType() == ItpElement.TYPE_INCLUDES);
  }

}
