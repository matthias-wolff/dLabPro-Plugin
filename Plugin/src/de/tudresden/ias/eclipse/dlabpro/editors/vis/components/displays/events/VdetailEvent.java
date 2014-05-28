// dLabPro Plugin for Eclipse
// - Vertical viewing detail changed display event
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay;

/**
 * Vertical viewing detail changed display event. Fired when vertical zooming or
 * scrolling occured. 
 * 
 * <h2>Remarks</h2>
 * <ul>
 *   <li>DataDisplayPanel is to listen to this one!</li>
 * </ul>
 */
public class VdetailEvent extends DisplayEvent
{
  /**
   * The new first item to be shown
   */
  int nFirst;

  /**
   * The new last item to be shown
   */
  int nLast;
  
  /**
   * Constructs a new vertical-detail-changed display event
   * @param iDd
   *          the data display concerned by this event
   * @param nFirst
   *          the new first item to be shown
   * @param nLast
   *          the new last item to be shown
   */
  public VdetailEvent(AbstractDataDisplay iDd, int nFirst, int nLast)
  {
    super(iDd);
    this.nFirst = nFirst;
    this.nLast  = nLast;
  }
}

// EOF
