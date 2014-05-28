
package de.tudresden.ias.eclipse.dlabpro.editors.def.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;

public class SegmentGroup extends Segment
{

  public static final int TYPE_METHOD                  = 10;
  public static final int TYPE_OPTION                  = 11;
  public static final int TYPE_FIELD                   = 12;
  public static final int TYPE_ERROR                   = 13;
  public static final int TYPE_NOTE                    = 14;
  public static final int TYPE_DEFINE                  = 15;
  public static final int TYPE_FILE                    = 16;
  // public static final int TYPE_RESEINCLUDES = 17;
  public static final int TYPE_INTERFACE_CODE_SNIPPETS = 18;

  List                    segments                     = new ArrayList();

  SegmentGroup(CommonElement parent, String name, int offset, int length)
  {
    super(parent, name, offset, length);
  }

  SegmentGroup(CommonElement parent, String name, int offset, int length, int type)
  {
    super(parent, name, offset, length, type);
  }

  public CommonElement[] getChildren()
  {
    if (segments.size() > 0) Collections.sort(segments, new Comparator());
    return (CommonElement[])segments.toArray(new CommonElement[segments.size()]);
  }

  public void addSegment(Segment segment)
  {
    segments.add(segment);
  }

}
