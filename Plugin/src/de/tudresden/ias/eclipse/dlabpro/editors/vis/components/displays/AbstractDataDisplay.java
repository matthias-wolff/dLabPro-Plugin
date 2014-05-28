// dLabPro Plugin for Eclipse
// - VisEditor data displays' base class
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays;

import java.util.LinkedList;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VisColorManager;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.AbstractComponent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoMouseMoveEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoScrollEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.IDisplayEventListener;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.Ruler;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.RulerCalculator;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataCompInfo;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataException;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.VisEditor;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.infoview.DisplayInformationView;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.infoview.IDisplayInfoListener;
import de.tudresden.ias.jlab.kernel.JlData;

public abstract class AbstractDataDisplay extends AbstractComponent
{

  // -- DRAW FLAGS -------------------------------------------------------------
  public static final int DRAW_CANVAS  = 0x0001;
  public static final int DRAW_DATA    = 0x0002;
  public static final int DRAW_MARKERS = 0x0004;
  public static final int DRAW_ALL     = 0xFFFF;
  
	// -- FIELDS -----------------------------------------------------------------

	/**
	 * Array of data component information objects defining the data to be
	 * displayed
	 */
	protected DataCompInfo[] m_aDci;

	/**
	 * The vertical Ruler of this Display, this attribute is not seen by derived
	 * classes, but it references the same ruler created by the derived classes
	 */
	private Ruler m_iVruler;

	/**
	 * The horizontal ruler of this display, cannot be <code>null</code>
	 */
	protected Ruler m_iHruler;

	/**
	 * Zero-based index of first record to be shown
	 */
	protected int m_nFirstRec;

	/**
	 * Zero-based index of last record to be shown
	 */
	protected int m_nLastRec;

	/**
	 * Zero-based index of first component to be shown
	 */
	protected int m_nFirstComp;

	/**
	 * Zero-based index of last component to be shown
	 */
	protected int m_nLastComp;

	/**
	 * Zero-based index of first component of this display's component set
	 */
	protected int m_nMinComp;

	/**
	 * Zero-based index of last component of this display's component set
	 */
	protected int m_nMaxComp;

	/**
	 * All VisEditors' semi-static common color manager
	 */
	protected VisColorManager m_iVcm;

	/**
	 * List of listeners to be notified on DisplayEvents
	 */
	private LinkedList m_iDisplayEventListeners;

	/**
	 * List of listeners for information about this display
	 */
	protected LinkedList m_iDisplayInfoListeners;

	/**
	 * The display information view
	 */
  private DisplayInformationView m_iInfoView;

	// -- CONSTRUCTORS AND STATIC METHODS ----------------------------------------

	/**
	 * Default VisEditor data display constructor.
	 * @param iParent
	 *          a composite control which will be the parent of the new instance
	 *          (cannot be <code>null</code>)
	 * @param nStyle
	 *          the style of control to construct
	 * @param aDci
	 *          an array of data component information objects defining the data
	 *          to be displayed, cannot be <code>null</code> 
	 * @param iHruler
	 *          the horizontal ruler of this display, cannot be <code>null</code>
	 * @throws DataException 
	 *          if there is a problem with <code>aDci</code>
	 * @throws NullPointerException
	 *          if <code>iHruler</code> is <code>null</code>
	 */
	public AbstractDataDisplay(Composite iParent, DataCompInfo[] aDci,
			Ruler iHruler) throws DataException {
		super(iParent);

		// Validate component set, throw DataException if don't like it
		if (aDci == null || aDci.length == 0)
			throw new DataException("Empty component array");

		if (aDci[0].iData == null || aDci[0].iData.getLength() == 0)
			throw new DataException("Empty data instance");

		if (!canDisplay(aDci))
			throw new DataException("Cannot display component set");

		// Validate horizonal ruler, i.e. need one!
		// if (iHruler==null) throw new NullPointerException();

		// Initialize
		m_aDci = aDci;
		m_iHruler = iHruler;
		m_nFirstRec = 0;
		m_nLastRec = aDci[0].iData.getLength() - 1;
		m_nMinComp = aDci[0].nComp;
		m_nMaxComp = aDci[aDci.length - 1].nComp;
		m_nFirstComp = m_nMinComp;
		m_nLastComp = m_nMaxComp;
		m_iVcm = new VisColorManager();
		m_iDisplayInfoListeners = new LinkedList();

		setBackground(m_iVcm.getBgColor(getDisplay()));

		// Add listeners
    addPaintListener(new PaintListener()
    {
      public void paintControl(PaintEvent iPe)
      {
        // NOTE: This may time out and re-run over and over -->
        // drawOn(iPe.gc);
        // <--
        paintCanvas(iPe.gc,null,m_iHruler.getCalculator(),m_iVruler.getCalculator());
        paintData(iPe.gc,null,m_iHruler.getCalculator(),m_iVruler.getCalculator());
        paintMarkers(iPe.gc,null,m_iHruler.getCalculator(),m_iVruler.getCalculator());
      }
    });

		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent iDe) {
				onDisposed(iDe);
			}
		});

		addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent iMe) {
				onMouseMove(iMe);
			}
		});
		
		addMouseTrackListener(new MouseTrackListener(){
		
			public void mouseHover(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		
			public void mouseExit(MouseEvent e) {
				onMouseExit(e);
			}
		
			public void mouseEnter(MouseEvent e) {
				fireDisplayInfoScrollEvent(createInfoEventOnScroll());				
			}			
		});
	}

	/**
	 * Determines if instances of this class are capable of displaying the
	 * components specified by <code>aDci</code>.
	 * <p style="color:red">
	 * <b>Instantiatable derived classes must provide an own version of this
	 * method!</b>
	 * </p>
	 * This default implementation returns <code>true</code> if
	 * <code>aDci</code> contains a consecutive component set, otherwise it
	 * returns <code>false</code>.
	 * 
	 * @param aDci
	 *            an array of data component information objects defining the
	 *            data to be displayed
	 * @return <code>true</code> if an instance displaying these data
	 *         components can be created, <code>false</code> otherwise.
	 */
	public static boolean canDisplay(DataCompInfo[] aDci) {
		for (int i = 0, nC = aDci[0].nComp; i < aDci.length; i++, nC++)
			if (nC != aDci[i].nComp)
				return false;
		return true;
	}

	/**
	 * Determines the file name of the display's icon relative to the plugin path.
	 * <p style="color:red"><b>Instantiatable derived classes must provide an own
	 * version of this method!</b></p>
	 * This default implementation returns "icons/obj16/display_obj.gif",
	 * instantiatable derived classes provide a version which returns the file
	 * name of their own icon.
	 * @return an icon file name
	 */
	public static String getIconFileName() {
		return ("icons/obj16/display_obj.gif");
	}

	// -- WIDGET SERVICE ---------------------------------------------------------

	/**
	 * Must be overwritten by derived classes to create and initialize a suitable
	 * vertical ruler for this display. Derived implementations may use the
	 * <code>aDci</code> and <code>m_nXxxComp</code> fields to initialize the
	 * ruler. If a derived class does not need a vertical ruler, it may return
	 * <code>null</code>.
	 * @param iParent
	 *          the parent widget
	 * @return a vertical ruler or <code>null</code> 
	 */
	protected abstract Ruler int_createVerticalRuler(Composite iParent);

	/**
	 * Calls <code>int_createVerticalRuler()</code> to create a  suitable vertical
	 * ruler for this display. Displays which do not need a vertical ruler return
	 * <code>null</code>.
	 * @param iParent
	 *          the parent widget
	 * @return a vertical ruler or <code>null</code> 
	 */
	public Ruler createVerticalRuler(Composite iParent) {
		m_iVruler = int_createVerticalRuler(iParent);
		return m_iVruler;
	}

	public void setHorizontalRuler(Ruler iHruler) {
		m_iHruler = iHruler;
	}

	//	/**
	//	 * Returns the display information view or <code>null</code> if no such view
	//	 * exists.
	//	 * @return the display information view (or <code>null</code>
	//	 */
	//	protected final DisplayInformationView getInfoView() {
	//		if (m_iInfoView != null)
	//			return m_iInfoView;
	//
	//		IWorkbenchPage page = PlatformUI.getWorkbench()
	//				.getActiveWorkbenchWindow().getActivePage();
	//		if (page == null)
	//			return null;
	//		m_iInfoView = (DisplayInformationView) page
	//				.findView("dLabPro Plugin.display.information");
	//		return m_iInfoView;
	//	}

	// -- EVENT HANDLING ---------------------------------------------------------

	/**
	 * Called when the widget is being disposed. Derived classes should override
	 * this method if they store graphics objects like <code>Color</code>s
	 * which need to be disposed.
	 * <p style="color:red">
	 * <b>Derived implementations <em>must</em> call the superclasses' method.</b>
	 * </p>
	 */
	protected void onDisposed(DisposeEvent iDe) {
	}

	/**
	 * Called when the mouse was moved over the display. The default
	 * implementation displays the mouse pointer position at the rulers and feeds
	 * the display info view. Derived classes may override this method, however,
	 * they should call the superclass implementation.
	 * @param iMe
	 *          the mouse event
	 */
	protected void onMouseMove(MouseEvent iMe) {
		if (m_iVruler != null)
			m_iVruler.drawPositionLine(iMe.y);
		m_iHruler.drawPositionLine(iMe.x);

		fireDisplayInfoMouseMoveEvent(createInfoEventOnMouse(iMe.x, iMe.y));
	}
	
	protected void onMouseExit(MouseEvent e) {
		//-200 magic number, is just a number outside the visible area.
		//Any negative value will do
		if (m_iVruler != null)
			m_iVruler.drawPositionLine(-1);
		m_iHruler.drawPositionLine(-1);
		
		fireDisplayInfoClearMouseInfoEvent();
	}

	// -- STATE AND INFO ---------------------------------------------------------

	/**
	 * Convenience method returing the <code>JlData</code> instance associated
	 * with this display.
	 * @return a <code>JlData</code> instance (or <code>null</code> which would
	 *         indicate a serious problem)
	 */
	public final JlData getJlData() {
		if (m_aDci == null || m_aDci.length == 0)
			return null;
		return m_aDci[0].iData;
	}

	/**
	 * Returns the minimum data value of all components.
	 * @return the overall minimum data value
	 */
	public final double getMinValue() {
		double nMin = Double.MAX_VALUE;
		for (int i = 0; i < m_aDci.length; i++)
			if (!Double.isNaN(m_aDci[i].nMin) && m_aDci[i].nMin < nMin)
				nMin = m_aDci[i].nMin;
		return nMin;
	}

	/**
	 * Returns the maximum data value of all components.
	 * @return the overall maximum data value
	 */
	public final double getMaxValue() {
		double nMax = -Double.MAX_VALUE;
		for (int i = 0; i < m_aDci.length; i++)
			if (!Double.isNaN(m_aDci[i].nMax) && m_aDci[i].nMax > nMax)
				nMax = m_aDci[i].nMax;
		return nMax;
	}

	/**
	 * Returns the position on the physical record axis for the given client area
	 * point. Derived classes may return <code>Double.NaN</code> to indicate that
	 * there is no position associated with the given point. 
	 * @param nX
	 *          client x-coordinate 
	 * @param nY
	 *          client y-coordinate 
	 * @return the position on the physical record axis
	 */
	protected final double getRecValueAt(int nX, int nY) {
		return m_iHruler.getValOfPos(nX);
	}

	/**
	 * Returns the position of the physical component axis for the given client
	 * area point. Derived classes may return <code>Double.NaN</code> to indicate
	 * that there is no position associated with the given point.
	 * @param nX
	 *          client x-coordinate 
	 * @param nY
	 *          client y-coordinate 
	 * @return the position on the physical component axis
	 */
	protected abstract double getCompValueAt(int nX, int nY);

	/**
	 * Returns the position of the physical data value axis for the given client
	 * area point. Derived classes may return <code>Double.NaN</code> to indicate
	 * that there is no position associated with the given point.
	 * @param nX
	 *          client x-coordinate 
	 * @param nY
	 *          client y-coordinate 
	 * @return the position on the physical data value axis
	 */
	protected abstract double getDataValueAt(int nX, int nY);

	/**
	 * Sets the record viewing detail. The method calls <code>invalidate()</code>
	 * and <code>redraw()</code>. It will be called on horizontal scroll or zoom
	 * events. If the record indices are out of range, they will be clipped. On
	 * scroll and zoom events first call this method, then call
	 * <code>getRecDetail</code> to determine the actually displayed records and
	 * at last adjust the ruler and the scrollbar.  
	 * @param nFirst
	 *          zero-based index of first record to be shown
	 * @param nLast
	 *          zero-based index of last record to be shown
	 */
	public void setRecDetail(int nFirst, int nLast) {
		if (nFirst == m_nFirstRec && nLast == m_nLastRec)
			return;

		// Validate
		int nNum = getJlData().getLength();
		if (nFirst <  0   ) nFirst = 0;
		if (nLast  <  0   ) nLast  = 0;
		if (nFirst >= nNum) nFirst = nNum - 1;
		if (nLast  >= nNum) nLast  = nNum - 1;

		// Set record viewing range
		m_nFirstRec = nFirst;
		m_nLastRec = nLast;

		// Have it displayed
		invalidate();
		redraw();
		fireDisplayInfoScrollEvent(createInfoEventOnScroll());
	}

	/**
	 * Determines the first and last record currently shown by this display. 
	 * @return a point where <code>x</code> contains the zero-based index of the
	 *         first shown record and <code>y</code> the index of the last one.
	 */
	public final Point getRecDetail() {
		return new Point(m_nFirstRec, m_nLastRec);
	}

	/**
	 * Sets the component viewing detail. The method calls
	 * <code>invalidate()</code> and <code>redraw()</code>. It will be called on
	 * vertical scroll or zoom events. If the component indices are out of range,
	 * they will be clipped. On scroll and zoom events first call this method,
	 * then call <code>getCompDetail</code> to determine the actually displayed
	 * components and at last adjust the ruler and the scrollbar.
	 * @param nFirst
	 *          zero-based index of first component to be shown
	 * @param nLast
	 *          zero-based index of last component to be shown
	 */
	public void setCompDetail(int nFirst, int nLast) {
		if (nFirst == m_nFirstComp && nLast == m_nLastComp)
			return;

		// Validate
		if (nFirst < m_nMinComp)
			nFirst = m_nMinComp;
		if (nLast < m_nMinComp)
			nLast = m_nMinComp;
		if (nFirst > m_nMaxComp)
			nFirst = m_nMaxComp;
		if (nLast > m_nMaxComp)
			nLast = m_nMaxComp;

		// Set component viewing range
		m_nFirstComp = nFirst;
		m_nLastComp = nLast;

		// Have it displayed
		invalidate();
		redraw();
	}

	/**
	 * Determines the first and last component currently shown by this display.
	 * Through vertical zooming, the display may currently <em>show</em> fewer
	 * componetns than it <em>displays</em>. Use <code>getComponents</code> to
	 * determine the displayed components. 
	 * @return a {@link Point} where <code>x</code> contains the zero-based index of the
	 *         first shown component and <code>y</code> the index of the last one.
	 */
	public final Point getCompDetail() {
		return new Point(m_nFirstComp, m_nLastComp);
	}

	/**
	 * Determines the first and last component display by this instance. 
	 * @return a point where <code>x</code> contains the zero-based index of the
	 *         first component and <code>y</code> the index of the last one.
	 */
	public final Point getCompRange() {
		return new Point(m_nMinComp, m_nMaxComp);
	}

	/**
	 * Determines the physical unit of the record axis.
	 * @return The unit name
	 */
	public final String getRunit()
  {
    String sUnit = "";
    try
    {
      sUnit = new String(m_aDci[0].iData.runit);
    }
    catch (Exception e)
    {
    }
    return sUnit;
  }

  /**
   * Determines the physical unit of the component axis.
   * @return The unit name
   */
  public final String getCunit()
  {
    String sUnit = "";
    try
    {
      sUnit = new String(m_aDci[0].iData.cunit);
    }
    catch (Exception e)
    {
    }
    return sUnit;
  }

  /**
   * Determines the physical unit of the value axis.
   * @return The unit name
   */
  public final String getVunit()
  {
    String sUnit = "";
    try
    {
      sUnit = new String(m_aDci[0].iData.vunit);
    }
    catch (Exception e)
    {
    }
    return sUnit;
  }
	
	// -- PAINTING ---------------------------------------------------------------

	/**
	 * Called to prepare the component for painting data. Derived classes should
	 * override this method to draw a guideline grid or similar "canvas" items.
	 * This method will always be called <em>prior to</em> <code>paintData</code>
	 * allowing the latter to "overpaint" what was drawn by
	 * <code>paintCanvas</code>. The default implementation does nothing.
	 * <h2>Remarks</h2>
	 * <ul>
	 *   <li>If possible, implementations should only repaint the damaged region.
	 *     It is also acceptable to repaint the entire display, however, this may
	 *     cause flickering.</li>
	 * </ul>
	 * @param iGc
	 *          the graphics context to use when painting that is configured to
	 *          use the colors, font and damaged region of the control. It is
	 *          valid only during the paint and must not be disposed
	 * @param iDamage
	 *          the bounding rectangle of the region requiring repainting 
	 */
	protected void paintCanvas(GC iGc, Rectangle iDamage, RulerCalculator iHrc, RulerCalculator iVrc)
	{
	}

	/**
	 * Called when the component's data plot needs to be repainted. Derived
	 * classes <em>must</em> override this method to draw the data displayed by
	 * this instance. This method will always be called <em>after</em>
	 * <code>paintCanvas</code> allowing the latter to draw background items like
	 * guidelines, etc. which will be "overpainted" by <code>paintData</code>.
	 * <h2>Remarks</h2>
	 * <ul>
	 *   <li>If possible, implementations should only repaint the damaged region.
	 *     It is also acceptable to repaint the entire display, however, this may
	 *     cause flickering.</li>
	 * </ul>
	 * @param iGc
	 *          the graphics context to use when painting that is configured to
	 *          use the colors, font and damaged region of the control. It is
	 *          valid only during the paint and must not be disposed
	 * @param iDamage
	 *          the bounding rectangle of the region requiring repainting 
	 */
	protected abstract void paintData(GC iGc, Rectangle iDamage, RulerCalculator iHrc, RulerCalculator iVrc);

  /**
   * Called to paint markers and similar items. This method is called after
   * everything has been painted. The default implementation does nothing.
   * <h2>Remarks</h2>
   * <ul>
   *   <li>If possible, implementations should only repaint the damaged region.
   *     It is also acceptable to repaint the entire display, however, this may
   *     cause flickering.</li>
   * </ul>
   * @param iGc
   *          the graphics context to use when painting that is configured to
   *          use the colors, font and damaged region of the control. It is
   *          valid only during the paint and must not be disposed
   * @param iDamage
   *          the bounding rectangle of the region requiring repainting 
   */
  protected void paintMarkers(GC iGc, Rectangle iDamage, RulerCalculator iHrc, RulerCalculator iVrc)
  {
  }	
	
	/**
	 * EXPERIMENTAL:
	 * 
	 * Called when scrolling or zooming occurs. Derived classes may override this
	 * method to destroy any private data used to quicken repainting. The default
	 * implementation does nothing. 
	 */
	public void invalidate() {

	}

	/**
   * Paints the canvas and data on an arbitrary graphics context (e.g. for printing) 
	 * 
   * @param iGc
   *          The graphics context to draw on
	 * @param iHrc
	 *          B.Y.O.H.R: "bring your own horizontal ruler"
	 * @param iVrc
	 *          B.Y.O.V.R: "bring your own vertical ruler"
	 * @param nFlags
	 *          A combination of <code>DRAW_XXX</code> flags
	 */
	public final void drawOn(GC iGc, RulerCalculator iHrc, RulerCalculator iVrc, int nFlg)
	{
    class DrawOnRunnable implements Runnable
    {
      GC              m_iGc;
      RulerCalculator m_iHrc;
      RulerCalculator m_iVrc;
      int             m_nFlg;
      
      DrawOnRunnable(GC iGc, RulerCalculator iHrc, RulerCalculator iVrc, int nFlg)
      {
        m_iGc  = iGc;
        m_iHrc = iHrc;
        m_iVrc = iVrc;
        m_nFlg = nFlg;
      }
      
      public void run()
      {
        Point iSize = new Point(m_iHrc.getLength(),m_iVrc.getLength());
        m_iGc.setClipping(0,0,iSize.x,iSize.y);
        if ((m_nFlg&DRAW_CANVAS )!=0) paintCanvas (m_iGc,null,m_iHrc,m_iVrc);
        if ((m_nFlg&DRAW_DATA   )!=0) paintData   (m_iGc,null,m_iHrc,m_iVrc);
        if ((m_nFlg&DRAW_MARKERS)!=0) paintMarkers(m_iGc,null,m_iHrc,m_iVrc);
        m_iGc.setClipping((Rectangle)null);
      }
    }
    
    if (iHrc==null && m_iHruler!=null) iHrc = m_iHruler.getCalculator(); 
    if (iVrc==null && m_iVruler!=null) iVrc = m_iVruler.getCalculator();
    try
    {
      PlatformUI.getWorkbench().getDisplay().syncExec(new DrawOnRunnable(iGc,iHrc,iVrc,nFlg));
    }
    catch (Throwable e)
    {
      Display.getDefault().syncExec(new DrawOnRunnable(iGc,iHrc,iVrc,nFlg));
    }
	}

  /**
   * Paints the canvas and data on an arbitrary graphics context (e.g. for printing) 
   * 
   * @param iGc
   *          The graphics context to draw on.
   */
	public final void drawOn(GC iGc)
	{
	  drawOn(iGc,null,null,DRAW_ALL);
	}
	
	/**
	 * Create a {@link DisplayInfoMouseMoveEvent} after the mouse was moved over this display.
	 * 
	 * @param x 
	 * 			The x position of the mouse
	 * @param y
	 * 			The y position of the mouse
	 * @return
	 * 			The DisplayInfoEvent containing all information
	 */
	protected abstract DisplayInfoMouseMoveEvent createInfoEventOnMouse(int x,
			int y);

	/**
	 * Create a {@link DisplayInfoMouseMoveEvent} after a scrolling operation occurred.
	 * 
	 * @return
	 * 			The DisplayInfoEvent containing all information
	 */
	protected abstract DisplayInfoScrollEvent createInfoEventOnScroll();

	// -- EVENT HANDLING ---------------------------------------------------------

	/**
	 * Seeks the display information view an attaches it to this data display.
	 */
	public final void attachDisplayInformationView()
  {
    if (m_iInfoView == null)
    {
      try
      {
        m_iInfoView = VisEditor.findDisplayInformationView();
        addDisplayInfoListener(m_iInfoView);
      }
      catch (Throwable e)
      {
      }
    }
  }

	/**
	 * Adds a listener for status information
	 * 
	 * @param listener
	 * 			The listener
	 */
	public void addDisplayInfoListener(IDisplayInfoListener listener)
  {
    if (listener != null && !m_iDisplayInfoListeners.contains(listener))
    {
      m_iDisplayInfoListeners.add(listener);
    }
    // fire event, so the listener gets updated properly
    fireDisplayInfoScrollEvent(createInfoEventOnScroll());
  }

	/**
	 * Dispatches an info event to all listeners registered through
	 * <code>addDisplayInfoListener</code> after the mouse was moved.
	 * @param e
	 */
	protected void fireDisplayInfoMouseMoveEvent(DisplayInfoMouseMoveEvent e)
  {
	  attachDisplayInformationView();
	  IDisplayInfoListener listener = null;
    for (int i = 0; i < m_iDisplayInfoListeners.size(); i++)
    {
      listener = (IDisplayInfoListener)m_iDisplayInfoListeners.get(i);
      listener.informationChangedMouseMove(e);
    }
  }
	
	/**
	 * Dispatches an info event to all listeners registered through
	 * <code>addDisplayInfoListener</code> after the display was scrolled.
	 * @param e
	 */
	protected void fireDisplayInfoScrollEvent(DisplayInfoScrollEvent e)
  {
    attachDisplayInformationView();
    IDisplayInfoListener listener = null;
    for (int i = 0; i < m_iDisplayInfoListeners.size(); i++)
    {
      listener = (IDisplayInfoListener)m_iDisplayInfoListeners.get(i);
      listener.informationChangedScroll(e);
    }
  }
	
	protected void fireDisplayInfoClearMouseInfoEvent()
  {
    attachDisplayInformationView();
    IDisplayInfoListener listener = null;
    for (int i = 0; i < m_iDisplayInfoListeners.size(); i++)
    {
      listener = (IDisplayInfoListener)m_iDisplayInfoListeners.get(i);
      listener.clearMouseInformation();
    }
  }

	/**
	 * Adds a new display event listener to this component.
	 * @param iListener
	 *          the listener
	 */
	public void addDisplayEventListener(IDisplayEventListener iListener) {
		m_iDisplayEventListeners.add(iListener);
	}

	/**
	 * Dispatches a display event to the listeners registered through
	 * <code>addDisplayEventListener</code>.
	 * @param iDe
	 *          The event to be dispatched
	 */
	protected void fireDisplayEvent(DisplayEvent iDe) {
		IDisplayEventListener iListener;
		for (int i = 0; i < m_iDisplayEventListeners.size(); i++) {
			iListener = (IDisplayEventListener) m_iDisplayEventListeners.get(i);
			iListener.onDisplayEvent(iDe);
		}
	}

}

// EOF

