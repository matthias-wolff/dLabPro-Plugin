package de.tudresden.ias.eclipse.dlabpro.utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;

public class UIUtil
{

  static boolean   bStatic;
  static String    sStatic1;
  static String    sStatic2;
  static Exception eStatic;

  /**
   * Opens an error dialog. The method can be called from outside the SWT UI
   * thread.
   *  
   * @param sMsg  the error message
   * @param sHint an additional hint (e.g. how to solve the problem)
   * @param e     the exception causing the error
   */
  public static synchronized void showErrorDialog(String sMsg, String sHint, Exception e)
  {
    sStatic1 = sMsg;
    sStatic2 = sHint;
    eStatic  = e;
    
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
    {
      public void run()
      {
        Shell iShell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
        Status iStatus = new Status(IStatus.ERROR,DLabProPlugin.PLUGIN_NAME,0,sStatic2,eStatic);
        ErrorDialog.openError(iShell,"Error",sStatic1,iStatus,IStatus.ERROR);
      }
    });
  }

  /**
   * Opens a simple confirm (OK/Cancel) dialog. The method can be called from
   * outside the SWT UI thread.
   * 
   * @param sTitle
   *          the dialog's title or <code>null</code> if none
   * @param sMessage
   *          the message
   * @return <code>true</code> if the user presses the OK button,
   *         <code>false</code> otherwise
   */
  public static synchronized boolean openConfirmDialog(String sTitle, String sMessage)
  {
    bStatic  = false;
    sStatic1 = sTitle;
    sStatic2 = sMessage;
  
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
    {
      public void run()
      {
        Shell iShell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
        bStatic = MessageDialog.openConfirm(iShell,sStatic1,sStatic2);
      }
    });
  
    sStatic1 = null;
    sStatic2 = null;
    return bStatic;
  }

}
