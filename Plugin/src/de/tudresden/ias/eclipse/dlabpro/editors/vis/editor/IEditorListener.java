package de.tudresden.ias.eclipse.dlabpro.editors.vis.editor;

public interface IEditorListener {
	
	/**
	 * After the data object is refreshed all listeners will be notified.
	 * 
	 * @param e
	 *          The editor event
	 */
	public void editorChanged(EditorEvent e);

}
