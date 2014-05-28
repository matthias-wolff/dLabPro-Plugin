// dLabPro Plugin for Eclipse
// - VisEditor spectrogram data display
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.VisColorManager;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.NumberFormatter;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.Ruler;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.RulerCalculator;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataCompInfo;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataException;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.preferences.IVisEditorPreferenceConstants;
import de.tudresden.ias.jlab.kernel.JlData;

public class Spectrogram extends AbstractRcvDataDisplay
implements IVisEditorPreferenceConstants
{

	private double m_nMaxValue;

	private double m_nMinValue;

	private double[][] m_nVal;
	
  private Color[] m_aColors;
  
  private double m_nRatio; 

  private double[] m_nLevels;
  
  private int m_nSig = 0; 
  
	public Spectrogram(Composite iParent, int nStyle, DataCompInfo[] aDci,
			Ruler iHruler) throws DataException {
		super(iParent, aDci, iHruler);
		computeValueRange();
		getValues();
		m_nLevels = VIS.decimalZoning(m_nMinValue, m_nMaxValue, 10);
		setCompDetail(m_aDci[0].nComp, m_aDci[m_aDci.length - 1].nComp);
	}

	/*
	 * (non-Javadoc)
	 */
	public static boolean canDisplay(DataCompInfo[] aDci) {
		JlData iData = aDci[0].iData;
		for (int nComp = 0; nComp < aDci.length; nComp++)
			if (!JlData.isNumericType(iData.getCompType(aDci[nComp].nComp)))
				return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 */
	public static String getIconFileName() {
		return "icons/obj16/spec_comp_obj.gif";
		// return ("icons/obj16/spec_obj.gif");
	}

	/**
	 * Detects if the level curve at <code>v0</code> runs between two data points.
	 * 
	 * @param r1
	 *          Record index of first data point
	 * @param c1
   *          Component index of first data point
	 * @param r2
   *          Record index of second data point
	 * @param c2
	 *          Component index of seconds data point
	 * @param v0
	 *          Data value of the level curve
	 * @return
	 */
	private boolean Borderline(int r1, int c1, int r2, int c2, double v0)
	{
	  try
	  {
	    double v1 = m_nVal[r1][c1];
	    double v2 = m_nVal[r2][c2];
	    return ((v1-v0)*(v2-v0))<=0.;
	  }
    // Never mind these ...
	  catch (ArrayIndexOutOfBoundsException e) { return false; }
	}
	
	/**
	 * Paints one data point
	 * 
	 * @param iGc
	 *          the graphics context
	 * @param x
	 *          the x coordinate of the data point's rectangle
	 * @param y
   *          the y coordinate of the data point's rectangle
	 * @param w
   *          the width of the data point's rectangle
	 * @param h
   *          the height of the data point's rectangle
	 * @param r
   *          the record index of the data value
   * @param c
   *          the component index of the data value
	 */
	protected void paintDataPoint(GC iGc, int x, int y, int w, int h, int r, int c)
	{
    double v = m_nVal[r][c];
	  if (Double.isNaN(v)) return;
	  
    // Paint data point
    int col = (int)Math.round((v - m_nMinValue) * m_nRatio);
	  if (VIS.bSpecShowValues)
	  {
      if (col >= m_aColors.length) col = m_aColors.length - 1;
      if (col < 0) col = 0;
      iGc.setBackground(m_aColors[col]);
      iGc.fillRectangle(x,y,w,h);
	  }

    // Chose a contrasty foreground color
    Color iFgColor = getDisplay().getSystemColor(SWT.COLOR_BLACK);
    if (VIS.bSpecShowValues)
      if (VisColorManager.getColorValue(m_aColors[col])<128)
        iFgColor = getDisplay().getSystemColor(SWT.COLOR_WHITE);

    // Paint level curve
    if (VIS.bSpecShowLevels)
      for (int l=1; l<m_nLevels.length-1; l++)
      {
        double v0 = m_nLevels[l];
        boolean bB1 = Borderline(r  ,c,r  ,c+1,v0);
        boolean bB2 = Borderline(r  ,c,r-1,c  ,v0);
        boolean bB3 = Borderline(r-1,c,r-1,c+1,v0);
        boolean bB4 = Borderline(r  ,c,r+1,c  ,v0);
        iGc.setForeground(iFgColor);
        if      (bB1 &&  bB2) iGc.drawLine(x+w,y,x  ,y+h);
        else if (bB1 &&  bB4) iGc.drawLine(x  ,y,x+w,y+h);
        else if (bB2 && !bB3) iGc.drawLine(x  ,y,x  ,y+h);
        else if (bB1        ) iGc.drawLine(x  ,y,x+w,y  );
      }
      
    // Paint data label
    if (m_nSig>=0 && w>10 && h>10)
    {
      String s = NumberFormatter.formatAndAdjust(v,m_nSig);
      Point  t = iGc.textExtent(s);
      if (t.x<w && t.y<h)
      {
        iGc.setForeground(iFgColor);
        iGc.drawString(s,x+(w-t.x)/2,y+(h-t.y)/2,true);
      }
    }
	}
	
	/*
	 * (non-Javadoc)
	 */
	protected void paintData(GC iGc, Rectangle iDamage, RulerCalculator iHrc, RulerCalculator iVrc)
  {
    Point[] current = null;
    Point[] last = null;
    int sizeY = iVrc.getLength();
    int xoff = (iHrc.getPosOfDataPoint(m_nFirstRec + 1) - iHrc
        .getPosOfDataPoint(m_nFirstRec)) / 2;
    // switch the subtraction because smaller comp has bigger y coordinate
    int yoff = (iVrc.getPosOfDataPoint(0) - iVrc
        .getPosOfDataPoint(1)) / 2;

    VIS.MSG("- Spectrogram:");
    VIS.MSG("  components [" + m_nMinComp + "..." + m_nMaxComp + "]");
    VIS.MSG("  visible    [" + m_nFirstComp + "..." + m_nLastComp + "]");

    // Get color information
    m_aColors = m_iVcm.getValueColors();
    m_nRatio  = m_aColors.length / (m_nMaxValue - m_nMinValue);
    Color iBkColor = iGc.getBackground();
    Color iFgColor = iGc.getForeground();

    // Get painting preferences 
    IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();
    if (!VIS.bSpecShowValues && !VIS.bSpecShowLevels) VIS.bSpecShowValues = true;
    
    // - Label painting information
    if (iStore.getBoolean(P_VIS_SPECLABEL))
    {
      for (m_nSig=5; m_nSig>=0; m_nSig--)
      {
        double n1 = Math.round(m_nMaxValue) + 0.111111;
        double n2 = Math.round(m_nMinValue) + 0.111111;
        int w1 = iGc.textExtent(NumberFormatter.formatAndAdjust(n1,m_nSig)).x;
        int w2 = iGc.textExtent(NumberFormatter.formatAndAdjust(n2,m_nSig)).x;
        if (w1<xoff*2 && w2<xoff*2) break;
      }
    }
    else m_nSig = -1;
    
    // iterate over records;
    for (int i = m_nFirstRec; i <= m_nLastRec; i++)
    {
      int xPos = iHrc.getPosOfDataPoint(i);
      xPos += xoff;
      current = new Point[m_nLastComp - m_nFirstComp + 1];

      // iterate over components
      for (int j = m_nFirstComp, k = 0; j <= m_nLastComp; j++, k++)
      {
        int yPos = iVrc.getPosOfDataPoint(k);
        yPos -= yoff;
        current[k] = new Point(xPos,yPos);
        DataCompInfo iDci = m_aDci[j-m_nMinComp]; 

        if (iDci.bVisible)
        {
          // Compute y-coordinate
          int prevY = sizeY;
          if (k > 0) prevY = current[k - 1].y;

          if (last == null)
            // start at the left edge of the display
            paintDataPoint(iGc,0,yPos,xPos,prevY-yPos,i,j-m_nMinComp);
          else
            paintDataPoint(iGc,last[k].x,yPos,xPos-last[k].x,prevY-yPos,i,j-m_nMinComp);
        }
      }
      last = current;
      current = null;
    }
    
    // Restore GC colors
    iGc.setBackground(iBkColor);
    iGc.setForeground(iFgColor);
  }
	
	private final void getValues()
	{
	  JlData iData = m_aDci[0].iData;
	  int    nXR   = iData.getLength();
	  int    nXC   = iData.getDimension();
	  
	  m_nVal = new double[nXR][nXC];
	  for (int nR=0; nR<nXR; nR++)
	    for (int nC=0; nC<nXC; nC++)
	      m_nVal[nR][nC] = iData.dFetch(nR,nC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay#setCompDetail(int,
	 *      int)
	 */
	public void setCompDetail(int first, int last) {
		int nFirstVisible = m_aDci[0].nComp;
		int nLastVisible = m_aDci[m_aDci.length - 1].nComp;

		for (int i = 0; i < m_aDci.length; i++)
			if (!m_aDci[i].bVisible)
				nFirstVisible = m_aDci[i].nComp + 1;
			else
				break;
		for (int i = m_aDci.length - 1; i >= 0; i--)
			if (!m_aDci[i].bVisible)
				nLastVisible = m_aDci[i].nComp - 1;
			else
				break;
		if (first < nFirstVisible)
			first = nFirstVisible;
		if (last > nLastVisible)
			last = nLastVisible;

		super.setCompDetail(first, last);
	}

	private final void computeValueRange() {
		m_nMaxValue = -Double.MAX_VALUE;
		m_nMinValue = Double.MAX_VALUE;

		// Compute global min and max values ignoring invisible components
		for (int i = 0; i < m_aDci.length; i++) {
			if (!m_aDci[i].bVisible)
				continue;
			if (m_aDci[i].nMax > m_nMaxValue)
				m_nMaxValue = m_aDci[i].nMax;
			if (m_aDci[i].nMin < m_nMinValue)
				m_nMinValue = m_aDci[i].nMin;
		}
	}	
}

// EOF
