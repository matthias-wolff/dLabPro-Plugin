// dLabPro Plugin for Eclipse
// - Reinitialize display event
// 
package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay;

/**
 * Reinitialize display event. Fired when the data displays are to be destroyed
 * and the VisEditor is to be reinitialized.
 * 
 * <h2>Remarks</h2>
 * <ul>
 *   <li>VisEditor is to listen to this one!</li>
 * </ul>
 */
public class ReinitEvent extends DisplayEvent
{
  /**
   * <code>true</code> if the JlData instance displayed has changed and the
   * DataCompInfo array has to be reinitialized.
   */
  boolean bDataChanged = false;
  
  /**
   * Constructs a new reinitialize display event
   * @param iDd
   *          the data display concerned by this event
   * @param bDataChanged
   *          <code>true</code> if the JlData instance displayed has changed and
   *          the DataCompInfo array has to be reinitialized.
   */
  public ReinitEvent(AbstractDataDisplay iDd, boolean bDataChanged)
  {
    super(iDd);
    this.bDataChanged = bDataChanged; 
  }
}

// EOF
