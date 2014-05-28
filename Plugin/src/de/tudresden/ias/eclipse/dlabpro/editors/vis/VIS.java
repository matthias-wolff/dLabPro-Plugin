package de.tudresden.ias.eclipse.dlabpro.editors.vis;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.VisEditor;
import de.tudresden.ias.eclipse.dlabpro.utils.UIUtil;

/**
 * Klasse enth�lt alle Konstanten, die f�r den VisEditor ben�tigt werden, so k�nnen ohne Probleme
 * Werte ver�ndert werden.
 * 
 * Gliederung: - Ruler-Konstanten - VerticalSeparator-Konstanten - ComponentPanel-Konstanten -
 * Oscillogram-Konstanten - VisOutlinePage-Konstanten - XML-Konstanten
 * 
 * @author Stephan Larws
 * 
 */
public class VIS {

  public static boolean bSpecShowValues = true;
  public static boolean bSpecShowLevels = false;
  
  public static int VERBOSE = 0;
  
  public static final double EPS = 1.E-300;
  
	// ----begin ruler constants-------------------------------
	// style constants for the horizontal ruler
	public static final int HORIZONTAL_TOP = 0;

	public static final int HORIZONTAL_BOTTOM = 1;

	// height of the horizontal ruler
	public static final int OTHERDIMENSION_Y = 20;

	// style constants for the vertical ruler
	public static final int VERTICAL = 0;

	public static final int VERTICAL_CENTERED = 1;

	// width of the vertical ruler
	public static final int OTHERDIMENSION_X = 20;

	public static final int X_INDENT = 0;

	// pixel values for the rulers
	public static final int VERTICAL_TEXT_OFFSET = 1;

	public static final int HORIZONTAL_TEXT_OFFSET = 1;
	
	public static final int TEXT_LINE_OFFSET = 2;

	public static final int TWO_SPACES_INBETWEEN_LOW = 10;
	public static final int TWO_SPACES_INBETWEEN_HIGH = 20;
	public static final int FOUR_SPACES_INBETWEEN_LOW = 20;
	public static final int FOUR_SPACES_INBETWEEN_HIGH = 80;
	public static final int FIVE_SPACES_INBETWEEN_LOW = 81;
	public static final int FIVE_SPACES_INBETWEEN_HIGH = 133;
	public static final int TEN_SPACES_INBETWEEN_LOW = 134;
	public static final int TEN_SPACES_INBETWEEN_HIGH = 200;
	
	// size of the marking lines on the ruler
	
	public static final int SHORT_LINE = 5;
	
	public static final int LONG_LINE = 10;

	// exponent values and number of digits for scientific notation
	public static final int EXPONENT_FOR_SCIENTIFIC_NOTATION_LOW = -4;

	public static final int EXPONENT_FOR_SCIENTIFIC_NOTATION_HIGH = 4;

	public static final int NUMBER_OF_DIGITS_FOR_WHOLE_NUMBER = 6;

	// ----end ruler constants-----------------------------------

	// ----Anfang VerticalSeparator Konstanten-------------------

	public static final int VS_VERTICAL_SPACE = 2;

	// ----Ende VerticalSeparator Konstanten---------------------

	// ----Anfang ComponentPanel Stylekonstanten-----------------

	public static final int CP_STYLE_OSCILLOGRAM = 1;

	public static final int CP_STYLE_SPECTROGRAM = 2;

  public static final int CP_STYLE_BARDIAGRAM = 3;

  public static final int CP_STYLE_3DVIEW = 4;

  // ----Ende ComponentPanel Stylekonstanten-------------------

	// ----Anfang Oscillogram Konstanten----------------------------

	public static final int OSCI_POINT_DISTANCE = 8;

	public static final int OSCI_POINT_RADIUS = 2;

	// ----Ende Oscillogram Konstanten------------------------------

	// ----Anfang VisOutlinePage Konstanten----------------------

  public static final int VOP_MAX_VISIBLE_DISPLAYS = 50;
	public static final int VOP_DEF_VISIBLE_DISPLAYS = 16;

	// ----Ende VisOutlinePage Konstanten------------------------

	// ----Anfang ZoomEvent Konstanten---------------------------

	public static final int ZOOM_IN = 1;

	public static final int ZOOM_OUT = 2;

	// ----Ende ZoomEvent Konstanten-----------------------------
	
	// -- Graphics methods --
	
	 /**
   * Draws a by 90� to the left rotated image onto a given GC.
   * 
   * @param gc
   *        The GC on which to draw
   * @param s
   *        The text that needs to be rotated and drawn
   * @param point
   *        The top left corner of the image that will be drawn
   */
  public static void drawRotatedString(GC gc, String s, int x, int y)
  {
    Point p = gc.stringExtent(s);
    gc.setAdvanced(true);
    if (gc.getAdvanced())
    {
      Transform iTfOld = new Transform(gc.getDevice());
      Transform iTf    = new Transform(gc.getDevice());
      gc.getTransform(iTfOld);
      gc.getTransform(iTf);
      iTf.translate(x,y+p.x);
      iTf.rotate(-90);
      gc.setTransform(iTf);
      gc.drawString(s,0,0);
      gc.setTransform(iTfOld);
      iTf.dispose();
      iTfOld.dispose();
    }
    else
    {
      if (p.x <= 0) p.x = 1;
      if (p.y <= 0) p.y = 1;
      
      // Draw text on a compatible image
      Image iIo = new Image(gc.getDevice(),p.x,p.y);
      GC iImgGc = new GC(iIo);
      iImgGc.setFont(gc.getFont());
      iImgGc.setForeground(gc.getForeground());
      iImgGc.setBackground(gc.getBackground());
      iImgGc.drawString(s,0,0);

      // Rotate image
      ImageData iIdo =  iIo.getImageData();
      ImageData iIdr = new ImageData(iIdo.height, iIdo.width, iIdo.depth, iIdo.palette);
      for (int j = 0; j < iIdo.width; j++)
        for (int i= 0; i < iIdo.height; i++)
          iIdr.setPixel(i,j,iIdo.getPixel(iIdo.width-1-j,i));
      Image iIr = new Image(gc.getDevice(),iIdr);

      // Draw
      gc.drawImage(iIr,x,y);

      // Clean up
      iImgGc.dispose();
      iIo.dispose();
      iIr.dispose();
    }
  }

  // -- Miscellaneous methods --

  /**
   * Computes an array of at most <code>nMaxInts</code> decimal intervals which fully accommodate
   * the values from <code>nLo</code> through <code>nHi</code>.
   * 
   * @param nLo
   *          The minimal value
   * @param nHi
   *          The maximal value
   * @param nMaxInts
   *          The maximal number of intervals
   * @return An array with at most <code>nMaxInts</code>+1 values defining the interval boundaries
   */
  public static double[] decimalZoning(double nLo, double nHi, int nMaxInts)
  {
    if (nLo==Double.NaN) nLo = 0.;
    if (nHi==Double.NaN) nHi = 1.;
    if (nLo>nHi) { double n = nHi; nHi=nLo; nLo=n; }

    // Compute magnitude of ruler and initial interval size
    double m = Math.max(Math.log10(Math.abs(nLo)), Math.log10(Math.abs(nHi)));
    double b = Math.pow(10., Math.floor(m));

    // Decimate intervals until maximal count is reached
    double n = Math.ceil(nHi/b) - Math.floor(nLo/b);
    while (n <= nMaxInts)
    {
      if (((Math.ceil(nHi/b) - Math.floor(nLo/b)) * 10) > nMaxInts) break;
      b /= 10.;
      n = Math.ceil(nHi/b) - Math.floor(nLo/b);
    }

    // Write zone boundaries into an array
    double b0 = Math.floor(nLo / b) * b;
    double[] aZones = new double[(int)n + 1];
    for (int i = 0; i <= n; i++)
      aZones[i] = b0 + i * b;

    return aZones;
  }

  /**
   * Abbreviates a string so that its width in pixels is not greater than a
   * given value. If the string is abbreviated it will be suffixed by "...". If
   * no abbreviation with at least one remaining letter is possible the
   * method returns an empty string. 
   * 
   * @param iGc
   *          The graphics context to draw the string at (needed for the
   *          computation of the string's dimensions, cannot be <code>null</code>)
   * @param sText
   *          The string
   * @param nWidth
   *          The width in pixels to fit the string into
   * @return The original, abbreviated or empty string
   */
  public static String abbreviateToFit(GC iGc, String sText, int nWidth)
  {
    if (sText==null) return null;
    if (iGc.stringExtent(sText).x<=nWidth) return sText;
    while (sText.length()>0)
    {
      if (iGc.stringExtent(sText+"...").x<=nWidth) return sText+"...";
      sText=sText.substring(0,sText.length()-1);
    }
    return "";
  }
  
  /**
   * Opens a file in the VIS editor. The file must be a resource in the workspace.
   * 
   * @param iFile
   *          the file
   */
  public static VisEditor openVisEditorFor(File iFile)
  {
    System.out.println("VIS.openVisEditorFor("+iFile.getAbsolutePath()+")");
    if (iFile==null) return null;
    if (!iFile.exists() || !iFile.isFile())
    {
      UIUtil.showErrorDialog("Cannot open VisEditor for " +
        iFile.getAbsolutePath()+".","The item does not exist or is not a file.",
        null);
      return null;
    }

    IWorkbenchPage iWbp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    IFile iWsf = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(iFile.getAbsolutePath()));
    if (iWsf==null)
    {
      UIUtil.showErrorDialog("Cannot open VisEditor for " +
        iFile.getAbsolutePath()+".","The file is outside the current workspace.",
        null);
      return null;
    }
    try
    {
      //iWsf.refreshLocal(IResource.DEPTH_ZERO,null);
      return (VisEditor)iWbp.openEditor(new FileEditorInput(iWsf),"dLabPro.VisEditor");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  // -- Debuggin' methods --
  
	public static final void MSG(String sMsg)
	{
	  if (VERBOSE>0) System.out.println(sMsg);
	}
	
	public static final void EXCEPTION(Exception e)
	{
	  System.out.println("!EXCEPTION: " + e.getClass().getCanonicalName() + " @ "
        + e.getStackTrace()[0]);
	  if (e.getMessage()!=null)
	    System.out.println("!- Reason : "+e.getMessage());
	}
}
