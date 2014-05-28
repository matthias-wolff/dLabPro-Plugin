
package de.tudresden.ias.eclipse.dlabpro.editors.def.model;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;

public class ListSegment extends Segment
{

  public static final int DEFINE  = 0;
  public static final int FILE    = 1;
  public static final int INCLUDE = 2;

  ListSegment(CommonElement parent, String name, int offset, int length, int type)
  {
    super(parent, name, offset, length, type);
  }

}
