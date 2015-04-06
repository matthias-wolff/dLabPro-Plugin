package de.tudresden.ias.eclipse.dlabpro.editors.vis;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.VisEditor;
import de.tudresden.ias.eclipse.dlabpro.utils.UIUtil;

/**
 * @author Stephan Larws
 * @deprecated
 */
public class VIS {

  
  /**
   * Opens a file in the VIS editor. The file must be a resource in the workspace.
   * 
   * @param iFile
   *          the file
   */
  public static VisEditor openVisEditorFor(File iFile)
  {
    System.out.println("DdUtils.openVisEditorFor("+iFile.getAbsolutePath()+")");
    if (iFile==null) return null;
    if (!iFile.exists() || !iFile.isFile())
    {
      UIUtil.showErrorDialog("Cannot open VisEditor for " +
        iFile.getAbsolutePath()+".","The item does not exist or is not a file.",
        null);
      return null;
    }

    IWorkbenchPage iWbp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    IFile iWsf = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(iFile.getAbsolutePath()));
    if (iWsf==null)
    {
      UIUtil.showErrorDialog("Cannot open VisEditor for " +
        iFile.getAbsolutePath()+".","The file is outside the current workspace.",
        null);
      return null;
    }
    try
    {
      //iWsf.refreshLocal(IResource.DEPTH_ZERO,null);
      return (VisEditor)iWbp.openEditor(new FileEditorInput(iWsf),"dLabPro.VisEditor");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
}
