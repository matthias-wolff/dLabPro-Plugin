
package de.tudresden.ias.eclipse.dlabpro;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import de.tucottbus.kt.jlab.datadisplays.utils.DdUtils;
import de.tudresden.ias.eclipse.dlabpro.utils.LaunchUtil;

/**
 * The main plug-in class to be used in the desktop.
 */
public class DLabProPlugin extends AbstractUIPlugin
{
  private static DLabProPlugin plugin;

  public static final String PLUGIN_NAME = "de.tudresden.ias.eclipse.dlabpro";

  /**
   * The constructor
   */
  public DLabProPlugin()
  {
    super();
    plugin = this;
  }

  /**
   * Returns the dLabPro-Plugin singleton
   */
  public static DLabProPlugin getDefault()
  {
    return plugin;
  }

  /**
   * This method is called upon plug-in activation
   */
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    LaunchUtil.getDlabproExe(true);
    LaunchUtil.getCgenExe(true);
    LaunchUtil.getUasrHome(true);

    // Use the plug-in's preference store for jLab data displays.
    DdUtils.setPreferenceStore(getPreferenceStore());
  }

  /**
   * This method is called when the plug-in is stopped
   */
  public void stop(BundleContext context) throws Exception
  {
    super.stop(context);
  }

  // -- Miscellaneous services ---

  /**
   * Obtains an icon image descriptor.
   * 
   * @param sFile
   *            the location of the icon relative to the plugin
   * @return the descriptor 
   */
  public static ImageDescriptor getIconImage(String sFile)
  {
    Bundle iBundle  = DLabProPlugin.getDefault().getBundle();
    Path   iRelPath = new Path(sFile); 
    URL    iIconUrl = FileLocator.find(iBundle,iRelPath,null);
    return ImageDescriptor.createFromURL(iIconUrl);
  }
  
  /**
   * Loads an icon image.
   * 
   * @param sFile
   *            the location of the icon relative to the plug-in, (e. g.
   *            "icons/etool16/add.gif")
   * @return The loaded Image or <code>null</code> if the loading failed. The 
   *         returned image must be explicitly disposed using the image's dispose
   *         call. 
   */
  public static Image loadIconImage(String sFile)
  {
    return getIconImage(sFile).createImage();
  }

  /**
   * Determines if running on Linux
   * @deprecated Use DdUtils.isLinux() instead!
   */
  public static boolean isLinux()
  {
    return System.getProperty("os.name").toLowerCase().contains("nux");
  }
  
  /*
  String pluginPath = VisEditor.getInstallLocation().toOSString();
  try
  {
    ImageData d = new ImageData(pluginPath + relativePath);
    return new Image(null,d);
  }
  catch (Exception e)
  {
    DdUtils.MSG("loadIconImage hat ne Exception geworfen");
  }
  return null;*/
  
}
