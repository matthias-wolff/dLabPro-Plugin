
package de.tudresden.ias.eclipse.dlabpro.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor;
import de.tudresden.ias.eclipse.dlabpro.editors.IDocumentPartitionScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.itp.ItpDocumentPartitionScanner;

/**
 * This class provides common methods
 * <ul>
 *   <li>to get opened documents and files from the workbench and</li>
 *   <li>to show common dialogs.</li>
 * </ul>
 * 
 * @author Christian Feig and Matthias Wolff
 */
public final class WorkbenchUtil
{
  
  /**
   * this method returns the currently selected IFile
   * 
   * @return the currently selected IFile
   */
  public static IFile getSelectedIFile()
  {
    IWorkbenchPage page = DLabProPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
        .getActivePage();
    IFile file = null;

    if (page != null)
    {
      file = getIFileFromSelection(page.getSelection());
      if (file == null)
      {
        file = getIFileFromEditor(page);
      }
    }
    return file;
  }

  /**
   * this method returns the <code>IFile</code> from the currently shown/selected editor
   * 
   * @return
   */
  public static IFile getIFileFromEditor()
  {
    return getIFileFromEditor(DLabProPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
        .getActivePage());
  }

  public static IEditorPart getActiveEditor()
  {
    return DLabProPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
    .getActivePage().getActiveEditor();
  }
  
  /**
   * this method gets the IDocument shown in the actually opened/shown editor
   * 
   * @return
   */
  public static IDocument getEditorIDocument()
  {
    IEditorPart ed = getActiveEditor();

    IDocument doc = null;
    if (ed instanceof AbstractDecoratedTextEditor) doc = ((AbstractDecoratedTextEditor)ed)
        .getDocumentProvider().getDocument(ed.getEditorInput());

    return doc;
  }

  /**
   * this method checks wether the actually opened editor is dirty and saves changes in this case
   * 
   */
  public static void doSaveCurrentEditor()
  {
    IEditorPart editor = DLabProPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
        .getActivePage().getActiveEditor();
    if (editor.isDirty()) ((AbstractEditor)editor).doSave();
  }

  /**
   * this method returns <code>IFile</code> from the active editor opened in the given
   * <code>IWorkbenchPage</code>
   * 
   * @param page
   * @return
   */
  private static IFile getIFileFromEditor(IWorkbenchPage page)
  {
    IFile file = null;
    IEditorPart editor = page.getActiveEditor();
    if (editor != null)
    {
      file = ((IFileEditorInput)editor.getEditorInput()).getFile();
    }
    return file;
  }

  /**
   * This method returns <code>IFile</code> from the selected component of the given
   * <code>IWorkbenchPage</code> if the component is an instance of
   * <code>IStructuredSelection</code>
   * 
   * @param page
   * @return
   */
  public static IFile getIFileFromSelection(ISelection selection)
  {
    IFile file = null;
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ss = (IStructuredSelection)selection;
      if (!ss.isEmpty())
      {
        Object obj = ss.getFirstElement();
        if (obj instanceof IFile)
        {
          file = (IFile)obj;
        }
      }
    }
    return file;
  }
  
  /**
   * Opens a dialog which prompts for a dLabPro script (extension
   * <code>*.?tp</code>)
   * 
   * @param iShell the parent shell for the dialog
   * @return the selected resource or <code>null</code> if the dialog was
   * canceled
   */
  public static IFile openXtpScriptSelectionDialog(Shell iShell)
  {
    ResourcePatternFilter iXtpFilter = new ResourcePatternFilter()
    {
      public boolean select(Viewer viewer, Object parentElement, Object element)
      {
        if (element instanceof IFile)
        {
          return !super.select(viewer, parentElement, element);
        }
        else
        {
          return true;
        }
      }
    };
    iXtpFilter.setPatterns(new String[] { "*.?tp" });
    
    WorkbenchLabelProvider iLp = new WorkbenchLabelProvider();
    WorkbenchContentProvider iCp = new WorkbenchContentProvider();
    ElementTreeSelectionDialog iDlg = new ElementTreeSelectionDialog(iShell,
        iLp,iCp);
    iDlg.setInput(ResourcesPlugin.getWorkspace().getRoot());
    iDlg.setTitle("Script Selection");
    iDlg.setMessage("Choose a dLabPro script");
    iDlg.addFilter(iXtpFilter);
    
    if (iDlg.open() == Window.OK)
      return (IFile)iDlg.getFirstResult();
    else
      return null;
  }

  /**
   * Reads a dLabPro script file and returns its contents in a document. The
   * document will be properly connected with a
   * {@link ItpDocumentPartitionScanner}.
   * 
   * @param iFile
   *          The file.
   * @return The document.
   * @throws FileNotFoundException
   *           if the file was not found
   * @throws IOException
   *           if the file could not be read
   */
  public static Document getXtpDocumentFromIFile(IFile iFile)
  throws FileNotFoundException, IOException
  {
    File                fil = new File(iFile.getLocation().toString());
    FileInputStream     fin = new FileInputStream(fil);
    BufferedInputStream bin = new BufferedInputStream(fin);
    byte[]              buf = new byte[1024];
    int                 brd = 0;
    String              str = "";

    try
    {
      while ((brd = bin.read(buf)) != -1)
        str += new String(buf,0,brd);
      
      Document document = new Document(str);
      IDocumentPartitionScanner scanner = new ItpDocumentPartitionScanner();
      IDocumentPartitioner partitioner = new FastPartitioner(scanner, scanner.getPartitionTypes());
      document.setDocumentPartitioner(partitioner);
      partitioner.connect(document);
      return document;
    }
    finally
    {
      try
      {
        if (bin != null) bin.close();
      }
      catch (IOException e)
      {
      }
    }
  }
  
  /**
   * Returns a handle to the file identified by the argument.
   * 
   * @param sFullpath
   *          The fully qualified file name.
   * @return The file handle or <code>null</code> if the argument is ill-formatted.
   */
  public static IFile getIFileFromFilename(String sFullpath)
  {
    try
    {
      Path iPath = new Path(sFullpath);
      return ResourcesPlugin.getWorkspace().getRoot().getFile(iPath);
    }
    catch (Exception e)
    {
      return null;
    }
  }
  
}
