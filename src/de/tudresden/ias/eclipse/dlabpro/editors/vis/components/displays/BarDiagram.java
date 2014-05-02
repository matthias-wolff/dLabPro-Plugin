// dLabPro Plugin for Eclipse
// - VisEditor bar diagram data display
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoMouseMoveEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.Ruler;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.RulerCalculator;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataCompInfo;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataException;
import de.tudresden.ias.jlab.kernel.JlData;

public class BarDiagram extends AbstractRvDataDisplay
{

  /**
   * The minimal value of all bars stacked
   */
  protected double   m_nMin;

  /**
   * The maximal value of all bars stacked
   */
  protected double   m_nMax;

  public BarDiagram(Composite iParent, int nStyle, DataCompInfo[] aDci,
      Ruler iHruler) throws DataException
  {
    super(iParent,aDci,iHruler);
    if (!canDisplay(aDci)) throw new DataException(
        "Cannot display component set");

    // Force zero line
    m_nMin = Double.MAX_VALUE;
    m_nMax = -Double.MAX_VALUE;
    int nXR = m_aDci[0].iData.getLength();

    // Loop over components
    for (int nR = 0; nR < nXR; nR++)
    {
      double nMin = 0;
      double nMax = 0;
      for (int nC = 0; nC < m_aDci.length; nC++)
      {
        if (!m_aDci[nC].bVisible) continue;
        double nVal = getJlData().dFetch(nR,m_aDci[nC].nComp);
        if (nVal > 0) nMax += nVal;
        if (nVal < 0) nMin += nVal;
      }
      if (nMax > m_nMax) m_nMax = nMax;
      if (nMin < m_nMin) m_nMin = nMin;
    }
  }

  /*
   * (non-Javadoc)
   */
  public static boolean canDisplay(DataCompInfo[] aDci)
  {
    JlData iData = aDci[0].iData;
    for (int nComp = 0; nComp < aDci.length; nComp++)
      if (!JlData.isNumericType(iData.getCompType(aDci[nComp].nComp))) return false;
    return true;
  }

  /*
   * (non-Javadoc)
   */
  public static String getIconFileName()
  {
    return "icons/obj16/bard_comp_obj.gif";
    //return ("icons/obj16/bars_obj.gif");
  }

  /* (non-Javadoc)
   * @see de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractRvDataDisplay#int_createVerticalRuler(org.eclipse.swt.widgets.Composite)
   */
  protected Ruler int_createVerticalRuler(Composite iParent)
  {
    m_iVruler = new Ruler(iParent,true);
    double vinc = 0.;
    try { vinc = this.m_aDci[0].iData.vinc; } catch (Exception e) { e.printStackTrace(); }
    m_iVruler.setRange(m_nMin,m_nMax,vinc,0,new String(this.m_aDci[0].iData.vunit));
    VIS.MSG("- BarDiagram: min="+m_nMin+", max="+m_nMax);
    return m_iVruler;
  }

  /*
   * (non-Javadoc)
   * @see de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay#paintData(org.eclipse.swt.graphics.GC, org.eclipse.swt.graphics.Rectangle)
   */
  protected void paintData(GC iGc, Rectangle iDamage, RulerCalculator iHrc, RulerCalculator iVrc)
  {
    JlData  iData = getJlData();
    int     nX0   = 0;
    int     nX1   = 0;
    int     nXC   = m_aDci.length;
    int     nY0   = iVrc.getPosOfVal(0);
    int     nXofs = (iHrc.getPosOfDataPoint(1)-iHrc.getPosOfDataPoint(0))/2;
    boolean bCpct = (iHrc.getLength() / (m_nLastRec - m_nFirstRec)) < 3;
    
    for (int nR=0; nR<iData.getLength(); nR++)
    {
      // Compute screen abscissa, do not paint if off screen or not changed
      nX1 = iHrc.getPosOfDataPoint(nR)+nXofs;
      if (nX0 >  iHrc.getLength()) break;
      if (nX1 <  0               ) continue;
      if (nX1 == nX0             ) continue;

      // Draw bars
      int nYp = nY0;
      int nYn = nY0;
      for (int nC=0; nC<nXC; nC++)
      {
        if (!m_aDci[nC].bVisible) continue;
        double nVal = iData.dFetch(nR,m_aDci[nC].nComp);
        int    nLen = Math.abs(iVrc.getPosOfVal(nVal)-nY0);
        
        iGc.setForeground(m_iVcm.getFgColor(getDisplay()));
        iGc.setBackground(m_iVcm.getCompBgColor(nC));

        if (nVal<0)
        {
          iGc.fillRectangle(nX0,nYn,nX1-nX0,nLen);
          if (!bCpct) iGc.drawRectangle(nX0,nYn,nX1-nX0,nLen);
          nYn+=nLen;
        }
        else
        {
          nYp-=nLen;
          iGc.fillRectangle(nX0,nYp,nX1-nX0,nLen);
          if (!bCpct) iGc.drawRectangle(nX0,nYp,nX1-nX0,nLen);
        }
      }
      nX0 = nX1;
    }
  } 

  /*
   * (non-Javadoc)
   * @see de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractRvDataDisplay#createInfoEventOnMouse(int, int)
   */
  protected DisplayInfoMouseMoveEvent createInfoEventOnMouse(int x, int y)
  {
    // TODO: - Implement for stacked bars
    // TODO: - Implement tool tip
    return super.createInfoEventOnMouse(x,y);
  }
}

// EOF
