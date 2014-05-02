package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay;

/**
 * Event that is fired when the mouse is moved over a display. 
 * 
 * @author Stephan Larws
 *
 */
public class DisplayInfoMouseMoveEvent extends DisplayEvent
{
  /** X-axis is [records|components|values] */
  public String sXDisp = "";
  
  /** Physical value on X ruler */
  public String sXValA = "";
  
  /** Physical value of nearest data point on X-axis */
  public String sXValD = "";
  
  /** Physical unit of X-axis */
  public String sXUnit = "";
  
  /** Description of nearest data point on X-axis */
  public String sXData = "";
  
  /** Y-axis is [records|components|values] */
  public String sYDisp = "";
  
  /** Physical value on Y ruler */
  public String sYValA = "";
  
  /** Physical value of nearest data point on Y-axis */
  public String sYValD = "";
  
  /** Physical unit of Y-axis */
  public String sYUnit = "";
  
  /** Description of nearest data point on Y-axis */
  public String sYData = "";
  
  /** Z-axis is [records|components|values] */
  public String sZDisp = "";
  
  /** Physical value of nearest data point on Z-axis */
  public String sZValD = "";
  
  /** Physical unit of Z-axis */
  public String sZUnit = "";

  /**
   * Creates a new, empty instance
   */
  public DisplayInfoMouseMoveEvent(AbstractDataDisplay iDd)
  {
    super(iDd);
  }

  /**
   * Stores information on the X-position in the instance
   * 
   *  @param sXValA
   *           Physical value on X ruler
   *  @param sXValD
   *           Physical value of nearest data point on X-axis
   *  @param sXUnit
   *           Physical unit of X-axis
   *  @param sXData
   *           Description of nearest data point on X-axis
   */
  public void StoreX(String sXValA, String sXValD, String sXUnit, String sXData)
  {
    this.sXValA = sXValA;
    this.sXValD = sXValD;
    this.sXUnit = sXUnit;
    this.sXData = sXData;
  }

  /**
   * Stores information on the Y-position in the instance
   * 
   *  @param sYValA
   *           Physical value on Y ruler
   *  @param sYValD
   *           Physical value of nearest data point on Y-axis
   *  @param sYUnit
   *           Physical unit of Y-axis
   *  @param sYData
   *           Description of nearest data point on Y-axis
   */
  public void StoreY(String sYValA, String sYValD, String sYUnit, String sYData)
  {
    this.sYValA = sYValA;
    this.sYValD = sYValD;
    this.sYUnit = sYUnit;
    this.sYData = sYData;
  }
  
  /**
   * Stores information on the Z-position in the instance
   * 
   *  @param sZValD
   *           Physical value of nearest data point on Z-axis
   *  @param sZUnit
   *           Physical unit of Z-axis
   */
  public void StoreZ(String sZValD, String sZUnit)
  {
    this.sZValD = sZValD;
    this.sZUnit = sZUnit;
  }

}
