/*
 * Created on 01.09.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * this class inherits the abstract class <code>AbstractScanner</code> and implements a scanner
 * for preprocessors from the type ${ }
 * 
 * @author Xian
 * 
 */
public class PreprocessorWithBracketsScanner extends AbstractScanner
{

  public PreprocessorWithBracketsScanner(IColorManager manager, IPreferenceStore store)
  {
    super(manager, store);
  }

  /**
   * this method overrides createRules()from the superclass
   * 
   * @see AbstractScanner#createRules()
   */
  protected List createRules()
  {
    List list = new ArrayList();
    IToken preprocessorToken = getToken(IPreferenceConstants.PREPROCESSOR_WITH_BRACKETS);
    IRule preprocessorRule = new SingleLineRule("${", "}", preprocessorToken);
    list.add(preprocessorRule);
    IRule preprocessor2Rule = new SingleLineRule("$[", "]", preprocessorToken);
    list.add(preprocessor2Rule);
    return list;
  }

  /**
   * this method overrides initTokenProperties()from the superclass
   * 
   * @see AbstractScanner#initTokenProperties()
   */
  protected void initTokenProperties()
  {
    addTokenProperty(IPreferenceConstants.PREPROCESSOR_WITH_BRACKETS);

  }

}
