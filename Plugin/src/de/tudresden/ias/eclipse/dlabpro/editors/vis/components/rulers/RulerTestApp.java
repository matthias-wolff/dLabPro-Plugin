package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class RulerTestApp
{
  /* UI elements */
  //private Display  iDisplay;
  private Shell    iShell;
  private Ruler    iVr;
  private Ruler    iHr;
  private MyDummy  iFlr;
  private MyCanvas iCnv;

  /**
   * Main function of stand-alone application.
   * @param args
   */
  public static void main(String[] args)
  {
    Display iDisplay = new Display();
    RulerTestApp iApp = new RulerTestApp();
    Shell iShell = iApp.open(iDisplay,null);
    while (!iShell.isDisposed())
    {
      if (!iDisplay.readAndDispatch()) iDisplay.sleep();
    }
    iApp.close();
    iDisplay.dispose();
  }

  /**
   * Opens the main program.
   */
  public Shell open(Display iDisplay, Shell iShell)
  {
    //this.iDisplay = iDisplay;
    this.iShell = (iShell != null) ? iShell : new Shell();
    createShellContents();
    this.iShell.open();
    return this.iShell;
  }

  /**
   * Called when the application is closed
   */
  public void close()
  {
  }
  
  /**
   * Populates the shell
   */
  private void createShellContents()
  {
    iShell.setText("Ruler Test Application (SWT)");

    GridLayout iGl = new GridLayout(2,false);
    iGl.marginHeight = iGl.marginWidth = 0;
    iGl.horizontalSpacing = iGl.verticalSpacing = 0;
    iShell.setLayout(iGl);

    iVr = new Ruler(iShell,true);
    iVr.setDataRange(0,128,0.,500./128.,"kHz");
    iVr.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true));
    iCnv = new MyCanvas(iShell);
    iCnv.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
    iFlr = new MyDummy(iShell);
    iFlr.setLayoutData(new GridData(SWT.FILL,SWT.FILL));
    iHr = new Ruler(iShell,false);
    iHr.setDataRange(172,581,0.,0.0625,"ms");
    iHr.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
    
    iCnv.setRulers(iHr,iVr);
  }

  class MyDummy extends Canvas
  {
    public MyDummy(Composite iParent)
    {
      super(iParent,SWT.NONE);
    }
  }

  class MyCanvas extends Canvas
  {
    Ruler iHr = null;
    Ruler iVr = null;
    
    public MyCanvas(Composite iParent)
    {
      super(iParent,SWT.NONE);

      addPaintListener(new PaintListener()
      {
        public void paintControl(PaintEvent e)
        {
          e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
          e.gc.fillRectangle(e.x,e.y,e.width,e.height);
        }
      });
      
      addMouseMoveListener(new MouseMoveListener()
      {
        public void mouseMove(MouseEvent e)
        {
          String sH = "";
          String sV = "";
          if (iHr!=null)
          {
            iHr.drawPositionLine(e.x);
            sH=NumberFormatter.formatAndAdjust(iHr.getValOfPos(e.x));
            sH+=" "+iHr.getUnit();
            if (iHr.isQuantized())
              sH = iHr.getDataPointOfPos(e.x)+"\t"+sH;
            sH = "X: "+sH;
          }
          if (iVr!=null)
          {
            iVr.drawPositionLine(e.y);
            sV=NumberFormatter.formatAndAdjust(iVr.getValOfPos(e.y));
            sV+=" "+iVr.getUnit();
            if (iVr.isQuantized())
              sV = iVr.getDataPointOfPos(e.y)+"\t"+sV;
            sV = "Y: "+sV;
          }
          setToolTipText(sH+(sH.length()>0&&sV.length()>0?"\n":"")+sV);
        }
      });
    }
    
    public void setRulers(Ruler iHr, Ruler iVr)
    {
      this.iHr = iHr;
      this.iVr = iVr;
    }
  }
  
}