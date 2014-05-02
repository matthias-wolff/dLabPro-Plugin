package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoMouseMoveEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoScrollEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.Ruler;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.RulerCalculator;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataCompInfo;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataException;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.preferences.IVisEditorPreferenceConstants;
import de.tudresden.ias.jlab.kernel.JlData;

public class ThreeDDisplay extends AbstractDataDisplay
implements IVisEditorPreferenceConstants
{
  public static final int PERSP_DEFAULT =   0; // Default view 
  public static final int PERSP_BIRD    =   1; // Default bird's-eye view
  public static final int PERSP_TOP     =   2; // View directly from above 
  public static final int PERSP_FRONT   =   3; // View directly from the front
  public static final int PERSP_LEFT    =   4; // View directly from the left
  public static final int PERSP_REAR    =   5; // View directly from the rear 
  public static final int PERSP_RIGHT   =   6; // View directly from the right 
  public static final int PERSP_BIRD_N  =   7; // Bird's-eye view from north
  public static final int PERSP_BIRD_NE =   8; // Bird's-eye view from north
  public static final int PERSP_BIRD_E  =   9; // Bird's-eye view from east
  public static final int PERSP_BIRD_SE =  10; // Bird's-eye view from south-east
  public static final int PERSP_BIRD_S  =  11; // Bird's-eye view from south
  public static final int PERSP_BIRD_SW =  12; // Bird's-eye view from south-west
  public static final int PERSP_BIRD_W  =  13; // Bird's-eye view from west
  public static final int PERSP_BIRD_NW =  14; // Bird's-eye view from north-west
  public static final int PERSP_EXP     = 100; // Experimental perspective

  protected static final int MAX_FACES = 1000;
  
  protected Ruler         m_iVruler;
  protected Perspective   m_iPersp;
  protected TreeSet<Face> m_iFaces;
  protected Face          m_iLastPaintedFace;
  protected int           m_nWaitingPaints = -1;
  protected Image         m_iImgRepaint;

  public ThreeDDisplay(Composite iParent, int nStyle, DataCompInfo[] aDci, Ruler iHruler) throws DataException
  {
    super(iParent, aDci, iHruler);
    getValues();
    
    m_iImgRepaint = DLabProPlugin.loadIconImage("icons/elcl16/refresh_edit.gif");
    
    addMouseListener(new MouseListener()
    {
      public void mouseDoubleClick(MouseEvent e)
      {
      }
      public void mouseDown(MouseEvent e)
      {
      }
      public void mouseUp(MouseEvent e)
      {
        if (e.x>getSize().x-20 && e.y<20 && e.button==1)
        {
          m_iLastPaintedFace = null;
          m_nWaitingPaints = -1;
          redraw(); update();
        }
      }
    });
    
    addDisposeListener(new DisposeListener()
    {
      public void widgetDisposed(DisposeEvent e)
      {
        if (m_iImgRepaint!=null) m_iImgRepaint.dispose();
      }
    });
  }

  /*
   * (non-Javadoc)
   */
  public static String getIconFileName() {
    return "icons/obj16/3D_comp_obj.gif";
    // return ("icons/obj16/spec_obj.gif");
  }

  @Override
  protected DisplayInfoMouseMoveEvent createInfoEventOnMouse(int x, int y)
  {
    return null;
  }

  @Override
  protected DisplayInfoScrollEvent createInfoEventOnScroll()
  {
    return null;
  }

  @Override
  protected double getCompValueAt(int nx, int ny)
  {
    return 0;
  }

  @Override
  protected double getDataValueAt(int nx, int ny)
  {
    return 0;
  }

  @Override
  public void setRecDetail(int first, int last)
  {
    super.setRecDetail(first, last);
    getValues();
  }
  
  @Override
  protected Ruler int_createVerticalRuler(Composite iParent)
  {
    int nStart = 0;
    //note: added -1 because drawing starts at -0.5comps
    int nEnd   = m_aDci.length;
    for (int i=0; i<m_aDci.length; i++)
      if (!m_aDci[i].bVisible) nStart++; else break;
    for (int i=m_aDci.length-1; i>=0; i--)
      if (!m_aDci[i].bVisible) nEnd--; else break;

    JlData iData = getJlData();
    m_iVruler = new Ruler(iParent,true);
    m_iVruler.setDataRange(nStart,nEnd-nStart,iData.cofs,iData.cinc,iData.cunit);
    m_iVruler.setEnabled(false);
    return m_iVruler;
  }

  protected void drawRepaintIcon(GC iGc, int x, int y)
  {
    iGc.setBackground(iGc.getDevice().getSystemColor(SWT.COLOR_WHITE));
    iGc.fillRectangle(x,y,16,16);
    iGc.drawImage(m_iImgRepaint,x,y);
  }
  
  @Override
  protected void paintData(GC iGc, Rectangle iDamage, RulerCalculator iHrc, RulerCalculator iVrc)
  {
    class PaintRunnable implements Runnable
    {
      GC              iGc;
      RulerCalculator iHrc;
      RulerCalculator iVrc;
      
      public PaintRunnable(GC iGc, RulerCalculator iHrc, RulerCalculator iVrc)
      {
        this.iGc  = iGc;
        this.iHrc = iHrc;
        this.iVrc = iVrc;
      }
      
      public void run()
      {        
        if (isDisposed()) return;

        boolean bDisposeGc = false;
       
        Face f1 = new Face(null,1,0,0,0,0,1); f1.transform(m_iPersp);
        Face f2 = new Face(null,1,0,1,1,0,1); f2.transform(m_iPersp);
        
        // Get preferences
        IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();
        int nSrfce = iStore.getInt(P_VIS_3DSURFACE);
        int nAlpha = 255-(int)((float)iStore.getInt(P_VIS_3DTRANSPARENCY)*2.55);

        int nW = iHrc.getLength();
        int nH = iVrc.getLength();
        if (iGc.isDisposed())
        {
          iGc = new GC(ThreeDDisplay.this);
          bDisposeGc = true;
        }
        iGc.setForeground(iGc.getDevice().getSystemColor(SWT.COLOR_BLACK));
        iGc.setBackground(iGc.getDevice().getSystemColor(SWT.COLOR_WHITE));
        iGc.setAdvanced(true);

        Color[] aColors = m_iVcm.getValueColors();
        
        Iterator<Face> iFite = m_iFaces.iterator();
        if (m_iLastPaintedFace!=null)
        {
          iFite = m_iFaces.tailSet(m_iLastPaintedFace).iterator();
          if (iFite.hasNext()) iFite.next();
        }
        int  nCtr = 0;
        Face f    = null;
        while (iFite.hasNext())
        {
          f = iFite.next();
          if (m_nWaitingPaints==0)
          {
            int x1 = (int)Math.round(f.X1*nW); int y1 = (int)Math.round(f.Y1*nH);
            int x2 = (int)Math.round(f.X2*nW); int y2 = (int)Math.round(f.Y2*nH);
            int x3 = (int)Math.round(f.X3*nW); int y3 = (int)Math.round(f.Y3*nH);
            if (m_iPersp.b3>=0) { y1 = nH-y1; y2=nH-y2; y3=nH-y3; }
            float v = (f.v1+f.v2+f.v3)/3;
  
            Color iClr = iGc.getDevice().getSystemColor(SWT.COLOR_WHITE);
            if (nSrfce>0)
            {
              boolean bBack;
              if (f.x2==f.x3) bBack=(f.B!=f1.B);
              else            bBack=(f.B!=f2.B);
              if (m_iPersp.b3<0) bBack = !bBack;
              
              if (bBack)
                iClr = iGc.getDevice().getSystemColor(SWT.COLOR_GRAY);
              else
              {
                int col = (int)Math.round(v * aColors.length);
                if (col >= aColors.length) col = aColors.length - 1;
                if (col < 0) col = 0;
                iClr = aColors[col];
              }
            }
            iGc.setBackground(iClr);
            if (iGc.getAdvanced()) iGc.setAlpha(nAlpha);
            iGc.fillPolygon (new int[]{x1,y1,x2,y2,x3,y3});
            if (iGc.getAdvanced()) iGc.setAlpha(255);
            if (nSrfce!=1)
            {
              iGc.setForeground(nSrfce==3 ? iClr : iGc.getDevice().getSystemColor(SWT.COLOR_BLACK));
              iGc.drawPolyline(new int[]{x1,y1,x2,y2,x3,y3});
            }
          }
          if (++nCtr==MAX_FACES) break; 
        }
        if (iFite.hasNext())
        {
          m_iLastPaintedFace = f;
        }
        else
        {
          m_iLastPaintedFace = null;
          m_nWaitingPaints--;
        }
        if (!(iGc.getDevice() instanceof Printer))
          drawRepaintIcon(iGc,iHrc.getLength()-20,4);
        
        if (bDisposeGc) iGc.dispose();
      }
    }

    /* SWT (or someone else?) will interrupt sync and async runnables on the GUI
     * thread if they do not finish within two seconds or so. The work-around is
     * to do the painting in several shorter parts (paint runners). Paint
     * runners for the widget are called asynchronously to keep the GUI reacting
     * to user input. Painting runners for printers are called synchronously as
     * the printer must not start until all painting is done.
     * 
     * This is no solution to be happy with. One issue is that repainting is
     * missed sometimes leaving an incomplete image on the screen. 
     */
    if (iGc.getDevice() instanceof Printer) optimizeFaces(5000);
    m_nWaitingPaints++;
    int nRunners = m_iFaces.size()/MAX_FACES +1;
    for (int i=0; i<nRunners; i++)
      if (iGc.getDevice() instanceof Printer)
        PlatformUI.getWorkbench().getDisplay().syncExec(new PaintRunnable(iGc,iHrc,iVrc));
      else
        PlatformUI.getWorkbench().getDisplay().asyncExec(new PaintRunnable(iGc,iHrc,iVrc));
    if (!(iGc.getDevice() instanceof Printer))
      drawRepaintIcon(iGc,iHrc.getLength()-20,4);

  }
  
  private final void getValues()
  {
    JlData           iData  = m_aDci[0].iData;
    double           nMinV  = getMinValue();
    double           nMaxV  = getMaxValue();
    IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();      
    
    if (iStore.getInt(P_VIS_3DPERSPECTIVE)==PERSP_BIRD)
    {
      float nYaw   = iStore.getFloat(P_VIS_3DYAW  );
      float nPitch = iStore.getFloat(P_VIS_3DPITCH);
      m_iPersp = new Perspective(nYaw,nPitch);      
    }
    else
      m_iPersp = new Perspective(iStore.getInt(P_VIS_3DPERSPECTIVE));
    //m_iPersp = new Perspective(PERSP_EXP);

    // Create faces list (sorted by distance from viewing plain)
    m_iFaces = new TreeSet<Face>();
    try
    {
      for (int nR=m_nFirstRec+1; nR<=m_nLastRec; nR++)
        for (int nC=m_nFirstComp+1; nC<=m_nLastComp; nC++)
        {
          if (!m_aDci[nC].bVisible) continue;
          Face f1 = new Face(iData,nR,nC-1,nR-1,nC-1,nR-1,nC);
          f1.normalize(m_nFirstRec,m_nLastRec,m_nFirstComp,m_nLastComp,nMinV,nMaxV);
          f1.transform(m_iPersp);
          m_iFaces.add(f1);
          Face f2 = new Face(iData,nR,nC-1,nR,nC,nR-1,nC);
          f2.normalize(m_nFirstRec,m_nLastRec,m_nFirstComp,m_nLastComp,nMinV,nMaxV);
          f2.transform(m_iPersp);
          m_iFaces.add(f2);
        }
    }
    catch (OutOfMemoryError e)
    {
      m_iFaces.clear();
      ErrorDialog.openError(getShell(),"3D-View Error",
        "Too many faces to draw. Switch to another diagram type!",
        new Status(Status.ERROR,DLabProPlugin.PLUGIN_NAME,-1,"Out of memory",e));
    }
  }

  /**
   * Optimize faces list (remove covered faces); obviously only if no
   * transparency is used
   */
  void optimizeFaces(long nTimeout)
  {
    /* TODO: Will crash the JVM on some Linuxes */
    if (DLabProPlugin.isLinux()) return;

    /* TODO: This code is for compatibility with Java 1.5. It makes a copy of
     * the face list to be able to iterate it backwards. The likely
     * OutOfMemoryError is caught and ignored (i.e. there's no optimization
     * in this case). When migrating this code to Java 1.6, simply use
     * NavigatableSet.descendingIterator!
     */
    IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();      
    if (iStore.getInt(P_VIS_3DTRANSPARENCY)!=0) return;

    Cursor iC = new Cursor(getShell().getDisplay(), SWT.CURSOR_WAIT);
    getShell().setCursor(iC);
    try
    {
      System.out.print("ThreeDDisplay: optimizeFaces from "+m_iFaces.size());
      long nStartTime = (new Date()).getTime();
      Region iR = new Region();
      Object[] aF = m_iFaces.toArray();
      for (int nF = aF.length - 1; nF >= 0; nF--)
      {
        Face f = (Face)aF[nF];
        Point P1 = new Point((int)(1000 * f.X1), (int)(1000 * f.Y1));
        Point P2 = new Point((int)(1000 * f.X2), (int)(1000 * f.Y2));
        Point P3 = new Point((int)(1000 * f.X3), (int)(1000 * f.Y3));
        Region r = new Region();
        r.add(new int[] { P1.x, P1.y, P2.x, P2.y, P3.x, P3.y });
        r.subtract(iR);
        if (r.isEmpty()) m_iFaces.remove(f);
        else
        {
          r.add(new int[] { P1.x, P1.y, P2.x, P2.y, P3.x, P3.y });
          iR.add(r);
        }
        r.dispose();
        if (nF % 1000 == 0 && (new Date()).getTime() - nStartTime > nTimeout)
        {
          System.out.print(" (timed out, aborted)");
          break;
        }
      }
      iR.dispose();
    }
    catch (OutOfMemoryError e)
    {
      System.out.print(" (out-of-memory, cancelled)");
    }
    getShell().setCursor(null);
    iC.dispose();
    System.out.println(" down to "+m_iFaces.size());
  }
  
  class Perspective
  {    
    float a1, a2, a3;                     // X = a1*x + a2*y + a3*v;
    float b1, b2, b3;                     // Y = b1*x + b2*y + b3*v;
    float nMinX, nRngX;                   // X normalization constants
    float nMinY, nRngY;                   // Y normalization constants

    public Perspective(int nDefaultTransform)
    {
      switch (nDefaultTransform)
      {
      case PERSP_TOP    : a1= 1; a2= 0; a3= 0; b1= 0; b2= 1; b3= 0; break;
      case PERSP_FRONT  : a1= 1; a2= 0; a3= 0; b1= 0; b2= 0; b3= 1; break;
      case PERSP_LEFT   : a1= 0; a2=-1; a3= 0; b1= 0; b2= 0; b3= 1; break;
      case PERSP_REAR   : a1=-1; a2= 0; a3= 0; b1= 0; b2= 0; b3= 1; break;
      case PERSP_RIGHT  : a1= 0; a2= 1; a3= 0; b1= 0; b2= 0; b3= 1; break;
      case PERSP_BIRD   : a1= 4; a2= 5; a3= 0; b1=-2; b2= 2; b3= 3; break;
      case PERSP_BIRD_N : a1=-1; a2= 0; a3= 0; b1= 0; b2=-2; b3= 1; break;
      case PERSP_BIRD_NE: a1=-1; a2= 1; a3= 0; b1=-2; b2=-2; b3= 1; break;
      case PERSP_BIRD_E : a1= 0; a2= 1; a3= 0; b1=-2; b2= 0; b3= 1; break;
      case PERSP_BIRD_SE: a1= 1; a2= 1; a3= 0; b1=-1; b2= 1; b3= 1; break;
      case PERSP_BIRD_S : a1= 1; a2= 0; a3= 0; b1= 0; b2= 3; b3= 1; break;
      case PERSP_BIRD_SW: a1= 1; a2=-1; a3= 0; b1= 2; b2= 2; b3= 1; break;
      case PERSP_BIRD_W : a1= 0; a2=-1; a3= 0; b1= 2; b2= 0; b3= 1; break;
      case PERSP_BIRD_NW: a1=-1; a2=-1; a3= 0; b1= 2; b2=-2; b3= 1; break;
      case PERSP_EXP    : a1= 1; a2= 1; a3= 0; b1=-1; b2= 1; b3= 10; break;
      default           : a1= 2; a2= 1; a3= 0; b1= 0; b2= 1; b3= 2; break;
      }
      normalize();
    }
    
    public Perspective(float a1, float a2, float a3, float b1, float b2, float b3)
    {
      if (a1==0. && a2==0. && a3==0.) a1 = 1;
      if (b1==0. && b2==0. && b3==0.) b2 = 1;
      this.a1 = a1; this.a2 = a2; this.a3 = a3;
      this.b1 = b1; this.b2 = b2; this.b3 = b3;
      normalize();
    }
    
    public Perspective(float nYaw, float nPitch)
    {
      if (nPitch> 89.99999f) nPitch =  89.99999f;
      if (nPitch<-89.99999f) nPitch = -89.99999f;
      nYaw   = nYaw  /360*2*(float)Math.PI;
      nPitch = nPitch/360*2*(float)Math.PI;
      
      a1=(float)Math.cos(nYaw); a2=-(float)Math.sin(nYaw);
      b1=(float)Math.sin(nYaw); b2= (float)Math.cos(nYaw); 
      
      a3 = 0;
      b3=(float)(1./Math.tan(nPitch));
      if (b3>100000) b3=100000;
      
      normalize();
    }
    
    private void normalize()
    {
      nMinX = Math.min(0,Math.min(a1,Math.min(a2,a1+a2)));
      nRngX = Math.max(a3,Math.max(a1+a3,Math.max(a2+a3,a1+a2+a3)))-nMinX;

      if (b3>=0)
      {
        nMinY = Math.min(0,Math.min(b1,Math.min(b2,b1+b2)));
        nRngY = Math.max(b3,Math.max(b1+b3,Math.max(b2+b3,b1+b2+b3)))-nMinY;
      }
      else
      {
        nMinY = Math.min(b3,Math.min(b1+b3,Math.min(b2+b3,b1+b2+b3)));
        nRngY = Math.max(0,Math.max(b1,Math.max(b2,b1+b2)))-nMinY;
      }
    }
    
    public float transformX(float x, float y, float v)
    {
      return (a1*x+a2*y+a3*v-nMinX)/nRngX;
    }

    public float transformY(float x, float y, float v)
    {
      return (b1*x+b2*y+b3*v-nMinY)/nRngY;
    }
  }
  
  class Face implements Comparable<Face>
  {
    float   x1, x2, x3; // Original x-coordinates (records)
    float   y1, y2, y3; // Original y-coordinates (components)
    float   v1, v2, v3; // Original values coordinates
    float   X1, X2, X3; // Transformed x-coordinates
    float   Y1, Y2, Y3; // Transformed y-coordinates
    float   Dmin, Dmax; // Minimal and maximal distance from view plain
    boolean B;          // Transformed face is seen from behind!

    
    public Face(JlData iData, int x1, int y1, int x2, int y2, int x3, int y3)
    {
      this.x1 = x1; this.y1 = y1; 
      this.x2 = x2; this.y2 = y2; 
      this.x3 = x3; this.y3 = y3; 
      if (iData!=null)
      {
        this.v1 = (float)iData.dFetch(x1,y1);
        this.v2 = (float)iData.dFetch(x2,y2);
        this.v3 = (float)iData.dFetch(x3,y3);
      }
      else
        this.v1=this.v2=this.v3=0;
    }
    
    public void normalize(float nMinX, float nMaxX, float nMinY, float nMaxY, double nMinV, double nMaxV)
    {
      float  nRngX = nMaxX-nMinX; if (nRngX==0) nRngX = 1;
      float  nRngY = nMaxY-nMinY; if (nRngY==0) nRngY = 1;
      double nRngV = nMaxV-nMinV; if (nRngV==0) nRngV = 1; 
      x1=(x1-nMinX)/nRngX; y1=(y1-nMinY)/nRngY; v1=(float)((v1-nMinV)/nRngV);
      x2=(x2-nMinX)/nRngX; y2=(y2-nMinY)/nRngY; v2=(float)((v2-nMinV)/nRngV);
      x3=(x3-nMinX)/nRngX; y3=(y3-nMinY)/nRngY; v3=(float)((v3-nMinV)/nRngV);
    }
    
    public void transform(Perspective T)
    {
      X1 = T.transformX(x1,y1,v1);
      X2 = T.transformX(x2,y2,v2);
      X3 = T.transformX(x3,y3,v3);
      Y1 = T.transformY(x1,y1,v1);
      Y2 = T.transformY(x2,y2,v2);
      Y3 = T.transformY(x3,y3,v3);
      
      // Find out, if face is seen from behind
      // TODO: works for south-eastern bird's-eye views only :(
      if (X2!=X3)
      {
        float m = (Y3-Y2)/(X3-X2);
        float n = (X2*Y3-X3*Y2)/(X2-X3);
        float g = m*X1-Y1+n;
        B = g<0;
      }
      
      // Compute distance from view plain
      float D1 = -T.transformY(x1,y1,0);
      float D2 = -T.transformY(x2,y2,0);
      float D3 = -T.transformY(x3,y3,0);
      Dmin = Math.min(D1,Math.min(D2,D3));
      Dmax = Math.max(D1,Math.max(D2,D3));
    }
    
    public int compareTo(Face o)
    {
      if (Dmin>o.Dmax) return 1;
      if (Dmax<o.Dmin) return -1;
      if (Dmin>o.Dmin) return 1;
      if (Dmin<o.Dmin) return -1;
      if (Dmax>o.Dmax) return 1;
      if (Dmax<o.Dmax) return -1;
      if (Dmax+Dmin>o.Dmax+o.Dmin) return 1;
      if (Dmax+Dmin<o.Dmax+o.Dmin) return -1;
      if (!B && o.B) return 1;
      if (!o.B && B) return -1;
      if (x1>o.x1) return 1; if (x1<o.x1) return -1;
      if (y1>o.y1) return 1; if (y1<o.y1) return -1;
      if (x2>o.x2) return 1; if (x2<o.x2) return -1;
      if (y2>o.y2) return 1; if (y2<o.y2) return -1;
      if (x3>o.x3) return 1; if (x3<o.x3) return -1;
      if (y3>o.y3) return 1; if (y3<o.y3) return -1;
      return 0;
    }
  }
  
}
