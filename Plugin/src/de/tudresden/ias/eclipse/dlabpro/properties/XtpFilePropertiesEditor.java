package de.tudresden.ias.eclipse.dlabpro.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

public class XtpFilePropertiesEditor extends ScrolledComposite
{

  private XtpFileProperties      iPrp;
  private Image                  iDirIcon;
  private Image                  iFileIcon;

  private Composite              iRootCmps;
  private Button                 iBtnAuto;
  private Button                 iBtnDontAsk;
  private Text                   iWorkDir;
  private Button                 iBrwsWorkDir;
  private Button                 iBtnAdd;
  private Button                 iBtnRem;
  private Group                  iGrpArg;
  private Vector<ArgEditor>      viArgEditors  = new Vector<ArgEditor>();
  private GridLayout             iGl_1;
  private Composite              iGrp_1;

  private Vector<ModifyListener> viListeners;

  private boolean                bAutoResize = false;
  
  /**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance.
   * 
   * @param iParent
   *          a widget which will be the parent of the new instance (cannot be
   *          <code>null</code>)
   * @param nStyle
   *          the style of widget to construct
   * @param iProperties
   *          the {@link XtpFileProperties} object to be edited by this instance
   */
  public XtpFilePropertiesEditor(Composite iParent, int style, XtpFileProperties iProperties)
  {
    super(iParent,style);
    
    // Initialize
    iPrp = iProperties;
    viArgEditors = new Vector<ArgEditor>();
    viListeners  = new Vector<ModifyListener>();

    // Load images
    iDirIcon  = ResourceManager.getPluginImage("de.tudresden.ias.eclipse.dlabpro","icons/elcl16/dir_browse.gif" );
    iFileIcon = ResourceManager.getPluginImage("de.tudresden.ias.eclipse.dlabpro","icons/elcl16/file_browse.gif");

    // Set layout
    setExpandVertical(true);
    setExpandHorizontal(true);    

    iRootCmps = new Composite(this,SWT.NULL);
    GridLayout gl_iRootCmps = new GridLayout(1,false);
    gl_iRootCmps.marginWidth = 0;
    gl_iRootCmps.marginHeight = 0;
    iRootCmps.setLayout(gl_iRootCmps);
    
    // Create the working directory group
    Composite iGrp = new Composite(iRootCmps,SWT.NULL);
    iGrp.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
    iGrp.setLayout(new GridLayout(3,false));
    
    Label iLabel = new Label(iGrp,SWT.NULL);
    iLabel.setText("Working directory:");
    iLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,3,1));
    
    iBtnAuto = new Button(iGrp,SWT.CHECK);
    iBtnAuto.setText("Automatic");
    iBtnAuto.addSelectionListener(new SelectionListener(){
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        boolean bAuto = ((Button)e.widget).getSelection(); 
        iWorkDir    .setEnabled(!bAuto);
        iBrwsWorkDir.setEnabled(!bAuto);
        iPrp.setAutoWorkDir(bAuto);
        String sWorkDir = iPrp.getWorkDir();
        if (sWorkDir!=null) iWorkDir.setText(sWorkDir);
        fireModified();
      }
    });
    
    iWorkDir = new Text(iGrp,SWT.SINGLE|SWT.BORDER);
    iWorkDir.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
    iWorkDir.addKeyListener(new KeyListener()
    {
      public void keyPressed(KeyEvent e)
      {
      }
      public void keyReleased(KeyEvent e)
      {
        fireModified();
      }
    });
    
    iBrwsWorkDir = new Button(iGrp,SWT.PUSH);
    iBrwsWorkDir.setText(" Browse ... ");
    iBrwsWorkDir.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
    iBrwsWorkDir.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        Shell iShell = iBrwsWorkDir.getShell();
        DirectoryDialog iPathDlg = new DirectoryDialog(iShell);
        iPathDlg.setText("Directory Selection");
        iPathDlg.setMessage("Chose a working directory:");
        String sWorkDir = iPathDlg.open();
        if (sWorkDir!=null)
        {
          iWorkDir.setText(sWorkDir);
          fireModified();
        }
      }
    });

    // Create the script arguments group
    iGrp_1 = new Composite(iRootCmps,SWT.NULL);
    iGrp_1.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
    iGl_1 = new GridLayout(2,false);
    iGl_1.marginBottom = 5;
    iGl_1.marginHeight = 0;
    iGrp_1.setLayout(iGl_1);
    
    iLabel = new Label(iGrp_1,SWT.NULL);
    iLabel.setText("Script arguments:");
    iLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
    
    Composite iCps = new Composite(iGrp_1,SWT.NULL);
    iCps.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false));
    GridLayout iGl = new GridLayout(2,false);
    iGl.horizontalSpacing = 1;
    iGl.verticalSpacing = 0;
    iGl.marginHeight = 0;
    iGl.marginWidth = 0;
    iGl.marginRight = 7;
    iCps.setLayout(iGl);
    
    iBtnAdd = new Button(iCps,SWT.PUSH);
    iBtnAdd.setImage(ResourceManager.getPluginImage("de.tudresden.ias.eclipse.dlabpro", "icons/elcl16/add.gif"));
    iBtnAdd.setToolTipText("Create a new argument");
    iBtnAdd.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
    iBtnAdd.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        addArgEditor(true,true);
        fireModified();
      }
    });
    
    iBtnRem = new Button(iCps,SWT.PUSH);
    iBtnRem.setImage(ResourceManager.getPluginImage("de.tudresden.ias.eclipse.dlabpro", "icons/elcl16/rem.gif"));
    iBtnRem.setToolTipText("Remove the last argument");
    iBtnRem.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
    iBtnRem.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        removeArgEditor(false);
        fireModified();
      }
    });
    
    iGrpArg = new Group(iGrp_1,SWT.NONE);
    iGrpArg.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
    iGrpArg.setLayout(new GridLayout(4,false));
    
    iLabel = new Label(iGrpArg,SWT.NULL);
    iLabel.setText("Name");
    iLabel.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
    
    iLabel = new Label(iGrpArg,SWT.NULL);
    iLabel.setText("Value");
    iLabel.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false));
    
    iLabel = new Label(iGrpArg,SWT.NULL);
    iLabel.setText(iPrp!=null && iPrp.isPersistent()?"Description":"");
    iLabel.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
    
    iLabel = new Label(iGrpArg,SWT.NULL);
    iLabel.setText("");
    iLabel.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
    
    iBtnDontAsk = new Button(iGrp_1, SWT.CHECK);
    iBtnDontAsk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    iBtnDontAsk.setText("Do not ask for aguments when launching");
    new Label(iGrp_1, SWT.NONE);

    // Initialize
    setContent(iRootCmps);
    setMinSize(iRootCmps.computeSize(450,SWT.DEFAULT));
    initializeValues(false);
    updateRemButton();
  }
  
  /**
   * Adds a new argument editor.
   * 
   * @return the editor
   */
  private ArgEditor addArgEditor(boolean bLayout, boolean bIsExtra)
  {
    ArgEditor iEdi = new ArgEditor(iGrpArg,"$"+(viArgEditors.size()+1),bIsExtra);
    viArgEditors.add(iEdi);
    if (bLayout)
    {
      getShell().layout(true,true);
      setMinSize(iRootCmps.computeSize(450,SWT.DEFAULT));
      if (isAutoResize())
        getShell().pack(true);
    }
    updateRemButton();
    return iEdi;
  }
  
  /**
   * Removes the last argument editor (if this is possible).
   * 
   * @param bForce
   *          Force removal
   */
  private void removeArgEditor(boolean bForce)
  {
    if (viArgEditors.size()==0) return;
    ArgEditor iEdi = viArgEditors.get(viArgEditors.size()-1);
    if (iEdi.isExtra() || bForce)
    {
      viArgEditors.remove(viArgEditors.size()-1);
      iEdi.dispose();
    }
    getShell().layout(true,true);
    setMinSize(iRootCmps.computeSize(450,SWT.DEFAULT));
    if (isAutoResize())
      getShell().pack(true);
    updateRemButton();
  }
  
  /**
   * Updates the state of the [-] button.
   */
  private void updateRemButton()
  {
    boolean bEnable = false;
    if (viArgEditors.size()>0)
      bEnable = viArgEditors.get(viArgEditors.size()-1).isExtra();
    iBtnRem.setEnabled(bEnable);
  }

  // -- Data connections --
  
  /**
   * Initializes the editor from the {@link XtpFileProperties}.
   */
  protected void initializeValues(boolean bLayout)
  {
    while (viArgEditors.size()>0) removeArgEditor(true);
    if (iPrp==null) return;

    // Initialize working directory
    String sWorkDir = iPrp.getWorkDir();
    if (sWorkDir!=null) iWorkDir.setText(sWorkDir);
    boolean bAutoWorkDir = iPrp.isAutoWorkDir();
    iBtnAuto.setSelection(bAutoWorkDir);
    iBtnDontAsk.setSelection(iPrp.isDontAsk());
    iWorkDir.setEnabled(!bAutoWorkDir);
    iBrwsWorkDir.setEnabled(!bAutoWorkDir);
    
    // Initialize arguments
    ArrayList<String> iArgs = iPrp.getArgs();
    for (int i=0; i<iArgs.size(); i++)
    {
      ArgEditor iEdi = addArgEditor(bLayout,iPrp.isArgExtra(i+1));
      iEdi.setDescription(iPrp.getArgDescription(i+1));
      iEdi.setText(iArgs.get(i));
    }
  }
  
  /**
   * Stores the contents of the editor in the {@link XtpFileProperties}.
   */
  protected void storeValues()
  {
    if (iPrp==null) return;
    iPrp.setWorkDir(iWorkDir.getText());
    iPrp.setAutoWorkDir(iBtnAuto.getSelection());
    iPrp.setDontAsk(iBtnDontAsk.getSelection());
    ArrayList<String> lsArgs = new ArrayList<String>();
    for (int i=0; i<viArgEditors.size(); i++)
      lsArgs.add(viArgEditors.get(i).getText());
    iPrp.setArgs(lsArgs);
  }
  
  // -- Getters and setters --
  
  /**
   * Sets the {@link XtpFileProperties} object to be edited by this instance.
   * 
   * @param iProperties
   *          the new <code>XtpFileProperties</code> object
   */
  public void setProperties(XtpFileProperties iProperties)
  {
    iPrp = iProperties;
    initializeValues(true);
  }

  /**
   * Gets the {@link XtpFileProperties} object to be edited by this instance.
   */
  public XtpFileProperties getProperties()
  {
    storeValues();
    return iPrp;
  }

  /**
   * Sets the auto-resize property. In auto-resize mode the parent of this
   * composite will be re-packed when script arguments are added or removed by
   * the user.
   * 
   * @param bAutoResize
   *          The new auto-layout property.
   * @see #isAutoResize()
   */
  public void setAutoResize(boolean bAutoResize)
  {
    this.bAutoResize = bAutoResize;
  }

  /**
   * Returns the auto-resize property. In auto-resize mode the parent of this
   * composite will be re-packed when script arguments are added or removed by
   * the user.
   * 
   * @see #setAutoResize(boolean)
   */
  public boolean isAutoResize()
  {
    return bAutoResize;
  }
  
  
  // -- Modify listeners --
  
  /**
   * Adds the listener to the collection of listeners who will be notified when
   * the receiver's text is modified, by sending it one of the messages defined
   * in the <code>ModifyListener</code> interface.
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void addModifyListener(ModifyListener iListener)
  {
    if (iListener==null) SWT.error(SWT.ERROR_NULL_ARGUMENT  );
    if (isDisposed()   ) SWT.error(SWT.ERROR_WIDGET_DISPOSED);
    viListeners.add(iListener);
  }
  
  /**
   * Removes the listener from the collection of listeners who will be notified
   * when the receiver's text is modified.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void removedModifyListener(ModifyListener iListener)
  {
    if (iListener==null) SWT.error(SWT.ERROR_NULL_ARGUMENT  );
    if (isDisposed()   ) SWT.error(SWT.ERROR_WIDGET_DISPOSED);
    viListeners.remove(iListener);
  }

  /**
   * Notifies all {@link org.eclipse.swt.events.ModifyListener ModifyListener}s
   * that the contents of this editor has changed.
   */
  private void fireModified()
  {
    Event e = new Event();
    e.widget = this;
    for (int i=0; i<viListeners.size(); i++)
      viListeners.get(i).modifyText(new ModifyEvent(e));
  }
  
  // -- ArgEditor Class --
  
  /**
   * Instances of this class provide an editor for one argument of a dLabPro
   * script.
   */
  private class ArgEditor
  {
    private Label     iLabel;
    private Text      iText;
    private Label     iDescr;
    private Button    iBtnFile;
    private Button    iBtnDir;
    private Composite iToolbar;
    private boolean   bIsExtra;
    
    /**
     * Constructs a new dLabPro script argument editor by adding controls to
     * the parent composite. The constructor assumes a 4 column grid layout of
     * the parent. 
     * 
     * @param iParent
     *          the parent composite
     * @param sLabel
     *          the argument name
     * @param sDescr
     *          the argument description
     * @param bIsExtra
     *          if <code>true</code> the editor is for an extra argument an can
     *          be removed by the user.
     */
    public ArgEditor(Composite iParent, String sLabel, boolean bIsExtra)
    {
      this.bIsExtra = bIsExtra;
      
      iLabel = new Label(iParent,SWT.NULL);
      iLabel.setText(sLabel);
      iLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));

      iText = new Text(iParent,SWT.SINGLE|SWT.BORDER);
      GridData iGd = new GridData(SWT.FILL,SWT.CENTER,true,false);
      iGd.minimumWidth = 200;
      iText.setLayoutData(iGd);
      iText.addModifyListener(new ModifyListener()
      {
        public void modifyText(ModifyEvent e)
        {
          fireModified();
        }
      });

      iDescr = new Label(iParent,SWT.NULL);
      iDescr.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
    
      iToolbar = new Composite(iParent,SWT.NULL);
      iToolbar.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
      GridLayout iGl = new GridLayout(2,false);
      iGl.horizontalSpacing = 1;
      iGl.verticalSpacing = 0;
      iGl.marginHeight = 0;
      iGl.marginWidth = 0;
      iToolbar.setLayout(iGl);
      
      iBtnFile = new Button(iToolbar,SWT.PUSH);
      iBtnFile.setImage(iFileIcon);
      iBtnFile.setToolTipText("Browse for files");
      iBtnFile.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
      iBtnFile.addSelectionListener(new SelectionListener()
      {
        public void widgetDefaultSelected(SelectionEvent e)
        {
        }
        public void widgetSelected(SelectionEvent e)
        {
          Shell iShell = ((Button)e.widget).getParent().getShell();
          FileDialog iFileDlg = new FileDialog(iShell,SWT.OPEN);
          iFileDlg.setText("File selection");
          String sFileName = iFileDlg.open();
          if (sFileName!=null)
          {
            if (File.separatorChar=='\\') sFileName = sFileName.replace('\\','/');
            iText.setText(sFileName);
            fireModified();
          }
        }
      });
      
      iBtnDir = new Button(iToolbar,SWT.PUSH);
      iBtnDir.setImage(iDirIcon);
      iBtnDir.setToolTipText("Browse for directories");
      iBtnDir.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
      iBtnDir.addSelectionListener(new SelectionListener()
      {
        public void widgetDefaultSelected(SelectionEvent e)
        {
        }
        public void widgetSelected(SelectionEvent e)
        {
          Shell iShell = ((Button)e.widget).getParent().getShell();
          DirectoryDialog iPathDlg = new DirectoryDialog(iShell);
          iPathDlg.setText("Directory selection");
          iPathDlg.setMessage("Chose a directory parameter");
          String sFileName = iPathDlg.open();
          if (sFileName!=null)
          {
            if (File.separatorChar=='\\') sFileName = sFileName.replace('\\','/');
            iText.setText(sFileName);
            fireModified();
          }
        }
      });
    }

    /**
     * Disposes of the controls of this editor and removes them from the parent
     * composite.
     */
    public void dispose()
    {
      if (iLabel  !=null) iLabel  .dispose();
      if (iText   !=null) iText   .dispose();
      if (iDescr  !=null) iDescr  .dispose();
      if (iBtnFile!=null) iBtnFile.dispose();
      if (iBtnDir !=null) iBtnDir .dispose();
      if (iToolbar!=null) iToolbar.dispose();
    }
    
    // -- Getters and setters --
    
    /**
     * Sets the contents of the field value editor to the given string.
     * 
     * @param sText
     *          the argument text
     */
    public void setText(String sText)
    {
      iText.setText(sText);
    }
    
    /**
     * Returns the widget text.
     * 
     * @return the widget text
     */
    public String getText()
    {
      return iText.getText();
    }
    
    /**
     * Sets the description text of the edited argument.
     * 
     * @param sDescription the new description text
     */
    public void setDescription(String sDescription)
    {
      iDescr.setText(sDescription!=null?sDescription:"");
    }
    
    /**
     * Returns of the editor is for an extra argument which may be removed.
     * 
     * @return <code>true</code> if and only if the edited argument is extra
     */
    public boolean isExtra()
    {
      return bIsExtra;
    }
    
  }

}
