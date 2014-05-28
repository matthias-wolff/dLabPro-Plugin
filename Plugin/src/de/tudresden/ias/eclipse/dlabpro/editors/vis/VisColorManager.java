
package de.tudresden.ias.eclipse.dlabpro.editors.vis;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.preferences.IVisEditorPreferenceConstants;

/**
 * Manages Color objects for all VisEditor DataDisplays. The main purpose of this class is that DataDisplays may use the
 * same standard colors without having to care about their disposal.
 */
public class VisColorManager implements IVisEditorPreferenceConstants
{
  /**
   * The reference counter
   */
  private static int     m_nRc                 = 0;

  /**
   * An array of foreground colors representing data components
   */
  private static Color[] m_aCompFgColor        = null;

  /**
   * An array of background colors representing data components
   */
  private static Color[] m_aCompBgColor        = null;

  /**
   * An array of images representing the component colors
   */
  private static Image[] m_aCompColorIcon     = null;
  
  /**
   * An array of colors representing data values
   */
  private static Color[] m_aValueColor         = null;

  /**
   * The currently active data component color table
   */
  private static int     m_nCurCompColorTable  = -1;

  /**
   * The currently active data value color table
   */
  private static int     m_nCurValueColorTable = -1;

  /**
   * Creates a new (reference to) the VisColorManager
   */
  public VisColorManager()
  {
    // Increment reference counter
    m_nRc++;
    
    // Make default colors available
    try
    {
      IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();
      if (m_nCurCompColorTable <0) m_nCurCompColorTable  = iStore.getInt(P_VIS_COMPCOLORTAB );
      if (m_nCurValueColorTable<0) m_nCurValueColorTable = iStore.getInt(P_VIS_VALUECOLORTAB);
    }
    catch (Throwable e)
    {
      
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#finalize()
   */
  protected void finalize() throws Throwable
  {

    // Decrement reference counter and, if this is the last instance, clean up
    if (--m_nRc == 0)
    {
      VIS.MSG("VisColorManager: cleaning up");
      disposeCompColors();
      disposeValueColors();
    }

    // Call super class
    super.finalize();
  }

  // -- Color info -------------------------------------------------------------
  
  /**
   * Returns the value of a color. The value is the whiteness level (0 for black
   * and 255 for white)
   * 
   *  @throws NullPointerException if <code>iColor</code> is <code>null</code>
   */
  public static int getColorValue(Color iColor)
  {
    return (iColor.getBlue()+iColor.getGreen()+iColor.getRed())/3;
  }
  
  // -- Common Colors ----------------------------------------------------------

  /**
   * Returns the VisEditors' foreground color. Callers do not need to care about disposing of this color.
   * 
   * @return The foreground color
   */
  public Color getFgColor(Display iD)
  {
    return iD.getSystemColor(SWT.COLOR_LIST_FOREGROUND); 
  }

  /**
   * Returns the VisEditors' background color. Callers do not need to care about disposing of this color.
   * 
   * @return The background color
   */
  public Color getBgColor(Display iD)
  {
    return iD.getSystemColor(SWT.COLOR_LIST_BACKGROUND); 
  }

  // -- Data Component Colors --------------------------------------------------

  /**
   * Returns the foreground color representing the data component <code>nComp</code>. Callers do not need to care
   * about disposing of this color.
   * 
   * @param nComp
   *          The zero-based component index
   * @return The color
   */
  public Color getCompFgColor(int nComp)
  {
    if (m_aCompFgColor == null) switchCompColors(m_nCurCompColorTable);
    return m_aCompFgColor[nComp % m_aCompFgColor.length];
  }

  /**
   * Returns the background color representing the data component <code>nComp</code>. Callers do not need to care
   * about disposing of this color.
   * 
   * @param nComp
   *          The zero-based component index
   * @return The color
   */
  public Color getCompBgColor(int nComp)
  {
    if (m_aCompBgColor == null) switchCompColors(m_nCurCompColorTable);
    return m_aCompBgColor[nComp % m_aCompBgColor.length];
  }

  /**
   * Returns an image for displaying the foreground and background colors representing the data component
   * <code>nComp</code>. Callers do not need to care about disposing of this image.
   * 
   * @param nComp
   *          The zero-based component index
   * @return The image
   */
  public Image getCompColorIcon(int nComp)
  {
    if (m_aCompColorIcon == null) switchCompColors(m_nCurCompColorTable);
    return m_aCompColorIcon[nComp % m_aCompColorIcon.length];
  }

  /**
   * Returns the names of the available component color tables
   */
  public static String[] getCompColorTables()
  {
    return new String[]
    {
      "dLabPro",
      "Microsoft Excel"
    };
  }

  /**
   * Creates or switches the data component color table.
   * 
   * @param nCompColorTable
   *          A color table index (0..1) or -1 to switch to the next color table
   */
  public void switchCompColors(int nCompColorTable)
  {
    // Initialize
    disposeCompColors();
    if (nCompColorTable < 0) nCompColorTable = m_nCurCompColorTable + 1;

    // Component color table #1 (MS Excel)
    if (nCompColorTable == 1)
    {
      m_aCompBgColor = new Color[8];
      m_aCompBgColor[0] = new Color(null,153,153,255);
      m_aCompBgColor[1] = new Color(null,153,051,102);
      m_aCompBgColor[2] = new Color(null,255,255,204);
      m_aCompBgColor[3] = new Color(null,204,255,255);
      m_aCompBgColor[4] = new Color(null,102,000,102);
      m_aCompBgColor[5] = new Color(null,255,128,128);
      m_aCompBgColor[6] = new Color(null,000,102,204);
      m_aCompBgColor[7] = new Color(null,204,204,255);
      m_nCurCompColorTable = 1;
    }
    // Component color table #0 (default)
    else
    {
      m_aCompBgColor = new Color[5];
      m_aCompBgColor[0] = new Color(null,192,192,192);
      m_aCompBgColor[1] = new Color(null,255,051,102);
      m_aCompBgColor[2] = new Color(null,000,102,204);
      m_aCompBgColor[3] = new Color(null,255,255,102);
      m_aCompBgColor[4] = new Color(null,051,204,102);
      m_nCurCompColorTable = 0;
    }
    
    // Create foreground colors from background colors
    m_aCompFgColor = new Color[m_aCompBgColor.length];
    for (int i = 0; i < m_aCompBgColor.length; i++)
    {
      int r = m_aCompBgColor[i].getRed();
      int g = m_aCompBgColor[i].getGreen();
      int b = m_aCompBgColor[i].getBlue();
      double p1 = 220./(double)(Math.max(Math.max(r,g),b));
      double p2 = 300./(double)(r+g+b);
      double p = (p1+p2)/2; if (p>1) p=1;
      //double p = Math.min(p1,p2); if (p>1) p=1;
      m_aCompFgColor[i] = new Color(null,(int)(p*r),(int)(p*g),(int)(p*b));
    }
    
    // Create color icons
    m_aCompColorIcon = new Image[m_aCompBgColor.length];
    for (int i = 0; i < m_aCompBgColor.length; i++)
    {
      m_aCompColorIcon[i] = new Image(null,16,16);
      GC gc = new GC(m_aCompColorIcon[i]);
      gc.setBackground(m_aCompBgColor[i]);
      gc.setForeground(m_aCompFgColor[i]);
      gc.fillRectangle(0,4,16,8);
      gc.drawRectangle(0,4,15,7);
      gc.dispose();
    }
  }
  
  /**
   * Disposes the all data component colors.
   */
  private void disposeCompColors()
  {
    if (m_aCompFgColor != null)
      for (int i = 0; i < m_aCompFgColor.length; i++)
        if (m_aCompFgColor[i] != null) m_aCompFgColor[i].dispose();
    m_aCompFgColor = null;

    if (m_aCompBgColor != null)
      for (int i = 0; i < m_aCompBgColor.length; i++)
        if (m_aCompBgColor[i] != null) m_aCompBgColor[i].dispose();
    m_aCompBgColor = null;

    if (m_aCompColorIcon != null)
      for (int i = 0; i < m_aCompColorIcon.length; i++)
        if (m_aCompColorIcon[i] != null) m_aCompColorIcon[i].dispose();
    m_aCompColorIcon = null;
  }

  // -- Data Value Colors ------------------------------------------------------

  /**
   * Returns the names of the available value color tables
   */
  public static String[] getValueColorTables()
  {
    return new String[]
    {
      "dLabPro",
      "Labview",
      "Hot Metal",
      "Grey",
      "Inverse Grey"
    };
  }
  
  /**
   * Returns an array of Color objects for representing data values. Callers do not need to care about disposing of
   * these colors.
   * 
   * @return The color array
   */
  public Color[] getValueColors()
  {
    if (m_aValueColor == null) switchValueColors(m_nCurValueColorTable);
    return m_aValueColor;
  }

  /**
   * Creates or switches the data value color table.
   * 
   * @param nCColorTable
   *          A color table index (0..2) or -1 to switch to the next color table
   */
  public void switchValueColors(int nValueColorTable)
  {
    // Initialize
    disposeValueColors();
    if (nValueColorTable < 0) nValueColorTable = m_nCurValueColorTable + 1;

    // Value color table #1
    if (nValueColorTable == 1)
    {
      m_aValueColor = new Color[65];
      m_aValueColor[0] = new Color(null,0,0,0);
      m_aValueColor[1] = new Color(null,0,0,255);
      m_aValueColor[63] = new Color(null,255,230,0);
      m_aValueColor[64] = new Color(null,255,0,0);
      m_nCurValueColorTable = 1;
    }
    // Value color table #2
    else if (nValueColorTable == 2)
    {
      m_aValueColor = new Color[65];
      m_aValueColor[0] = new Color(null,3,121,83);
      m_aValueColor[2] = new Color(null,9,104,99);
      m_aValueColor[4] = new Color(null,18,82,121);
      m_aValueColor[6] = new Color(null,26,60,143);
      m_aValueColor[8] = new Color(null,35,40,162);
      m_aValueColor[10] = new Color(null,43,21,179);
      m_aValueColor[12] = new Color(null,52,4,191);
      m_aValueColor[14] = new Color(null,60,13,199);
      m_aValueColor[16] = new Color(null,69,23,202);
      m_aValueColor[18] = new Color(null,77,34,199);
      m_aValueColor[20] = new Color(null,86,42,191);
      m_aValueColor[22] = new Color(null,95,47,179);
      m_aValueColor[24] = new Color(null,103,49,162);
      m_aValueColor[26] = new Color(null,112,49,143);
      m_aValueColor[28] = new Color(null,120,45,121);
      m_aValueColor[30] = new Color(null,129,39,99);
      m_aValueColor[32] = new Color(null,137,30,77);
      m_aValueColor[34] = new Color(null,145,20,59);
      m_aValueColor[36] = new Color(null,153,7,41);
      m_aValueColor[38] = new Color(null,162,8,27);
      m_aValueColor[40] = new Color(null,170,26,17);
      m_aValueColor[42] = new Color(null,179,45,12);
      m_aValueColor[44] = new Color(null,187,66,12);
      m_aValueColor[46] = new Color(null,196,88,18);
      m_aValueColor[48] = new Color(null,205,110,28);
      m_aValueColor[50] = new Color(null,213,132,43);
      m_aValueColor[52] = new Color(null,222,155,61);
      m_aValueColor[54] = new Color(null,230,177,82);
      m_aValueColor[56] = new Color(null,239,198,104);
      m_aValueColor[58] = new Color(null,247,218,126);
      m_aValueColor[60] = new Color(null,254,237,147);
      m_aValueColor[62] = new Color(null,246,253,166);
      m_aValueColor[64] = new Color(null,238,244,180);
      m_nCurValueColorTable = 2;
    }
    // Value color table #3
    else if (nValueColorTable == 3)
    {
      m_aValueColor = new Color[65];
      m_aValueColor[0] = new Color(null,255,255,255);
      m_aValueColor[64] = new Color(null,0,0,0);
      m_nCurValueColorTable = 3;
    }
    // Value color table #4
    else if (nValueColorTable == 4)
    {
      m_aValueColor = new Color[65];
      m_aValueColor[0] = new Color(null,0,0,0);
      m_aValueColor[64] = new Color(null,255,255,255);
      m_nCurValueColorTable = 4;
    }
    // Value color table #0 (default)
    else
    {
      m_aValueColor = new Color[65];
      m_aValueColor[0] = new Color(null,0,17,37);
      m_aValueColor[43] = new Color(null,0,115,255);
      m_aValueColor[58] = new Color(null,255,238,0);
      m_aValueColor[64] = new Color(null,255,0,47);
      m_nCurValueColorTable = 0;
    }

    // Interpolate missing colors
    if (m_aValueColor[0] == null) m_aValueColor[0] = new Color(null,0,0,0);
    if (m_aValueColor[m_aValueColor.length - 1] == null) m_aValueColor[m_aValueColor.length - 1] = new Color(
        null,255,255,255);
    for (int i = 0, j; i < m_aValueColor.length;)
    {
      for (j = i + 1; j < m_aValueColor.length && m_aValueColor[j] == null; j++)
        ;
      if (j > i + 1) for (int k = i + 1; k < j; k++)
      {
        double nInt = (double)(k - i) / (double)(j - i);
        int nR = (int)(nInt * (m_aValueColor[j].getRed() - m_aValueColor[i].getRed()) + m_aValueColor[i]
            .getRed());
        int nG = (int)(nInt
            * (m_aValueColor[j].getGreen() - m_aValueColor[i].getGreen()) + m_aValueColor[i]
            .getGreen());
        int nB = (int)(nInt * (m_aValueColor[j].getBlue() - m_aValueColor[i].getBlue()) + m_aValueColor[i]
            .getBlue());
        m_aValueColor[k] = new Color(null,nR,nG,nB);
      }
      i = j;
    }
  }

  /**
   * Disposes the all data value colors.
   */
  private void disposeValueColors()
  {
    if (m_aValueColor != null) for (int i = 0; i < m_aValueColor.length; i++)
      if (m_aValueColor[i] != null) m_aValueColor[i].dispose();
    m_aValueColor = null;
  }

}

// EOF
