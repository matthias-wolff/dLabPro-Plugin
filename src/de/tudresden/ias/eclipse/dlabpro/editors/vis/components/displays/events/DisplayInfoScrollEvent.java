package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay;

/**
 * Event that is fired when a display is scrolled. It contains the
 * information about the rulers, horizontal and vertical ruler.
 * 
 * @author Stephan Larws
 */
public class DisplayInfoScrollEvent extends DisplayEvent
{
  /** X-axis is [records|components|values] */
  public String sXAxis = "";

  /** Physical range of X-axis, e.g. "0 ... 1000" */
  public String sXRngP = "";
  
  /** Physical unit of X-axis */ 
  public String sXUnit = "";

  /** Logical range of X-axis, e.g. "0 ... 99 (100)" */
  public String sXRngL = "";
  
  /** Y-axis is [records|components|values] */
  public String sYAxis = "";

  /** Physical range of Y-axis, e.g. "0 ... 1000" */
  public String sYRngP = "";
  
  /** Physical unit of Y-axis */ 
  public String sYUnit = "";

  /** Logical range of Y-axis, e.g. "0 ... 99 (100)" */
  public String sYRngL = "";

  /** Z-axis is [records|components|values] */
  public String sZAxis = "";

  /** Physical range of Z-axis, e.g. "0 ... 1000" */
  public String sZRngP = "";
  
  /** Physical unit of Z-axis */ 
  public String sZUnit = "";

  /**
   * Creates a new, empty instance
   */
  public DisplayInfoScrollEvent(AbstractDataDisplay iDd)
  {
    super(iDd);
  }

  /**
   * Stores information on the X display range in the instance
   * 
   * @param sXAxis
   *          X-axis is [records|components|values]
   * @param sXRngP
   *          Physical range of X-axis, e.g. "0 ... 1000"
   * @param sXUnit
   *          Physical unit of X-axis
   * @param sXRngL
   *          Logical range of X-axis, e.g. "0 ... 99 (100)"
   */
  public void StoreX(String sXAxis, String sXRngP, String sXUnit, String sXRngL)
  {
    this.sXAxis = sXAxis;
    this.sXRngP = sXRngP;
    this.sXUnit = sXUnit;
    this.sXRngL = sXRngL;
  }

  /**
   * Stores information on the Y display range in the instance
   * 
   * @param sYAxis
   *          Y-axis is [records|components|values]
   * @param sYRngP
   *          Physical range of Y-axis, e.g. "0 ... 1000"
   * @param sYUnit
   *          Physical unit of Y-axis
   * @param sYRngL
   *          Logical range of Y-axis, e.g. "0 ... 99 (100)"
   */
  public void StoreY(String sYAxis, String sYRngP, String sYUnit, String sYRngL)
  {
    this.sYAxis = sYAxis;
    this.sYRngP = sYRngP;
    this.sYUnit = sYUnit;
    this.sYRngL = sYRngL;
  }

  /**
   * Stores information on the Z display range in the instance
   * 
   * @param sZAxis
   *          Z-axis is [records|components|values]
   * @param sZRngP
   *          Physical range of Z-axis, e.g. "0 ... 1000"
   * @param sZUnit
   *          Physical unit of Z-axis
   */
  public void StoreZ(String sZAxis, String sZRngP, String sZUnit)
  {
    this.sZAxis = sZAxis;
    this.sZRngP = sZRngP;
    this.sZUnit = sZUnit;
  }

}
