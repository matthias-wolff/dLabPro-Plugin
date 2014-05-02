
package de.tudresden.ias.eclipse.dlabpro.editors.def.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonModel;

/**
 * This implementation of CommonElement represents the root node of def-documents.
 * 
 * @author Christian Feig
 * 
 */
public class ClassSection extends CommonElement
{

  CommonElement[]         segments;
  public static final int CLASS            = 0;
  public static final int cLib_CLASS       = 1;
  public static final int NO_CLASS_SECTION = 2;

  /**
   * Constructor
   * 
   * @param defModel -
   *          the model this element belongs to
   * @param name -
   *          the name (title) of the def-documents class
   * @param offset
   * @param length
   * @param id -
   *          the type, should match one of the fields {@link #CLASS}, {@link #cLib_CLASS} or
   *          {@link #NO_CLASS_SECTION}
   */
  ClassSection(CommonModel defModel, String name, int offset, int length, int id)
  {
    super(defModel, name, offset, length, id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonElement#getChildren()
   */
  public CommonElement[] getChildren()
  {
    if (segments != null)
    {
      List l = Arrays.asList(segments);
      Collections.sort(l, new Comparator());
      segments = (CommonElement[])l.toArray();
    }
    return segments == null ? NO_CHILDREN : segments;
  }

  /**
   * 
   * @param segments -
   *          the segments of the document
   */
  public void setSegments(CommonElement[] segments)
  {
    this.segments = segments;
  }

}
