/*
 * Created on 21.08.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * this class inherits the abstract class <code>AbstractScanner</code> and implements a scanner
 * for comments
 * 
 * @author Xian
 * 
 */
public class CommentScanner extends AbstractScanner
{

  public CommentScanner(IColorManager manager, IPreferenceStore store)
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
    IToken green = getToken(IPreferenceConstants.P_CLR_COMMENT);
    IRule endOfLineRule = new EndOfLineRule("#", green);
    list.add(endOfLineRule);
    return list;
  }

  /**
   * this method overrides initTokenProperties()from the superclass
   * 
   * @see AbstractScanner#initTokenProperties()
   */
  protected void initTokenProperties()
  {
    addTokenProperty(IPreferenceConstants.P_CLR_COMMENT);

  }
}
