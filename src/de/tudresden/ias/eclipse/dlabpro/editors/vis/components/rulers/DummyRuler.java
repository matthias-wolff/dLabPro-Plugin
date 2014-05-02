package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;

/**
 * Dummy ruler which does not display and calculate anything. Its only purpose
 * is to supply a display length.
 * 
 * @author Matthias Wolff
 */
public class DummyRuler extends Ruler
{
  public DummyRuler(Composite iParent, boolean bVertical)
  {
    super(iParent,bVertical);
  }

  /*
   * (non-Javadoc)
   * @see de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers_new.Ruler#computeSize(int, int, boolean)
   */
  @Override
  public Point computeSize(int wHint, int hHint, boolean changed)
  {
    return new Point(Ruler.RULER_WIDTH_X,Ruler.RULER_WIDTH_Y);
  }
    
  @Override
  public void drawOn(GC gc, Rectangle region)
  {
    // Does not paint anything!
  }

  @Override
  public void drawPositionLine(int pos)
  {
    // Does not paint anything!
  }

}
