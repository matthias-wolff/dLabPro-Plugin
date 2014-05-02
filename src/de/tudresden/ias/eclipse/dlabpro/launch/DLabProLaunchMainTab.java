package de.tudresden.ias.eclipse.dlabpro.launch;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.utils.LaunchUtil;
import de.tudresden.ias.eclipse.dlabpro.utils.WorkbenchUtil;

public class DLabProLaunchMainTab extends AbstractLaunchConfigurationTab
  implements IDlabProLaunchConfigurationConstants
{

  private Image  iImage;
  private Button iBtnAuto;
  private Button iBrwsExe;
  private Label  iLblExe;
  private Text   iTxtExe;
  private Text   iTxtArgs;
  private Text   iTxtScr;

  private final String MSG_DEFAULT = "Run a dLabPro script";
  
  /**
   * Constructs a new "Main" tab for configuring a dLabPro launch configuration.
   */
  public DLabProLaunchMainTab()
  {
    super();
    iImage = DLabProPlugin.loadIconImage("icons/obj16/main_tab.gif");
  }

  /* (non-Javadoc)
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#dispose()
   */
  @Override
  public void dispose()
  {
    if (iImage!=null) iImage.dispose();
    super.dispose();
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
   */
  public void createControl(Composite parent)
  {
    setMessage(MSG_DEFAULT);
    
    // Initialize control component
    Composite iCps = new Composite(parent, SWT.NONE);
    setControl(iCps);
    GridLayout iGl = new GridLayout(2,false);
    iCps.setLayout(iGl);

    createVerticalSpacer(iCps,2);

    // Interpreter executable group
    iBtnAuto = new Button(iCps,SWT.CHECK);
    iBtnAuto.setText("Use default interpreter");
    iBtnAuto.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,false,2,1));
    iBtnAuto.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        setAutoInterpreter(iBtnAuto.getSelection());
        updateLaunchConfigurationDialog();
      }
    });
    
    iLblExe = new Label(iCps,SWT.NULL);
    iLblExe.setText("Interpreter executable:");
    iLblExe.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,false,2,1));
    
    iTxtExe = new Text(iCps,SWT.SINGLE|SWT.BORDER);
    iTxtExe.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
    iTxtExe.addKeyListener(new KeyListener()
    {
      public void keyPressed(KeyEvent e)
      {
      }
      public void keyReleased(KeyEvent e)
      {
        updateLaunchConfigurationDialog();
      }
    });
    
    iBrwsExe = new Button(iCps,SWT.PUSH);
    iBrwsExe.setText(" Browse ... ");
    iBrwsExe.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
    iBrwsExe.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        FileDialog iDlg = new FileDialog(getShell());
        iDlg.setText("Select dLabPro Executable File");
        String sFile = iDlg.open();
        if (sFile != null)
        {
          iTxtExe.setText(sFile);
          updateLaunchConfigurationDialog();
        }
      }
    });
    
    createVerticalSpacer(iCps,2);

    // Interpreter arguments group
    Label iLabel = new Label(iCps,SWT.NULL);
    iLabel.setText("Interpreter arguments:");
    iLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,false,2,1));
    
    iTxtArgs = new Text(iCps,SWT.SINGLE|SWT.BORDER);
    iTxtArgs.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,1,1));
    iTxtArgs.addKeyListener(new KeyListener()
    {
      public void keyPressed(KeyEvent e)
      {
      }
      public void keyReleased(KeyEvent e)
      {
        updateLaunchConfigurationDialog();
      }
    });
    iTxtArgs.addFocusListener(new FocusListener()
    {
      public void focusGained(FocusEvent e)
      {
        setMessage("Please note that these arguments are additional to the " +
        		"ones defined on the dLabPro preference page.");
        updateLaunchConfigurationDialog();
      }
      public void focusLost(FocusEvent e)
      {
        setMessage(MSG_DEFAULT);
        updateLaunchConfigurationDialog();
      }
    });
    
    Button iBtn = new Button(iCps,SWT.PUSH);
    iBtn.setText(" Defaults ");
    iBtn.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
    iBtn.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        iTxtArgs.setText(LaunchUtil.joinCommandLine(LaunchUtil.getDlabproExeArgs()));
        updateLaunchConfigurationDialog();
      }
    });
    
    createVerticalSpacer(iCps,2);

    // Script group
    iLabel = new Label(iCps,SWT.NULL);
    iLabel.setText("Script:");
    iLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,false,2,1));
    
    iTxtScr = new Text(iCps,SWT.SINGLE|SWT.BORDER);
    iTxtScr.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
    iTxtScr.addKeyListener(new KeyListener()
    {
      public void keyPressed(KeyEvent e)
      {
      }
      public void keyReleased(KeyEvent e)
      {
        updateLaunchConfigurationDialog();
        scriptChanged(iTxtScr.getText());
      }
    });
    
    iBtn = new Button(iCps,SWT.PUSH);
    iBtn.setText(" Browse ... ");
    iBtn.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
    iBtn.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        IFile iFile = WorkbenchUtil.openXtpScriptSelectionDialog(getShell());
        if (iFile!=null)
        {
          iTxtScr.setText(iFile.getFullPath().toString());
          updateLaunchConfigurationDialog();
          scriptChanged(iTxtScr.getText());
        }
      }
    });
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
   */
  public String getName()
  {
    return "Main";
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
   */
  @Override
  public Image getImage()
  {
    return iImage;
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
   */
  public boolean isValid(ILaunchConfiguration launchConfig)
  {
    setErrorMessage(null);
    
    // Check interpreter executable
    if (iTxtExe.getText().length()==0)
    {
      setErrorMessage("You must specify an interpreter executable.");
      return false;
    }
    File iExe = new File(iTxtExe.getText());
    if (!iExe.isFile() || !iExe.exists())
    {
      setErrorMessage("The interpreter executable does not exist");
      return false;
    }
    
    // Check script
    if (iTxtScr.getText().length()>0)
      try
      {
        IFile iScr = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(iTxtScr.getText()));
        if (iScr==null || !iScr.exists())
        {
          setErrorMessage("The script file does not exist.");
          return false;
        }
      }
      catch (IllegalArgumentException e)
      {
        setErrorMessage("The script file does not exist.");
        return false;
      }
    
    // Nothing suspicious found ...
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
   */
  public void initializeFrom(ILaunchConfiguration iCfg)
  {
    try
    {
      iTxtExe. setText(iCfg.getAttribute(LA_EXE     ,""));
      iTxtArgs.setText(iCfg.getAttribute(LA_EXE_ARGS,""));
      iTxtScr. setText(iCfg.getAttribute(LA_SCRIPT  ,""));
      scriptChanged(iTxtScr.getText());

       // NOTE: must come last because default means "the current default"!
      setAutoInterpreter(iCfg.getAttribute(LA_EXE_USEDEF,true));
    }
    catch (CoreException e)
    {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
   */
  public void performApply(ILaunchConfigurationWorkingCopy iCfg)
  {
    iCfg.setAttribute(LA_EXE_USEDEF,iBtnAuto.getSelection());
    iCfg.setAttribute(LA_EXE       ,iTxtExe .getText());
    iCfg.setAttribute(LA_EXE_ARGS  ,iTxtArgs.getText());
    iCfg.setAttribute(LA_SCRIPT    ,iTxtScr .getText());
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
   */
  public void setDefaults(ILaunchConfigurationWorkingCopy iCfg)
  {
    File iExe = LaunchUtil.getDlabproExe(true);
    
    iCfg.setAttribute(LA_EXE_USEDEF,true);
    iCfg.setAttribute(LA_EXE,iExe!=null?iExe.getAbsolutePath():"dlabpro");
    iCfg.setAttribute(LA_EXE_ARGS,"");
    iCfg.setAttribute(LA_SCRIPT,"");
  }
 
  /**
   * Toggles the usage of the default dLabPro interpreter. 
   * 
   * @param bAuto use default or don't
   */
  private void setAutoInterpreter(boolean bAuto)
  {
    iBtnAuto.setSelection(bAuto);
    iLblExe.setEnabled(!bAuto);
    iTxtExe.setEnabled(!bAuto);
    iBrwsExe.setEnabled(!bAuto);
    if (bAuto)
    {
      File iExe = LaunchUtil.getDlabproExe(true);
      iTxtExe.setText(iExe!=null?iExe.getAbsolutePath():"dlabpro");
    }
  }

  /**
   * Returns the currently selected script file name.
   */
  String getScript()
  {
    return iTxtScr.getText();
  }
  
  /**
   * Called when the dLabPro script of this launch configuration has changed.
   */
  private void scriptChanged(String sScript)
  {
    //ILaunchConfigurationTab[] iTabs = getLaunchConfigurationDialog().getTabs();
    //DLabProLaunchArgsTab iArgsTab = (DLabProLaunchArgsTab)iTabs[1];
    //iArgsTab.scriptChanged(sScript);
  }
}
