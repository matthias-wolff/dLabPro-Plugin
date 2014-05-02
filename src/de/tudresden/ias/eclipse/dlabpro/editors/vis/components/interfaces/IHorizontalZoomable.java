
package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.interfaces;

/**
 * this interface should be implemented by classes which displays need
 * to have horizontal zoom functionality
 * 
 * @author Stephan Larws
 */
public interface IHorizontalZoomable {

	
	/**
	 * enlarges the view to the selected area of the display. if nothing
	 * is seclected the view is enlarged by cutting off 25% from the 
	 * left and right side of the viewed area.
	 */
	public void zoomInHorizontal();

	
	/**
	 * increases the viewed area by adding 25% to each side of the view
	 */
	public void zoomOutHorizontal();	
	
	/**
	 * tells the horizontal zoom status of the display
	 * 
	 * @return		zoom value
	 */
	public boolean isZoomedHorizontal();
	
	/**
	 * sets the horizontal zoom status of this display
	 * 
	 * @param the new value of the horizontal zoom status
	 */
	public void setZoomedHorizontal(boolean zoom);
}
