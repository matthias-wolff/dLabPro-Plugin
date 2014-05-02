// dLabPro Plugin for Eclipse
// - VisEditor widgets' base class
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.components;


import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
/**
 * Base class of all VisEditor widgets (data displays, rulers, etc...).
 * 
 * <h2>Remarks</h2>
 * <ul>
 *   <li>Currently there is only one display event listener list. If there will
 *     be to much traffic in it, we should have a separate list for each event.
 *     </li>
 * </ul>
 */
public abstract class AbstractComponent extends Canvas
{ 
  
  Point   m_iSizeBuf;
  boolean m_bEnabledBuf;
  
  // -- CONSTRUCTORS -----------------------------------------------------------
  
  /**
   * Default VisEditor component constructor 
   * @param iParent
   *          a composite control which will be the parent of the new instance
   *          (cannot be <code>null</code>)
   * @param nStyle
   *          the style of control to construct
   */
  public AbstractComponent(Composite iParent)
  {
    super(iParent, SWT.NONE);
  }  

  /**
   * Returns a point describing the receiver's size. The method synchronizes
   * with the GUI thread may be called from outside it.
   * 
   * @return the receiver's size
   */
  public Point getSizeSync()
  {
    try
    {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          m_iSizeBuf = getSize();
        }
      });
      return m_iSizeBuf;
    }
    catch (NoClassDefFoundError e)
    {
      return getSize();
    }
    catch (IllegalStateException e2)
    {
      Display.getDefault().syncExec(new Runnable()
      {
        public void run()
        {
          m_iSizeBuf = getSize();
        }
      });
      return m_iSizeBuf;
    }
  }

  /**
   * Returns <code>true</code> if the receiver is enabled, and
   * <code>false</code> otherwise. A disabled control is typically not
   * selectable from the user interface and draws with an inactive or "grayed"
   * look. The method synchronizes with the GUI thread may be called from
   * outside it.
   *
   * @return the receiver's enabled state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public boolean getEnabledSync()
  {
    try
    {
      // PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
      Display.getDefault().syncExec(new Runnable()
      {
        public void run()
        {
          m_bEnabledBuf = getEnabled();
        }
      });
      return m_bEnabledBuf;
    }
    catch (Exception e)
    {
      return getEnabled();
    }
  }

}

// EOF
