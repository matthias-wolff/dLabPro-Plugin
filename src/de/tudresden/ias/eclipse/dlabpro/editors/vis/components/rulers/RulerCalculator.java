
package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers;

import java.util.LinkedList;
import java.util.Locale;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * NOTE: CLASS NOT YET IN USE!
 * 
 * @author Matthias Wolff
 */
public class RulerCalculator
{
  public static final int ROUND = 0;
  public static final int CEIL  = 1;
  public static final int FLOOR = 3;
  
  private final boolean DEBUGME             = false;
  private final int     nDefaultMinTickSize = 4;
  private final Point   iDefaultEX          = new Point(6,9);

  private Locale        locale = Locale.ENGLISH;
  private double        nValLo = 0.;         // Low value (left/bottom)
  private double        nValHi = 1.;         // High value (right/top)
  private double        nValQn = 0.;         // Value quantum
  private double        nValMg = -1;         // Heading and tailing margin value 
  private int           nDatLo = 0;          // Data point index of low value
  private int           nLen   = 100;        // Length of ruler on the display in (pixels)
  private boolean       bRev   = false;      // Reverse ruler
  private double        nScale = 1.;         // Value difference equivalent to one pixel
  private String        sUnit  = null;       // The physical unit
  private Point         iEX    = iDefaultEX; // Dimensions of the letter "X" (pixels) 
  private int           nMinTickSize = nDefaultMinTickSize;
  private LinkedList<RulerScaleLine> alInts;
  
  /**
   * Creates a new ruler calculator.
   * 
   * @param iGc
   *          The graphics context to work for (can be <code>null</code>)
   * @param bRev
   *          Reverse ruler
   */
  public RulerCalculator(boolean bRev)
  {
    this.bRev = bRev;
  }

  /**
   * Copy constructor.
   * 
   * @param iSrc The ruler calculator to copy
   */
  public RulerCalculator(RulerCalculator iSrc)
  {
    this.locale = iSrc.locale;
    this.nValLo = iSrc.nValLo;
    this.nValHi = iSrc.nValHi;
    this.nValQn = iSrc.nValQn;
    this.nValMg = iSrc.nValMg;
    this.nDatLo = iSrc.nDatLo;
    this.nLen   = iSrc.nLen;
    this.bRev   = iSrc.bRev;
    this.nScale = iSrc.nScale;
    this.sUnit  = iSrc.sUnit;
    this.iEX    = iSrc.iEX;
    zoning();
  }

  /**
   * Creates a new ruler calculator.
   * 
   * @param iGc
   *          The graphics context to work for (can be <code>null</code>)
   * @param bRev
   *          Reverse ruler
   * @param nLo
   *          The low data value (left or bottom end of ruler)
   * @param nHi
   *          The high data value (right or top end of ruler)
   * @param nQn
   *          The value quantum (increment between two neighboring data points, 0 for continuous
   *          rulers)
   * @param nLen
   *          Length of ruler on the display in (pixels)
   */
  public RulerCalculator(boolean bRev, double nLo, double nHi, double nQn, int nLen)
  {
    this.bRev = bRev;
    setValInfo(nLo, nHi, nQn, -1, null, true);
    setPosInfo(nLen, true);
    zoning();
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
    this.locale = locale;
  }
  
  /**
   * Sets the range of data values for this ruler calculator.
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
   * @param bDefer
   *          Just set the values, do not zoning
   * @param sUnit
   *          The physical unit (may be <code>null</code>)
   */
  protected void setValInfo
  (
    double nLo,
    double nHi,
    double nQn,
    double nMg,
    String sUnit,
    boolean bDefer
  )
  {
    if (nLo==Double.NaN) nLo = 0.;
    if (nHi==Double.NaN) nHi = 1.;
    this.nValQn = Math.abs(nQn);
    this.nValMg = nMg>=0?nMg:this.nValQn/2.;
    this.nValLo = Math.min(nLo,nHi)-nValMg;
    this.nValHi = Math.max(nLo,nHi)+nValMg;
    if (this.nValHi==this.nValLo) { this.nValHi++; this.nValLo --; }
    this.sUnit  = sUnit;
    if (!bDefer) zoning();
  }

  /**
   * Sets the range of data values for this ruler calculator.
   * 
   * @param nLo
   *          The low data value (left or bottom end of ruler)
   * @param nHi
   *          The high data value (right or top end of ruler)
   * @param nQn
   *          The value quantum (increment between two neighboring data points, 0 for continuous
   *          rulers)
   * @param sUnit
   *          The physical unit (may be <code>null</code>)
   */
  public void setValInfo
  (
    double nLo,
    double nHi,
    double nQn,
    String sUnit
  )
  {
    setValInfo(nLo,nHi,nQn,-1,sUnit,false);
  }

  /**
   * Sets the displayed length of this ruler calculator.
   * 
   * @param nLen
   *          Length of ruler on the display in (pixels)
   * @param bDefer
   *          Just set the value, do not zoning
   */
  protected void setPosInfo(int nLen, boolean bDefer)
  {
    this.nLen = nLen;
    if (!bDefer) zoning();
  }

  /**
   * Sets the displayed length of this ruler calculator.
   * 
   * @param nLen
   *          Length of ruler on the display (in pixels)
   */
  public void setPosInfo(int nLen)
  {
    setPosInfo(nLen, false);
  }

  /**
   * Sets the data point information for this ruler calculator. Data point information can only
   * be used with quantized rulers. 
   * 
   * @param nLo The data point index of the ruler's low value
   */
  public void setDataInfo(int nLo)
  {
    this.nDatLo = nLo;
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
    if (nValInc==0.                     ) nValInc = 1.;
    if (sUnit!=null && sUnit.length()==0) sUnit   = null;
    
    double nValLo =  nDataFirst              *nValInc + nValOfs;
    double nValHi = (nDataFirst+nDataCount-1)*nValInc + nValOfs;
    
    setDataInfo(nDataFirst);
    setValInfo(nValLo,nValHi,nValInc,sUnit);
  }
  
  /**
   * Returns a quantized data value. If the ruler calculator is not quantized,
   * the method returns the argument. 
   * 
   * @param val
   *          The data value.
   * @param mode
   *          The quantization mode: {@link #ROUND}, {@link #CEIL}, or {@link #FLOOR}.
   */
  public double quantizeVal(double val, int mode)
  {
    if (nValQn<=0) return val;
    if (mode==CEIL ) return Math.ceil(val/nValQn)*nValQn;
    if (mode==FLOOR) return Math.floor(val/nValQn)*nValQn;
    return Math.round(val/nValQn)*nValQn;
  }
  
  /**
   * Computes the value of the given position. If the ruler is quantized, the returned value will be
   * quantized as well. If the position is outside the display area, the method will return a data
   * value <em>outside</em> the value range of the ruler calculator.
   * 
   * @param nPos
   *          The position
   * @return The value
   */
  public double getValOfPos(int nPos)
  {
    if (bRev) nPos = this.nLen - nPos;
    double nVal = nPos * this.nScale + this.nValLo;
    if (this.nValQn > 0.) nVal = Math.round(nVal / this.nValQn) * this.nValQn;
    if (nVal<nValLo+nValQn/2) nVal = nValLo+nValQn/2;
    if (nVal>nValHi-nValQn/2) nVal = nValHi-nValQn/2;
    return nVal;
  }

  /**
   * Computes the index of the ruler interval immediately left or below the given display position.
   * 
   * @param nPos
   *          The position.
   * @return A ruler interval index or -1 if the specified position is outside the display area.
   */
  public int getIntervalOfPos(int nPos)
  { 
    int nIH = this.alInts.size()-1; 
    int nIL = 0;
    if (nIH<0) return -1;

    if (bRev)
    {
      if (nPos>this.alInts.get(0  ).getPos()) return -1;
      if (nPos<this.alInts.get(nIH).getPos()) return nIH;
    }
    else
    {
      if (nPos<this.alInts.get(0  ).getPos()) return -1;
      if (nPos>this.alInts.get(nIH).getPos()) return nIH;
    }
    while (nIH-nIL>1)
    {
      int nIM = nIL+(nIH-nIL)/2;
      if (bRev)
      {
        if (nPos>this.alInts.get(nIM).getPos()) nIH=nIM;
        else                                    nIL=nIM;
      }
      else
      {
        if (nPos<this.alInts.get(nIM).getPos()) nIH=nIM;
        else                                    nIL=nIM;
      }
    }
    
    //MSG("- Result: " + nIL + " (" + alInts.get(nIL).getPos() + "..." + nPos + "..."
    //    + alInts.get(nIL + 1).getPos() + ")\n");
    return nIL;
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
    int nPos = (int)Math.round((nVal - this.nValLo) / this.nScale);
    if (this.bRev) nPos = this.nLen - nPos;
    return nPos;
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
    return (int)Math.round((getValOfPos(nPos)-(nValLo+nValQn/2))/nValQn)+nDatLo;
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
    return getPosOfVal((nDataPoint-this.nDatLo)*this.nValQn+this.nValLo+this.nValQn/2);
  }  
  
  /**
   * Returns the displayed length of this ruler calculator (in pixels).
   */
  public int getLength()
  {
    return nLen;
  }
  
  /**
   * Returns the name of the physical unit of the ruler scale
   */
  public String getUnit()
  {
    return sUnit;
  }
  
  /**
   * Determines if the ruler is quantized.
   */
  public boolean isQuantized()
  {
    return nValQn>0.;
  }
  
  /**
   * Notifies the ruler calculator of the graphics context to work for. The graphics context
   * is necessary for estimating text sizes.
   * 
   * @param iGc The graphics context (can be <code>null</code> for display on screen)
   */
  public void notifyOfGC(GC iGc)
  {
    if (iGc==null)
    {
      iEX = iDefaultEX;
      nMinTickSize = nDefaultMinTickSize;
    }
    else 
    {
      iEX = iGc.stringExtent("X");
      nMinTickSize = iEX.y/2;
    }
    zoning();
  }
  
  /**
   * Returns the scale of this ruler calculator.
   * 
   * @return The scale
   */
  public LinkedList<RulerScaleLine> getScale()
  {
    return this.alInts;
  }

  /**
   * Appends a ruler scale line to interval list. If the position of the line is outside the display
   * range, the method does nothing.
   * 
   * @param iRsl
   *          The ruler scale line to add.
   */
  private void addInterval(RulerScaleLine iRsl)
  {
    if (iRsl.getPos() < 0        ) return;
    if (iRsl.getPos() > this.nLen) return;
    this.alInts.add(iRsl);
  }

  /**
   * (Re-)calculates the ruler intervals.
   */
  protected void zoning()
  {
    MSG("Ruler zoning ["+nValLo+" ... "+nValHi+"] @ "+nLen+"px\n");
    nScale = (nValHi-nValLo)/nLen;
    if (nLen<=0) return;

    // Compute maximal number of displayable intervals
    int nIntvCount = this.nLen / (7*iEX.x);
    if (nValQn>0. && nIntvCount>(nValHi-nValLo)/nValQn)
      nIntvCount=(int)((nValHi-nValLo)/nValQn);

    // Get interval range and number of ticks
    double r = quantizeVal((nValHi-nValLo)/nIntvCount,CEIL);
    long   m = (long)Math.floor(Math.log10(r)); r /= Math.pow(10,m);
    int nTicks = 10;
    if      (r<=2) nTicks = 2;
    else if (r<=5) nTicks = 5;
    double mi = nTicks*Math.pow(10,m);
    if (nTicks==10) m++;
    MSG("- mi="+mi+", nTicks="+nTicks+", mi/nTicks="+mi/nTicks+", nValQn="+nValQn+"\n");
    while (nTicks>0 && mi/nTicks<nValQn)
      switch (nTicks)
      {
      case 10: nTicks = 5; break;
      case  5: nTicks = 2; break;
      default: nTicks = 0;
      }
    
    // Create the ruler scale lines
    alInts = new LinkedList<RulerScaleLine>();
    for (double v=(Math.floor(nValLo/mi))*mi; v<(Math.ceil(nValHi/mi))*mi; v+=mi)
    {
      String sf = String.format("%%.%df",m<0?-m:0); 
      String sl = String.format(locale,sf,v);
      addInterval(new RulerScaleLine(v,getPosOfVal(v),sl));
      for (int t=1; t<nTicks; t++)
      {
        double vt = v+t*mi/nTicks;
        addInterval(new RulerScaleLine(vt,getPosOfVal(vt),null));
      }
    }

    // Add unit to rightmost possible label
    int xE = nLen;
    boolean first = true;
    if (sUnit!=null)
      for (int i=alInts.size()-1; i>=0; i--)
      {
        RulerScaleLine iRsl = alInts.get(i);
        if (!iRsl.isMainLine()) continue;
        int x = bRev ? this.nLen-iRsl.getPos() : iRsl.getPos();
        if ((iRsl.getLab()+" "+sUnit).length()*iEX.x<xE-x)
        {
          iRsl.setLab(iRsl.getLab()+" "+sUnit);
          break;
        }
        if (!first)
        {
          iRsl.setLab(sUnit);
          break;
        }
        xE = x;
        first = false;
      }
    
  }

  /**
   * Prints a debug message.
   * 
   * @param sMsg
   *          The message
   */
  private final void MSG(String sMsg)
  {
    if (DEBUGME) System.out.print(sMsg);
  }

}
