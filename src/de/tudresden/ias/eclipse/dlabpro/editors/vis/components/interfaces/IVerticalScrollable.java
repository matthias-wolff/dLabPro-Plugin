package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.interfaces;

/**
 * this interface should be implemented by all classes which displays
 * are supposed to be vertical scrollable 
 * 
 * @author Stephan Larws
 */
public interface IVerticalScrollable {
	/**
	 * this method is called when the associated scroll bar fires its event
	 * the parameters are the value and maximum received from that scroll
	 * bar.
	 * 
	 * @param value 	current value
	 * @param max		maximum value
	 */
	public void scrollVertical(int value, int max);
}
