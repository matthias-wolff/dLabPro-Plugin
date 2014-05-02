package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.listeners;

/**
 * This interface is to be implemented by all classes that need to 
 * know whether a zoom operation was performed or not.
 * 
 * @author Stephan Larws
 *
 */
public interface VerticalActionListener {

	/**
	 * Is called when a vertical zoom took place
	 * 
	 * @param z 	the ZoomEvent
	 */
	public void handleVerticalZoomIn(ZoomEvent z);
	
	/**
	 * Is called when a vertical zoom took place
	 * 
	 * @param z 	the ZoomEvent
	 */
	public void handleVerticalZoomOut(ZoomEvent z);
	
	/**
	 * Is called when a vertical scroll action took place
	 * 
	 * @param z		the direction in which to scroll
	 */
	public void handleVerticalScrollEvent(int direction);
}
