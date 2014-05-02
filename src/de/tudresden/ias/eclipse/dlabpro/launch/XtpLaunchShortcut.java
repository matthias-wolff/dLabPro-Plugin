package de.tudresden.ias.eclipse.dlabpro.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import de.tudresden.ias.eclipse.dlabpro.dialogs.XtpFilePropertiesDialog;
import de.tudresden.ias.eclipse.dlabpro.properties.XtpFileProperties;
import de.tudresden.ias.eclipse.dlabpro.utils.LaunchUtil;
import de.tudresden.ias.eclipse.dlabpro.utils.WorkbenchUtil;

/**
 * Contributes the "Run-as" menu item for dLabPro scripts.
 * 
 * @author Matthias Wolff
 */
public class XtpLaunchShortcut implements ILaunchShortcut
{

  public void launch(ISelection selection, String mode)
  {
    if (!"run".equals(mode)) return;
    launch(WorkbenchUtil.getIFileFromSelection(selection));
  }

  public void launch(IEditorPart editor, String mode)
  {
    if (!"run".equals(mode)) return;
    if (!(editor.getEditorInput() instanceof IFileEditorInput)) return;
    launch(((IFileEditorInput)editor.getEditorInput()).getFile());
  }
  
  protected void launch(IFile iFile)
  {
    XtpFileProperties iPrp = new XtpFileProperties(iFile,true);
    if (iPrp.needsUserInput())
      iPrp = XtpFilePropertiesDialog.open(iPrp);        
    if (iPrp==null) return;
    LaunchUtil.launchDlabpro(null,iPrp.getScriptFile(),iPrp.getArgs(),
      iPrp.getWorkDirFile());
  }

}
