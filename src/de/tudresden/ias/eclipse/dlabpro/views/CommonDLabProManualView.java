
package de.tudresden.ias.eclipse.dlabpro.views;

import java.io.File;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;

/**
 * This class represents an abstract superclass for all DLabPro help views using a a browser
 * component to show content. Known subclass is
 * {@link de.tudresden.ias.eclipse.dlabpro.views.ManualContentView}.
 * 
 * @author Christian Feig
 */
abstract public class CommonDLabProManualView extends ViewPart implements IPropertyChangeListener
{
  Browser        browser;
  private String docHome;

  /**
   * Constructor
   * 
   */
  public CommonDLabProManualView()
  {
    super();
    DLabProPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    docHome = DLabProPlugin.getDefault().getPreferenceStore().getString(getPreferenceName());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl(Composite parent)
  {
    try
    {
      browser = new Browser(parent, SWT.NONE);
    }
    catch (SWTError e)
    {
      // This just creates an info text in the view area ->
      Label iLabel = new Label(parent,SWT.NONE);
      iLabel.setText("Cannot create browser widget\n\n"
          +"Here is the stack trace:\n"+e.toString());
      // <-- This would create an error message -->
      //throw new RuntimeException("Cannot create browser widget");
      // <--
    }
    showContent();
    //browser.addLocationListener(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  public void setFocus()
  {
    if (browser!=null) browser.setFocus();
  }

  /**
   * Forces subclasses to (re)show their content. This method is called too after property changed,
   * so subclasses should use {@link #getHomePath()} to show their contents.
   * 
   */
  abstract protected void showContent();

  /**
   * 
   * @return {@link Browser}
   */
  protected Browser getBrowser()
  {
    return browser;
  }

  /**
   * 
   * @return the preference name as String the help view shall act with
   */
  abstract protected String getPreferenceName();

  /**
   * 
   * @return {@link String} the home path for this help
   */
  protected String getHomePath()
  {
    if (!docHome.endsWith(File.separator)) if (docHome.length() > 0) docHome += File.separator;
    else docHome = null;
    return docHome;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent event)
  {
    if (event.getProperty().equals(getPreferenceName()))
    {
      if (!event.getNewValue().equals(event.getOldValue()))
      {
        if (event.getNewValue() != null) docHome = (String)event.getNewValue();
        else docHome = "";
        // getBrowser().removeLocationListener(this);
        showContent();
        // getBrowser().addLocationListener(this);
      }
    }

  }

}
