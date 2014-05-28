/*
 * Created on 12.03.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

package de.tudresden.ias.eclipse.dlabpro.editors.def;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WordRule;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonCodeScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.IKeywordConstants;
import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * @author Xian
 * 
 */
public class DefCodeScanner extends CommonCodeScanner implements IKeywordConstants, IPreferenceConstants
{

  /**
   * This class inherits the class <code>CommonCodeScanner</code> and implements methods for
   * scanning def-scripts
   * 
   * @param manager
   * @param store
   */
  public DefCodeScanner(IColorManager manager, IPreferenceStore store)
  {
    super(manager, store);
  }

  /**
   * overrides initTokenProperties() from superclass
   */
  protected void initTokenProperties()
  {
    addTokenProperty(P_CLR_PREPROC);
    super.initTokenProperties();
  }

  /**
   * overrides createRules() from superclass
   */
  protected List createRules()
  {
    List list = super.createRules();
    // list.add(createPreprocessorRuleWithLeading$());

    list.add(createPreprocessor1Rule());

    list.add(createDEFKeywordRule());
    list.add(createInitializersRule());
    // list.add(createPreprocessor3Rule());
    list.add(createPreprocessor4Rule());
    return list;
  }

  /**
   * creates a rule for detecting $1 - $99
   */
  // private IRule createPreprocessorRuleWithLeading$(){
  // IToken preprocessorToken = getToken(PREPROCESSOR_WITH_LEADING$);
  // IRule characterChainRule = new PreprocessorRule("$",preprocessorToken);
  // return characterChainRule;
  // }
  // just def keywords
  /**
   * this method implements a IRule for detecting def specific keywords
   */
  private IRule createDEFKeywordRule()
  {

    IToken keywortToken = getToken(P_CLR_KEYWORD);
    WordRule wordRule = new WordRule(new IWordDetector()
    {

      public boolean isWordStart(char c)
      {
        if (c == '/' || c == '-') return true;
        else return Character.isJavaIdentifierStart(c);
      }

      public boolean isWordPart(char c)
      {
        if (c == ':') return true;
        else return Character.isJavaIdentifierPart(c);
      }
    });
    for (int i = 0; i < DEF_KEYWORDS.length; i++)
      wordRule.addWord(DEF_KEYWORDS[i], keywortToken);

    // for(int i = 0; i < SYNTAX_OFF_KEYWORDS.length; i++)
    // wordRule.addWord(SYNTAX_OFF_KEYWORDS[i], keywortToken);

    return wordRule;

  }

  // just for def-scripts?
  // Initializers: {TEXT}
  /**
   * this method implements a IRule for detecting initilaizers
   */
  private IRule createInitializersRule()
  {
    IToken constantsToken = getToken(P_CLR_CONSTANTS);
    IRule initializersRule = new SingleLineRule("{", "}", constantsToken);
    return initializersRule;
  }

  // just for def-scripts?
  // Preprocessor rule for $$
  /**
   * this method implements a IRule for detecting preprocessors from the type $$
   */
  private IRule createPreprocessor1Rule()
  {
    IToken preprocessorToken = getToken(P_CLR_PREPROC);
    IRule preprocessorRule = new EndOfLineRule("$$", preprocessorToken); // TODO
    // oder
    // SingleLineRule?
    // oder
    // nur
    // WordRule?
    return preprocessorRule;
  }

  // just for def-scripts?
  // Preprocessor rule for ${TEXT}
  // private IRule createPreprocessor3Rule(){
  // IToken preprocessorToken = getToken(PREPROCESSOR);
  // IRule multiLineRule = new SingleLineRule("${","}", preprocessorToken);
  // return multiLineRule;
  // }

  // just for def-scripts?
  // Preprocessor rule for ^TEXT;
  /**
   * this method implements a IRule for detecting preprocessors from the type ^ until ;
   */
  private IRule createPreprocessor4Rule()
  {
    IToken preprocessorToken = getToken(P_CLR_PREPROC);
    IRule characterChainRule = new SingleLineRule("^", ";", preprocessorToken);
    return characterChainRule;
  }

}
