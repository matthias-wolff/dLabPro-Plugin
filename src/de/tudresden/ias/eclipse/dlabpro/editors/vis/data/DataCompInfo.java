// dLabPro Plugin for Eclipse
// - Data component information for DataDisplays (partly persistent)
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.data;

import java.lang.reflect.Array;

import org.eclipse.ui.PartInitException;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.BarDiagram;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.LabelDisplay;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.Oscillogram;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.Spectrogram;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.ThreeDDisplay;
import de.tudresden.ias.jlab.kernel.JlData;

/**
 * Instances hold (partly persistent) information on one data component
 * displayed in a DataDisplay.
 * @author Matthias Wolff
 */
public class DataCompInfo
{
  
  /**
   * Reference to data container, volatile.
   */
  public JlData iData;
  
  /**
   * Zero-based index of data component in {@link iData}, persistent.
   */
  public int nComp;

  /**
   * Expected data type of component, persistent. This field is used for
   * integrity checks when restoring persistent properties. 
   */
  public String sCompType;

  /**
   * Name of data display class which should preferably used to visualize
   * this component, persistent
   */
  public String sDisplayType;
  
  /**
   * Consecutive component group number or -1 for a stand-alone component,
   * persistent.
   */
  public int nGroup;
  
  /**
   * Minimal data value, volatile
   */
  public double nMin;
  
  /**
   * Maximal data value, volatile
   */
  public double nMax;
  
  /**
   * Visibility flag of component, persistent
   */
  public boolean bVisible;
  
  /**
   * Constructor
   * @param iData
   *          The data instance holding the component described by this instance
   * @param nComp
   *          The zero-based component index
   * @param sProps
   *          Persistent properties string, may be <code>null</code>
   * @throws PartInitException
   */
  public DataCompInfo(JlData iData, int nComp, String sProps)
  throws DataException
  {
    this.iData = iData;
    this.nComp = nComp;

    // Validate arguments
    if (iData==null)
      throw new DataException("Data instance is null");
    if (nComp<0 || nComp>=iData.getDimension())
      throw new DataException("Invalid data component index "+nComp);
    
    // Get minimum and maximum data values
    computeMinMax(false, false);
    
    // Initialize persistent fields
    nGroup       = -1;
    bVisible     = true;
    sCompType    = iData.getCompType(nComp).getSimpleName();
    sDisplayType = sCompType.equals("String") ? 
                   LabelDisplay.class.getCanonicalName() :
                   Oscillogram.class.getCanonicalName();
    if (sProps!=null) fromPropString(sProps);
  }

  /**
   * Computes and stores the minimal and maximal data value
   * @param bVcenter
   *           Force min=-max
   * @param bZero
   *           Force min<=0<=max
   */
  public void computeMinMax(boolean bVcenter, boolean bZero)
  {
    if (JlData.isNumericType(iData.getCompType(nComp)))
    {
      nMin = bZero ? 0 : Double.MAX_VALUE;
      nMax = bZero ? 0 : -Double.MAX_VALUE;
      
      int    nXR   = iData.getLength();
      Object aData = iData.getComp(nComp); 
      for (int nR=0; nR<nXR; nR++)
      {
        double nVal = Array.getDouble(aData,nR);
        if (nVal==Double.NaN) continue;
        if (nVal<nMin) nMin = nVal;
        if (nVal>nMax) nMax = nVal;
      }
      if (bVcenter)
      {
        nMax = Math.max(Math.abs(nMin),Math.abs(nMax));
        nMin = -nMax;
      }
    }
    else
    {
      nMin = Double.NaN;
      nMax = Double.NaN;
    }
  }

  /**
   * Creates an array of initialized <code>DataCompInfo</code>s for a
   * <code>JlData</code> instance. 
   * @param iData
   *          the data instance
   * @param sProps
   *          property string to initialize from
   * @return an array of initialized <code>DataCompInfo</code>s
   * @throws DataException
   */
  public static DataCompInfo[] createFromData(JlData iData, String sProps)
  throws DataException
  {
    // Validate
    if (iData == null || iData.getDimension() == 0) return null;

    int nC;
    int nXC = iData.getDimension();

    // Create component info array
    DataCompInfo[] aDci = new DataCompInfo[nXC];
    for (nC = 0; nC < aDci.length; nC++)
      aDci[nC] = new DataCompInfo(iData,nC,null);

    // Get property strings
    //System.out.print("\n*** DISPLAY PROPERTIES: \""+sProps+"\"");
    //System.out.print("\n*** RTEXT             : \""+iData.rtext+"\"");
    String aProps[] = sProps != null ? sProps.split("\n") : null;
    try
    {
      if (aProps == null) throw new DataException("");
      if (aProps.length != nXC) throw new DataException("");
      for (nC = 0; nC < nXC; nC++)
        aDci[nC].fromPropString(aProps[nC]);
    }
    catch (DataException e)
    {
      try
      {
        // Try taking layout hints from rtext
        if (iData.rtext==null || iData.rtext.length()==0)
          throw new DataException("");
        aProps = iData.rtext.split("\n");
        if (aProps.length != nXC) throw new DataException("");
        for (nC = 0; nC < nXC; nC++)
        {
          String aCompProps[] = aProps[nC].split(",");
          if (aCompProps.length!=2) throw new DataException("");
          int    nGrp  = Integer.parseInt(aCompProps[0]);
          String sType = aCompProps[1];
          if (sType.indexOf(".")<0)
            sType = "de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays." + sType;
          aDci[nC].fromPropString(aDci[nC].nComp+","+aDci[nC].sCompType+"," 
            +nGrp+","+sType+",TRUE");
        }
      }
      catch (DataException e1)
      {
        // Fall back to automatic layout
        autoLayout(aDci);
      }
    }

    return aDci;
  }
  
  /**
   * Modifies the display types, grouping and visibility of the committed
   * component info array.
   * @param aDci
   *          Component info array
   */
  public static void autoLayout(DataCompInfo[] aDci)
  {
    if (aDci==null || aDci.length==0) return;
    
    if (aDci.length>=VIS.VOP_DEF_VISIBLE_DISPLAYS)
      spectrogramLayout(aDci);
    else
      oscillogramLayout(aDci);
  }

  /**
   * Modifies the display types, grouping and visibility of the committed
   * component info array.
   * @param aDci
   *          COmponent info array
   */
  public static void oscillogramLayout(DataCompInfo[] aDci)
  {
    if (aDci==null || aDci.length==0) return;

    for (int nC=0,nComps=0; nC<aDci.length; nC++)
      if (!aDci[nC].sCompType.equals("String"))
      {
        nComps++;
        aDci[nC].nGroup = -1;
        aDci[nC].sDisplayType = Oscillogram.class.getCanonicalName();
        aDci[nC].bVisible = nComps<VIS.VOP_DEF_VISIBLE_DISPLAYS;
      }
  }

  /**
   * Modifies the display types, grouping and visibility of the committed
   * component info array.
   * @param aDci
   *          COmponent info array
   */
  public static void barDiagramLayout(DataCompInfo[] aDci)
  {
    if (aDci==null || aDci.length==0) return;
    for (int nC=0,nComps=0; nC<aDci.length; nC++)
      if (!aDci[nC].sCompType.equals("String"))
      {
        nComps++;
        aDci[nC].nGroup = -1;
        aDci[nC].sDisplayType = BarDiagram.class.getCanonicalName();
        aDci[nC].bVisible = nComps<VIS.VOP_DEF_VISIBLE_DISPLAYS;
      }
  }

  /**
   * Modifies the display types, grouping and visibility of the committed
   * component info array.
   * @param aDci
   *          component info array
   */
  public static void spectrogramLayout(DataCompInfo[] aDci)
  {
    if (aDci==null || aDci.length==0) return;

    for (int nC=0; nC<aDci.length; nC++)
      if (!aDci[nC].sCompType.equals("String"))
      {
        aDci[nC].nGroup = 0;
        aDci[nC].sDisplayType = Spectrogram.class.getCanonicalName();
        aDci[nC].bVisible = true;
      }
  }

  /**
   * Modifies the display types, grouping and visibility of the committed
   * component info array.
   * @param aDci
   *          component info array
   */
  public static void threeDLayout(DataCompInfo[] aDci)
  {
    if (aDci==null || aDci.length==0) return;

    for (int nC=0; nC<aDci.length; nC++)
      if (!aDci[nC].sCompType.equals("String"))
      {
        aDci[nC].nGroup = 0;
        aDci[nC].sDisplayType = ThreeDDisplay.class.getCanonicalName();
        aDci[nC].bVisible = true;
      }
  }
  
  /**
   * Returns the persistent properties for the committed component info array.
   * @param aDci
   *          component info array
   * @return the properties string
   */
  public static String toPropString(DataCompInfo[] aDci)
  {
    if (aDci==null || aDci.length==0) return null;
    String sProps = "";
    for (int nC=0; nC<aDci.length; nC++)
      sProps += aDci[nC].toPropString();
    return sProps;
  }
  
  /**
   * Returns the persistent properties of this instance as a property string.
   * @return A properties string
   * @see fromPropString
   */
  public String toPropString()
  {
    String sDci = "";
    sDci += nComp        + ",";
    sDci += sCompType    + ",";
    sDci += nGroup       + ",";
    sDci += sDisplayType + ",";
    sDci += bVisible     + "\n";
    return sDci;
  }
  
  /**
   * Initializes the persistent properties of this instance from a property
   * string.
   * @param sProps
   *          Properties string to be restored (may be <code>null</code>)
   * @throws DataException
   * @see toPropString
   */
  public void fromPropString(String sProps) throws DataException
  {
    // Validate
    if (sProps==null) return;

    // Parse properties string 
    String aProps[] = sProps.split(",");
    if (aProps.length!=5) throw new DataException("");
    int nComp           = Integer.parseInt(aProps[0]);
    String sCompType    = aProps[1];
    int nGroup          = Integer.parseInt(aProps[2]);
    String sDisplayType = aProps[3];
    boolean bVisible    = Boolean.parseBoolean(aProps[4]);
    
    // Validate again
    if (this.nComp!=nComp                ) throw new DataException("");
    if (!this.sCompType.equals(sCompType)) throw new DataException("");

    // Initialize fields from properties
    this.nGroup       = nGroup;
    this.sDisplayType = sDisplayType;
    this.bVisible     = bVisible;
  }

}

// EOF

