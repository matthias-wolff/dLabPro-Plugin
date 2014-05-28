package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;

public class VerticalLabelRuler extends Ruler
{
  private String[] asLabels;
  
  public VerticalLabelRuler(Composite parent, String[] asLabels)
  {
    super(parent,true);
    this.asLabels = asLabels;
  }

  /*
   * (non-Javadoc)
   * @see de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers_new.Ruler#drawOn(org.eclipse.swt.graphics.GC, org.eclipse.swt.graphics.Rectangle, de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers_new.RulerCalculator, int, int)
   */
  @Override
  public void drawOn(GC iGc, Rectangle iRegion, RulerCalculator iRcalc, int nWidth, int nFlags)
  {try{
    if (iRcalc==null) iRcalc = this.iRcalc;
    if (nWidth==0) nWidth = bVert ? RULER_WIDTH_X : RULER_WIDTH_Y;

    int sizeY  = iRcalc.getLength();
    int sizeX  = nWidth;
    int nC     = iRcalc.getDataPointOfPos(sizeY-1);
    int nLab   = 0;
    int nPrev  = sizeY;
    iGc.setClipping(0,0,sizeX,sizeY);
    for (int i=sizeY-1; i>=0; i--)
    {
      if (nC==iRcalc.getDataPointOfPos(i) && i>0) continue;
      String sLabel = ""+nC;
      if (asLabels!=null) sLabel = (nLab<asLabels.length ? asLabels[nLab] : ""); 
      sLabel = VIS.abbreviateToFit(iGc,sLabel,nPrev-i);
      if (sLabel.length()>0)
      {
        int w = iGc.textExtent(sLabel).x;
        VIS.drawRotatedString(iGc,sLabel,VIS.VERTICAL_TEXT_OFFSET,i+(nPrev-i-w)/2);
      }
      iGc.drawLine(0,i,sizeX,i);
      nC = iRcalc.getDataPointOfPos(i);
      nPrev = i;
      nLab ++;
    }
    iGc.setClipping((Rectangle)null);

    // Paint position line and outline
    if (!(iGc.getDevice() instanceof Printer)) drawPositionLine(nPosLine,false);
    if ((nFlags&DRAW_BORDER)!=0) iGc.drawRectangle(0,0,sizeX-1,sizeY);
  } catch (Throwable e) { e.printStackTrace(); }
  }
}
