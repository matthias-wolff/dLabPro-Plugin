package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.interfaces;

/**
 * this interface should be implemented by classes which displays need
 * to have vertical zoom functionality
 * 
 * @author Stephan Larws
 */
public interface IVerticalZoomable {

	/**
	 * enlarges the view to the selected area of the display. if nothing
	 * is seclected the view is enlarged by cutting off 25% from the 
	 * top and bottom side of the viewed area. 
	 * 
	 * ----------------------------------------------
	 * The return value is used
	 * to differentiate between vertically zoomable displays and those
	 * which do not support this feature.
	 * 
	 * @return 		true if zooming is supported false otherwise
	 */
	public void zoomInVertical();

	
	/**
	 * increases the viewed area by adding 25% to each side of the view. 
	 * 
	 * ---------------------------------------------------------------
	 * The return value is used to differentiate between vertically 
	 * zoomable displays and those which do not support this feature.
	 * 
	 * @return 		true if zooming is supported false otherwise
	 */
	public void zoomOutVertical();	
	
	/**
	 * tells the vertical zoom status of the display
	 * 
	 * @return		zoom value
	 */
	public boolean isZoomedVertical();
	
	/**
	 * sets the vertical zoom status of this display
	 * 
	 * @param the new value of the vertical zoom status
	 */
	public void setZoomedVertical(boolean zoom);
}
