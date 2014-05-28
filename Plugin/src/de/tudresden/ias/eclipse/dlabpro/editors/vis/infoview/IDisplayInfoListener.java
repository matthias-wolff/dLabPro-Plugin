package de.tudresden.ias.eclipse.dlabpro.editors.vis.infoview;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoMouseMoveEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoScrollEvent;

public interface IDisplayInfoListener {

	/**
	 * Informs the listener that new information is available after
	 * the mouse was moved
	 * 
	 * @param e
	 * 			The event containing the information
	 */
	public void informationChangedMouseMove(DisplayInfoMouseMoveEvent e);
	
	/**
	 * Resets the information about the mouse position
	 */
	public void clearMouseInformation();
	
	/**
	 * Informs the listener that new information is available after
	 * the display was scrolled
	 * 
	 * @param e
	 * 			The event containing the information
	 */
	public void informationChangedScroll(DisplayInfoScrollEvent e);	
}
