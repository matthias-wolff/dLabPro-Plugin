
package de.tudresden.ias.eclipse.dlabpro.editors.vis.outline;

import de.tucottbus.kt.jlab.datadisplays.data.DataCompInfo;

/**
 * Fired when the Outline requests changing the layout (grouping, visibility or display types).
 * 
 * @author Stephan Larws
 */
public class OutlineEvent
{
  public DataCompInfo[] aDci;
  
  /**
   * Creates an OutlineEvent.
   * 
   * @param aDci
   *          Data component info array containing the changes
   */
  public OutlineEvent(DataCompInfo[] aDci)
  {
    this.aDci = aDci;
  }
}
