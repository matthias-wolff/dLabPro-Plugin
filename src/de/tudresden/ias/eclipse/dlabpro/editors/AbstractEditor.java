/*
 * Created on 24.06.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Christian Feig
 * 
 */
abstract public class AbstractEditor extends TextEditor
{
  CommonElement               fModel;
  protected CommonOutlinePage fOutlinePage;

  private ProjectionSupport   fProjectionSupport;

  public AbstractEditor()
  {
    super();
    setRulerContextMenuId("#DLabProRulerContext");
  }
  
  /**
   * this method performs the save operation
   * 
   */
  public void doSave()
  {
    super.doSave(getProgressMonitor());
  }
  
//  /*
//   * (non-Javadoc)
//   * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeEditor()
//   */
//  protected void initializeEditor() {
//	  super.initializeEditor();
////	  setPreferenceStore(EditorsPlugin.getDefault().getPreferenceStore());
////	  configureInsertMode(SMART_INSERT, false);
////	  setInsertMode(INSERT);
//  };

  /**
   * @return the model
   */
  public CommonElement getModel()
  {
    return fModel;
  }

  /**
   * this method sets the model
   * 
   * @param model -
   *          the model presenting the structure of editors content
   */
  public void setModel(CommonElement model)
  {
    fModel = model;
    if (fOutlinePage != null) fOutlinePage.setModel(model);

    // if (fOccurrencesUpdater != null)
    // fOccurrencesUpdater.update(getSourceViewer());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPart#dispose()
   */
  public void dispose()
  {
    super.dispose();
  }

  /*
   * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite,
   *      org.eclipse.jface.text.source.IVerticalRuler, int)
   */
  protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
  {
    fAnnotationAccess = createAnnotationAccess();
    fOverviewRuler = createOverviewRuler(getSharedColors());

    ISourceViewer viewer = new ProjectionViewer(parent, ruler, fOverviewRuler, true, styles);
    // ensure decoration support has been created and configured:
    getSourceViewerDecorationSupport(viewer);
    return viewer;
  }

  /**
   * 
   * @return the CommonOutlinePage implementation to be used, must not be null
   * 
   */
  protected abstract CommonOutlinePage getOutlinePage();

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
  @SuppressWarnings("unchecked")
  public Object getAdapter(Class required)
  {
    if (IContentOutlinePage.class.equals(required))
    {
      if (fOutlinePage == null) fOutlinePage = getOutlinePage();
      return fOutlinePage;
    }
    if (fProjectionSupport != null)
    {
      Object adapter = fProjectionSupport.getAdapter(getSourceViewer(), required);
      if (adapter != null) return adapter;
    }
    return super.getAdapter(required);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl(Composite parent)
  {
    super.createPartControl(parent);

    ProjectionViewer projectionViewer = (ProjectionViewer)getSourceViewer();
    fProjectionSupport = new ProjectionSupport(projectionViewer, getAnnotationAccess(),
        getSharedColors());
    fProjectionSupport.install();
    projectionViewer.doOperation(ProjectionViewer.TOGGLE);
  }

  public abstract CommonReconcilingStrategy getReconcilingStrategy();
  
  public void doFolding(int nMode)
  {
    ProjectionViewer iPv = (ProjectionViewer)getSourceViewer();
    if (nMode==0)
      iPv.doOperation(ProjectionViewer.COLLAPSE_ALL);
    else if (nMode==1)
      iPv.doOperation(ProjectionViewer.EXPAND_ALL);
    else if (nMode==2)
    {
      try
      {
        CommonReconcilingStrategy iRs = getReconcilingStrategy();
        iRs.setDocument(getDocumentProvider().getDocument(getEditorInput()));
        iRs.initialReconcile();
      }
      catch (Throwable e)
      {
        e.printStackTrace();
      }
    }
  }
  
}
