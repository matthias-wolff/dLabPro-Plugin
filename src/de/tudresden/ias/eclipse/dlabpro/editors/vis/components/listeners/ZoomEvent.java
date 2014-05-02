package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.listeners;

/**
 * The ZoomEvent class has information about the zoom that was performed.
 * Sofar this class is empty and has no functionality
 * 
 * @author Stephan Larws
 *
 */
public class ZoomEvent {
	
	private int mScrollBarWidth;
	private boolean firingWidgetIsZoomed = true;
	
	public ZoomEvent() {
		mScrollBarWidth = 0;
	}
	
	public ZoomEvent(int width) {
		mScrollBarWidth = width;
	}
	
	public int getScrollBarWidth() {
		return mScrollBarWidth;
	}
	
	/**
	 * sets the zoom state of the widget that fired this event
	 * 
	 * @param b the zoom state
	 */
	public void setWidgetZoomState(boolean b) {
		firingWidgetIsZoomed = b;
	}
	
	/**
	 * returns true if the widget that originally fired this event is still zoomed.
	 * otherwise the return value is false.
	 * 
	 * @return the zoom status of the widget which fired this event
	 */
	/**
	 * @return
	 */
	public boolean getWidgetZoomState() {
		return firingWidgetIsZoomed;
	}
}
