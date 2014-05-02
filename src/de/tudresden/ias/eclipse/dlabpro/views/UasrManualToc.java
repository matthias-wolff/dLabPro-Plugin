
package de.tudresden.ias.eclipse.dlabpro.views;

import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

public class UasrManualToc extends CommonManualToc
{

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.views.CommonManualToc#getPreferenceName()
   */
  protected String getPreferenceName()
  {
    return IPreferenceConstants.P_PRG_UASRDOC;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.views.CommonManualToc#getNoHomepathText()
   */
  protected String getNoHomepathText()
  {
    return "no uasr doc homepath specified";
  }

}
