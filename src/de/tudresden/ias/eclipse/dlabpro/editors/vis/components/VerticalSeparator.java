
package de.tudresden.ias.eclipse.dlabpro.editors.vis.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;

/**
 * Dient zur optischen Trennung der einzelnen Komponenten auf einem ComponentPanel in vertikaler
 * Richtung.
 * 
 * @author Stephan Larws
 * 
 */
public class VerticalSeparator extends Canvas
{

  public VerticalSeparator(Composite parent)
  {
    super(parent, SWT.NONE);

    addListeners();
  }

  public Point computeSize(int wHint, int hHint, boolean changed)
  {
    return (new Point(wHint, VIS.VS_VERTICAL_SPACE));
  }

  private void addListeners()
  {
    addPaintListener(new PaintListener()
    {

      public void paintControl(PaintEvent e)
      {
        VerticalSeparator.this.paintControl();
      }

    });

    addDisposeListener(new DisposeListener()
    {

      public void widgetDisposed(DisposeEvent e)
      {
        VerticalSeparator.this.widgetDestroyed();
      }

    });
  }

  private void widgetDestroyed()
  {

  }

  private void paintControl()
  {
    setBackground(new Color(null, 128, 128, 128));
  }

}
