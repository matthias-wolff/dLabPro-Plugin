package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers;

import java.util.LinkedList;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.AbstractComponent;

public class Ruler extends AbstractComponent
{
  public static final int RULER_WIDTH_X = 20;
  public static final int RULER_WIDTH_Y = 20;
  public static final int DRAW_SCALE    = 0x0001;
  public static final int DRAW_TICKS    = 0x0002;
  public static final int DRAW_BORDER   = 0x0004;
  public static final int DRAW_ALL      = 0x7FFF;
  
  protected boolean         bVert    = false; // Is a vertical ruler 
  protected RulerCalculator iRcalc   = null;  // Ruler calculator
  protected Color           iFgColor = null;  // Foreground color 
  protected Color           iPlColor = null;  // Position line color
  protected Color           iBkColor = null;  // Background color
  protected int             nPosLine = -1;    // Previous position line
  
  /**
   * Creates a new ruler instance
   * @param iParent
   */
  public Ruler(Composite iParent, boolean bVertical)
  {
    super(iParent);
    this.bVert  = bVertical;
    this.iRcalc = new RulerCalculator(bVertical);
    this.iFgColor = getDisplay().getSystemColor(SWT.COLOR_BLACK);
    this.iPlColor = getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
    this.iBkColor = iParent.getBackground();
    setBackground(iBkColor);

    addPaintListener(new PaintListener()
    {
      public void paintControl(PaintEvent e)
      {
        drawOn(e.gc,new Rectangle(e.x,e.y,e.width,e.height));
      }
    });

    addListener(SWT.Resize, new Listener()
    {
      public void handleEvent(Event event)
      {
        iRcalc.setPosInfo(bVert?getSize().y:getSize().x);
      }
    });
  
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
   */
  public Point computeSize(int wHint, int hHint, boolean changed)
  {
    if (bVert) return new Point(RULER_WIDTH_X,hHint);
    else       return new Point(wHint,RULER_WIDTH_Y);
  }
  
  /**
   * Sets the locale for displaying numbers.
   * 
   * @param locale
   *          The locale, if <code>null</code> the method will assume
   *          {@link Locale#ENGLISH}.
   */
  public void setLocale(Locale locale)
  {
    if (locale==null) locale = Locale.ENGLISH;
    iRcalc.setLocale(locale);
  }

  /**
   * Sets the range of data values for this ruler.
   * 
   * @param nLo
   *          The low data value (left or bottom end of ruler)
   * @param nHi
   *          The high data value (right or top end of ruler)
   * @param nQn
   *          The value quantum (increment between two neighboring data points, 0 for continuous
   *          rulers)
   * @param nMg
   *          Heading and tailing margin (as value, &lt;0 for automatic choice)
   * @param sUnit
   *          The physical unit (may be <code>null</code>)
   */
  public void setRange(double nLo, double nHi, double nQn, double nMg, String sUnit)
  {
    iRcalc.setValInfo(nLo,nHi,nQn,nMg,sUnit,false);
    redraw();
  }

  /**
   * Sets the range of data values for this ruler.
   *     
   * @param nLo   The low data value (left or bottom end of ruler)
   * @param nHi   The high data value (right or top end of ruler)
   * @param nQn   The value quantum (increment between two neighboring data
   *              points, 0 for continuous rulers) 
   * @param sUnit The physical unit (may be <code>null</code>)
   */
  public void setRange(double nLo, double nHi, double nQn, String sUnit)
  {
    iRcalc.setValInfo(nLo,nHi,nQn,sUnit);
    redraw();
  }

  /**
   * Convenience method to create a ruler for <code>JlData</code> record or component sequences.
   * 
   * @param nDataFirst
   *          The first visible record/component
   * @param nDataCount
   *          The number of visible records/components
   * @param nValOfs
   *          The physical value of the 0th(!) record/component (independently of the first
   *          <em>visible</em> item!)
   * @param nValInc
   *          The physical value increment between two subsequent records/components (cannot be 0!)
   * @param sUnit
   *          The name of the physical unit (e.g. "ms", "kHz", etc.; may be <code>null</code>)   
   */
  public void setDataRange(int nDataFirst, int nDataCount, double nValOfs, double nValInc, String sUnit)
  {
    iRcalc.setDataRange(nDataFirst,nDataCount,nValOfs,nValInc,sUnit);
    redraw();
  }

  /**
   * Convenience method to create a ruler for <code>JlData</code> record or component sequences.
   * 
   * @param nDataFirst
   *          The first visible record/component
   * @param nDataCount
   *          The number of visible records/components
   * @param nValOfs
   *          The physical value of the 0th(!) record/component (independently of the first
   *          <em>visible</em> item!)
   * @param nValInc
   *          The physical value increment between two subsequent records/components (cannot be 0!)
   * @param acUnit
   *          The name of the physical unit (e.g. "ms", "kHz", etc.; may be <code>null</code>)   
   */
  public void setDataRange(int nDataFirst, int nDataCount, double nValOfs, double nValInc, char[] acUnit)
  {
    String sUnit = "";
    try { sUnit = new String(acUnit); } catch (Exception e) {}
    setDataRange(nDataFirst,nDataCount,nValOfs,nValInc,sUnit);
  }
  
  /**
   * Sets the data point information for this ruler calculator. Data point information can only
   * be used with quantized rulers. 
   * 
   * @param nLo The data point index of the ruler's low value
   */
  public void setDataInfo(int nLo)
  {
    iRcalc.setDataInfo(nLo);
  }  
  
  /**
   * Returns the calculator used by this ruler.
   */
  public RulerCalculator getCalculator()
  {
    return iRcalc;
  }
  
  /**
   * Computes the value of the given position. If the ruler is quantized, the returned value will be
   * quantized as well. If the position is outside the display area, the method will return a data
   * value <em>outside</em> the value range of the ruler.
   * 
   * @param nPos
   *          The position
   * @return The value
   */
  public double getValOfPos(int nPos)
  {
    return iRcalc.getValOfPos(nPos);
  }

  /**
   * Computes the position of the given value.
   * 
   * @param nVal
   *          The value
   * @return The position
   */
  public int getPosOfVal(double nVal)
  {
    return iRcalc.getPosOfVal(nVal);
  }

  /**
   * Computes the data point index of the given position. Data point information can only
   * be used with quantized rulers. 
   * 
   * @param nPos
   *          The position
   * @return The data point index
   */
  public int getDataPointOfPos(int nPos)
  {
    return iRcalc.getDataPointOfPos(nPos);
  }

  /**
   * Computes the position of the given data point index. Data point information can only
   * be used with quantized rulers. 
   * 
   * @param nDataPoint 
   *          The data point index
   * @return The position 
   */
  public int getPosOfDataPoint(int nDataPoint)
  {
    return iRcalc.getPosOfDataPoint(nDataPoint);
  }
  
  /**
   * Returns the name of the physical unit of the ruler scale
   */
  public String getUnit()
  {
    return iRcalc.getUnit();
  }
  
  /**
   * Returns the length of the ruler in display pixels.
   */
  public int getLength()
  {
    return iRcalc.getLength();
  }
  
  /**
   * Determines if the ruler is quantized.
   */
  public boolean isQuantized()
  {
    return iRcalc.isQuantized();
  }

  /**
   * Draws one ruler interval
   */
  private void drawIntervalOn(GC iGc, RulerCalculator iRcalc, int nIterval, Point iSize, int nFlags)
  {    
    LinkedList<RulerScaleLine> iScale = iRcalc.getScale();
    
    if (nIterval<0)
    {
      // Left end
      return;
    }
    else if (nIterval>=iScale.size())
    {
      // Right end
      return;      
    }
    
    RulerScaleLine iRsl = iScale.get(nIterval);
    int            nLen = bVert?iSize.x:iSize.y;
    int            nEm  = iGc.stringExtent("E").y;

    if (iRsl.getLab()==null) nLen/=5;
    if (iRsl.getLab()!=null && (nFlags&DRAW_SCALE)==0) return;
    if (iRsl.getLab()==null && (nFlags&DRAW_TICKS)==0) return;

    if (bVert)
    {
      // Ordinary vertical interval
      iGc.drawLine(iSize.x-nLen-1,iRsl.getPos(),iSize.x,iRsl.getPos());
      if (iRsl.getLab()!=null)
      {
        int nTe = iGc.stringExtent(iRsl.getLab()).x;
        VIS.drawRotatedString(iGc,iRsl.getLab(),0,iRsl.getPos()-nTe-nLen/10);
      }
    }
    else
    {
      // Ordinary horizontal interval
      iGc.drawLine(iRsl.getPos(),0,iRsl.getPos(),nLen);
      if (iRsl.getLab()!=null)
        iGc.drawString(iRsl.getLab(),iRsl.getPos()+nLen/10,iSize.y-nEm,true);
    }
  }

  /**
   * Paints the ruler.
   * 
   * @param iGc
   *          The graphics context to paint on
   * @param iRegion
   *          The region to be repainted
   * @param iRcalc
   *          The ruler calculator to be used. <br>
   *          <b style="color:red">NOTE:</b> if not <code>null</code>, implementations <em>must</em>
   *          use the scale and coordinate conversion of the committed ruler calculator rather than
   *          their own!
   * @param nWidth
   *          The width of vertical and the height of horizontal rulers
   * @param nFlags
   *          Drawing flags, a combination if the following<br>
   *          - <code>DRAW_SCALE</code><br>
   *          - <code>DRAW_TICKS</code><br>
   *          - <code>DRAW_BORDER</code><br>
   *          or <code>DRAW_ALL</code> for all of the above
   */
  public void drawOn(GC iGc, Rectangle iRegion, RulerCalculator iRcalc, int nWidth, int nFlags)
  {
    if (iRcalc==null) iRcalc = this.iRcalc;
    if (nWidth==0) nWidth = bVert ? RULER_WIDTH_X : RULER_WIDTH_Y;
    
    Point iSz;
    if (bVert) iSz = new Point(nWidth,iRcalc.getLength());
    else       iSz = new Point(iRcalc.getLength(),nWidth);
    
    if (getEnabledSync())
    {
      // Compute ruler intervals to paint
      int nXI = iRcalc.getScale().size();
      int nIL = -1;
      int nIH = nXI;
      if (iRegion!=null)
        if (bVert)
        {
          nIH = iRcalc.getIntervalOfPos(iRegion.y);
          nIL = iRcalc.getIntervalOfPos(iRegion.y+iRegion.height);
        }
        else
        {
          nIL = iRcalc.getIntervalOfPos(iRegion.x);
          nIH = iRcalc.getIntervalOfPos(iRegion.x+iRegion.width);
        }
      
      // Paint ruler intervals
      iGc.setForeground(this.iFgColor);
      for (int nI=nIL; nI<=nIH; nI++) drawIntervalOn(iGc,iRcalc,nI,iSz,nFlags);
      
      // Paint position line
      if (!(iGc.getDevice() instanceof Printer)) drawPositionLine(nPosLine,false);
      if ((nFlags&DRAW_BORDER)!=0) iGc.drawRectangle(0,0,iSz.x-1,iSz.y-1);
    }
    else if ((nFlags&DRAW_BORDER)!=0 && !(iGc.getDevice() instanceof Printer))
      iGc.drawRectangle(0,0,iSz.x-1,iSz.y-1);
  }
  
  /**
   * Paints the ruler.
   * 
   * @param iGc
   *          The graphics context to paint on
   * @param iRegion
   *          The region to be repainted
   */
  protected void drawOn(GC iGc, Rectangle iRegion)
  {
    drawOn(iGc,iRegion,null,0,DRAW_ALL);
  }
  
  /**
   * Draws the cursor position marker.
   * 
   * @param nPos The position
   */
  public void drawPositionLine(int nPos)
  {
    drawPositionLine(nPos,true);
  }
  
  /**
   * Draws the cursor position marker.
   * 
   * @param nPos The position
   * @param bInvalidate Repaint at old position line
   */
  protected void drawPositionLine(int nPos, boolean bInvalidate)
  {
    if (!getEnabled()) return;
    LinkedList<RulerScaleLine> iScale = iRcalc.getScale();
    GC iGc = new GC(this);
    iGc.setForeground(iPlColor);
    iGc.setBackground(iPlColor);

    if (bVert)
    {
      if (bInvalidate)
      {
        int nLabLine = nPosLine;
        for (int i=iRcalc.getIntervalOfPos(nPosLine); i>=0; i--)
          if (iScale.get(i).getLab()!=null)
          {
            nLabLine = iScale.get(i).getPos();
            break;
          }
        redraw(1,nPosLine-4,Ruler.RULER_WIDTH_X-2,nLabLine-nPosLine+9,false);
      }
      iGc.drawLine(1,nPos,Ruler.RULER_WIDTH_X-2,nPos);
      iGc.fillPolygon(new int[]{1,nPos-4,8,nPos,1,nPos+4});
    }
    else
    {
      if (bInvalidate)
      {
        int nLabLine = nPosLine;
        for (int i=iRcalc.getIntervalOfPos(nPosLine); i>=0; i--)
          if (iScale.get(i).getLab()!=null)
          {
            nLabLine = iScale.get(i).getPos();
            break;
          }
        redraw(nLabLine-4,1,nPosLine-nLabLine+9,Ruler.RULER_WIDTH_Y-2,false);
      }
      iGc.drawLine(nPos,1,nPos,Ruler.RULER_WIDTH_Y-2);
      iGc.fillPolygon(new int[]{nPos-4,RULER_WIDTH_Y-1,nPos,RULER_WIDTH_Y-9,nPos+4,RULER_WIDTH_Y-1});
    }
    
    nPosLine=nPos;
    iGc.dispose();
  }
  
}
