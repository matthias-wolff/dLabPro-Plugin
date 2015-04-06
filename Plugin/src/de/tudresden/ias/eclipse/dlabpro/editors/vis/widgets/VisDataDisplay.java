package de.tudresden.ias.eclipse.dlabpro.editors.vis.widgets;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Composite;

import de.tucottbus.kt.jlab.datadisplays.utils.DpiConverter;
import de.tucottbus.kt.jlab.datadisplays.widgets.ComponentPanel;
import de.tucottbus.kt.jlab.datadisplays.widgets.DataDisplayPanel;
import de.tucottbus.kt.jlab.datadisplays.widgets.displays.AbstractDataDisplay;
import de.tucottbus.kt.jlab.datadisplays.widgets.displays.LabelDisplay;
import de.tucottbus.kt.jlab.datadisplays.widgets.rulers.DummyRuler;
import de.tucottbus.kt.jlab.datadisplays.widgets.rulers.Ruler;
import de.tucottbus.kt.jlab.datadisplays.widgets.rulers.RulerCalculator;
import de.tucottbus.kt.jlab.kernel.JlData;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.VisPrintDialog;

/**
 * The jLab data display widget customized for the VIS editor.
 * 
 * @author Matthias Wolff
 */
public class VisDataDisplay extends ComponentPanel
{

  public VisDataDisplay(Composite parent)
  {
    super(parent);
  }

  public VisDataDisplay(Composite parent, JlData iData, String sProps)
  {
    super(parent, iData, sProps);
  }

  public VisDataDisplay(Composite parent, Exception e)
  {
    super(parent, e);
  }
  
  // -- VIS Editor Printing --
  
  // TODO: Code duplicate! Use drawOn to actually paint the component panel!
  public void doPrint(Printer printer, String sTitle)
  {
    // Do the VIS print dialog
    VisPrintDialog iPd = new VisPrintDialog(printer,sTitle);
    if (iPd.syncOpen()!=Window.OK) return;
    
    // Print
    if (printer.startJob(sTitle))
    {
      // Initialize
      GC              iGc  = new GC(printer);                // Print graphics context
      Point           R    = printer.getDPI();               // Print resolution
      Rectangle       C    = printer.getClientArea();        // Printable area (in pixels)
      Rectangle       T    = printer.computeTrim(0,0,0,0);   // Printer trim (in pixels)
      DpiConverter    iXc  = new DpiConverter(R.x);          // X-lengths converter
      DpiConverter    iYc  = new DpiConverter(R.y);          // Y-lengths converter
      RulerCalculator iHrc = null;                           // X ruler calculator
      RulerCalculator iVrc = null;                           // Y ruler calculator 
      Transform       iTf  = new Transform(printer);         // Drawing transform
      Font            iF   = null;                           // Normal font
      Font            iFh  = null;                           // Header font
      int             nUhX = iYc.pt2px(iPd.nFontSize);       // Height of upright letter "X"
      int             nRhX = iXc.pt2px(iPd.nFontSize);       // Height of rotated letter "X"
      int             nWs  = 0;                              // Screen width of component panel
      int             nHs  = 0;                              // Screen height of component panel
      int             nL   = 0;                              // Left base line (in pixels)
      int             nT   = 0;                              // Top base line (in pixels)
      int             nW   = 0;                              // Diagram width (in pixels)
      int             nH   = 0;                              // Diagram height (in pixels)
      int             nWa  = 0;                              // Available width (in pixels)
      int             nHa  = 0;                              // Available height (in pixels)
      int             nHd  = 0;                              // Height of currently drawn display (in pixels)
      int             nRf  = 0;                              // Ruler flags
      int             nPf  = AbstractDataDisplay.DRAW_DATA;  // Paint flags
      
      // Make ruler drawing flags
      if (iPd.bPrntScale) nRf |= Ruler.DRAW_SCALE;
      if (iPd.bPrntTicks) nRf |= Ruler.DRAW_TICKS;
      if (iPd.nStyle==0 ) nRf |= Ruler.DRAW_BORDER;
      
      // Make display paint flags
      if (iPd.bPrntCanvas) nPf |= AbstractDataDisplay.DRAW_CANVAS;
      
      // Zoning
      // - Get screen width/height of diagram ares (excluding rulers)
      nWs = getSizeSync().x-Ruler.RULER_WIDTH_X;
      nHs = getSizeSync().y-mHorizontalRuler.getSizeSync().y;

      // - Get desired width/height of diagram area (excluding rulers)
      nL = (iPd.bPrntRulers ? 3*nRhX/2 : 0) + iXc.mm2px(iPd.nMrgnLeft)+T.x;
      nT = (iPd.bPrntTitle  ? 4*nUhX   : 0) + iYc.mm2px(iPd.nMrgnTop )+T.y;
      nW = iPd.nFitMode==2 ? iXc.mm2px(iPd.nWidth) : C.width;
      switch (iPd.nFitMode)
      {
      case 2 : nH = iYc.mm2px(iPd.nHeight); break;
      case 1 : nH = C.height; break;
      default: nH = (int)Math.round((double)nHs/(double)nWs*(double)nW);
      }
      if (iPd.nStyle==1)
      {
        nT += nUhX;
        if (iPd.nFitMode!=2) nW -= nRhX;
      }
      
      // - Fit desired to available dimensions
      nWa =  C.width  - (iXc.mm2px(iPd.nMrgnRight )-(T.x+T.width )) - nL;
      nHa =  C.height - (iYc.mm2px(iPd.nMrgnBottom)-(T.y+T.height)) - nT;
      if (nW>nWa    ) nW = nWa;
      if (nH>nHa    ) nH = nHa;
      if (nW<10*nRhX) nW = 10*nRhX; 
      if (nH<10*nUhX) nH = 10*nUhX; 
      
      // Some funny console print out
      System.out.println("PRINT");
      System.out.println("- UHX="+nUhX+" px, RHX="+nRhX+" px");
      System.out.println("- Printer    : "+printer.getPrinterData().name);
      System.out.println("- Resolution : "+R.x+" x "+R.y+" dpi");
      System.out.println("- Print area : X=["+C.x+","+C.width+"], Y=["+C.y+","+C.height+"]");
      System.out.println("- Trim       : L="+(-1*T.x)+", T="+(-1*T.x)+", R="+(T.x+T.width)+", B="+(T.y+T.height));
      System.out.println("- Settings");
      System.out.println("  - Font     : "+iPd.sFont);
      System.out.println("  - Font size: "+iPd.nFontSize+" pt");
      System.out.println("  - Margins  : L="+iPd.nMrgnLeft+" mm, T="+iPd.nMrgnTop+" mm, R="+iPd.nMrgnRight+" mm, B="+iPd.nMrgnBottom+" mm");
      System.out.println("  - Size     : W="+iPd.nWidth+" mm, H="+iPd.nHeight+" mm");

      // Setup graphics context
      iGc.setAntialias(SWT.OFF);
      iGc.setLineWidth(iXc.pt2px(iPd.nLineWidth));
      iGc.setLineCap(SWT.CAP_ROUND);
      iGc.setLineJoin(SWT.JOIN_ROUND);

      // Create fonts
      try
      {
        FontData iFd = new FontData(iPd.sFont);
        iFd.setHeight(iPd.nFontSize);
        iF = new Font(printer,iFd);
      }
      catch (Exception e)
      {
        iF = iGc.getDevice().getSystemFont();
      }
      try
      {
        FontData iFd = new FontData(iPd.sFont);
        iFd.setHeight((int)Math.round(iPd.nFontSize*1.4));
        iFd.setStyle(SWT.BOLD);
        iFh = new Font(printer,iFd);

      }
      catch (Exception e)
      {
        iFh = iGc.getDevice().getSystemFont();
      }
      
      // Do printing
      printer.startPage();
      iTf.translate(nL,nT); iGc.setTransform(iTf);

      // - Print title
      if (iPd.bPrntTitle)
      {
        iGc.setForeground(printer.getSystemColor(SWT.COLOR_BLACK));
        iGc.setFont(iFh);
        iGc.drawString(sTitle,0,(iPd.nStyle==1?-5:-4)*nUhX,true);
      }
      if (iPd.nStyle==1 && iPd.bPrntRulers)
      {
        iGc.drawLine(0,0,0,-nUhX);
        iGc.drawLine(-nUhX/4,-nUhX/2,0,-nUhX);
        iGc.drawLine( nUhX/4,-nUhX/2,0,-nUhX);
      }      
      // - Print displays and vertical rulers
      //   TODO: compute display heights in advance because label displays may
      //         use too much space! 
      iGc.setFont(iF);
      iHrc = new RulerCalculator(getHorizontalRuler().getCalculator());
      iHrc.setPosInfo(nW); iHrc.notifyOfGC(iGc);
      for (int i=0; i<mDataDisplayPanelList.size(); i++)
      {
        DataDisplayPanel iDdp = (DataDisplayPanel)mDataDisplayPanelList.get(i);
        AbstractDataDisplay iDd = iDdp.getDataDisplay();

        iVrc = new RulerCalculator(iDdp.getVerticalRuler().getCalculator());
        nHd  = (int)Math.round((double)iDd.getSizeSync().y/(double)nHs*(double)nH);
        if (iDd instanceof LabelDisplay) nHd = 3*nRhX/2; // Label displays special!
        iVrc.setPosInfo(nHd); iVrc.notifyOfGC(iGc);
        
        iGc.setForeground(printer.getSystemColor(SWT.COLOR_BLACK));
        iGc.setBackground(printer.getSystemColor(SWT.COLOR_WHITE));
        iGc.setFont(iF);
        iDd.drawOn(iGc,iHrc,iVrc,nPf);
        iGc.setForeground(printer.getSystemColor(SWT.COLOR_BLACK));
        if (iPd.nStyle==0)
          iGc.drawRectangle(0,0,nW,nHd);
        else if (iPd.bPrntRulers)
        {
          iGc.drawLine(-3*nRhX/2,nHd,nW+nRhX,nHd);
          iGc.drawLine(nW+nRhX/2,nHd-nRhX/4,nW+nRhX,nHd);
          iGc.drawLine(nW+nRhX/2,nHd+nRhX/4,nW+nRhX,nHd);
          iGc.drawLine(0,0,0,nHd);
        }
        
        if (iPd.bPrntRulers && !(iDdp.getVerticalRuler() instanceof DummyRuler))
        {
          iTf.translate(-3*nRhX/2,0); iGc.setTransform(iTf);
          //iGc.setClipping(0,0,3*nRhX/2,iVrc.getLength());
          iGc.setForeground(printer.getSystemColor(SWT.COLOR_BLACK));
          iGc.setBackground(printer.getSystemColor(SWT.COLOR_WHITE));
          iDdp.getVerticalRuler().drawOn(iGc,null,iVrc,3*nRhX/2,nRf);
          //iGc.setClipping((Rectangle)null);
          iTf.translate(3*nRhX/2,0); iGc.setTransform(iTf);
        }
        
        iTf.translate(0,nHd); iGc.setTransform(iTf);
      }

      // - Print horizontal ruler
      if (iPd.bPrntRulers)
      {
        iGc.setForeground(printer.getSystemColor(SWT.COLOR_BLACK));
        iGc.setBackground(printer.getSystemColor(SWT.COLOR_WHITE));
        mHorizontalRuler.drawOn(iGc,null,iHrc,3*nUhX/2,nRf);
        if (iPd.nStyle==1) iGc.drawLine(0,0,0,3*nUhX/2);
      }

      // That's all folks
      printer.endPage();
      printer.endJob();
      iF.dispose();
      iFh.dispose();
      iTf.dispose();
      iGc.dispose();
    }
  }

}
