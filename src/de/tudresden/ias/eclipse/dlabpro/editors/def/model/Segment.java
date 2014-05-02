
package de.tudresden.ias.eclipse.dlabpro.editors.def.model;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;

public class Segment extends CommonElement
{

  Segment(CommonElement parent, String name, int offset, int length, int type)
  {
    super(parent, name, offset, length, type);
  }

  Segment(CommonElement parent, String name, int offset, int length)
  {
    super(parent, name, offset, length, -1);
  }

  public CommonElement[] getChildren()
  {
    return NO_CHILDREN;
  }

}
