// dLabPro Plugin for Eclipse
// - Base class of VisEditor events
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events;

import org.eclipse.swt.widgets.Event;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay;

/**
 * Base class of VisEditor events
 */
public class DisplayEvent extends Event
{
  /**
   * The data display concerned by this event.
   */
  AbstractDataDisplay iDd;
  
  /**
   * Constructs a new display event
   * @param iDd
   *          the data display concerned by this event
   */
  public DisplayEvent(AbstractDataDisplay iDd)
  {
    this.iDd = iDd;
  }
}

// EOF
