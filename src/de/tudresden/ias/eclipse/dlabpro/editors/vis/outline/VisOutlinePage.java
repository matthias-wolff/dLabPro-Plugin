
package de.tudresden.ias.eclipse.dlabpro.editors.vis.outline;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.VisColorManager;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.BarDiagram;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.Oscillogram;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.Spectrogram;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.ThreeDDisplay;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataCompInfo;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataException;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.EditorEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.IEditorListener;
import de.tudresden.ias.jlab.kernel.JlData;

/**
 * This class displays the outline of a VisEditor in the OutlineView of the workbench as a table. The rows of the tree
 * represent the different components of the displayed XML file.
 * 
 * @author Stephan Larws
 * 
 */
public class VisOutlinePage implements IContentOutlinePage, IEditorListener,
    SelectionListener
{

  private FormToolkit                  m_iFmtk;
  private ScrolledForm                 m_iForm;
  private Vector<IOutlinePageListener> m_iListeners;
  private DataCompInfo[]               m_aDci;
  private Tree                         m_iTree;
  private Menu                         m_iTreeMenu;
  private Image                        m_iLabCompIcon;
  private Image                        m_iOsciIcon;
  private Image                        m_iBardIcon;
  private Image                        m_iSpecIcon;
  private Image                        m_i3DIcon;
  private Action                       m_iVScaleLockAction;
  private Action                       m_iVZerolineAction;
  private Action                       m_iVCenteredAction;
  private Action                       m_iTransposeAction;
  private ToolAction                   m_iOsciCompAction;
  private ToolAction                   m_iBardCompAction;
  private ToolAction                   m_iSpecCompAction;
  private ToolAction                   m_i3DCompAction;
  private ToolAction                   m_iGrpNewAction;
  private ToolAction                   m_iGrpBrkAction;
  private Text                         m_iMaxVisibleCompsText;

  private final int                    UA_DATAPROPS   = 1 << 0;
  private final int                    UA_DISPLAYTYPE = 1 << 1;
  private final int                    AV_LIMIT       = 1 << 0;
  private final int                    AV_NONE        = 1 << 1;
  private final int                    AV_ALL         = 1 << 2;
  private final int                    AV_FIRST       = 1 << 3;
  private final int                    AV_LAST        = 1 << 4;
  private final int                    AV_NEXT        = 1 << 5;
  private final int                    AV_PREV        = 1 << 6;
  private final int                    AV_SYMBOLIC    = 1 << 7;

  /**
   * TODO: Move this class to a separate source file!?
   * 
   * @author Matthias Wolff
   */
  private abstract class ToolAction implements SelectionListener
  {

    private String   m_sName;

    private int      m_nStyle;

    private Image    m_iImage;

    private MenuItem m_iMi;

    private ToolItem m_iTi;

    /**
     * Creates a new instance of this class
     * 
     * @param sName
     *          The name of the action (Button text)
     * @param nStyle
     *          The SWT style of the <code>Item</code>
     * @param sImageFile
     *          The file name of the image associated with the action (may be <code>null</code>).
     */
    public ToolAction(String sName, int nStyle)
    {
      m_sName = sName;
      m_nStyle = nStyle;
      m_iImage = null;
      m_iMi = null;
      m_iTi = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable
    {
      if (m_iImage != null) m_iImage.dispose();
      super.finalize();
    }

    /**
     * Sets the action's image. Callers do not need to care about disposing of the
     * 
     * @param iImage
     *          the image
     */
    public void setImage(String sImageFilename)
    {
      if (m_iImage != null) m_iImage.dispose();
      m_iImage = DLabProPlugin.loadIconImage(sImageFilename);
    }

    /**
     * Returns the action's image or <code>null</code> if the action does not have an image.
     * 
     * @return the image
     */
    public Image getImage()
    {
      return m_iImage;
    }

    /**
     * Adds this tool action to a menu. Please note: a <code>ToolAction</code> can only be in one menu!
     * 
     * @param iMenu
     *          The menu
     */
    public void addToMenu(Menu iMenu)
    {
      m_iMi = new MenuItem(iMenu,m_nStyle);
      m_iMi.addSelectionListener(this);
      m_iMi.setImage(m_iImage);
      m_iMi.setText(m_sName);
    }

    /**
     * Adds this tool action to a tool bar. Please note: a <code>ToolAction</code> can only be in one tool bar!
     * 
     * @param iMenu
     *          The menu
     */
    public void addToToolBar(ToolBar iToolBar)
    {
      m_iTi = new ToolItem(iToolBar,m_nStyle);
      m_iTi.addSelectionListener(this);
      m_iTi.setImage(m_iImage);
      m_iTi.setToolTipText(m_sName);
    }

    /**
     * Sets the selection state of the action.
     * 
     * @param bSelected
     *          the new selection state
     */
    public void setSelection(boolean bSelected)
    {
      if (m_iMi != null) m_iMi.setSelection(bSelected);
      if (m_iTi != null) m_iTi.setSelection(bSelected);
    }

    /**
     * Returns <code>true</code> if the action is selected, and <code>false</code> otherwise.
     * 
     * @return the selection state
     */
    public boolean getSelection()
    {
      boolean bSelected = false;
      if (m_iMi != null) bSelected = m_iMi.getSelection();
      if (m_iTi != null) bSelected |= m_iTi.getSelection();
      setSelection(bSelected);
      return bSelected;
    }

    /**
     * Enables the action if the argument is <code>true</code>, and disables it otherwise.
     * 
     * @param bEnabled
     *          the new enabled state
     */
    public void setEnabled(boolean bEnabled)
    {
      if (m_iMi != null) m_iMi.setEnabled(bEnabled);
      if (m_iTi != null) m_iTi.setEnabled(bEnabled);
    }

    /**
     * Returns <code>true</code> if the action is enabled, and <code>false</code> otherwise.
     * 
     * @return the enabled state
     */
    public boolean getEnabled()
    {
      boolean bEnabled = false;
      if (m_iMi != null) bEnabled = m_iMi.getEnabled();
      if (m_iTi != null) bEnabled |= m_iTi.getEnabled();
      setEnabled(bEnabled);
      return bEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent iEvent)
    {
      run(iEvent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected(SelectionEvent iEvent)
    {
      run(iEvent);
    }

    /**
     * The default implementation of this method does nothing. Subclasses must override this method.
     */
    abstract void run(TypedEvent iEvent);

  }

  // -- Creation, Destruction and Layout --

  public VisOutlinePage(JlData iData, String sProps)
  {
    m_iListeners = new Vector<IOutlinePageListener>();
    if (iData == null) return;
    try
    {
      m_aDci = DataCompInfo.createFromData(iData,sProps);
    }
    catch (DataException e)
    {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  public void createControl(Composite parent)
  {
    if (m_aDci == null) return;

    m_iFmtk = new FormToolkit(parent.getDisplay());
    m_iForm = m_iFmtk.createScrolledForm(parent);
    m_iForm.setLayoutData(new GridData(GridData.FILL_BOTH));
    m_iForm.getBody().setLayout(new GridLayout(1,false));

    // Create actions
    m_iOsciCompAction = new OsciCompAction();
    m_iBardCompAction = new BardCompAction();
    m_iSpecCompAction = new SpecCompAction();
    m_i3DCompAction   = new ThreeDCompAction();
    m_iGrpNewAction   = new GrpNewAction();
    m_iGrpBrkAction   = new GrpBrkAction();

    // Create widgets
    createIcons();
    createGroupingToolbar(m_iForm.getBody());
    createTree(m_iForm.getBody());
    createNavigationToolbar(m_iForm.getBody());
  }

  /**
   * Loads all the icons
   */
  private final void createIcons()
  {
    m_iLabCompIcon = DLabProPlugin.loadIconImage("icons/obj16/lab_comp_obj.gif");
    m_iOsciIcon = DLabProPlugin.loadIconImage("icons/obj16/osci_obj.gif");
    m_iBardIcon = DLabProPlugin.loadIconImage("icons/obj16/bard_obj.gif");
    m_iSpecIcon = DLabProPlugin.loadIconImage("icons/obj16/spec_obj.gif");
    m_i3DIcon   = DLabProPlugin.loadIconImage("icons/obj16/3D_obj.gif");
  }

  /**
   * Initializes the tool buttons
   * 
   * @param parent
   *          Parent composite
   */
  private final void createGroupingToolbar(Composite parent)
  {
    // Create composite with a form layout
    Composite iPanel = m_iFmtk.createComposite(parent,SWT.NONE);
    FormLayout iLayout = new FormLayout();
    iLayout.marginBottom = 1;
    iLayout.marginTop = 0;
    iPanel.setLayout(iLayout);

    // Create tool bar and add items
    ToolBar iTb = new ToolBar(iPanel,SWT.FLAT | SWT.HORIZONTAL);
    iTb.setBackground(m_iForm.getBackground());
    m_iOsciCompAction.addToToolBar(iTb);
    m_iBardCompAction.addToToolBar(iTb);
    m_iSpecCompAction.addToToolBar(iTb);
    m_i3DCompAction.addToToolBar(iTb);
    // iTi = new ToolItem(iTb,SWT.SEPARATOR);
    m_iGrpNewAction.addToToolBar(iTb);
    m_iGrpBrkAction.addToToolBar(iTb);

    // Layout tool bar (right aligned)
    iTb.pack();
    FormData iFd = new FormData();
    iFd.top = new FormAttachment(0,0);
    iFd.left = new FormAttachment(0,0);
    //iFd.right = new FormAttachment(100,0);
    iTb.setLayoutData(iFd);

    // Layout composite
    GridData iLd = new GridData(GridData.FILL_HORIZONTAL);
    iLd.heightHint = 24;
    iPanel.setLayoutData(iLd);
    iPanel.pack();

  }

  /**
   * Creates the tree. Fills it with the information of the Components
   * 
   * @param parent
   *          Parent composite of tree
   */
  private final void createTree(Composite parent)
  {
    // Create section
    // Section iSec = m_iFmtk.createSection(m_iForm.getBody(), Section.TITLE_BAR|Section.COMPACT);
    // iSec.setText("Components");
    // Composite iTreePanel = m_iFmtk.createComposite(iSec);
    // iTreePanel.setLayout(new GridLayout(1,false));
    // iTreePanel.setLayoutData(new GridData(GridData.FILL_BOTH));

    // Create Tree
    m_iTree = m_iFmtk.createTree(parent,SWT.MULTI | SWT.FULL_SELECTION
        | SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL);
    m_iTree.setHeaderVisible(true);
    m_iTree.setLinesVisible(true);
    TreeColumn iCol = null;
    iCol = new TreeColumn(m_iTree,SWT.LEFT);
    iCol.setText("Component");
    iCol = new TreeColumn(m_iTree,SWT.CENTER);
    iCol.setText("Color");
    iCol = new TreeColumn(m_iTree,SWT.LEFT);
    iCol.setText("Name");
    iCol = new TreeColumn(m_iTree,SWT.LEFT);
    iCol.setText("Type");
    iCol = new TreeColumn(m_iTree,SWT.RIGHT);
    iCol.setText("C-Axis");
    fillTree();
    layoutTreeColumns();
    GridData gd = new GridData(GridData.FILL_BOTH);
    int h = m_iTree.getItemHeight() * 16;
    Rectangle r = m_iTree.computeTrim(0,0,0,h);
    gd.heightHint = r.height;
    m_iTree.setLayoutData(gd);

    // m_iTree.setMenu(menu);

    m_iTree.addListener(SWT.Selection,new Listener()
    {

      public void handleEvent(Event event)
      {
        if (event.detail == SWT.CHECK)
        {
          TreeItem ti = (TreeItem)event.item;
          onTreeItemChecked(ti);
        }
        else if (event.type == SWT.Selection)
        {
          updateActions(UA_DISPLAYTYPE);
        }

      }
    });

    // Create context menu
    m_iTreeMenu = new Menu(parent.getShell());
    m_iOsciCompAction.addToMenu(m_iTreeMenu);
    m_iBardCompAction.addToMenu(m_iTreeMenu);
    m_iSpecCompAction.addToMenu(m_iTreeMenu);
    m_i3DCompAction.addToMenu(m_iTreeMenu);
    new MenuItem(m_iTreeMenu,SWT.SEPARATOR);
    m_iGrpNewAction.addToMenu(m_iTreeMenu);
    m_iGrpBrkAction.addToMenu(m_iTreeMenu);
    m_iTree.setMenu(m_iTreeMenu);

    // Pack
    // iSec.setClient(iTreePanel);
  }

  /**
   * Creates the panel with the buttons below the table for navigating through the components
   * 
   * @param parent
   *          Parent composite
   */
  private final void createNavigationToolbar(Composite parent)
  {
    Composite iPanel = m_iFmtk.createComposite(parent,SWT.NONE);
    FormLayout iLayout = new FormLayout();

    // Layout composite
    iLayout.marginBottom = 0;
    iLayout.marginTop = 5;
    iPanel.setLayout(iLayout);

    // Create widgets
    Label iLab = m_iFmtk.createLabel(iPanel,"Activate");
    iLab.setLayoutData(makeFormBarData(null,0));

    Button iBtn1 = m_iFmtk.createButton(iPanel,"none",SWT.PUSH);
    iBtn1.setData("onActivateNone");
    iBtn1.addSelectionListener(this);
    iBtn1.setLayoutData(makeFormBarData(iLab,5));

    Button iBtn2 = m_iFmtk.createButton(iPanel,"all",SWT.PUSH);
    iBtn2.setData("onActivateAll");
    iBtn2.addSelectionListener(this);
    iBtn2.setLayoutData(makeFormBarData(iBtn1,3));

    Button iBtn3 = m_iFmtk.createButton(iPanel,"symbolic",SWT.PUSH);
    iBtn3.setData("onActivateSymbolic");
    iBtn3.addSelectionListener(this);
    iBtn3.setLayoutData(makeFormBarData(iBtn2,3));

    Label iSep = m_iFmtk.createSeparator(iPanel,SWT.NONE);
    iSep.setLayoutData(makeFormBarData(iBtn3,5));

    m_iMaxVisibleCompsText = m_iFmtk.createText(iPanel,new Integer(
        VIS.VOP_DEF_VISIBLE_DISPLAYS).toString());
    m_iMaxVisibleCompsText.setEditable(true);
    m_iMaxVisibleCompsText.setLayoutData(makeFormBarData(iSep,5));

    Button iBtn4 = m_iFmtk.createButton(iPanel,"|<",SWT.PUSH);
    iBtn4.setData("onActivateFirst");
    iBtn4.addSelectionListener(this);
    iBtn4.setLayoutData(makeFormBarData(m_iMaxVisibleCompsText,3));

    Button iBtn5 = m_iFmtk.createButton(iPanel,"<<",SWT.PUSH);
    iBtn5.setData("onActivatePrevious");
    iBtn5.addSelectionListener(this);
    iBtn5.setLayoutData(makeFormBarData(iBtn4,3));

    Button iBtn6 = m_iFmtk.createButton(iPanel,">>",SWT.PUSH);
    iBtn6.setData("onActivateNext");
    iBtn6.addSelectionListener(this);
    iBtn6.setLayoutData(makeFormBarData(iBtn5,3));

    Button iBtn7 = m_iFmtk.createButton(iPanel,">|",SWT.PUSH);
    iBtn7.setData("onActivateLast");
    iBtn7.addSelectionListener(this);
    iBtn7.setLayoutData(makeFormBarData(iBtn6,3));

    // Layout composite
    GridData iLd = new GridData(GridData.FILL_HORIZONTAL);
    iLd.heightHint = 24;
    iPanel.setLayoutData(iLd);
    iPanel.pack();
  }

  /**
   * Creates a FormData object for creating a vertically centered row of controls
   * 
   * @param iLeftNeighbour
   *          Left neighbor of control for which the FormData is being created
   * @param nOffset
   *          Offset from left neighbor
   * @return
   */
  private final FormData makeFormBarData(Control iLeftNeighbor, int nOffset)
  {
    FormData iFd = new FormData();

    if (iLeftNeighbor == null)
    {
      iFd.top = new FormAttachment(0,0);
      iFd.left = new FormAttachment(0,0);
    }
    else
    {
      iFd.top = new FormAttachment(iLeftNeighbor,0,SWT.CENTER);
      iFd.left = new FormAttachment(iLeftNeighbor,nOffset);
    }
    return iFd;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.part.IPage#setActionBars(org.eclipse.ui.IActionBars)
   */
  public void setActionBars(IActionBars actionBars)
  {
    m_iVScaleLockAction = new VScaleLockAction();
    m_iVZerolineAction = new VZerolineAction();
    m_iVCenteredAction = new VCenteredAction();
    m_iTransposeAction = new TransposeAction();
    //actionBars.getToolBarManager().add(new DebugAction());
    actionBars.getToolBarManager().add(new Separator());
    actionBars.getToolBarManager().add(new SpecModeAction());
    actionBars.getToolBarManager().add(new PaletteAction());
    actionBars.getToolBarManager().add(m_iVScaleLockAction);
    actionBars.getToolBarManager().add(m_iVZerolineAction);
    actionBars.getToolBarManager().add(m_iVCenteredAction);
    actionBars.getToolBarManager().add(m_iTransposeAction);
    actionBars.getToolBarManager().add(new RefreshAction());
    updateActions(UA_DATAPROPS);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.part.IPage#dispose()
   */
  public void dispose()
  {
    if (m_iLabCompIcon != null) m_iLabCompIcon.dispose();
    if (m_iOsciIcon != null) m_iOsciIcon.dispose();
    if (m_iBardIcon != null) m_iBardIcon.dispose();
    if (m_iSpecIcon != null) m_iSpecIcon.dispose();
    if (m_i3DIcon   != null) m_i3DIcon.dispose();
  }

  // -- Tree Layout --

  private final int internal_layoutTreeColumns(GC gc, TreeItem iItm, int nCol)
  {
    int nW = 0;
    TreeItem[] aItm = iItm == null ? m_iTree.getItems() : iItm.getItems();
    // check, so no unexpected behavior occurs
    if (aItm.length > 0 && aItm[0].isDisposed()) { return 0; }

    for (int nItm = 0; nItm < aItm.length; nItm++)
    {
      nW = Math.max(nW,gc.stringExtent(aItm[nItm].getText(nCol)).x);
      if (aItm[nItm].getItemCount() > 0) nW = Math.max(nW,
          internal_layoutTreeColumns(gc,aItm[nItm],nCol));
    }

    return nW;
  }

  /**
   * Adjusts the widths of the tree columns
   */
  private final void layoutTreeColumns()
  {
    TreeColumn[] aCol = m_iTree.getColumns();
    GC gc = new GC(Display.getCurrent());

    for (int nCol = 0; nCol < aCol.length; nCol++)
    {
      int nW;

      // All but the color icon column
      if (nCol != 1)
      {
        // Determine with needed to fit all strings into column
        nW = gc.stringExtent(aCol[nCol].getText()).x + 12;
        nW = Math.max(nW,internal_layoutTreeColumns(gc,null,nCol));

        // Add insets
        nW += 8;
        if (nCol == 0) nW += 32;
      }
      // The color icon column
      else nW = 20;

      // Apply column width
      aCol[nCol].setWidth(nW);
    }

    gc.dispose();
  }

  /**
   * Create a new leaf within the outline tree, pass <code>null</code> as parent if the leaf spawn directly from the
   * tree.
   * 
   * @param parent
   *          The parent tree item
   * @param icon
   *          The shown icon
   * @param comp
   *          The number of the component for which the item is created
   */
  private final void createLeaf(TreeItem parent, Image[] icon, int comp)
  {
    double off = m_aDci[0].iData.cofs;
    double val = m_aDci[0].iData.cinc * comp + off;
    String sVal = String.format("%12.6g",new Object[]
    { Double.valueOf(val) });
    String sName = m_aDci[comp].iData.getCompName(comp);
    String sType = m_aDci[comp].sCompType;

    TreeItem ti = null;
    if (parent == null) ti = new TreeItem(m_iTree,SWT.NONE);
    else ti = new TreeItem(parent,SWT.NONE);

    ti.setData(m_aDci[comp]);
    ti.setText(new String[]
    { "" + comp, "", sName, sType, "" + sVal });
    ti.setImage(icon);
    if (m_aDci[comp].bVisible) ti.setChecked(true);
  }

  /**
   * Creates a group node including its leafs
   * 
   * @param icon
   * @param start
   * @param end
   */
  private final void createParent(int start, int end)
  {
    TreeItem ti = new TreeItem(m_iTree,SWT.NONE);
    String cunit = new String(m_aDci[0].iData.cunit);
    ti.setImage(treeIconForDisplayType(m_aDci[start].sDisplayType,true));
    ti.setText(new String[]
    { "[" + start + "-" + end + "]", "", "", "", "[" + cunit + "]" });
    boolean check = false;

    for (int i = start; i <= end; i++)
    {
      Image iCompIcon = treeIconForDisplayType(m_aDci[i].sDisplayType,false);
      Image iColorIcon = colorIconForDisplayType(m_aDci[i].sDisplayType,i
          - start);
      createLeaf(ti,new Image[]
      { iCompIcon, iColorIcon },i);
      if (m_aDci[i].bVisible) check = true;
    }
    ti.setChecked(check);
    ti.setExpanded(true);
  }

  /**
   * (Re-)fills the outline tree from the component info array <code>m_aDci</code>.
   */
  private final void fillTree()
  {
    int i;
    int nC;
    int nG;
    int nXC = m_aDci.length;
    int nEnd;
    Image iCompIcon;

    // Remember tree state
    HashMap<String,String> iSelected = new HashMap<String,String>();
    TreeItem[] aTi = m_iTree.getSelection();
    for (i = 0; i < aTi.length; i++)
    {
      DataCompInfo aDci = (DataCompInfo)aTi[i].getData();
      if (aDci == null) continue;
      iSelected.put(Integer.toString(aDci.nComp),"");
    }

    // Clear tree
    m_iTree.removeAll();

    // Build new tree from m_aDci
    for (nC = 0, nG = -1; nC < nXC;)
    {
      if (m_aDci[nC].nGroup < 0)
      {
        iCompIcon = treeIconForDisplayType(m_aDci[nC].sDisplayType,false);
        createLeaf(null,new Image[]
        { iCompIcon, null },nC);
        nC++;
        nG = -1;
      }
      else if (m_aDci[nC].nGroup != nG)
      {
        nG = m_aDci[nC].nGroup;
        for (nEnd = nC; nEnd < nXC; nEnd++)
          if (m_aDci[nEnd].nGroup != nG)
          {
            nEnd--;
            break;
          }
        if (nEnd >= nXC) nEnd = nXC - 1;
        createParent(nC,nEnd);
        nC = nEnd + 1;
      }
      else nC++;
    }

    // Reset tree state
    Vector<TreeItem> aSelected = new Vector<TreeItem>();
    aTi = m_iTree.getItems();
    for (i = 0; i < aTi.length; i++)
    {
      TreeItem[] aTc = aTi[i].getItems();
      for (int j = 0; j < aTc.length; j++)
      {
        DataCompInfo aDci = (DataCompInfo)aTc[j].getData();
        if (aDci == null) continue;
        if (iSelected.get(Integer.toString(aDci.nComp)) != null) aSelected
            .add(aTc[j]);
      }
      DataCompInfo aDci = (DataCompInfo)aTi[i].getData();
      if (aDci == null) continue;
      if (iSelected.get(Integer.toString(aDci.nComp)) != null) aSelected
          .add(aTi[i]);
    }
    aTi = new TreeItem[aSelected.size()];
    m_iTree.setSelection((TreeItem[])aSelected.toArray(aTi));

    // Update actions
    updateActions(UA_DISPLAYTYPE);
  }

  /**
   * Returns the tree icon for a display type (DataDisplay class name)
   * 
   * @param sDisplayType
   *          Canonical name of DataDisplay class
   * @param bGroup
   *          If <code>true</code> return the icon for a component group insted of the icon for a single component
   * @return The icon or <code>null</code> in case of errors
   */
  Image treeIconForDisplayType(String sDisplayType, boolean bGroup)
  {
    if (bGroup)
    {
      if (sDisplayType.endsWith("Oscillogram"  )) return m_iOsciIcon;
      if (sDisplayType.endsWith("BarDiagram"   )) return m_iBardIcon;
      if (sDisplayType.endsWith("Spectrogram"  )) return m_iSpecIcon;
      if (sDisplayType.endsWith("ThreeDDisplay")) return m_i3DIcon;
    }
    else
    {
      if (sDisplayType.endsWith("Oscillogram")) return m_iOsciCompAction
          .getImage();
      if (sDisplayType.endsWith("BarDiagram")) return m_iBardCompAction
          .getImage();
      if (sDisplayType.endsWith("Spectrogram")) return m_iSpecCompAction
          .getImage();
      if (sDisplayType.endsWith("ThreeDDisplay")) return m_i3DCompAction
          .getImage();
      if (sDisplayType.endsWith("LabelDisplay")) return m_iLabCompIcon;
    }
    return null;
  }

  /**
   * Returns the color icon for a display type (DataDisplay class name) and component index
   * 
   * @param sDisplayType
   *          Canonical name of DataDisplay class
   * @param nComp
   *          Zero-based component index <em>within</em> group
   * @return The icon or <code>null</code> in case of errors
   */
  Image colorIconForDisplayType(String sDisplayType, int nComp)
  {
    if (sDisplayType.endsWith("Spectrogram"  )) return null;
    if (sDisplayType.endsWith("LabelDisplay" )) return null;
    if (sDisplayType.endsWith("ThreeDDisplay")) return null;

    VisColorManager iCm = new VisColorManager();
    return iCm.getCompColorIcon(nComp);
  }

  // -- Actions --

  /**
   * Switch spectrogram color palette.
   */
  class PaletteAction extends Action
  {
    PaletteAction()
    {
      super("Change Spectrogram Palette");
      setImageDescriptor(DLabProPlugin.getIconImage("icons/elcl16/spectcolor_edit.gif"));
    }

    public void run()
    {
      VisColorManager iCm = new VisColorManager();
      iCm.switchValueColors(-1);
      fireOutlineChangedEvent();
    }
  }

  /**
   * Switch spectrogram color palette.
   */
  class SpecModeAction extends Action
  {
    SpecModeAction()
    {
      super("Toggle level curves / value colors");
      setImageDescriptor(DLabProPlugin.getIconImage("icons/elcl16/spectmode_edit.gif"));
    }

    public void run()
    {
      if (VIS.bSpecShowValues && VIS.bSpecShowLevels)
      {
        VIS.bSpecShowValues = false;
      }
      else if (VIS.bSpecShowValues && !VIS.bSpecShowLevels)
      {
        VIS.bSpecShowLevels = true;
      }
      else if (!VIS.bSpecShowValues && VIS.bSpecShowLevels)
      {
        VIS.bSpecShowValues = true;
        VIS.bSpecShowLevels = false;
      }
      fireOutlineChangedEvent();
    }
  }

  /**
   * For debuggin' use; comment references out for release versions!
   */
  class DebugAction extends Action
  {
    DebugAction()
    {
      super("DEBUGGIN'");
      setImageDescriptor(DLabProPlugin.getIconImage("icons/elcl16/debuggin_edit.gif"));
    }

    public void run()
    {
    }
  }
  
  /**
   * Forces the value axis to include zero.
   */
  class VScaleLockAction extends Action
  {
    VScaleLockAction()
    {
      super("Lock Value Axes' Scales",AS_CHECK_BOX);
      setImageDescriptor(DLabProPlugin.getIconImage("icons/elcl16/vscalelock_edit.gif"));
    }

    public void run()
    {
      setVScales(m_iVScaleLockAction.isChecked(),
          m_iVCenteredAction.isChecked(),m_iVZerolineAction.isChecked());
      fireOutlineChangedEvent();
    }

  }

  /**
   * Forces the value axis to include zero.
   */
  class VZerolineAction extends Action
  {
    VZerolineAction()
    {
      super("Value Axes Contain Zero Lines",AS_CHECK_BOX);
      setImageDescriptor(DLabProPlugin.getIconImage("icons/elcl16/vzeroline_edit.gif"));
    }

    public void run()
    {
      setVScales(m_iVScaleLockAction.isChecked(),
          m_iVCenteredAction.isChecked(),m_iVZerolineAction.isChecked());
      fireOutlineChangedEvent();
    }

  }

  /**
   * Forces the value axis to be symmetric to zero.
   */
  class VCenteredAction extends Action
  {
    VCenteredAction()
    {
      super("Value Axes Symmetric",AS_CHECK_BOX);
      setImageDescriptor(DLabProPlugin.getIconImage("icons/elcl16/vcentered_edit.gif"));
    }

    public void run()
    {
      setVScales(m_iVScaleLockAction.isChecked(),
          m_iVCenteredAction.isChecked(),m_iVZerolineAction.isChecked());
      fireOutlineChangedEvent();
    }

  }

  /**
   * Refreshes the data display by reloading <em>and</em> transposing the underlying resource
   */
  class TransposeAction extends Action
  {
    TransposeAction()
    {
      super("Transpose",AS_CHECK_BOX);
      setImageDescriptor(DLabProPlugin.getIconImage("icons/elcl16/transpose_edit.gif"));
    }

    public void run()
    {
      fireReloadEvent(isChecked());
    }
  }

  /**
   * Refreshes the data display by reloading the underlying resource
   */
  class RefreshAction extends Action
  {
    RefreshAction()
    {
      super("Refresh");
      setImageDescriptor(DLabProPlugin.getIconImage("icons/elcl16/refresh_edit.gif"));
    }

    public void run()
    {
      fireReloadEvent(false);
    }
  }

  /**
   * Displays the selected components as oscillograms.
   */
  class OsciCompAction extends ToolAction
  {

    public OsciCompAction()
    {
      super("Oscillograms",SWT.PUSH);
      setImage(Oscillogram.getIconFileName());
    }

    void run(TypedEvent event)
    {
      VIS.MSG("OsciCompAction.run()");
      setCompDisplayType(Oscillogram.class.getCanonicalName());
    }

  }

  /**
   * Displays the selected components as bar diagrams.
   */
  class BardCompAction extends ToolAction
  {

    public BardCompAction()
    {
      super("Bar Diagrams",SWT.PUSH);
      setImage(BarDiagram.getIconFileName());
    }

    void run(TypedEvent event)
    {
      setCompDisplayType(BarDiagram.class.getCanonicalName());
    }

  }

  /**
   * Displays the selected components as spectrogram.
   */
  class SpecCompAction extends ToolAction
  {

    public SpecCompAction()
    {
      super("Spectrogram",SWT.PUSH);
      setImage(Spectrogram.getIconFileName());
    }

    void run(TypedEvent event)
    {
      setCompDisplayType(Spectrogram.class.getCanonicalName());
    }

  }

  /**
   * Displays the selected components as spectrogram.
   */
  class ThreeDCompAction extends ToolAction
  {

    public ThreeDCompAction()
    {
      super("3D",SWT.PUSH);
      setImage(ThreeDDisplay.getIconFileName());
    }

    void run(TypedEvent event)
    {
      setCompDisplayType(ThreeDDisplay.class.getCanonicalName());
    }

  }

  /**
   * Creates a new group display.
   */
  class GrpNewAction extends ToolAction
  {

    public GrpNewAction()
    {
      super("Create Component Group",SWT.PUSH);
      setImage("icons/elcl16/grpnew_nav.gif");
    }

    void run(TypedEvent event)
    {
      // Validate
      TreeItem aSel[] = getGrpNewSelection();
      if (aSel == null) return;

      // Create a new group ID
      int nGroup = -1;
      for (int nC = 0; nC < m_aDci.length; nC++)
        if (m_aDci[nC].nGroup > nGroup) nGroup = m_aDci[nC].nGroup;
      nGroup++;

      // Group the selection
      String sDisplayType = ((DataCompInfo)aSel[0].getData()).sDisplayType;
      for (int i = 0; i < aSel.length; i++)
      {
        ((DataCompInfo)aSel[i].getData()).nGroup = nGroup;
        ((DataCompInfo)aSel[i].getData()).sDisplayType = sDisplayType;
      }

      // Update tree and editor
      fillTree();
      fireOutlineChangedEvent();
    }

  }

  /**
   * Breaks a group display
   */
  class GrpBrkAction extends ToolAction
  {

    public GrpBrkAction()
    {
      super("Break Component Group",SWT.PUSH);
      setImage("icons/elcl16/grpbrk_nav.gif");
    }

    void run(TypedEvent event)
    {
      // Validate
      TreeItem aSel[] = getGrpBrkSelection();
      if (aSel == null) return;

      // Break group
      aSel = aSel[0].getItems();
      for (int i = 0; i < aSel.length; i++)
      {
        DataCompInfo iDci = (DataCompInfo)aSel[i].getData();
        iDci.nGroup = -1;
      }

      // Update tree and editor
      fillTree();
      autoVisibility(AV_LIMIT,VIS.VOP_DEF_VISIBLE_DISPLAYS,false);
      fireOutlineChangedEvent();
    }

  }

  // -- Button Event Handlers --

  protected void onActivateAll(Widget iWidget)
  {
    autoVisibility(AV_ALL,VIS.VOP_MAX_VISIBLE_DISPLAYS,true);
  }

  protected void onActivateNone(Widget iWidget)
  {
    autoVisibility(AV_NONE,VIS.VOP_MAX_VISIBLE_DISPLAYS,true);
  }

  protected void onActivateSymbolic(Widget iWidget)
  {
    autoVisibility(AV_SYMBOLIC,VIS.VOP_MAX_VISIBLE_DISPLAYS,true);
  }

  protected void onActivateNext(Widget iWidget)
  {
    autoVisibility(AV_NEXT,getMaxVisibleComps(),true);
  }

  protected void onActivatePrevious(Widget iWidget)
  {
    autoVisibility(AV_PREV,getMaxVisibleComps(),true);
  }

  protected void onActivateFirst(Widget iWidget)
  {
    autoVisibility(AV_FIRST,getMaxVisibleComps(),true);
  }

  protected void onActivateLast(Widget iWidget)
  {
    autoVisibility(AV_LAST,getMaxVisibleComps(),true);
  }

  // -- Update Handlers --

  /**
   * Determines the set of components whose display type is to be changed trough {@link onCompDisplayType}. The
   * selection may be arbitrary set of leafs which do not belong to any group. All selected components must be
   * numerical. Other selections are inappropriate and the method will return <code>null</code>.
   * 
   * @return Array containing the tree items whose display type is to be changed or <code>null</code>
   */
  protected TreeItem[] getCompDisplayTypeSelection()
  {
    TreeItem aSel[] = m_iTree.getSelection();
    if (aSel.length == 0) return null;

    for (int i = 0; i < aSel.length; i++)
    {
      if (aSel[i].getParentItem() != null) return null;
      DataCompInfo iDci = (DataCompInfo)aSel[i].getData();
      if (iDci == null) continue;
      if (!JlData.isNumericType(iDci.iData.getCompType(iDci.nComp))) return null;
    }
    return aSel;
  }

  /**
   * Determines the group to create by {@link onGrpNew()} from the current selection of the component tree. The
   * selection must consist of at least two consecutive component leafs which do not belong to any group. All selected
   * components must be numerical. Other selections are inappropriate and the method will return <code>null</code>.
   * 
   * @return Array containing the tree items to group or <code>null</code>
   */
  protected TreeItem[] getGrpNewSelection()
  {
    TreeItem aSel[] = m_iTree.getSelection();
    if (aSel.length < 2) return null;

    int nC = -1;
    for (int i = 0; i < aSel.length; i++)
    {
      if (aSel[i].getParentItem() != null) return null;
      DataCompInfo iDci = (DataCompInfo)aSel[i].getData();
      if (!JlData.isNumericType(iDci.iData.getCompType(iDci.nComp))) return null;
      if (nC < 0 || nC + 1 == iDci.nComp) nC = iDci.nComp;
      else return null;
    }
    return aSel;
  }

  /**
   * Determines the group to break by {@link onGrpBreak()} from the current selection of the component tree. The
   * selection must be exactly one group leaf or an arbitrary selection of component leafs in one and the same group.
   * Other selections are inappropriate and the method will return <code>null</code>.
   * 
   * @return Array containing exactly one tree item (the group to break) or <code>null</code>
   */
  protected TreeItem[] getGrpBrkSelection()
  {
    TreeItem aSel[] = m_iTree.getSelection();
    if (aSel.length == 0) return null;

    // Determine group to break
    TreeItem iGroup = null;
    if (aSel[0].getParentItem() == null)
    {
      if (aSel[0].getItems().length == 0) return null;
      if (aSel.length > 1) return null;
      iGroup = aSel[0];
    }
    else
    {
      iGroup = aSel[0].getParentItem();
      for (int i = 0; i < aSel.length; i++)
        if (aSel[i].getParentItem() != iGroup) return null;
    }

    // Return array with one element (group to break)
    TreeItem[] aRet =
    { iGroup };
    return aRet;
  }

  /**
   * Updates the checked and enabled states of the outline actions.
   * 
   * @param nHint
   *          Combination of <code>UA_XXX</code> constants
   */
  private final void updateActions(int nHint)
  {
    if (m_aDci == null) return;

    if ((nHint & UA_DATAPROPS) != 0)
    {
      boolean bVCentered = true;
      boolean bVZeroline = true;
      boolean bVLock     = true;
      double nMin = 0;
      double nMax = 0;
      int    nCnt = 0;
      for (int nC = 0; nC < m_aDci.length; nC++)
        if (m_aDci[nC].bVisible)
          if (JlData.isNumericType(m_aDci[nC].iData.getCompType(nC)))
          {
            if (nCnt==0)
            {
              nMin = m_aDci[nC].nMin;
              nMax = m_aDci[nC].nMax;
            }
            else
            {
              if (nMin!=m_aDci[nC].nMin) bVLock = false;
              if (nMax!=m_aDci[nC].nMax) bVLock = false;
            }
            if (m_aDci[nC].nMax != -m_aDci[nC].nMin) bVCentered = false;
            if (m_aDci[nC].nMin > 0 || m_aDci[nC].nMax < 0) bVZeroline = false;
            nCnt++;
          }
      //if (bVCentered) bVZeroline = false;
      m_iVScaleLockAction.setChecked(nCnt > 0 && bVLock    );
      m_iVCenteredAction .setChecked(nCnt > 0 && bVCentered);
      m_iVZerolineAction .setChecked(nCnt > 0 && bVZeroline);
    }

    if ((nHint & UA_DISPLAYTYPE) != 0)
    {
      boolean bEnabled = getCompDisplayTypeSelection() != null;
      m_iOsciCompAction.setEnabled(bEnabled);
      m_iBardCompAction.setEnabled(bEnabled);
      m_iSpecCompAction.setEnabled(bEnabled);
      m_i3DCompAction.setEnabled(bEnabled);
      m_iGrpNewAction.setEnabled(getGrpNewSelection() != null);
      m_iGrpBrkAction.setEnabled(getGrpBrkSelection() != null);
    }
  }

  // -- Display Structure Operations --

  /**
   * Changes the display type of the items currently selected in the component tree. The display type is defined through
   * <code>iWidget</code>.
   * 
   * @param iWidget
   *          Widget firing the event
   */
  protected void setCompDisplayType(String sDisplayType)
  {
    // Validate
    TreeItem aSel[] = getCompDisplayTypeSelection();
    if (aSel == null) return;

    // Switch display type
    for (int i = 0; i < aSel.length; i++)
    {
      TreeItem[] aGrp = aSel[i].getItems();
      if (aGrp.length == 0) ((DataCompInfo)aSel[i].getData()).sDisplayType = sDisplayType;
      else for (int j = 0; j < aGrp.length; j++)
        ((DataCompInfo)aGrp[j].getData()).sDisplayType = sDisplayType;
    }

    // Update tree and editor
    fillTree();
    fireOutlineChangedEvent();
  }

  /**
   * Veeery tricky! Don't do at home!
   * 
   * @param nHint
   *          one of the <code>AV_XXX</code> constants
   */
  private final void autoVisibility(int nHint, int nMaxVisible,
      boolean bDoUpdate)
  {
    TreeItem[] aTi = m_iTree.getItems();
    int i = 0;
    int nVisible = 0;
    int nFirstToCheck = -1;
    int nFirstChecked = -1;
    int nLastChecked = -1;

    // Initialize
    for (i = 0; i < aTi.length; i++)
    {
      if (!aTi[i].getChecked()) continue;
      DataCompInfo iDci = (DataCompInfo)aTi[i].getData();
      if (iDci != null) if (!JlData.isNumericType(iDci.iData
          .getCompType(iDci.nComp))) continue;
      if (nFirstChecked == -1) nFirstChecked = i;
      nLastChecked = i;
    }
    if ((nHint & AV_FIRST) != 0) nFirstToCheck = 0;
    else if ((nHint & AV_LAST) != 0) nFirstToCheck = aTi.length - nMaxVisible;
    else if ((nHint & AV_NEXT) != 0) nFirstToCheck = nLastChecked + 1;
    else if ((nHint & AV_PREV) != 0) nFirstToCheck = nFirstChecked
        - nMaxVisible;
    if (nFirstToCheck < 0) nFirstToCheck = 0;
    if (nFirstToCheck > aTi.length - nMaxVisible) nFirstToCheck = aTi.length
        - nMaxVisible;

    VIS.MSG("- autoVisibility()");
    VIS.MSG("  nFirstChecked = " + nFirstChecked);
    VIS.MSG("  nLastChecked  = " + nLastChecked);
    VIS.MSG("  nFirstToCheck = " + nFirstToCheck);

    // Clear some or all checked root items
    if ((nHint & AV_SYMBOLIC) == 0) for (i = 0, nVisible = 0; i < aTi.length; i++)
    {
      if (aTi[i].getChecked()) nVisible++;
      DataCompInfo iDci = (DataCompInfo)aTi[i].getData();
      if (iDci != null && (nHint & AV_NONE) == 0) if (!JlData
          .isNumericType(iDci.iData.getCompType(iDci.nComp))) continue;
      if (((nHint & AV_LIMIT) != 0) && nVisible <= nMaxVisible) continue;
      if (iDci != null) iDci.bVisible = false;
      aTi[i].setChecked(false);
    }

    // Check-up
    if ((nHint & AV_LIMIT) != 0 || (nHint & AV_NONE) != 0)
    {
      if (bDoUpdate) fireOutlineChangedEvent();
      return;
    }

    // Check some or all root items
    for (i = 0, nVisible = 0; i < aTi.length; i++)
    {
      DataCompInfo iDci = (DataCompInfo)aTi[i].getData();
      boolean bCheck = iDci != null
          && !JlData.isNumericType(iDci.iData.getCompType(iDci.nComp));
      if (!bCheck && (nHint & AV_LIMIT) == 0 && (nHint & AV_SYMBOLIC) == 0)
      {
        if (i < nFirstToCheck) continue;
        if (nVisible >= nMaxVisible) continue;
        bCheck = true;
      }
      if (!bCheck) continue;
      if (iDci != null) iDci.bVisible = true;
      aTi[i].setChecked(true);
      nVisible++;
    }

    // Do update now?
    if (bDoUpdate) fireOutlineChangedEvent();
  }

  /**
   * Changes the visibility of one or several components.
   * 
   * @param nStart
   *          Zero-based index of first component
   * @param nLength
   *          Number of components
   * @param bVisible
   *          New visibility
   */
  private final void changeVisibility(int nStart, int nLength, boolean bVisible)
  {
    if (m_aDci == null) return;
    if (nStart < 0) nStart = 0;
    if (nStart + nLength > m_aDci.length) nLength = m_aDci.length - nStart;
    if (nLength <= 0) return;

    boolean bChanged = false;
    for (int nC = nStart; nC < nStart + nLength; nC++)
    {
      if (m_aDci[nC].bVisible != bVisible) bChanged = true;
      m_aDci[nC].bVisible = bVisible;
    }
    if (bChanged) fireOutlineChangedEvent();
  }

  /**
   * Reads, verifies and, if necessray, corrects the value of the {@link m_iMaxVisibleCompsText} text field.
   * 
   * @return The maximal number of simultaneously visible display
   */
  private final int getMaxVisibleComps()
  {
    int nMaxVisibleComps = VIS.VOP_DEF_VISIBLE_DISPLAYS;
    try
    {
      nMaxVisibleComps = Integer.parseInt(m_iMaxVisibleCompsText.getText());
    }
    catch (NumberFormatException e)
    {
      nMaxVisibleComps = VIS.VOP_DEF_VISIBLE_DISPLAYS;
      m_iMaxVisibleCompsText.setText(new Integer(nMaxVisibleComps).toString());
    }
    if (nMaxVisibleComps < 1)
    {
      nMaxVisibleComps = 1;
      m_iMaxVisibleCompsText.setText(new Integer(nMaxVisibleComps).toString());
    }
    if (nMaxVisibleComps > VIS.VOP_MAX_VISIBLE_DISPLAYS)
    {
      nMaxVisibleComps = VIS.VOP_MAX_VISIBLE_DISPLAYS;
      m_iMaxVisibleCompsText.setText(new Integer(nMaxVisibleComps).toString());
    }

    return nMaxVisibleComps;
  }

  /**
   * Sets the value scale options.
   * 
   * @param bLock
   *          All value axes have the same scale
   * @param bVcenter
   *          All axes are centerd around 0 (min=-max)
   * @param bZero
   *          Force all axes to contain the zero line (min<=0<=max)
   */
  private final void setVScales(boolean bLock, boolean bVcenter, boolean bZero)
  {
    int nC;
    double nMin = Double.MAX_VALUE;
    double nMax = -Double.MAX_VALUE;
    for (nC = 0; nC < m_aDci.length; nC++)
    {
      if (!m_aDci[nC].bVisible) continue;
      m_aDci[nC].computeMinMax(bVcenter,bZero);
      if (m_aDci[nC].nMax>nMax) nMax = m_aDci[nC].nMax; 
      if (m_aDci[nC].nMin<nMin) nMin = m_aDci[nC].nMin;
    }
    if (nMax == nMin)
    {
      nMin = nMax - 1.0;
      nMax = nMax + 1.0;
    }
    if (bLock)
    {
      if (bZero)
      {
        if (nMax<0) nMax = 0;
        if (nMin>0) nMin = 0;
      }
      if (bVcenter)
      {
        nMax = Math.max(Math.abs(nMax),Math.abs(nMin));
        nMin = -nMax;
      }
      for (nC = 0; nC < m_aDci.length; nC++)
      {
        if (!m_aDci[nC].bVisible) continue;
        m_aDci[nC].nMax = nMax; 
        m_aDci[nC].nMin = nMin;
      }
    }
    updateActions(UA_DATAPROPS);
  }
  
  // -- Events --

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
   */
  public void widgetDefaultSelected(SelectionEvent e)
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
   */
  @SuppressWarnings("unchecked")
  public void widgetSelected(SelectionEvent e)
  {
    try
    {
      Method iHdl = null;
      Class[] iSgn =
      { Widget.class };
      Object[] iArg =
      { e.widget };
      iHdl = getClass().getDeclaredMethod((String)e.widget.getData(),iSgn);
      iHdl.invoke(this,iArg);
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
    }
  }

  /**
   * Finds the components that belong to the checked item and fires a visibility changed event.
   * 
   * @param item
   *          The {@link TreeItem} that was selected
   */
  private final void onTreeItemChecked(TreeItem item)
  {
    int index = m_iTree.indexOf(item);
    TreeItem[] items = m_iTree.getItems();
    int start = 0;
    int length = 0;
    // top level was checked
    if (index > -1)
    {
      // count the components in front of the selected subtree
      for (int i = 0; i < index; i++)
      {
        length = items[i].getItems().length;
        if (length == 0) length = 1;

        start += length;
      }
      length = items[index].getItems().length;
      if (length == 0) length = 1;
      // so the parent item is checked as well
      // mTree.getItem(index).setChecked(item.getChecked());
    }
    // a lower level check occurred
    else
    {
      for (int i = 0; i < items.length; i++)
      {
        index = items[i].indexOf(item);
        if (index == -1)
        {
          length = items[i].getItems().length;
          if (length == 0) length = 1;

          start += length;
        }
        else
        {
          start += index;
          length = 1;
          break;
        }
      }
    }
    changeVisibility(start,length,item.getChecked());
  }

  /**
   * Notifies all <code>IOutlinePageListeners</code> that the grouping, visibility and/or display type was changed by
   * the user.
   */
  private final void fireOutlineChangedEvent()
  {
    IOutlinePageListener listener;
    if (m_iListeners != null) for (int i = 0; i < m_iListeners.size(); i++)
    {
      listener = (IOutlinePageListener)m_iListeners.get(i);
      listener.outlineChanged(new OutlineEvent(m_aDci));
    }
  }

  /**
   * Notifies all <code>IOutlinePageListeners</code> that the user requested
   * a refresh or transpose.
   * @param bTranspose
   *          Transpose flag
   */
  private final void fireReloadEvent(boolean bTranspose)
  {
    IOutlinePageListener listener;
    if (m_iListeners != null) for (int i = 0; i < m_iListeners.size(); i++)
    {
      listener = (IOutlinePageListener)m_iListeners.get(i);
      listener.reload(bTranspose);
    }
  }

  /*
   * (non-Javadoc)
   * @see de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.IEditorListener#editorChanged(de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.EditorEvent)
   */
  public final void editorChanged(EditorEvent e)
  {
    if (e.hasDataCompInfo()) m_aDci = e.aDci;
    if (e.hasTranspose()) m_iTransposeAction.setChecked(e.isTransposed());
    updateActions(UA_DATAPROPS);
    fillTree();
  }

  // -- Service --

  /**
   * Adds an outline page event listener.
   * @param listener
   *          The listener
   */
  public void addListener(IOutlinePageListener listener)
  {
    if (m_iListeners != null) m_iListeners.add(listener);
  }

  /**
   * Removes an outline page event listener.
   * @param listener
   *          The listener
   */
  public void removeListener(IOutlinePageListener listener)
  {
    if (m_iListeners != null) m_iListeners.remove(listener);
  }

  // -- SWT Interface Implementations --

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.part.IPage#getControl()
   */
  public Control getControl()
  {
    return m_iForm;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.part.IPage#setFocus()
   */
  public void setFocus()
  {
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
   */
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
   */
  public ISelection getSelection()
  {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
   */
  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
   */
  public void setSelection(ISelection selection)
  {
  }

}

// EOF
