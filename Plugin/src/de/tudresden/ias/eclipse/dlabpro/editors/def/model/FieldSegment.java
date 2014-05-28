
package de.tudresden.ias.eclipse.dlabpro.editors.def.model;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;

public class FieldSegment extends Segment
{

  public static final int PUBLIC    = 0;
  public static final int PROTECTED = 1;
  public static final int PRIVATE   = 2;

  FieldSegment(CommonElement parent, String name, int offset, int length, int type)
  {
    super(parent, name, offset, length, type);
  }

}
