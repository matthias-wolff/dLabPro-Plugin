
package de.tudresden.ias.eclipse.dlabpro.editors.def;

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
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.ClassSection;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.ErrorSegment;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.FieldSegment;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.ListSegment;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.MethodElement;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.NoteSegment;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.OptionSegment;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.SegmentGroup;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.SnippetSegment;

/**
 * This class represents the implementation of
 * {@link de.tudresden.ias.eclipse.dlabpro.editors.CommonOutlinePage} used for def-documents/editors
 * 
 * @author Christian Feig
 * 
 */
public class DefOutlinePage extends CommonOutlinePage
{

  private static class DefLabelProvider extends LabelProvider
  {

    private final Image iIconClassObj    = loadObjIcon("class_obj.gif"    );
    private final Image iIconLibObj      = loadObjIcon("lib_obj.gif"      );
    private final Image iIconList        = loadObjIcon("list.gif"         );
    private final Image iIconMethodList  = loadObjIcon("method_list.gif"  );
    private final Image iIconMethodObj   = loadObjIcon("method_obj.gif"   );
    private final Image iIconFieldList   = loadObjIcon("field_list.gif"   );
    private final Image iIconPubFieldObj = loadObjIcon("pub_field_obj.gif");
    private final Image iIconPrtFieldObj = loadObjIcon("prt_field_obj.gif");
    private final Image iIconPrvFieldObj = loadObjIcon("prv_field_obj.gif");
    private final Image iIconOptionList  = loadObjIcon("option_list.gif"  );
    private final Image iIconOptionObj   = loadObjIcon("option_obj.gif"   );
    private final Image iIconErrorList   = loadObjIcon("error_list.gif"   );
    private final Image iIconErrorObj    = loadHiresObjIcon("error_obj.png"    );
    private final Image iIconWarningObj  = loadHiresObjIcon("warning_obj.png"  );
    private final Image iIconNoteObj     = loadObjIcon("note_obj.gif"     );
    private final Image iIconDefineList  = loadObjIcon("define_list.gif"  );
    private final Image iIconDefineObj   = loadObjIcon("define_obj.gif"   );
    private final Image iIconFileList    = loadObjIcon("file_list.gif"    );
    private final Image iIconCFileObj    = loadObjIcon("c_file_obj.gif"   );
    private final Image iIconHFileObj    = loadObjIcon("h_file_obj.gif"   );
    private final Image iIconSnippetList = loadObjIcon("snippet_list.gif" );
    private final Image iIconSnippetObj  = loadObjIcon("snippet_obj.gif"  );

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element)
    {
      return ((CommonElement)element).getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object object)
    {

      CommonElement element = null;
      if (object instanceof CommonElement) element = (CommonElement)object;

      if (element != null)
      {

        int type = element.getType();

        if (element instanceof ClassSection)
        {
          if (type == ClassSection.cLib_CLASS) return iIconLibObj;
          if (type == ClassSection.CLASS) return iIconClassObj;
          return iIconList;
        }

        if (element instanceof SegmentGroup)
        {
          if (type == SegmentGroup.TYPE_METHOD) return iIconMethodList;
          if (type == SegmentGroup.TYPE_FIELD) return iIconFieldList;
          if (type == SegmentGroup.TYPE_OPTION) return iIconOptionList;
          if (type == SegmentGroup.TYPE_INTERFACE_CODE_SNIPPETS) return iIconSnippetList;

          if (type == SegmentGroup.TYPE_ERROR) return iIconErrorList;
          if (type == SegmentGroup.TYPE_NOTE) return iIconNoteObj;
          if (type == SegmentGroup.TYPE_DEFINE) return iIconDefineList;
          if (type == SegmentGroup.TYPE_FILE) return iIconFileList;
          // if (type == SegmentGroup.TYPE_RESEINCLUDES) return
          // fIncludeList;
        }

        if (element instanceof MethodElement) return iIconMethodObj;

        if (element instanceof FieldSegment)
        {
          if (type == FieldSegment.PRIVATE) return iIconPrvFieldObj;
          if (type == FieldSegment.PROTECTED) return iIconPrtFieldObj;
          return iIconPubFieldObj;
        }

        if (element instanceof OptionSegment) return iIconOptionObj;

        if (element instanceof ErrorSegment)
        {
          if (type == ErrorSegment.WARNING) return iIconWarningObj;
          return iIconErrorObj;

        }
        if (element instanceof SnippetSegment) return iIconSnippetObj;

        if (element instanceof NoteSegment) return iIconNoteObj;

        if (element instanceof ListSegment)
        {
          if (type == ListSegment.DEFINE) return iIconDefineObj;
          if (type == ListSegment.FILE) return iIconCFileObj;
          if (type == ListSegment.INCLUDE) return iIconHFileObj;
        }

      }
      return super.getImage(element);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose()
    {
      super.dispose();
      if (iIconClassObj    != null) iIconClassObj   .dispose();
      if (iIconLibObj      != null) iIconLibObj     .dispose();
      if (iIconList        != null) iIconList       .dispose();
      if (iIconMethodObj   != null) iIconMethodObj  .dispose();
      if (iIconMethodList  != null) iIconMethodList .dispose();
      if (iIconPubFieldObj != null) iIconPubFieldObj.dispose();
      if (iIconFieldList   != null) iIconFieldList  .dispose();
      if (iIconPrtFieldObj != null) iIconPrtFieldObj.dispose();
      if (iIconPrvFieldObj != null) iIconPrvFieldObj.dispose();
      if (iIconOptionList  != null) iIconOptionList .dispose();
      if (iIconOptionObj   != null) iIconOptionObj  .dispose();
      if (iIconErrorList   != null) iIconErrorList  .dispose();
      if (iIconErrorObj    != null) iIconErrorObj   .dispose();
      if (iIconWarningObj  != null) iIconWarningObj .dispose();
      if (iIconNoteObj     != null) iIconNoteObj    .dispose();
      if (iIconDefineList  != null) iIconDefineList .dispose();
      if (iIconDefineObj   != null) iIconDefineObj  .dispose();
      if (iIconFileList    != null) iIconFileList   .dispose();
      if (iIconCFileObj    != null) iIconCFileObj   .dispose();
      if (iIconHFileObj    != null) iIconHFileObj   .dispose();
      if (iIconSnippetList != null) iIconSnippetList.dispose();
      if (iIconSnippetObj  != null) iIconSnippetObj .dispose();
    }
  }

  /**
   * Constructor
   * 
   * @param editor -
   *          the editor to create the outline page for
   */
  public DefOutlinePage(DEFEditor editor)
  {
    fEditor = editor;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  public void createControl(Composite parent)
  {
    super.createControl(parent);
    TreeViewer treeViewer = getTreeViewer();
    treeViewer.setLabelProvider(new DefLabelProvider());
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
        CommonElement cElement = (CommonElement)element;
        fEditor.selectAndReveal(cElement.getOffset(), cElement.getLength());
      }
    });

    setModel(fEditor.getModel());
  }

  
  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonOutlinePage#showObject(java.lang.Object)
   */
  protected boolean showObject(CommonElement element)
  {
    return element instanceof ListSegment
        && (element.getType() == ListSegment.FILE || element.getType() == ListSegment.INCLUDE);
  }

}
