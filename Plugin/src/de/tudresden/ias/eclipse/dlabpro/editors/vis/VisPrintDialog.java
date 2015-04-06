package de.tudresden.ias.eclipse.dlabpro.editors.vis;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.tucottbus.kt.jlab.datadisplays.DdPreferenceConstants;
import de.tucottbus.kt.jlab.datadisplays.utils.DpiConverter;
import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.Comparator;

public class VisPrintDialog extends TitleAreaDialog implements ModifyListener,
DdPreferenceConstants
{
  private static final String sDefaultMessage = "Data displays are printed as seen on screen.";
  
  private Printer      iPrinter;
  private int          nRetVal;
  private Image        iTitleImg;
  private Image        iInfoImg;
  private String       sDisplayTitle;
  private Point        R;
  private Rectangle    C;
  private Rectangle    T;
  private DpiConverter iXc;
  private DpiConverter iYc;
  private String       sPrevUnit;
  
  private Combo   m_iUnits; 
  private Text    m_iMrgnLeft;
  private Text    m_iMrgnTop;
  private Text    m_iMrgnRight;
  private Text    m_iMrgnBottom;
  private Button  m_iSizeFitWidth;
  private Button  m_iSizeFitPage;
  private Button  m_iSizeUser;
  private Label   m_iLblWidth;
  private Text    m_iWidth;
  private Label   m_iLblHeight;
  private Text    m_iHeight;
  private Label   m_iLblSizeHint;
  private Label   m_iLblSizeHint2;
  private Combo   m_iStyle;
  private Button  m_iPrntTitle;
  private Button  m_iPrntRulers;
  private Button  m_iPrntScale;
  private Button  m_iPrntTicks;
  private Button  m_iPrntCanvas;
  private Button  m_iPrntMarkers;
  private Combo   m_iFont;
  private Combo   m_iFontSize;
  private Combo   m_iLineWidth;
  
  public  double  nMrgnLeft    = 25;
  public  double  nMrgnTop     = 10;
  public  double  nMrgnRight   = 10;
  public  double  nMrgnBottom  = 10;
  public  int     nFitMode     = 0;
  public  double  nWidth       = 100;
  public  double  nHeight      = 100;
  public  int     nStyle       = 0;
  public  boolean bPrntTitle   = true;
  public  boolean bPrntRulers  = true;
  public  boolean bPrntScale   = true;
  public  boolean bPrntTicks   = true;
  public  boolean bPrntCanvas  = true;
  public  boolean bPrntMarkers = true;
  public  double  nLineWidth   = 0.5;
  public  int     nFontSize    = 9;
  public  String  sFont        = "DIN-Regular";
  
  /**
   * Constructs a new print dialog.
   * 
   * @param iProperties
   * @throws NullPointerException if <code>iProperties</code> is <code>null</code>
   */
  public VisPrintDialog(Printer iPrinter, String sDisplayTitle)
  {
    super(null);
    this.iPrinter = iPrinter;
    this.sDisplayTitle = sDisplayTitle; 
    iTitleImg = DLabProPlugin.loadIconImage("icons/wizban/visprint_wiz.png" );
    iInfoImg  = DLabProPlugin.loadIconImage("icons/obj16/information.gif"   );
    R   = iPrinter.getDPI();
    C   = iPrinter.getClientArea();
    T   = iPrinter.computeTrim(0,0,0,0);
    iXc = new DpiConverter(R.x);
    iYc = new DpiConverter(R.y);

    // Default landscape margins
    if (C.width>C.height)
    {
      nMrgnLeft = 10;
      nMrgnTop  = 25;
    }
  }

  /**
   * Opens the VIS print dialog from outside the UI thread
   * @return the dialog return code
   */
  public int syncOpen()
  {
    try
    {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          nRetVal = open();
        }
      });
      return nRetVal;
    }
    catch (NoClassDefFoundError e)
    {
      return CANCEL;
    }
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed()
  {
    storeValues(DLabProPlugin.getDefault().getPreferenceStore());
    super.okPressed();
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.TrayDialog#close()
   */
  @Override
  public boolean close()
  {
    if (iTitleImg != null) iTitleImg.dispose();
    if (iInfoImg  != null) iInfoImg.dispose();
    return super.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
   */
  @Override
  protected void configureShell(Shell newShell)
  {
    newShell.setText("Data Display Print Options ...");
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

    setTitle("for " + (sDisplayTitle!=null ? sDisplayTitle : "unnamed display"));
    setTitleImage(iTitleImg);
    setMessage(sDefaultMessage,IMessageProvider.INFORMATION);

    return contents;
  }

  protected Label addLabel(Composite iParent, String sText, int nHalign, int nHspan)
  {
    Label iLbl = new Label(iParent,SWT.NONE);
    if (sText!=null) iLbl.setText(sText);
    GridData iGd = new GridData(nHalign,SWT.CENTER,false,false);
    iGd.horizontalSpan = nHspan;
    iLbl.setLayoutData(iGd);
    return iLbl;
  }
  
  protected void addSeparator(Composite iParent, int nHspan)
  {
    Label iLbl = new Label(iParent,SWT.SEPARATOR|SWT.HORIZONTAL);
    GridData iGd = new GridData(SWT.FILL,SWT.CENTER,true,false);
    iGd.horizontalSpan = nHspan;
    iLbl.setLayoutData(iGd);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @SuppressWarnings("unchecked")
  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite iCps;
    GridData  iGd;
    
    Composite iDlg = new Composite(parent,SWT.NO_RADIO_GROUP);
    iDlg.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
    GridLayout iGl = new GridLayout(2,false);
    iGl.horizontalSpacing = 25;
    iDlg.setLayout(iGl);
    
    // Page margins section
    addLabel(iDlg,"Page margins",SWT.LEFT,1);
    iCps = new Composite(iDlg,SWT.NULL);
    iCps.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
    iGl = new GridLayout(7,false);
    iGl.horizontalSpacing = 1;
    iGl.verticalSpacing = 1;
    iGl.marginWidth = 0;
    iGl.marginHeight = 0;
    iCps.setLayout(iGl);

    // - Line 1
    addLabel(iCps,"top ",SWT.RIGHT,2);
    m_iMrgnTop = new Text(iCps,SWT.SINGLE|SWT.BORDER|SWT.RIGHT);
    m_iMrgnTop.setToolTipText("The top margin");
    iGd = new GridData(SWT.FILL,SWT.FILL,false,false); iGd.widthHint = 50;
    m_iMrgnTop.setLayoutData(iGd);
    m_iMrgnTop.addModifyListener(this);
    addLabel(iCps,null,SWT.CENTER,4);

    // - Line 2
    addLabel(iCps,"left ",SWT.RIGHT,1);
    m_iMrgnLeft = new Text(iCps,SWT.SINGLE|SWT.BORDER|SWT.RIGHT);
    m_iMrgnLeft.setToolTipText("The left margin");
    iGd = new GridData(SWT.FILL,SWT.FILL,false,false); iGd.widthHint = 50;
    m_iMrgnLeft.setLayoutData(iGd);
    m_iMrgnLeft.addModifyListener(this);
    addLabel(iCps,null,SWT.CENTER,1);
    m_iMrgnRight = new Text(iCps,SWT.SINGLE|SWT.BORDER|SWT.RIGHT);
    m_iMrgnRight.setToolTipText("The right margin");
    iGd = new GridData(SWT.FILL,SWT.FILL,false,false); iGd.widthHint = 50;
    m_iMrgnRight.setLayoutData(iGd);
    m_iMrgnRight.addModifyListener(this);
    addLabel(iCps," right     ",SWT.LEFT,1);
    addLabel(iCps,"Units  ",SWT.LEFT,1);
    m_iUnits = new Combo(iCps,SWT.DROP_DOWN|SWT.READ_ONLY);
    m_iUnits.setToolTipText("The unit to measure margins and diagram dimensions in");
    m_iUnits.add("mm");
    m_iUnits.add("inch");
    m_iUnits.add("pt");
    m_iUnits.select(0);
    m_iUnits.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,false));
    m_iUnits.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        String sUnit = m_iUnits.getText();
        convertUnit(m_iMrgnLeft  ,sPrevUnit,sUnit);
        convertUnit(m_iMrgnTop   ,sPrevUnit,sUnit);
        convertUnit(m_iMrgnRight ,sPrevUnit,sUnit);
        convertUnit(m_iMrgnBottom,sPrevUnit,sUnit);
        convertUnit(m_iWidth     ,sPrevUnit,sUnit);
        convertUnit(m_iHeight    ,sPrevUnit,sUnit);
        sPrevUnit = sUnit;
      }
    });

    // - Line 3
    addLabel(iCps,"bottom ",SWT.RIGHT,2);
    m_iMrgnBottom = new Text(iCps,SWT.SINGLE|SWT.BORDER|SWT.RIGHT);
    m_iMrgnBottom.setToolTipText("The bottom margin");
    iGd = new GridData(SWT.FILL,SWT.FILL,false,false); iGd.widthHint = 50;
    m_iMrgnBottom.setLayoutData(iGd);
    m_iMrgnBottom.addModifyListener(this);
    addLabel(iCps,null,SWT.CENTER,4);
    addSeparator(iDlg,2);
    
    // Diagram size section
    addLabel(iDlg,"Diagram size",SWT.LEFT,1);
    m_iSizeFitWidth = new Button(iDlg,SWT.RADIO);
    m_iSizeFitWidth.setLayoutData(new GridData(SWT.LEFT,SWT.FILL,false,false));
    m_iSizeFitWidth.setText("Fit page width");
    m_iSizeFitWidth.setToolTipText("Fit width of page, aspect will be as on screen (if possible)");
    m_iSizeFitWidth.addSelectionListener(new SelectionListener(){
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        if (m_iSizeFitWidth.getSelection()) setFitMode(0);
      }
    });
    addLabel(iDlg,null,SWT.LEFT,1);
    m_iSizeFitPage = new Button(iDlg,SWT.RADIO);
    m_iSizeFitPage.setLayoutData(new GridData(SWT.LEFT,SWT.FILL,false,false));
    m_iSizeFitPage.setText("Fit page");
    m_iSizeFitPage.setToolTipText("Fit width and height of page");
    m_iSizeFitPage.addSelectionListener(new SelectionListener(){
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        if (m_iSizeFitPage.getSelection()) setFitMode(1);
      }
    });
    addLabel(iDlg,null,SWT.LEFT,1);
    iCps = new Composite(iDlg,SWT.NO_RADIO_GROUP);
    iCps.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
    iGl = new GridLayout(5,false);
    iGl.horizontalSpacing = 3;
    iGl.verticalSpacing = 0;
    iGl.marginWidth = 0;
    iGl.marginHeight = 0;
    iCps.setLayout(iGl);
    m_iSizeUser = new Button(iCps,SWT.RADIO);
    m_iSizeUser.setLayoutData(new GridData(SWT.LEFT,SWT.FILL,false,false));
    m_iSizeUser.setText("User defined:");
    m_iSizeUser.setToolTipText("User defined dimensions (excluding rulers/axes)");
    m_iSizeUser.addSelectionListener(new SelectionListener(){
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        if (m_iSizeUser.getSelection()) setFitMode(2);
      }
    });
    m_iLblWidth = addLabel(iCps,"width",SWT.RIGHT,1);
    m_iWidth = new Text(iCps,SWT.SINGLE|SWT.BORDER|SWT.RIGHT);
    iGd = new GridData(SWT.FILL,SWT.FILL,false,false); iGd.widthHint = 50;
    m_iWidth.setLayoutData(iGd);
    m_iWidth.addModifyListener(this);
    m_iLblHeight = addLabel(iCps,"height",SWT.RIGHT,1);
    m_iHeight = new Text(iCps,SWT.SINGLE|SWT.BORDER|SWT.RIGHT);
    iGd = new GridData(SWT.FILL,SWT.FILL,false,false); iGd.widthHint = 50;
    m_iHeight.setLayoutData(iGd);
    m_iHeight.addModifyListener(this);
    addLabel(iCps,"",SWT.LEFT,1);
    m_iLblSizeHint = addLabel(iCps,"",SWT.RIGHT,1);
    m_iLblSizeHint.setImage(iInfoImg);
    m_iLblSizeHint2 = addLabel(iCps,"Dimensions excluding rulers/axes!",SWT.LEFT,3);
    addSeparator(iDlg,2);
    
    // Diagram Style Section
    addLabel(iDlg,"Diagram style",SWT.LEFT,1);
    m_iStyle = new Combo(iDlg,SWT.DROP_DOWN|SWT.READ_ONLY);
    m_iStyle.add("chart");
    m_iStyle.add("diagram");
    m_iStyle.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,false));
    addSeparator(iDlg,2);
    
    // Print ... Section
    addLabel(iDlg,"Print ...",SWT.LEFT,1);
    m_iPrntTitle = new Button(iDlg,SWT.CHECK);
    m_iPrntTitle.setText("Diagram title");
    m_iPrntTitle.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
    addLabel(iDlg,null,SWT.LEFT,1);
    m_iPrntRulers = new Button(iDlg,SWT.CHECK);
    m_iPrntRulers.setText("Rulers/axes");
    m_iPrntRulers.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
    m_iPrntRulers.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
      public void widgetSelected(SelectionEvent e)
      {
        m_iPrntScale.setEnabled(m_iPrntRulers.getSelection());
        m_iPrntTicks.setEnabled(m_iPrntRulers.getSelection());
      }
    });
    addLabel(iDlg,null,SWT.LEFT,1);
    m_iPrntScale = new Button(iDlg,SWT.CHECK);
    m_iPrntScale.setText("Scale lines");
    iGd = new GridData(SWT.LEFT,SWT.CENTER,false,false);
    iGd.horizontalIndent = 20;
    m_iPrntScale.setLayoutData(iGd);
    addLabel(iDlg,null,SWT.LEFT,1);
    m_iPrntTicks = new Button(iDlg,SWT.CHECK);
    m_iPrntTicks.setText("Scale ticks");
    iGd = new GridData(SWT.LEFT,SWT.CENTER,false,false);
    iGd.horizontalIndent = 20;
    m_iPrntTicks.setLayoutData(iGd);
    addLabel(iDlg,null,SWT.LEFT,1);
    m_iPrntCanvas = new Button(iDlg,SWT.CHECK);
    m_iPrntCanvas.setText("Guidelines (if applicable)");
    m_iPrntCanvas.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
    addLabel(iDlg,null,SWT.LEFT,1);
    m_iPrntMarkers = new Button(iDlg,SWT.CHECK);
    m_iPrntMarkers.setText("Markes and legend (if applicable)");
    m_iPrntMarkers.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
    addSeparator(iDlg,2);
    
    // Font/Line Section
    addLabel(iDlg,"Font",SWT.LEFT,1);
    iCps = new Composite(iDlg,SWT.NULL);
    iCps.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
    iGl = new GridLayout(3,false);
    iGl.horizontalSpacing = 2;
    iGl.marginWidth = 0;
    iGl.marginHeight = 0;
    iCps.setLayout(iGl);
    m_iFont = new Combo(iCps,SWT.READ_ONLY|SWT.DROP_DOWN);
    m_iFont.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));

    FontData[] aiFonts = iPrinter.getFontList(null,true);
    Arrays.sort(aiFonts,new Comparator()
    {
      public int compare(Object o1, Object o2)
      {
        FontData fd1 = (FontData)o1;
        FontData fd2 = (FontData)o2;
        return fd1.getName().compareToIgnoreCase(fd2.getName());
      }
    });
    ArrayList<FontData> alFonts = new ArrayList<FontData>();
    for (int i=0; i<aiFonts.length; i++) alFonts.add(aiFonts[i]);
    aiFonts = iPrinter.getFontList(null,false);
    for (int i=0; i<aiFonts.length; i++) alFonts.add(aiFonts[i]);
    for (int i=0; i<alFonts.size(); i++)
    {
      String sName = alFonts.get(i).getName();
      if (sName.startsWith("@")) continue;
      if (sName.trim().equals("")) continue;
      if ((alFonts.get(i).getStyle() & SWT.BOLD  )!=0) sName += " Bold";
      if ((alFonts.get(i).getStyle() & SWT.ITALIC)!=0) sName += " Italic";
      if (m_iFont.indexOf(sName)<0)
      {
        m_iFont.add(sName);
        m_iFont.setData(sName,alFonts.get(i).toString());
      }
    }
    m_iFont.setVisibleItemCount(20);
    m_iFontSize = new Combo(iCps,SWT.READ_ONLY|SWT.DROP_DOWN);
    m_iFontSize.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
    m_iFontSize.add("4");
    m_iFontSize.add("5");
    m_iFontSize.add("6");
    m_iFontSize.add("7");
    m_iFontSize.add("8");
    m_iFontSize.add("9");
    m_iFontSize.add("10");
    m_iFontSize.add("11");
    m_iFontSize.add("12");
    m_iFontSize.add("14");
    m_iFontSize.add("16");
    m_iFontSize.add("18");
    m_iFontSize.add("24");
    m_iFontSize.setVisibleItemCount(11);
    addLabel(iCps,"pt",SWT.LEFT,1);

    addLabel(iDlg,"Line Width",SWT.LEFT,1);
    iCps = new Composite(iDlg,SWT.NULL);
    iCps.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
    iGl = new GridLayout(3,false);
    iGl.horizontalSpacing = 2;
    iGl.marginWidth = 0;
    iGl.marginHeight = 0;
    iCps.setLayout(iGl);
    m_iLineWidth = new Combo(iCps,SWT.READ_ONLY|SWT.DROP_DOWN);
    m_iLineWidth.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
    m_iLineWidth.add("0.1");
    m_iLineWidth.add("0.25");
    m_iLineWidth.add("0.33");
    m_iLineWidth.add("0.5");
    m_iLineWidth.add("0.75");
    m_iLineWidth.add("1");
    m_iLineWidth.add("1.5");
    m_iLineWidth.add("2");
    m_iLineWidth.add("4");
    m_iLineWidth.setVisibleItemCount(9);
    addLabel(iCps,"pt",SWT.LEFT,1);
    
    addSeparator(iDlg,2);

    // Initialize Values
    // HACK: Post it to come after standard radio button initialization
    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        initializeValues(DLabProPlugin.getDefault().getPreferenceStore());
      }
    });
    
    return iDlg;
  }
  
  protected void setFitMode(int nMode)
  {
    switch (nMode)
    {
    case 1: 
      m_iSizeFitWidth.setSelection(false);
      m_iSizeFitPage.setSelection(true);
      m_iSizeUser.setSelection(false);
      m_iLblHeight.setEnabled(false);
      m_iLblWidth.setEnabled(false);
      m_iHeight.setEnabled(false);
      m_iWidth.setEnabled(false);
      m_iLblSizeHint.setEnabled(false);
      m_iLblSizeHint2.setEnabled(false);
      break;
    case 2: 
      m_iSizeFitWidth.setSelection(false);
      m_iSizeFitPage.setSelection(false);
      m_iSizeUser.setSelection(true);
      m_iLblHeight.setEnabled(true);
      m_iLblWidth.setEnabled(true);
      m_iHeight.setEnabled(true);
      m_iWidth.setEnabled(true);
      m_iLblSizeHint.setEnabled(true);
      m_iLblSizeHint2.setEnabled(true);
      break;
    default: 
      m_iSizeFitWidth.setSelection(true);
      m_iSizeFitPage.setSelection(false);
      m_iSizeUser.setSelection(false);
      m_iLblHeight.setEnabled(false);
      m_iLblWidth.setEnabled(false);
      m_iHeight.setEnabled(false);
      m_iWidth.setEnabled(false);
      m_iLblSizeHint.setEnabled(false);
      m_iLblSizeHint2.setEnabled(false);
      break;
    }
    nFitMode = nMode;
  }

  protected void convertUnit(Text iTxt, String sPrevUnit, String sUnit)
  {
    try
    {
      double nVal = Double.valueOf(iTxt.getText());
      nVal = iXc.x2y(nVal,sPrevUnit,sUnit);
      iTxt.setText(String.valueOf(nVal));
    }
    catch (NumberFormatException e)
    {
    }
  }
  
  public boolean checkText(Text iTxt, String sName, DpiConverter iC, double nMin, double nMax, boolean bCorrect)
  {
    double nVal;
    nMin = iC.x2y(nMin,"px",m_iUnits.getText());
    nMax = iC.x2y(nMax,"px",m_iUnits.getText());
    try
    {
      nVal = Double.valueOf(iTxt.getText());
      if (bCorrect)
      {
        if (nVal<nMin) iTxt.setText(String.valueOf(nMin));
        if (nVal>nMax) iTxt.setText(String.valueOf(nMax));
      }
      else
      {
        if (nVal<0   ) return onError(sName+" must not be negative.");
        if (nVal<nMin) return onError(sName+" is too small."        );
        if (nVal>nMax) return onError(sName+" is too large."        );
      }
    }
    catch (NumberFormatException ex)
    {
      if (bCorrect)
        iTxt.setText(String.valueOf(nMin));
      else
        return onError(sName+" is not a valid number.");
    }
    return onError(null);
  }

  public boolean checkText(Text iTxt, String sName, DpiConverter iC, double nMin, double nMax)
  {
    return checkText(iTxt,sName,iC,nMin,nMax,false);
  }
  
  protected boolean onError(String sMsg)
  {
    Button iOkButton = getButton(OK);
    if (sMsg==null)
    {
      setErrorMessage(null);
      if (iOkButton!=null) iOkButton.setEnabled(true);
      return true;
    }
    else
    {
      setErrorMessage(sMsg);
      if (iOkButton!=null) iOkButton.setEnabled(false);
      return false;
    }
  }
  
  public void modifyText(ModifyEvent e)
  {
    if (getButton(OK)==null) return;
    boolean bOk = true;
    if (bOk) bOk = checkText(m_iMrgnLeft  ,"Left margin"    ,iXc,-1*T.x      ,C.width );
    if (bOk) bOk = checkText(m_iMrgnTop   ,"Top margin"     ,iYc,-1*T.y      ,C.height);
    if (bOk) bOk = checkText(m_iMrgnRight ,"Right margin"   ,iXc,T.x+T.width ,C.width );
    if (bOk) bOk = checkText(m_iMrgnBottom,"Bottom margin"  ,iYc,T.y+T.height,C.height);
    if (bOk) bOk = checkText(m_iWidth     ,"Diagram width"  ,iXc,0           ,C.width );
    if (bOk) bOk = checkText(m_iHeight    ,"Diagram height" ,iYc,0           ,C.height);
  }

  // -- Preference store methods --

  /**
   * Initializes the controls page with the default values in the preference store.
   * <p><b style="color:red">TODO:</b> Not yet implemented!</p>
   * 
   * @param iStore
   *          the preference store
   */
  protected void initializeDefaults(IPreferenceStore iStore)
  {
  }
  
  /**
   * Initializes the controls page with the values in the preference store.
   * 
   * @param iStore
   *          the preference store
   */
  protected void initializeValues(IPreferenceStore iStore)
  {
    // Fill editors
    m_iUnits      .setText(               iStore.getString(P_VIS_PRINT_UNITS     ) );
    m_iMrgnLeft   .setText(String.valueOf(iStore.getDouble(P_VIS_PRINT_MRGNLEFT  )));
    m_iMrgnRight  .setText(String.valueOf(iStore.getDouble(P_VIS_PRINT_MRGNRIGHT )));
    m_iMrgnTop    .setText(String.valueOf(iStore.getDouble(P_VIS_PRINT_MRGNTOP   )));
    m_iMrgnBottom .setText(String.valueOf(iStore.getDouble(P_VIS_PRINT_MRGNBOTTOM)));
    m_iWidth      .setText(String.valueOf(iStore.getDouble(P_VIS_PRINT_WIDTH     )));
    m_iHeight     .setText(String.valueOf(iStore.getDouble(P_VIS_PRINT_HEIGHT    )));
    setFitMode(iStore.getInt(P_VIS_PRINT_FITMODE));
    m_iStyle      .select      (iStore.getInt    (P_VIS_PRINT_STYLE  ));
    m_iPrntTitle  .setSelection(iStore.getBoolean(P_VIS_PRINT_TITLE  ));
    m_iPrntRulers .setSelection(iStore.getBoolean(P_VIS_PRINT_RULERS ));
    m_iPrntScale  .setSelection(iStore.getBoolean(P_VIS_PRINT_SCALE  ));
    m_iPrntScale  .setEnabled  (iStore.getBoolean(P_VIS_PRINT_RULERS ));
    m_iPrntTicks  .setSelection(iStore.getBoolean(P_VIS_PRINT_TICKS  ));
    m_iPrntTicks  .setEnabled  (iStore.getBoolean(P_VIS_PRINT_RULERS ));
    m_iPrntCanvas .setSelection(iStore.getBoolean(P_VIS_PRINT_CANVAS ));
    m_iPrntMarkers.setSelection(iStore.getBoolean(P_VIS_PRINT_MARKERS));

    String sFont = iStore.getString(P_VIS_PRINT_FONT);
    int nIdx = m_iFont.indexOf(sFont); 
    if (nIdx<0)
      nIdx = m_iFont.indexOf(iPrinter.getSystemFont().getFontData()[0].getName());
    if (nIdx<0)
      nIdx = m_iFont.indexOf(getShell().getDisplay().getSystemFont().getFontData()[0].getName());
    if (nIdx<0)
    {
      m_iFont.add(getShell().getDisplay().getSystemFont().getFontData()[0].getName());
      nIdx=m_iFont.getItemCount()-1;
    }
    m_iFont.select(nIdx);
    m_iFontSize .setText(String.valueOf(iStore.getInt   (P_VIS_PRINT_FONTSIZE )));
    m_iLineWidth.setText(String.valueOf(iStore.getDouble(P_VIS_PRINT_LINEWIDTH)));
    
    sPrevUnit = m_iUnits.getText();
    
    // Check printer margins
    checkText(m_iMrgnLeft  ,"Left margin"  ,iXc,-1*T.x      ,C.width ,true);
    checkText(m_iMrgnTop   ,"Top margin"   ,iYc,-1*T.y      ,C.height,true);
    checkText(m_iMrgnRight ,"Right margin" ,iXc,T.x+T.width ,C.width ,true);
    checkText(m_iMrgnBottom,"Bottom margin",iYc,T.y+T.height,C.height,true);    
  }

  /**
   * Stores the contents of the controls into the preference store.
   * 
   * @param iStore
   *          the preference store
   */
  protected void storeValues(IPreferenceStore iStore)
  {
    // Store control contents into fields
    String sUnit = m_iUnits.getText();
    nMrgnLeft    = iXc.x2y(Double.valueOf(m_iMrgnLeft  .getText()),sUnit,"mm");
    nMrgnTop     = iYc.x2y(Double.valueOf(m_iMrgnTop   .getText()),sUnit,"mm");
    nMrgnRight   = iXc.x2y(Double.valueOf(m_iMrgnRight .getText()),sUnit,"mm");
    nMrgnBottom  = iYc.x2y(Double.valueOf(m_iMrgnBottom.getText()),sUnit,"mm");
    nWidth       = iXc.x2y(Double.valueOf(m_iWidth     .getText()),sUnit,"mm");
    nHeight      = iYc.x2y(Double.valueOf(m_iHeight    .getText()),sUnit,"mm");
    if (m_iSizeFitWidth.getSelection()) nFitMode = 0;
    if (m_iSizeFitPage .getSelection()) nFitMode = 1;
    if (m_iSizeUser    .getSelection()) nFitMode = 2;
    nStyle       = m_iStyle      .getSelectionIndex();
    bPrntTitle   = m_iPrntTitle  .getSelection();
    bPrntRulers  = m_iPrntRulers .getSelection();
    bPrntScale   = m_iPrntScale  .getSelection();
    bPrntTicks   = m_iPrntTicks  .getSelection();
    bPrntCanvas  = m_iPrntCanvas .getSelection();
    bPrntMarkers = m_iPrntMarkers.getSelection();
    nFontSize    = Integer.valueOf(m_iFontSize.getText());
    nLineWidth   = Double.valueOf(m_iLineWidth.getText());
    sFont        = (String)m_iFont.getData(m_iFont.getText());
    if (sFont==null) sFont = iPrinter.getSystemFont().getFontData()[0].getName();

    // Store control contents into preference store
    iStore.setValue(P_VIS_PRINT_UNITS     ,sUnit                                  );
    iStore.setValue(P_VIS_PRINT_MRGNLEFT  ,Double.valueOf(m_iMrgnLeft  .getText()));
    iStore.setValue(P_VIS_PRINT_MRGNTOP   ,Double.valueOf(m_iMrgnTop   .getText()));
    iStore.setValue(P_VIS_PRINT_MRGNRIGHT ,Double.valueOf(m_iMrgnRight .getText()));
    iStore.setValue(P_VIS_PRINT_MRGNBOTTOM,Double.valueOf(m_iMrgnBottom.getText()));
    iStore.setValue(P_VIS_PRINT_WIDTH     ,Double.valueOf(m_iWidth     .getText()));
    iStore.setValue(P_VIS_PRINT_HEIGHT    ,Double.valueOf(m_iHeight    .getText()));
    iStore.setValue(P_VIS_PRINT_FITMODE   ,nFitMode                               );
    iStore.setValue(P_VIS_PRINT_STYLE     ,m_iStyle      .getSelectionIndex()     );
    iStore.setValue(P_VIS_PRINT_TITLE     ,m_iPrntTitle  .getSelection()          );
    iStore.setValue(P_VIS_PRINT_RULERS    ,m_iPrntRulers .getSelection()          );
    iStore.setValue(P_VIS_PRINT_SCALE     ,m_iPrntScale  .getSelection()          );
    iStore.setValue(P_VIS_PRINT_TICKS     ,m_iPrntTicks  .getSelection()          );
    iStore.setValue(P_VIS_PRINT_CANVAS    ,m_iPrntCanvas .getSelection()          );
    iStore.setValue(P_VIS_PRINT_MARKERS   ,m_iPrntMarkers.getSelection()          );
    iStore.setValue(P_VIS_PRINT_FONTSIZE  ,Integer.valueOf(m_iFontSize.getText()) );
    iStore.setValue(P_VIS_PRINT_LINEWIDTH ,Double.valueOf(m_iLineWidth.getText()) );
    iStore.setValue(P_VIS_PRINT_FONT      ,m_iFont.getText()                      );
  }  
  
  /**
   * Called by
   * {@link de.tudresden.ias.eclipse.dlabpro.preferences.PreferenceInitializer#initializeDefaultPreferences()}
   * in order to define the default print settings.
   * 
   * @param iStore
   *          The preference store to inialize
   */
  public static void defineDefaults(IPreferenceStore iStore)
  {
    iStore.setDefault(P_VIS_PRINT_UNITS     ,"mm"         );
    iStore.setDefault(P_VIS_PRINT_MRGNLEFT  ,25.          );
    iStore.setDefault(P_VIS_PRINT_MRGNTOP   ,10.          );
    iStore.setDefault(P_VIS_PRINT_MRGNRIGHT ,10.          );
    iStore.setDefault(P_VIS_PRINT_MRGNBOTTOM,10.          );
    iStore.setDefault(P_VIS_PRINT_WIDTH     ,100.         );
    iStore.setDefault(P_VIS_PRINT_HEIGHT    ,100.         );
    iStore.setDefault(P_VIS_PRINT_FITMODE   ,0            );
    iStore.setDefault(P_VIS_PRINT_STYLE     ,0            );
    iStore.setDefault(P_VIS_PRINT_TITLE     ,true         );
    iStore.setDefault(P_VIS_PRINT_RULERS    ,true         );
    iStore.setDefault(P_VIS_PRINT_SCALE     ,true         );
    iStore.setDefault(P_VIS_PRINT_TICKS     ,true         );
    iStore.setDefault(P_VIS_PRINT_CANVAS    ,true         );
    iStore.setDefault(P_VIS_PRINT_MARKERS   ,true         );
    iStore.setDefault(P_VIS_PRINT_FONT      ,"DIN-Regular");
    iStore.setDefault(P_VIS_PRINT_FONTSIZE  ,9            );
    iStore.setDefault(P_VIS_PRINT_LINEWIDTH ,0.5          );
  }

}
