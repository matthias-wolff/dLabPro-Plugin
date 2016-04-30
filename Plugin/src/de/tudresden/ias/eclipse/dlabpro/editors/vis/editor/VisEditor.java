
package de.tudresden.ias.eclipse.dlabpro.editors.vis.editor;

import java.io.CharConversionException;
import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.operations.TimeTriggeredProgressMonitorDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.xml.sax.SAXException;

import de.tucottbus.kt.jlab.datadisplays.data.DataCompInfo;
import de.tucottbus.kt.jlab.datadisplays.data.DataException;
import de.tucottbus.kt.jlab.datadisplays.events.DisplayEvent;
import de.tucottbus.kt.jlab.datadisplays.events.HdetailEvent;
import de.tucottbus.kt.jlab.datadisplays.events.IDisplayEventListener;
import de.tucottbus.kt.jlab.datadisplays.interfaces.Playable;
import de.tucottbus.kt.jlab.datadisplays.utils.DdUtils;
import de.tucottbus.kt.jlab.datadisplays.utils.PlayActionUtil;
import de.tucottbus.kt.jlab.kernel.JlData;
import de.tucottbus.kt.jlab.kernel.JlDataFile;
import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.X2XmlConverter;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.infoview.DisplayInformationView;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.outline.IOutlinePageListener;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.outline.OutlineEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.outline.VisOutlinePage;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.widgets.VisDataDisplay;

/**
 * the main class of the VisEditor in the dLabPro-Plugin. This class extends the workbench to create a new editor.
 * 
 * @author Stephan Larws
 * 
 */
public class VisEditor extends EditorPart implements IOutlinePageListener,
    IDisplayEventListener, Runnable
{

  private IFile          mResource;
  private String         mFilePath;
  private JlData         midDocument;
  private Composite      mParent;
  private VisDataDisplay mPanel;
  private Action         mRefreshHandler;
  private Action         mPrintHandler;

  private AbstractCollection<Throwable> mXmlWarnings = null;

  // -- Constructors and Setup --

  /**
   * Creates a new data display.
   */
  public VisEditor()
  {
    midDocument = new JlData();
    mvListeners = new Vector<IEditorListener>();
    mPlayActionCreated = false;
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
  throws PartInitException
  {
    setSite(site);
    setInput(input);
    setPartName(input.getName());

    mResource = null;
    mFilePath = null;
    if (input instanceof IFileEditorInput)
    {
      mResource = ((IFileEditorInput)input).getFile();
      mFilePath = mResource.getLocation().toOSString();
    }
    else if (input instanceof IPathEditorInput)
    {
      mFilePath = ((IPathEditorInput)input).getPath().toOSString();
    }
    if (mFilePath==null)
      throw new PartInitException("Invalid editor input");

    load(mFilePath,mXmlWarnings);
  }

  @Override
  public void dispose()
  {
    setAutoRefresh(false);
    super.dispose();
  }

  @Override
  public final void createPartControl(Composite parent)
  {
    mParent = parent;
    
    // Create the component panel
    createComponentPanel(midDocument,getResourceProperty("aDci"));
    
    // Crate a wire actions
    createActions();
    getEditorSite().getActionBars().setGlobalActionHandler
    (
      ActionFactory.REFRESH.getId(),mRefreshHandler
    );
    getEditorSite().getActionBars().setGlobalActionHandler
    (
      ActionFactory.PRINT.getId(),mPrintHandler
    );

    // Do the load warnings dialog.
    if (mXmlWarnings!=null && mXmlWarnings.size()>0)
      doLoadWarningsDialog();
  }

  /**
   * Creates the editor actions.
   */
  private final void createActions()
  {
    mRefreshHandler = new Action()
    {
      public void run()
      {
        reload(false);
        super.run();
      }
    };

    mPrintHandler = new Action()
    {
      public void run()
      {
        PrintDialog dialog = new PrintDialog(DLabProPlugin.getDefault()
            .getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.NULL);
        PrinterData data = dialog.open();
        if (data == null) return;

        final Printer printer = new Printer(data);
        Thread printingThread = new Thread("Printing")
        {
          public void run()
          {
            doPrint(printer);
            printer.dispose();
          }
        };
        printingThread.start();
      }
    };
  }

  private final void createComponentPanel(Exception e)
  {
    mPanel = new VisDataDisplay(mParent,e);
  }

  private final void createComponentPanel(JlData iData, String sProps)
  {
    mPanel = new VisDataDisplay(mParent,iData,sProps);
    mPanel.addDisplayEventListener(this);

    // Read persistent record detail
    int nFirst = 0;
    int nLast  = iData.getLength()-1;
    try
    {
      nFirst = Integer.parseInt(getResourceProperty("recDetailFirst"));
    }
    catch (NumberFormatException e) {}
    try
    {
      nLast = Integer.parseInt(getResourceProperty("recDetailLast"));
    }
    catch (Exception e) {}
    mPanel.setRecDetail(nFirst,nLast,false);

    if (!mPlayActionCreated) updatePlayAction(mPlayAction);
  }
  
  // -- Static API --
  
  /**
   * Retrieves the {@link DisplayInformationView}.
   */
  public static DisplayInformationView findDisplayInformationView()
  {
    try
    {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage();
      IViewPart view = null;
      if (page != null) view = page
          .findView("dLabPro Plugin.display.information");
      return (DisplayInformationView)view;
    }
    catch (NoClassDefFoundError e)
    {
      return null;
    }
  }

  /**
   * Returns the active data display. If the currently active editor is not a
   * {@link VisEditor}, the method returns <code>null</code>. If no editor is
   * active at all, the method returns <code>null</code> as well.
   */
  public static VisEditor getActiveVisEditor()
  {
    IWorkbenchWindow iWw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (iWw != null)
    {
      IWorkbenchPage iWp = iWw.getActivePage();
      if (iWp != null)
      {
        IEditorPart iEp = iWp.getActiveEditor();
        if (iEp instanceof VisEditor) return (VisEditor)iEp;
      }
    }
    return null;
  }
  
  // -- Common Editor Operations --

  @Override
  public boolean isSaveAsAllowed()
  {
    return false;
  }

  @Override
  public boolean isDirty()
  {
    return false;
  }

  @Override
  public void doSave(IProgressMonitor monitor)
  {
    // Data displays cannot be saved
  }

  @Override
  public void doSaveAs()
  {
    // Data displays cannot be saved
  }

  /**
   * Prints the data display as currently seen in the editor.
   */
  public void doPrint(Printer printer)
  {
    mPanel.doPrint(printer,getTitle());
  }
  
  // -- Resource Management --

  /**
   * Loads the specified file.
   * 
   * @param filePath
   *          The file system path.
   * @param warnings
   *          Filled with non-fatal errors and warnings that occurred while
   *          loading the file, can be <code>null</code>.
   * @throws PartInitException TODO: This may not be the appropriate exception type!
   */
  private void load(String filePath, AbstractCollection<Throwable> warnings)
  throws PartInitException
  {
    midDocument = new JlData();
    
    // Try native loading
    try
    {
      System.out.print("\nVisEditor: loading data file ...");
      midDocument = JlDataFile.readXml(new File(filePath),warnings);
    }
    catch (SAXException | CharConversionException e)
    {
      // Try loading as audio file
      if (JlDataFile.isAudioFile(new File(filePath)))
      {
        System.out.print("\nFAILED ("+e.toString()+")");
        System.out.print("\nVisEditor: try loading as audio file ...");
        midDocument = JlDataFile.readAudioFile(new File(filePath),false);
      }
      else
      {
        // Try loading through converter
        System.out.print("\nVisEditor: loading data file trough converter ...");
        String sFmt = X2XmlConverter.canConvert(filePath);
        String cnvFilePath = null;
        if (sFmt == null)
        {
          System.out.print("\nFAILED");
          throw new PartInitException("No suitable converter found.");
        }
        try
        {
          TimeTriggeredProgressMonitorDialog iPm = new TimeTriggeredProgressMonitorDialog(
              PlatformUI.getWorkbench().getDisplay().getActiveShell(),2000);
          X2XmlConverter iCnvt = new X2XmlConverter(filePath,sFmt);
          long nStartTime = System.currentTimeMillis();
          iPm.run(true,true,iCnvt);
          setAutoRefresh(System.currentTimeMillis() - nStartTime < 2000);
          cnvFilePath = iCnvt.getConvertedFileName();
          if (cnvFilePath == null) throw new PartInitException(
              "Error converting data file. " + "Here is the converter's log:\n\n"
                  + iCnvt.getLog());
        }
        catch (Exception e2)
        {
          System.out.print("\nFAILED ("+e2.toString()+")");
          throw new PartInitException("Error converting data file.",e2);
        }
  
        // Read (temporary) XML data file
        if (cnvFilePath!=null)
        {
          try
          {
            midDocument = JlDataFile.readXml(new File(cnvFilePath),warnings);
          }
          catch (Exception e2)
          {
            System.out.print("\nFAILED ("+e2.toString()+")");
            throw new PartInitException("Cannot open converted data file.",e2);
          }
          File f = new File(cnvFilePath);
          f.delete();
        }
      }
    }
    catch (IllegalArgumentException | IOException e)
    {
      throw new PartInitException("Cannot open data file.",e);
    }

    // Consistency checks and automatic corrections
    if (midDocument.rinc==0.) { midDocument.runit=""; midDocument.rinc=1.; }
    if (midDocument.cinc==0.) { midDocument.cunit=""; midDocument.cinc=1.; }
  }

  @Override
  public void reload(boolean bTranspose)
  {
    if (mPanel.isDisposed()) return;
    midDocument = new JlData();
    Rectangle r = mPanel.getBounds();
    mPanel.dispose();

    try
    {
      load(mFilePath,mXmlWarnings);
      createComponentPanel(midDocument,bTranspose ? null
          : getResourceProperty("aDci"));
    }
    catch (PartInitException e)
    {
      DdUtils.EXCEPTION(e);
      createComponentPanel(e);
    }
    if (midDocument != null && bTranspose) midDocument = transposeData(midDocument);

    mPanel.setBounds(r);
    mPanel.redraw();
    mPanel.update();
    mTransposed = bTranspose;
    fireEditorChanged(mTransposed);
  }

  /**
   * Convenience method storing a key-value-pair in the persistent properties
   * store of the editor's resource. The method does nothing f the editor was
   * not created for a file system resource.
   * 
   * @param sKey
   *          The key.
   * @param sProp
   *          The value.
   */
  private final void setResourceProperty(String sKey, String sProp)
  {
    if (mResource == null) return;
    try
    {
      // NOTE: Persistent properties must be shorter than 2 kB :((
      // So split long strings!

      // But first clean up the property store
      int nChunk = 0;
      QualifiedName iQn = new QualifiedName(DLabProPlugin.PLUGIN_NAME,sKey
          + "_chunks");
      String sChunks = mResource.getPersistentProperty(iQn);
      if (sChunks != null) try
      {
        nChunk = Integer.parseInt(sChunks);
      }
      catch (NumberFormatException e)
      {
      }
      for (nChunk--; nChunk >= 0; nChunk--)
      {
        iQn = new QualifiedName(DLabProPlugin.PLUGIN_NAME,sKey + "_" + nChunk);
        mResource.setPersistentProperty(iQn,null);
      }
      if (sProp == null) return;

      // Store property in chunks
      for (nChunk = 0; sProp.length() > 0; nChunk++)
      {
        int nEnd = Math.min(sProp.length(),2000);
        String sPart = sProp.substring(0,nEnd);
        sProp = sProp.substring(nEnd);
        iQn = new QualifiedName(DLabProPlugin.PLUGIN_NAME,sKey + "_" + nChunk);
        mResource.setPersistentProperty(iQn,sPart);
      }
      iQn = new QualifiedName(DLabProPlugin.PLUGIN_NAME,sKey + "_chunks");
      mResource.setPersistentProperty(iQn,String.valueOf(nChunk));
    }
    catch (CoreException e)
    {
      DdUtils.EXCEPTION(e);
    }
  }

  /**
   * Convenience method returning the string value of a persistent property of
   * the editior's resource. The method returns <code>null</code> if the editor
   * was not created for a resource or if <code>sKey</code> does not exist.
   * 
   * @param sKey
   *          The key.
   * @return The value.
   */
  private final String getResourceProperty(String sKey)
  {
    if (mResource == null) return null;
    try
    {
      String sProp = "";
      int nChunks = 0;
      QualifiedName iQn = new QualifiedName(DLabProPlugin.PLUGIN_NAME,sKey
          + "_chunks");
      String sChunks = mResource.getPersistentProperty(iQn);
      if (sChunks != null) try
      {
        nChunks = Integer.parseInt(sChunks);
      }
      catch (NumberFormatException e)
      {
      }
      for (int nChunk = 0; nChunk < nChunks; nChunk++)
      {
        iQn = new QualifiedName(DLabProPlugin.PLUGIN_NAME,sKey + "_" + nChunk);
        sProp += mResource.getPersistentProperty(iQn);
      }
      return sProp;
    }
    catch (CoreException e)
    {
      DdUtils.EXCEPTION(e);
      return null;
    }
  }

  /**
   * Stores the currently set record detail as persistent resource property.
   * Called upon all changes of the record detail.
   */
  private final void storeRecDetail()
  {
    Point rd = mPanel.getRecDetail();
    if (rd.x <= 0 || rd.y >= midDocument.getLength() - 1)
    {
      setResourceProperty("recDetailFirst",null);
      setResourceProperty("recDetailLast",null);
    }
    else
    {
      setResourceProperty("recDetailFirst",String.valueOf(rd.x));
      setResourceProperty("recDetailLast",String.valueOf(rd.y));
    }
  }

  // -- Layout and display operations --

  /**
   * Performs a layout of the data display.
   * 
   * @param nLayout
   *          The layout type, one of the {@link VIS}<code>.CP_STYLE_XXX</code>
   *          constants.
   */
  public void layout(int nLayout)
  {
    // Clear all persistent resource properties
    setResourceProperty("aDci",null);
    setResourceProperty("recDetailFirst",null);
    setResourceProperty("recDetailLast",null);

    // Do auto layout
    try
    {
      DataCompInfo[] aDci = DataCompInfo.createFromData(midDocument,null);
      switch (nLayout)
      {
      case DdUtils.CP_STYLE_OSCILLOGRAM:
        DataCompInfo.oscillogramLayout(aDci);
        break;
      case DdUtils.CP_STYLE_BARDIAGRAM:
        DataCompInfo.barDiagramLayout(aDci);
        break;
      case DdUtils.CP_STYLE_SPECTROGRAM:
        DataCompInfo.spectrogramLayout(aDci);
        break;
      case DdUtils.CP_STYLE_3DVIEW:
        DataCompInfo.threeDLayout(aDci);
        break;
      default:
        // Layout specified through data rtext or auto layout has been done
        // by DataCompInfo.createFromData
      }
      setResourceProperty("aDci",DataCompInfo.toPropString(aDci));
      mPanel.setup(aDci);
      mPanel.setRecDetail(0,midDocument.getLength() - 1,false);
      fireEditorChanged(aDci);
    }
    catch (DataException e)
    {
      DdUtils.EXCEPTION(e);
    }
  }
  
  /**
   * Displays selected components.
   * 
   * @param anComps
   *          Array of zero-based component indices. 
   */
  public void showComponents(int[] anComps)
  {
    if (anComps==null || anComps.length==0) return;

    String sProps = getResourceProperty("aDci");
    DataCompInfo[] aDci;
    try
    {
      aDci = DataCompInfo.createFromData(midDocument,sProps);
      for (int nC=0; nC<aDci.length; nC++) aDci[nC].bVisible=false;
      for (int nC=0; nC<anComps.length; nC++)
        if (anComps[nC]>=0 && anComps[nC]<aDci.length)
          aDci[anComps[nC]].bVisible=true;
      mPanel.setup(aDci);
      fireEditorChanged(aDci);
      setResourceProperty("aDci",DataCompInfo.toPropString(aDci));
    }
    catch (DataException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Horizontal zoom-in.
   */
  public final void zoomIn()
  {
    try
    {
      mPanel.zoomComponentsInHorizontal();
      storeRecDetail();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Horizontal zoom-out.
   */
  public final void zoomOut()
  {
    try
    {
      mPanel.zoomComponentsOutHorizontal();
      storeRecDetail();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Horizontal zoom.
   * 
   * @param nFirst
   *          First visible data record.
   * @param nLast
   *          Last visible data record.
   */
  public final void zoom(int nFirst, int nLast)
  {
    try
    {
      mPanel.setRecDetail(nFirst,nLast,true);
      storeRecDetail();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Full horizontal zoom-out showing all data records.
   */
  public final void zoomAll()
  {
    try
    {
      mPanel.setRecDetail(0,Integer.MAX_VALUE,true);
      storeRecDetail();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  /**
   * Scrolls to the left.
   */
  public void performScrollLeft()
  {
    ScrollBar iHsb = mPanel.getHorizontalBar();
    if (iHsb==null || !iHsb.isVisible() || !iHsb.isEnabled()) return;
    iHsb.setSelection(iHsb.getSelection()-iHsb.getPageIncrement());
    mPanel.scrollComponentsHorizontal();
  }
  
  /**
   * Scrolls to the right.
   */
  public void performScrollRight()
  {
    ScrollBar iHsb = mPanel.getHorizontalBar();
    if (iHsb==null || !iHsb.isVisible() || !iHsb.isEnabled()) return;
    iHsb.setSelection(iHsb.getSelection()+iHsb.getPageIncrement());
    mPanel.scrollComponentsHorizontal();
  }

  // -- Transposition --
  // FIXME: Fix transposition!
  private boolean mTransposed = false;

  public boolean getTransposed()
  {
    return mTransposed;
  }

  /**
   * Transposes a <code>JlData</code> object
   * 
   * @return The transposed object
   */
  private final JlData transposeData(JlData iData)
  {
    JlData iDataT = new JlData();

    iDataT.addNComps(double.class,iData.getLength());

    int newrecs = 0;
    for (int k = 0; k < iData.getDimension(); k++)
      if (JlData.isNumericType(iData.getCompType(k))) newrecs++;

    iDataT.allocate(newrecs);
    iDataT.setNRecs(newrecs);

    int recs = iData.getLength();
    for (int rec = 0; rec < recs; rec++)
    {
      int comps = iData.getDimension();
      int curcomp = 0;
      for (int comp = 0; comp < comps; comp++)
      {
        if (iData.getCompType(comp) != String.class)
        {
          iDataT.dStore(iData.dFetch(rec,comp),curcomp,rec);
          curcomp++;
        }
      }
    }

    iDataT.cunit = iData.runit;
    iDataT.cinc = iData.rinc;
    iDataT.cofs = iData.rofs;
    iDataT.runit = iData.cunit;
    iDataT.rinc = iData.cinc;
    iDataT.rofs = iData.cofs;

    return iDataT;
  }
  
  // -- Outline --

  @SuppressWarnings("rawtypes")
  @Override
  public final Object getAdapter(Class adapter)
  {
    if (adapter.equals(IContentOutlinePage.class))
    {
      return createOutlinePage();
    }
    return super.getAdapter(adapter);
  }

  /**
   * Creates the VisEditor's outline page
   * 
   * @return the outline page
   */
  private VisOutlinePage createOutlinePage()
  {
    VisOutlinePage outline = new VisOutlinePage(midDocument,
        getResourceProperty("aDci"));
    outline.addListener(this);
    addEditorListener(outline);
    return outline;
  }

  @Override
  public void outlineChanged(OutlineEvent e)
  {
    setResourceProperty("aDci",DataCompInfo.toPropString(e.aDci));
    mPanel.setup(e.aDci);
  }

  // -- Events --
  private Vector<IEditorListener> mvListeners;

  /**
   * Adds a new editor listener to this data display.
   * 
   * @param listener
   *          The new listener. The method does noting if this listener is
   *          already registered.
   */
  public final void addEditorListener(IEditorListener listener)
  {
    if (mvListeners.contains(listener)) return;
    mvListeners.add(listener);
  }
  
  /**
   * Notifies all listeners about changes of the display data.
   * 
   * @param aDci
   *          The new display data.
   */
  private final void fireEditorChanged(DataCompInfo[] aDci)
  {
    IEditorListener el;
    for (int i = 0; i < mvListeners.size(); i++)
    {
      el = (IEditorListener)mvListeners.get(i);
      el.editorChanged(new EditorEvent(aDci));
    }
  }

  /**
   * Notifies all listeners about changes of the transposed state.
   * 
   * @param bTranspose
   *          The new transposed state.
   */
  private final void fireEditorChanged(boolean bTranspose)
  {
    IEditorListener el;
    for (int i = 0; i < mvListeners.size(); i++)
    {
      el = (IEditorListener)mvListeners.get(i);
      try
      {
        String sProps = null;
        if (!bTranspose) sProps = getResourceProperty("aDci");
        el.editorChanged(new EditorEvent(DataCompInfo.createFromData(midDocument,
            sProps),bTranspose));
      }
      catch (DataException e)
      {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onDisplayEvent(DisplayEvent de)
  {
    if (de instanceof HdetailEvent) storeRecDetail();
  }

  // -- Sound play-back --

  public IAction  mPlayAction = null;
  private boolean mPlayActionCreated;

  public final void performPlay()
  {
    Playable iPdd = mPanel.canPlay(); // Get (only) playable display
    if (iPdd != null) iPdd.play(mPlayAction); // Have playable display do the work
  }

  public void updatePlayAction(IAction action)
  {
    if (action == null) return;
    try
    {
      mPlayAction = action;
      Playable iPdd = null;
      if (mPanel != null) iPdd = mPanel.canPlay();
      if (iPdd != null) iPdd.updatePlayAction(action);
      else PlayActionUtil.setDisabled(action,
          "there are no or too many playable displays");
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
    mPlayActionCreated = true;
  }
  
  // -- Auto refresh --
  private Thread  mAutoRefreshTread;
  private boolean mAutoRefresh;

  public boolean getAutoRefresh()
  {
    return mAutoRefresh;
  }

  /**
   * Switches the auto refresh surveillance thread on or off.
   * 
   * @param bOn
   *          the new auto refresh state
   */
  public void setAutoRefresh(boolean bOn)
  {
    if (bOn)
    {
      mAutoRefresh = true;
      mAutoRefreshTread = new Thread(this);
      mAutoRefreshTread.start();
    }
    else try
    {
      mAutoRefresh = false;
      if (mAutoRefreshTread != null) mAutoRefreshTread.join();
    }
    catch (InterruptedException e)
    {
    }
  }

  /**
   * Surveillance thread monitoring the synchronization state of the editors
   * resource. If not synchronized, the thread synchronizes the resource and
   * refreshes the display.
   */
  public void run()
  {
    for (int nCounter = 0; mAutoRefresh; nCounter++)
    {
      // Quickly react on request to terminate
      try
      {
        Thread.sleep(50);
      }
      catch (InterruptedException e)
      {
      }

      // Lazily check if resource has changed
      if (nCounter < 20) continue;
      nCounter = 0;

      if (mResource == null) continue;
      try
      {
        if (mResource.isSynchronized(IResource.DEPTH_ZERO)) continue;
      }
      catch (NullPointerException e)
      {
        try
        {
          Thread.sleep(100);
        }
        catch (InterruptedException e1)
        {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
      try
      {
        File f = mResource.getLocation().toFile();
        long nUnmodifiedSince = System.currentTimeMillis() - f.lastModified();
        if (nUnmodifiedSince < 100)
        {
          DdUtils.MSG("Autosync: resource change detected (" + nUnmodifiedSince
              + " ms ago), wait...");
          nCounter = 20;
          continue;
        }
      }
      catch (Exception e)
      {
        // Silently ignore all exceptions
      }
      DdUtils.MSG("Autosync: resource change detected, reloading...");
      try
      {
        mResource.refreshLocal(IResource.DEPTH_ZERO,null);
        mFilePath = mResource.getLocation().toOSString();
      }
      catch (CoreException e)
      {
      }

      PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          reload(false);
        }
      });

    }
  }

  // -- Other Stuff --
  
  @Override
  public void setFocus()
  {
    // Nothing to be done
  }
  
  /**
   * TODO: refurbish!
   */
  private final void doLoadWarningsDialog()
  {
    if (mXmlWarnings==null) return;
    if (mXmlWarnings.size()==0) return;
    Shell sh = mParent.getShell();

    String msg = "There were warnings parsing the XML input. "
               + "The displayed data may be wrong.\n";

    int cnt = 0;
    for (Throwable w : mXmlWarnings)
    {
      msg += "\n" + w.toString();
      if (++cnt==20)
      {
        msg += "\n" + (mXmlWarnings.size()-20) + " more...";
        break;
      }
    }

    MessageBox mb = new MessageBox(sh,SWT.ICON_WARNING | SWT.OK);
    mb.setText("Warning");
    mb.setMessage(msg);
    mb.open();
  }


}
