
package de.tudresden.ias.eclipse.dlabpro.views;

import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

public class DLabProManualToc extends CommonManualToc
{

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.views.CommonManualToc#getPreferenceName()
   */
  protected String getPreferenceName()
  {
    return IPreferenceConstants.P_PRG_DLPDOC;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.views.CommonManualToc#getNoHomepathText()
   */
  protected String getNoHomepathText()
  {
    return "no dlabPro doc homepath specified";
  }

}
