package de.tudresden.ias.eclipse.dlabpro.dialogs;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.properties.XtpFileProperties;
import de.tudresden.ias.eclipse.dlabpro.properties.XtpFilePropertiesEditor;

public class XtpFilePropertiesDialog extends TitleAreaDialog
{

  private XtpFileProperties       iPrp;
  private XtpFilePropertiesEditor iEdi;
  private Image                   iTitleImg;

  /**
   * Opens an argument dialog for the given dLabPro script properties.
   * 
   * @param iPrp the script properties
   * @return the (modified) script properties or <code>null</code> if the user
   * cancelled the dialog.
   */
  public static XtpFileProperties open(XtpFileProperties iPrp)
  {
    try
    {
      XtpFilePropertiesDialog iDialog = new XtpFilePropertiesDialog(iPrp);
      if (iDialog.open()==OK)
        return iDialog.getProperties();
      else
        return null;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Constructs a new dLabPro script arguments dialog.
   * 
   * @param iProperties
   * @throws NullPointerException if <code>iProperties</code> is <code>null</code>
   */
  public XtpFilePropertiesDialog(XtpFileProperties iProperties)
  {
    super(DLabProPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
        .getShell());
    setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
    
    iPrp = iProperties;
    if (iPrp==null) throw new NullPointerException();
    
    iTitleImg = DLabProPlugin.loadIconImage("icons/wizban/runxtp_wiz.png" );
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.TrayDialog#close()
   */
  @Override
  public boolean close()
  {
    if (iTitleImg != null) iTitleImg.dispose();
    iEdi.dispose();
    return super.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
   */
  protected void configureShell(Shell newShell)
  {
    newShell.setText("dLabPro Script Properties");
    super.configureShell(newShell);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
   */
  protected Control createContents(Composite parent)
  {
    Control contents = super.createContents(parent);

    File iScr = iPrp.getScriptFile();
    setTitle("for " + (iScr!=null ? iScr.getName() : "unknown file"));
    setTitleImage(iTitleImg);
    setMessage("You may add extra arguments using the \"New\" button.",IMessageProvider.INFORMATION);

    return contents;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent)
  {
    parent.setLayout(new GridLayout(1,false));
    Composite control = new Composite(parent,SWT.NONE);
    control.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
    GridLayout gl_control = new GridLayout(1, false);
    gl_control.marginWidth = 0;
    gl_control.marginHeight = 0;
    control.setLayout(gl_control);
    
    iEdi = new XtpFilePropertiesEditor(control,SWT.H_SCROLL|SWT.V_SCROLL,iPrp);
    iEdi.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    iEdi.setProperties(iPrp);
    
    Label label = new Label(control, SWT.SEPARATOR | SWT.HORIZONTAL);
    label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
    
    return control;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed()
  {
    iPrp = iEdi.getProperties();
    iPrp.store();
    super.okPressed();
  }

  /**
   * Returns the dLabPro script properties.
   * 
   * @return the dLabPro script properties
   */
  public XtpFileProperties getProperties()
  {
    return iPrp;
  }
  
}
