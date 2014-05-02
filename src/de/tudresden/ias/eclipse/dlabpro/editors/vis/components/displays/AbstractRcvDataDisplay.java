// dLabPro Plugin for Eclipse
// - Base class of VisEditor data displays which display data values at the
//   ordinate and records at the abscissa
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays;

import java.lang.reflect.Array;

import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoMouseMoveEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoScrollEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.NumberFormatter;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.Ruler;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.VerticalLabelRuler;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataCompInfo;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataException;
import de.tudresden.ias.jlab.kernel.JlData;

/**
 * Abstract base class of data displays which show component values at the
 * ordinate, records at the abscissa and data values by color or other 2D-means. 
 */
public abstract class AbstractRcvDataDisplay extends AbstractDataDisplay {
	/**
	 * The vertical ruler of this display, may be <code>null</code>
	 */
	protected Ruler m_iVruler;

	public AbstractRcvDataDisplay(Composite iParent, DataCompInfo[] aDci,
			Ruler iHruler) throws DataException {
		super(iParent, aDci, iHruler);
	}

	/*
	 * (non-Javadoc)
	 */
	protected double getCompValueAt(int nX, int nY) {
		return m_iVruler.getDataPointOfPos(nY);
	}

	/*
	 * (non-Javadoc)
	 */
	protected double getDataValueAt(int nX, int nY) {
		JlData iData = getJlData();
		double nRecVal = m_iHruler.getDataPointOfPos(nX);
		double nCompVal = m_iVruler.getDataPointOfPos(nY);
		int nRec = (int) ((nRecVal - iData.rofs) / iData.rinc);
		int nComp = (int) ((nCompVal - iData.cofs) / iData.cinc);
		if (nComp<0) nComp=0;
		if (nRec <0) nRec =0;
		if (nComp>=iData.getDimension()) nComp=iData.getDimension()-1;
		if (nRec>=iData.getLength()) nRec=iData.getLength()-1;
		return Array.getDouble(iData.getComp(nComp), nRec);
	}

	/*
	 * (non-Javadoc)
	 */
	protected Ruler int_createVerticalRuler(Composite iParent) {
		int nStart = 0;
		//note: added -1 because drawing starts at -0.5comps
		int nEnd   = m_aDci.length;
		for (int i=0; i<m_aDci.length; i++)
		  if (!m_aDci[i].bVisible) nStart++; else break;
    for (int i=m_aDci.length-1; i>=0; i--)
      if (!m_aDci[i].bVisible) nEnd--; else break;

		// Create a ruler
    JlData iData = getJlData();
    boolean bAllCompsNamed = true;
    for (int i=nStart; i<nEnd; i++)
      if (iData.getCompName(m_aDci[i].nComp).length()==0)
      {
        bAllCompsNamed = false;
        break;
      }

    if (iData.cinc!=1. || iData.cofs!=0. || !bAllCompsNamed)
		{
		  // Ordinary ruler
		  m_iVruler = new Ruler(iParent,true);
		}
		else
		{
		  // Label ruler
		  String[] asLabel = new String[nEnd-nStart];
		  for (int i=nStart; i<nEnd; i++)
		  {
		    asLabel[i-nStart]=m_aDci[i].iData.getCompName(m_aDci[i].nComp);
		    if (asLabel[i-nStart].length()==0)
		      asLabel[i-nStart] = ""+m_aDci[i].nComp;
		  }
		  m_iVruler = new VerticalLabelRuler(iParent,asLabel);
		}
    m_iVruler.setDataRange(nStart,nEnd-nStart,iData.cofs,iData.cinc,iData.cunit);
		return m_iVruler;
	}
	
	/* (non-Javadoc)
	 * @see de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay#createInfoEventOnMouse(int, int)
	 * default implementation
	 */
	protected DisplayInfoMouseMoveEvent createInfoEventOnMouse(int x, int y)
  {
	  String sValA; // Actual (ruler) value
	  String sValD; // Data value (at nearest data point)
	  int    nValL; // Logical value (data element index)
	  String sData; // Nearest data point description
	  String sTtip; // Tool tip text
	  JlData iData = getJlData();
	  DisplayInfoMouseMoveEvent e = new DisplayInfoMouseMoveEvent(this);
	  int    nC = m_iVruler.getDataPointOfPos(y) + m_nMinComp;
	  
	  // Store X (=record) axis information
	  sValA = NumberFormatter.formatAndAdjust(m_iHruler.getValOfPos(x));
	  nValL = m_iHruler.getDataPointOfPos(x);
	  sValD = NumberFormatter.formatAndAdjust(iData.rofs+nValL*iData.rinc);
	  sData = "rec. "+nValL;
	  sTtip = "R: "+nValL+"\t"+sValA+" "+getRunit()+"\n";
	  e.StoreX(sValA,sValD,getRunit(),sData);

	  // Store Y (=component) axis information
    sValA = NumberFormatter.formatAndAdjust(m_iVruler.getValOfPos(y));
    nValL = m_iVruler.getDataPointOfPos(y);
    sValD = NumberFormatter.formatAndAdjust(iData.cofs+nValL*iData.cinc);
    sData = new String(iData.getCompName(nValL));
    if (sData.length()>0) sData = " \""+sData+"\"";
    sData = "comp. "+nValL+sData; 
    sTtip += "C: "+nValL+"\t"+sValA+" "+getCunit()+"\n";
    e.StoreY(sValA,sValD,getCunit(),sData);

    // Store Z (=value) axis information
    sValD = NumberFormatter.formatAndAdjust(iData.dFetch(m_iHruler.getDataPointOfPos(x),nC));
    if (sValD.startsWith("NaN")) sValD = "(no data)";
    e.StoreZ(sValD,getVunit());
    sTtip += "V: "+sValD+" "+getVunit();

    // Set tool tip text
    setToolTipText(sTtip);
    
    return e;
  }

	/* (non-Javadoc)
	 * @see de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay#createInfoEventOnScroll()
	 * default implementation
	 */
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
    
    // Store Y (=component) display range information
    nMinValP = iData.cofs+m_nFirstComp*iData.cinc;
    nMaxValP = iData.cofs+m_nLastComp*iData.cinc;
    sRngP = NumberFormatter.formatAndAdjust(nMinValP)+" ... "+NumberFormatter.formatAndAdjust(nMaxValP);
    sRngL = m_nFirstComp+" ... "+m_nLastComp+" ("+(m_nLastComp-m_nFirstComp+1)+")";
    e.StoreY("components",sRngP,new String(iData.cunit),sRngL);

    // Store Z (=value) display range information
    nMinValP = getMinValue();
    nMaxValP = getMaxValue();
    sRngP = NumberFormatter.formatAndAdjust(nMinValP)+" ... "+NumberFormatter.formatAndAdjust(nMaxValP);
    e.StoreZ("values",sRngP,""/*iData.zunit*/);

    return e;
  }

}

// EOF