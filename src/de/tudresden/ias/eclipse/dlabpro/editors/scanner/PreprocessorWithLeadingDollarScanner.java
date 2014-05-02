/*
 * Created on 21.08.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.rules.PreprocessorWithLeadingDollarRule;
import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * this class inherits the abstract class <code>AbstractScanner</code> and implements a scanner
 * for preprocessors with a leading $
 * 
 * @author Xian
 * 
 */
public class PreprocessorWithLeadingDollarScanner extends AbstractScanner
{

  public PreprocessorWithLeadingDollarScanner(IColorManager manager, IPreferenceStore store)
  {
    super(manager, store);
  }

  /**
   * this method overrides createRules()from the superclass
   * 
   * @see AbstractScanner#createRules()()
   */
  protected List createRules()
  {
    List list = new ArrayList();
    IToken preprocessorToken = getToken(IPreferenceConstants.PREPROCESSOR_WITH_LEADING$);
    IRule preprocessorRule = new PreprocessorWithLeadingDollarRule(preprocessorToken);
    list.add(preprocessorRule);
    return list;
  }

  /**
   * this method overrides initTokenProperties()from the superclass
   * 
   * @see AbstractScanner#initTokenProperties()
   */
  protected void initTokenProperties()
  {
    addTokenProperty(IPreferenceConstants.PREPROCESSOR_WITH_LEADING$);

  }

}
