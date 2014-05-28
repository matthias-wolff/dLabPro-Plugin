/*
 * Created on 12.03.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

package de.tudresden.ias.eclipse.dlabpro.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.util.PropertyChangeEvent;

import de.tudresden.ias.eclipse.dlabpro.editors.rules.DLabProNumberRule;
import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * @author Christian Feig
 * 
 */
public class CommonCodeScanner extends AbstractScanner implements IKeywordConstants,
    IPreferenceConstants
{

  public static boolean isDlabProTokenStart(char c)
  {
    if (c=='?' || c=='/' || c=='-') return true;
    return Character.isJavaIdentifierStart(c);
  }

 public static boolean isDlabProTokenPart(char c)
  {
    if (c=='?' || c=='/' || c=='-') return true;
    return Character.isJavaIdentifierStart(c);
  }
  
  /**
   * This class inherits the abstract class <code>AbstractScanner</code> and scans the document
   * for patterns specified by the created rules and signs them with the specified token.
   * 
   * @param manager
   * @param store
   * @see AbstractScanner
   */
  public CommonCodeScanner(IColorManager manager, IPreferenceStore store)
  {
    super(manager, store);
    initTokenProperties();
  }

  /**
   * implements abstract method from the superclass
   * 
   */
  protected void initTokenProperties()
  {
    addTokenProperty(P_CLR_CONSTANTS);
    addTokenProperty(P_CLR_KEYWORD);
    addTokenProperty(DEFAULT);
  }

  /**
   * implements abstract method from the superclass
   */
  protected List createRules()
  {
    List list = new ArrayList();
    // list.add(createCharacterChainRule());
    // list.add(createCharacterChain2Rule());
    // list.add(createCommentRule());
    list.add(createCommonKeywordRule());
    list.add(createNumberRule());
    list.add(createConstantRule());
    // list.add(createFormelInterpreterRule());
    setDefaultReturnToken(getToken(DEFAULT));
    return list;
  }

  // für itp und def skripte TODO da gibt es noch probleme mit der "
  // identifizierung
  // private IRule createCharacterChainRule(){
  // IToken characterChainToken = getToken(CHARACTERCHAIN);
  // IRule characterChainRule = new SingleLineRule("\"","\"",
  // characterChainToken,'\\');
  // return characterChainRule;
  // }

  // für itp und def skripte TODO da gibt es noch probleme mit der "
  // identifizierung
  // private IRule createCharacterChain2Rule(){
  // IToken characterChainToken = getToken(CHARACTERCHAIN);
  // IRule characterChainRule = new SingleLineRule("'","'",
  // characterChainToken,'\\');
  // return characterChainRule;
  // }

  // private IRule createFormelInterpreterRule(){
  // IToken characterChainToken = getToken(CHARACTERCHAIN);
  // IRule characterChainRule = new SingleLineRule("\\n",";",
  // characterChainToken);
  // return characterChainRule;
  // }

  // für itp und def skripte
  // private IRule createCommentRule(){
  // IToken commentToken = getToken(COMMENT);
  // IRule commentRule = new EndOfLineRule("#", commentToken);
  // return commentRule;
  // }

  private IRule createConstantRule()
  {
     IToken constantToken = getToken(P_CLR_CONSTANTS);
     WordRule constantRule = new WordRule(new IWordDetector()
     {
      public boolean isWordPart(char c)
      {
        return isDlabProTokenPart(c);
      }
      public boolean isWordStart(char c)
      {
        return isDlabProTokenStart(c);
      }
     });
     
     constantRule.addWord("NULL" ,constantToken);
     constantRule.addWord("TRUE" ,constantToken);
     constantRule.addWord("FALSE",constantToken);
     
     return constantRule;
   }

  // itp und def?
  private IRule createNumberRule()
  {
    IToken numberToken = getToken(P_CLR_CONSTANTS);
    IRule numberRule = new DLabProNumberRule(numberToken);
    return numberRule;
  }

  // itp und def
  private IRule createCommonKeywordRule()
  {

    IToken keywortToken = getToken(P_CLR_KEYWORD);
    WordRule wordRule = new WordRule(new IWordDetector()
    {
      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
       */
      public boolean isWordStart(char c)
      {
        return isDlabProTokenStart(c);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
       */
      public boolean isWordPart(char c)
      {
        return isDlabProTokenPart(c);
      }
    })
    {
      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
       */
      public IToken evaluate(ICharacterScanner scanner)
      {

        if (scanner.getColumn() == 1) return super.evaluate(scanner);
        else
        {
          scanner.unread();
          int c = scanner.read();
          if (Character.isWhitespace((char)c) || (char)c == ':' || (char)c == ';') { return super
              .evaluate(scanner); }
        }

        return Token.UNDEFINED;
      }
    };

    for (int i = 0; i < COMMON_KEYWORDS.length; i++)
      wordRule.addWord(COMMON_KEYWORDS[i], keywortToken);

    return wordRule;

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner#adaptToPreferenceChange(org.eclipse.jface.util.PropertyChangeEvent)
   */
  public void adaptToPreferenceChange(PropertyChangeEvent event)
  {
    if (super.affectsBehavior(event)) super.adaptToPreferenceChange(event);
  }
}
