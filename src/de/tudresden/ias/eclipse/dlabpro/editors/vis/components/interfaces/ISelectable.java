package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.interfaces;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public interface ISelectable {

	/**
	 * Returns a rectangle if a selection was made or <code>null</code>
	 * if nothing is selected.
	 * 
	 * @return 	rectangle representing the selected area or <code>null</code>
	 */
	public Rectangle getSelection();

	/**
	 * @return
	 */
	public boolean isSelectionMade();

	/**
	 * Sets the selected area of this display.
	 * 
	 * @param p1	the top left corner of the selected area
	 * @param p2	the bottom right corner of the selected area
	 */
	public void setSelection(Point p1, Point p2);

	public void removeSelection();
}
