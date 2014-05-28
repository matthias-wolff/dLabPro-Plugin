
package de.tudresden.ias.eclipse.dlabpro.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Shell;

abstract public class CommonReconcilingStrategy implements IReconcilingStrategy,
    IReconcilingStrategyExtension
{
  private AbstractEditor                 fEditor;

  private IDocument                      fDocument;

  private IProgressMonitor               fProgressMonitor;

  private CommonParser                   fParser;

  private CommonFoldingStructureProvider fFoldingStructureProvider;

  /**
   * Constructor
   * 
   * @param editor -
   *          the editor to use this reconciling strategy for
   */
  public CommonReconcilingStrategy(AbstractEditor editor)
  {
    fEditor = editor;
    fParser = getParser();
    fFoldingStructureProvider = getFoldingStructureProvider(editor);
  }

  /**
   * 
   * @return the parser to use
   */
  abstract protected CommonParser getParser();

  /**
   * 
   * @param editor -
   *          the editor to get the folding structure provider for
   * @return the {@link CommonFoldingStructureProvider} to be used
   */
  abstract protected CommonFoldingStructureProvider getFoldingStructureProvider(
      AbstractEditor editor);

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
   */
  public void setDocument(IDocument document)
  {
    fDocument = document;
    fFoldingStructureProvider.setDocument(fDocument);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
   */
  public void setProgressMonitor(IProgressMonitor monitor)
  {
    fProgressMonitor = monitor;
    fFoldingStructureProvider.setProgressMonitor(fProgressMonitor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,
   *      org.eclipse.jface.text.IRegion)
   */
  public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
  {
    reconcile(false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
   */
  public void reconcile(IRegion partition)
  {
    reconcile(false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
   */
  public void initialReconcile()
  {
    reconcile(true);
  }

  /**
   * performs reconciling
   */
  private void reconcile(boolean bInit)
  {
    final CommonModel model = fParser.parse(fDocument);
    if (model == null) return;

    Shell shell = fEditor.getSite().getShell();
    if (shell == null || shell.isDisposed()) return;

    shell.getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        fEditor.setModel(model);
      }
    });
    fFoldingStructureProvider.updateFoldingRegions(model,bInit);
  }
}
