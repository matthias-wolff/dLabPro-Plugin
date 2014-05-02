package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers;

public class DpiConverter
{
  private              double nDpi;
  private static final double nMmPerInch = 25.4;
  private static final double nPtPerInch = 72;
  
  public DpiConverter(int nDpi)
  {
    this.nDpi = (double)nDpi;
  }
  
  public static double mm2inch(double nMm)
  {
    return nMm/nMmPerInch;
  }
  
  public static double mm2pt(double nMm)
  {
    return nMm/nMmPerInch*nPtPerInch;
  }

  public int mm2px(double nMm)
  {
    return (int)Math.round(mm2inch(nMm)*nDpi);
  }
  
  public static double inch2mm(double nInch)
  {
    return nInch*nMmPerInch;
  }

  public static double inch2pt(double nInch)
  {
    return nInch*nPtPerInch;
  }

  public double inch2px(double nInch)
  {
    return nInch*nDpi;
  }
  
  public static double pt2mm(double nPt)
  {
    return nPt/nPtPerInch*nMmPerInch;
  }
  
  public static double pt2inch(double nPt)
  {
    return nPt/nPtPerInch;
  }
  
  public int pt2px(double nPt)
  {
    return (int)Math.round(pt2inch(nPt)*nDpi);
  }
  
  public double px2mm(int nPx)
  {
    return (double)nPx/nDpi*nMmPerInch;
  }
  
  public double px2inch(int nPx)
  {
    return (double)nPx/nDpi;
  }
  
  public double px2pt(int nPx)
  {
    return (double)nPx/nDpi*nPtPerInch;
  }
  
  public double x2y(double nVal, String sUnitIn, String sUnitOut)
  {
    if ("mm".equals(sUnitIn))
    {
      if      ("mm"  .equals(sUnitOut)) return nVal;
      else if ("inch".equals(sUnitOut)) return mm2inch(nVal);
      else if ("pt"  .equals(sUnitOut)) return mm2pt(nVal);
      else if ("px"  .equals(sUnitOut)) return mm2px(nVal);
      else                              return Double.NaN;
    }
    else if ("inch".equals(sUnitIn))
    {
      if      ("mm"  .equals(sUnitOut)) return inch2mm(nVal);
      else if ("inch".equals(sUnitOut)) return nVal;
      else if ("pt"  .equals(sUnitOut)) return inch2pt(nVal);
      else if ("px"  .equals(sUnitOut)) return inch2px(nVal);
      else                              return Double.NaN;
    }
    else if ("pt".equals(sUnitIn))
    {
      if      ("mm"  .equals(sUnitOut)) return pt2mm(nVal);
      else if ("inch".equals(sUnitOut)) return pt2inch(nVal);
      else if ("pt"  .equals(sUnitOut)) return nVal;
      else if ("px"  .equals(sUnitOut)) return pt2px(nVal);
      else                              return Double.NaN;
    }
    else if ("px".equals(sUnitIn))
    {
      if      ("mm"  .equals(sUnitOut)) return px2mm((int)nVal);
      else if ("inch".equals(sUnitOut)) return px2inch((int)nVal);
      else if ("pt"  .equals(sUnitOut)) return px2pt((int)nVal);
      else if ("px"  .equals(sUnitOut)) return nVal;
      else                              return Double.NaN;
    }
    else return Double.NaN;
  }
  
}
