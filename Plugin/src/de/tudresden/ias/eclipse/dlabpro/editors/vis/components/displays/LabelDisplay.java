// dLabPro Plugin for Eclipse
// - VisEditor label data display
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoMouseMoveEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoScrollEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.DummyRuler;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.NumberFormatter;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.Ruler;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.RulerCalculator;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataCompInfo;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataException;
import de.tudresden.ias.jlab.kernel.JlData;

public class LabelDisplay extends AbstractDataDisplay {

	public LabelDisplay(Composite iParent, int nStyle, DataCompInfo[] aDci,
			Ruler iHruler) throws DataException {
		super(iParent, aDci, iHruler);
	}

	/*
	 * (non-Javadoc)
	 */
	public static boolean canDisplay(DataCompInfo[] aDci) {
		if (aDci.length != 1)
			return false;
		return JlData.isStringType(aDci[0].iData.getCompType(aDci[0].nComp));
	}

	/*
	 * (non-Javadoc)
	 */
	public static String getIconFileName() {
		return ("icons/obj16/lab_obj.gif");
	}

	/*
	 * (non-Javadoc)
	 */
	protected double getCompValueAt(int nX, int nY) {
		return Double.NaN;
	}

	/*
	 * (non-Javadoc)
	 */
	protected double getDataValueAt(int nX, int nY) {
		return Double.NaN;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected Ruler int_createVerticalRuler(Composite iParent)
  {
    return new DummyRuler(iParent, true);
  }

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(wHint, Ruler.RULER_WIDTH_Y);
	}

	/*
	 * (non-Javadoc)
	 */
	protected void paintData(GC iGc, Rectangle iDamage, RulerCalculator iHrc, RulerCalculator iVrc)
  {
    int nXOfs = (iHrc.getPosOfDataPoint(1)-iHrc.getPosOfDataPoint(0))/2; 
    int comp = m_aDci[0].nComp;
    int lsep = 0;
    String[] asComp = null;
    if (m_aDci[0].iData.getCompType(comp)==String.class)
      asComp = (String[])m_aDci[0].iData.getComp(comp);
    String s1 = (String)m_aDci[0].iData.fetch(m_nFirstRec,comp);
    String s2 = null;
    if (iGc.getDevice() instanceof Printer)
      iGc.setForeground(iGc.getDevice().getSystemColor(SWT.COLOR_BLACK));
    else
      iGc.setForeground(m_iVcm.getFgColor(getDisplay()));

    for (int i = m_nFirstRec + 1; i <= m_nLastRec+1; i++)
    {
      String val = "";
      try
      {
       val = asComp!=null ? asComp[i] : (String)m_aDci[0].iData.fetch(i,comp);
      }
      catch (ArrayIndexOutOfBoundsException e) {}
      s2 = (i<=m_nLastRec ? val : "");
      if (s2==null) s2="";
      if (!s2.equals(s1) || i==m_nLastRec+1)
      {
        int xPos = (i<=m_nLastRec ? iHrc.getPosOfDataPoint(i)-nXOfs : iHrc.getLength());
        int isize = xPos - lsep;
        s1 = VIS.abbreviateToFit(iGc,s1,isize-4);
        if (s1.length()>0)
        {
          int offset = (isize - iGc.textExtent(s1).x) / 2;
          iGc.drawString(s1,lsep+offset,VIS.VERTICAL_TEXT_OFFSET);
        }
        if (xPos > lsep && i <= m_nLastRec)
        {
          iGc.drawLine(xPos,0,xPos,iVrc.getLength());
          lsep = xPos;
        }
        s1 = s2;
      }
    }

    //draw border
    iGc.drawRectangle(0,0,iHrc.getLength()-1,iVrc.getLength());
  }

	protected DisplayInfoMouseMoveEvent createInfoEventOnMouse(int x, int y)
  {
    String sValA; // Actual (ruler) value
    String sValD; // Data value (at nearest data point)
    int    nValL; // Logical value (data element index)
    String sData; // Nearest data point description
    String sTtip; // Tool tip text
    JlData iData = getJlData();
    DisplayInfoMouseMoveEvent e = new DisplayInfoMouseMoveEvent(this);
    
    // Store X (=record) axis information
    sValA = NumberFormatter.formatAndAdjust(m_iHruler.getValOfPos(x));
    nValL = m_iHruler.getDataPointOfPos(x);
    sValD = NumberFormatter.formatAndAdjust(iData.rofs+nValL*iData.rinc);
    sData = "rec. "+nValL;
    sTtip = "R: "+nValL+"\n";
    e.StoreX(sValA,sValD,new String(iData.runit),sData);

    // Store Y (=label) axis information
    nValL = m_aDci[0].nComp;
    sValD = "\""+iData.sFetch(m_iHruler.getDataPointOfPos(x),nValL)+"\"";
    sData = new String(iData.getCompName(nValL));
    if (sData.length()>0) sData = " \""+sData+"\"";
    sData = "comp. "+nValL+sData;
    sTtip += "C: "+nValL+"\n";
    e.StoreY("",sValD,"",sData);

    // Set tool tip text
    sTtip += "V: "+sValD;
    setToolTipText(sTtip);
    
    return e;
  }
	
	protected DisplayInfoScrollEvent createInfoEventOnScroll()
  {
    double nMinValP;
    double nMaxValP;
    String sRngL;
    String sRngP;
    JlData iData = getJlData();
    DisplayInfoScrollEvent e = new DisplayInfoScrollEvent(this);
    
    // Store X (=record) display range information
    nMinValP = iData.rofs+m_nFirstRec*iData.rinc;
    nMaxValP = iData.rofs+m_nLastRec*iData.rinc;
    sRngP = NumberFormatter.formatAndAdjust(nMinValP)+" ... "+NumberFormatter.formatAndAdjust(nMaxValP);
    sRngL = m_nFirstRec+" ... "+m_nLastRec+" ("+(m_nLastRec-m_nFirstRec+1)+")";
    e.StoreX("records",sRngP,new String(iData.runit),sRngL);
    
    // Store Y (=labels) display range information
    e.StoreY("labels","","","");
    
    // Store Z display range information
    e.StoreZ("","","");

    return e;
  }

}

// EOF
