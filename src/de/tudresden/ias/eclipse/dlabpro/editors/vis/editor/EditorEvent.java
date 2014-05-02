
package de.tudresden.ias.eclipse.dlabpro.editors.vis.editor;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataCompInfo;

/**
 * Fired when the VisEditor has changed the data or layout (grouping, visibility or display types) other than as a
 * reaction on a <code>OutlineEvent</code>.
 * 
 * @author Stephan Larws
 */
public class EditorEvent
{

  public  DataCompInfo[] aDci;
  private int            nTranspose;

  /**
   * Creates an EditorEvent.
   * 
   * @param aDci
   *          Data component info array containing the changes
   */
  public EditorEvent(DataCompInfo[] aDci)
  {
    this.aDci = aDci;
    this.nTranspose = -1;
  }

  /**
   * Creates an EditorEvent to be fired when the data object has changed.
   * 
   * @param aDci
   *          Data component info array containing the changes
   * @param bTranspose
   *          Data object is transposed
   */
  public EditorEvent(DataCompInfo[] aDci, boolean bTranspose)
  {
    this.aDci = aDci;
    this.nTranspose = bTranspose ? 1 : 0;
  }
  
  /**
   * Returns <code>true</code> if the event contains a valid data component
   * info array in field <code>aDci</code>.
   */
  public boolean hasDataCompInfo()
  {
    return aDci!=null;
  }
  
  /**
   * Returns <code>true</code> if the event contains a transpose flag to be
   * returned by {@link isTransposed()}.
   */
  public boolean hasTranspose()
  {
    return nTranspose>=0;
  }
  
  /**
   * Returns the event's transpose flag. The returned value is meaningless if
   * {@link hasTranspose()} returns <code>false</code>.
   */
  public boolean isTransposed()
  {
    return nTranspose>0;
  }
  
}

// EOF
