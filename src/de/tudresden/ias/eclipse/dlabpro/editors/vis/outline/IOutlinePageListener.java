package de.tudresden.ias.eclipse.dlabpro.editors.vis.outline;

/**
 * All classes that need information from the OutlinePage need to implement
 * this interface and register a listener with the outline page.
 * 
 * @author Stephan Larws
 *
 */
public interface IOutlinePageListener {

  /**
   * Called when visibility, grouping or display type of one or several
   * components has changed.
   * @param e
   *          the editor event containing information about the changes
   */
  public void outlineChanged(OutlineEvent e);
	
	/**
	 * Is called after the refresh or transpose button was clicked
	 * @param bTransose
	 *          <code>true</code> if data object is to be transposed,
   *          <code>false</code> otherwise
	 */
	public void reload(boolean bTransose);
	
}
