
package de.tudresden.ias.eclipse.dlabpro.editors.itp.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;
import de.tudresden.ias.eclipse.dlabpro.editors.def.model.Comparator;

public class ItpElement extends CommonElement
{

  Vector                  elements      = null;

  public static final int TYPE_FUNCTION = 1;
  public static final int TYPE_JAVADOC  = 2;
  public static final int TYPE_INCLUDES = 3;

  public ItpElement(CommonElement parent, String name, int offset, int length, int type)
  {
    super(parent, name, offset, length, type);
  }

  public void addChildElement(CommonElement element)
  {
    if (elements == null) elements = new Vector();
    elements.add(element);
  }

  public CommonElement[] getChildren()
  {
    CommonElement[] elementArray = null;
    if (elements != null)
    {
      List l = Arrays.asList(elements.toArray());
      Collections.sort(l, new Comparator());
      elementArray = (CommonElement[])l.toArray(new CommonElement[l.size()]);
    }

    return elementArray == null ? NO_CHILDREN : elementArray;
  }

  public void setLength(int length)
  {
    super.setLength(length);
  }

}
