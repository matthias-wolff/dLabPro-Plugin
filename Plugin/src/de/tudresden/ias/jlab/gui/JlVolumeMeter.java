/* jLab type JlVolumeMeter
 * - Volume meter
 *
 * AUTHOR  : Johannes Neuber
 * UPDATE  : $Date: 2011-11-17 16:03:30 +0100 (Do, 17 Nov 2011) $, $Author: wolff $
 *           $Revision: 173 $
 * PACKAGE : de.tudresden.ias.jlab.gui
 * RCS-ID  : $Id: JlVolumeMeter.java 173 2011-11-17 15:03:30Z wolff $
 */

package de.tudresden.ias.jlab.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import de.tudresden.ias.jlab.kernel.JlObservableFloat;

/**
 * Use this class for the dynamic visualization of the Output/Input Volume. For this a JlVolumeMeter
 * Object Instance must be set as Observer for a JlSound Object instance. <br>
 * After that, call the start() method and enjoy ...
 * 
 * @author Johannes Neuber
 */
public class JlVolumeMeter extends JPanel implements Observer
{
  private static final long        serialVersionUID = -577893050846229066L;
  // private final static Font FONT16 = new Font("serif", Font.BOLD, 16);
  private final static Font        FONT12           = new Font("serif", Font.PLAIN, 12);
  private final static int         PADX             = 0;
  private final static int         PADY             = 0;
  private final static BasicStroke STROKE           = new BasicStroke(1.0f);
  private boolean                  isStatusShown    = true;
  private String                   statusText       = "";
  private Color[]                  colorArray;
  private int                      GRIDWIDTH        = 0;
  private int                      GRIDHEIGHT       = 1;
  private int                      INFOPAD          = 15;
  private int                      segmentsToPaint  = 0;
  private int                      w                = 0;
  private int                      h                = 0;
  private int                      gridWidth        = 0;
  private int                      gridHeight       = 0;
  private int                      x                = 0;
  private Rectangle2D              rect;
  private Dimension                d                = null;
  protected float                  m_nMaxLevel      = 3.0f;
  protected float                  m_nMinLevel      = -90.0f;
  private float                    level            = -90.0f;
  
  /**
   * The constructor needs only one parameter: the number of segments the Volume Meter shall have.
   */
  public JlVolumeMeter(int segments)
  {
    super();
    this.setBackground(Color.black);
    this.setMinimumSize(new Dimension(185, 21));
    this.setPreferredSize(new Dimension(240, 24));

    this.GRIDWIDTH = segments;
    this.colorArray = new Color[GRIDWIDTH];
    this.rect = new Rectangle2D.Float();
    int division = GRIDWIDTH / 3;
    for (int i = 0, j = (GRIDWIDTH - 1); i < GRIDWIDTH; i++, j--)
    {
      if (i < 2 * division)
      {
        colorArray[i] = new Color((int)((i + 1) * (230 / (2 * division))), 230, 0);
      }
      else if (i >= 2 * division && i < GRIDWIDTH)
      {
        colorArray[i] = new Color(230, (int)((j) * (230 / division)), 0);
      }
    }
  }

  public void paint(Graphics g)
  {
    // Calculate number of segments to be painted
    if (level < m_nMinLevel) level = m_nMinLevel;
    if (level > m_nMaxLevel) level = m_nMaxLevel;
    float nPercent = (level - m_nMinLevel) / (m_nMaxLevel - m_nMinLevel);
    segmentsToPaint = Math.round(nPercent * GRIDWIDTH);

    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (d == null)
    {
      d = getSize();
      w = d.width;
      h = d.height;

      if (h < 20 || !isStatusShown) this.INFOPAD = 0;

      gridWidth = (d.width - ((GRIDWIDTH + 1) * PADX)) / GRIDWIDTH;
      gridHeight = ((d.height - INFOPAD) - ((GRIDHEIGHT + 1) * PADY)) / GRIDHEIGHT - 1;
    }

    g2.setBackground(getBackground());
    g2.clearRect(0, 0, w, h);
    g2.setColor(getForeground());
    g2.fillRect(0, h - INFOPAD, w, INFOPAD);

    if (h >= 20 && isStatusShown)
    {
      g2.setColor(Color.black);
      g2.setFont(FONT12);
      g2.drawString(statusText, PADX, (h - (0.2f * INFOPAD)));
    }

    x = PADX;

    g2.setStroke(STROKE);
    for (int i = 0; i < GRIDWIDTH; i++)
    {
      // draw & fill Rectangle2D.Double
      rect.setRect(x, PADY, gridWidth, gridHeight);
      if ((i + 1) <= segmentsToPaint) g2.setPaint(colorArray[i]);
      else g2.setPaint(Color.black);
      g2.fill(rect);
      g2.setPaint(getForeground());
      g2.draw(rect);

      x += gridWidth + PADX;
    }
  }

  /**
   * Sets a new volume level.
   * 
   * @param volume
   *          The volume level.
   */
  public void setValue(float volume)
  {
    if (volume < m_nMinLevel) volume = m_nMinLevel;
    if (volume > m_nMaxLevel) volume = m_nMaxLevel;
    if (this.level==volume) return;
    this.level = volume;
    repaint();    
  }
  
  /**
   * Needed to fulfill the Observer Interface. To make the JlVolumeMeter show a correct value, the
   * arg(ument) parameter have to be a Float Object.
   * 
   * @param o
   *          An <code>JlObservableFloat</code> instance
   * @param arg
   *          Not used
   */
  public void update(Observable o, Object arg)
  {
    if (o instanceof JlObservableFloat)
    {
      setValue(((JlObservableFloat)o).get());
    }
  }

  /**
   * Define if the status area should be displayed or not. <br>
   * (NOTE: the status area will only be displayed if the height of the JLVolumeMeter is larger than
   * 20 ! ).
   */
  public void showStatus(boolean b)
  {
    isStatusShown = b;
    repaint();
  }

  /**
   * Use this method to display a status message in the status area of the VolumeMeter ( the status
   * area will only be displayed if the height of the JLVolumeMeter is larger than 20 ! ).
   */
  public void setStatus(String s)
  {
    statusText = s;
    repaint();
  }

  /**
   * Defines the displayed level range (default is -90 ... 3).
   */
  public void setRange(float nMinLevel, float nMaxLevel)
  {
    this.m_nMinLevel = nMinLevel;
    this.m_nMaxLevel = nMaxLevel;
  }

  /**
   * Returns the lower bound of the displayed level range.
   * 
   * @see #getRangeMax()
   * @see #setRange(float, float) 
   */
  public float getRangeMin()
  {
    return this.m_nMinLevel;
  }

  /**
   * Returns the upper bound of the displayed level range.
   * 
   * @see #getRangeMin()
   * @see #setRange(float, float) 
   */
  public float getRangeMax()
  {
    return this.m_nMaxLevel;
  }
}

/* EOF */