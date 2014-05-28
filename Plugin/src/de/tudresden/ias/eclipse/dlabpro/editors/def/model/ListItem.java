
package de.tudresden.ias.eclipse.dlabpro.editors.def.model;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;

public class ListItem extends CommonElement
{

  ListItem(CommonElement parent, String name, int offset, int length)
  {
    super(parent, name, offset, length, -1);
    // TODO Auto-generated constructor stub
  }

  public CommonElement[] getChildren()
  {
    return NO_CHILDREN;
  }

}
