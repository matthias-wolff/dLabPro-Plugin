// dLabPro Plugin for Eclipse
// - Horizontal-viewing-detail-changed display event
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay;

/**
 * Horizontal viewing detail changed display event. Fired when horizontal
 * zooming or scrolling occured. 
 * 
 * <h2>Remarks</h2>
 * <ul>
 *   <li>ComponentPanel is to listen to this one!</li>
 * </ul>
 */
public class HdetailEvent extends DisplayEvent
{
  /**
   * The new first item to be shown
   */
  public int nFirst;

  /**
   * The new last item to be shown
   */
  public int nLast;
  
  /**
   * Constructs a new horizontal-detail-changed display event
   * @param iDd
   *          the data display concerned by this event
   * @param nFirst
   *          the new first item to be shown
   * @param nLast
   *          the new last item to be shown
   */
  public HdetailEvent(AbstractDataDisplay iDd, int nFirst, int nLast)
  {
    super(iDd);
    this.nFirst = nFirst;
    this.nLast  = nLast;
  }
}

// EOF
