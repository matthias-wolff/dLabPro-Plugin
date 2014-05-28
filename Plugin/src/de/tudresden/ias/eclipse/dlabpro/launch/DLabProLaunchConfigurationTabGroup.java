/*
 * Created on 12.06.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;


/**
 * @author Christian Feig and Matthias Wolff
 */
public class DLabProLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup
{

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog,
   *      java.lang.String)
   */
  public void createTabs(ILaunchConfigurationDialog dialog, String mode)
  {
    setTabs(new ILaunchConfigurationTab[]
    {
      new DLabProLaunchMainTab(),
      new DLabProLaunchArgsTab()
    });
  }

}
