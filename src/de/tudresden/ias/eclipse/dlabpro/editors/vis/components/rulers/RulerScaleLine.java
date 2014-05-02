package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers;

/**
 * NOTE: CLASS NOT YET IN USE!
 * @author Matthias Wolff
 */
public class RulerScaleLine
{
  private double nVal = 0.;
  private int    nPos = 0;
  private String sLab = "";
  
  public RulerScaleLine(double nVal, int nPos, String sLab)
  {
    this.nVal = nVal;
    this.nPos = nPos;
    this.sLab = sLab;
  }
  
  public double getVal()
  {
    return this.nVal;
  }
  
  public int getPos()
  {
    return this.nPos;
  }
  
  public String getLab()
  {
    return this.sLab;
  }

  public void setLab(String sLab)
  {
    this.sLab = sLab;
  }
  
  public boolean isMainLine()
  {
    return (sLab!=null);
  }
  
  public boolean isTickLine()
  {
    return (sLab==null);
  }
}
