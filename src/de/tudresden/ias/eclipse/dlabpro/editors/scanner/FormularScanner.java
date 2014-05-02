/*
 * Created on 10.09.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.rules.FormularRule;
import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * this class is temporarely not used and shall be implement a Scanner for the formel interpreter
 * problem
 * 
 * @author Xian
 * 
 */
public class FormularScanner extends AbstractScanner
{

  public FormularScanner(IColorManager manager, IPreferenceStore store)
  {
    super(manager, store);

  }

  /**
   * this method overrides createRules() from the superclass
   * 
   * @see AbstractScanner#createRules()
   */
  protected List createRules()
  {
    List list = new ArrayList();
    IToken formelInterpreterToken = getToken(IPreferenceConstants.P_CLR_FORMULA);
    list.add(new FormularRule(formelInterpreterToken));
    return list;
  }

  /**
   * this method overrides initTokenProperties() from the superclass
   * 
   * @see AbstractScanner#initTokenProperties()
   */
  protected void initTokenProperties()
  {
    addTokenProperty(IPreferenceConstants.P_CLR_FORMULA);

  }

}
