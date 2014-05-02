
package de.tudresden.ias.eclipse.dlabpro.editors.def.model;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;

public class ErrorSegment extends Segment
{

  public static final int WARNING = 0;
  public static final int ERROR   = 1;

  ErrorSegment(CommonElement parent, String name, int offset, int length, int type)
  {
    super(parent, name, offset, length, type);
    // TODO Auto-generated constructor stub
  }

}
