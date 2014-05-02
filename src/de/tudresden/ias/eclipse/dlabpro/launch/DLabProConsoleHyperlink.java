
package de.tudresden.ias.eclipse.dlabpro.launch;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.utils.WorkbenchUtil;

/**
 * 
 * @author Christian Feig
 * 
 */
public class DLabProConsoleHyperlink implements IHyperlink
{

  private int  nLine;
  private Path iFile;

  /**
   * Constructor
   * 
   * @param sLine
   * @param sFile
   * @param console
   */
  public DLabProConsoleHyperlink(String sLine, String sFile, IConsole console)
  {
    try
    {
      // Get rid of ambiguous case in Windows
      iFile = new Path((new File(sFile)).getCanonicalPath());
    }
    catch (IOException e1)
    {
      // Silently ignore exceptions
    }
    try
    {
      nLine = Integer.parseInt(sLine);
    }
    catch (NumberFormatException e)
    {
      nLine = -1;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.console.IHyperlink#linkEntered()
   */
  public void linkEntered()
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.console.IHyperlink#linkExited()
   */
  public void linkExited()
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.console.IHyperlink#linkActivated()
   */
  public void linkActivated()
  {
    try
    {
      IFile iRes = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
          iFile);
      if (iRes != null)
      {
        IWorkbenchPage page = DLabProPlugin.getDefault().getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        IEditorPart iEditor = IDE.openEditor(page,iRes);
        if (iEditor != null && iEditor instanceof ITextEditor && nLine >= 1)
        {
          IDocument iDoc = WorkbenchUtil.getEditorIDocument();
          IRegion line = iDoc.getLineInformation((nLine - 1));
          ((ITextEditor)iEditor).selectAndReveal(line.getOffset(),line
              .getLength());
        }
      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
  }

}
