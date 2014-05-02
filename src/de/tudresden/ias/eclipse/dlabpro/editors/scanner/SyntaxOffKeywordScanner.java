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
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordRule;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.IKeywordConstants;
import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * @author Xian TODO temporarely not used, use it
 */
public class SyntaxOffKeywordScanner extends AbstractScanner
{

  public SyntaxOffKeywordScanner(IColorManager manager, IPreferenceStore store)
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
    // IToken defaultToken = getToken(IPreferenceConstants.DEFAULT);

    List list = new ArrayList();
    list.add(createSyntaxOffKeywordRule());
    return list;
  }

  private IRule createSyntaxOffKeywordRule()
  {

    IToken keywordToken = getToken(IPreferenceConstants.P_CLR_KEYWORD);

    WordRule wordRule = new SyntaxOffWordRule();

    for (int i = 0; i < IKeywordConstants.SYNTAX_OFF_SEGMENT_START_KEYWORDS.length; i++)
      wordRule.addWord(IKeywordConstants.SYNTAX_OFF_SEGMENT_START_KEYWORDS[i], keywordToken);
    for (int i = 0; i < IKeywordConstants.SEGMENT_END_KEYWORDS.length; i++)
      wordRule.addWord(IKeywordConstants.SEGMENT_END_KEYWORDS[i], keywordToken);
    return wordRule;

  }

  /**
   * this method overrides initTokenProperties() from the superclass
   * 
   * @see AbstractScanner#initTokenProperties()
   */
  protected void initTokenProperties()
  {
    addTokenProperty(IPreferenceConstants.P_CLR_KEYWORD);

  }

  private class SyntaxOffWordRule extends WordRule
  {

    public SyntaxOffWordRule()
    {
      super(new IWordDetector()
      {

        public boolean isWordStart(char c)
        {
          return Character.isJavaIdentifierStart(c);
        }

        public boolean isWordPart(char c)
        {
          if (c == ':') return true;
          else return Character.isJavaIdentifierPart(c);
        }
      });
    }
    // IToken keywordToken = getToken(IPreferenceConstants.COMMENT);
    // private StringBuffer fBuffer = new StringBuffer();

    // public IToken evaluate(ICharacterScanner scanner) {
    // int c= scanner.read();
    // if (fDetector.isWordStart((char) c)) {
    // if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {
    //
    // fBuffer.setLength(0);
    // do {
    // fBuffer.append((char) c);
    // c= scanner.read();
    // } while (c != ICharacterScanner.EOF && fDetector.isWordPart((char) c));
    // scanner.unread();
    //
    // IToken token= (IToken) fWords.get(fBuffer.toString());
    // if (token != null)
    // return token;
    //
    // if (fDefaultToken.isUndefined())
    // unreadBuffer(scanner);
    //
    // return fDefaultToken;
    // }
    // }
    //
    // scanner.unread();
    // return keywordToken;
    // }
    //		
    // /**
    // * Returns the characters in the buffer to the scanner.
    // *
    // * @param scanner the scanner to be used
    // */
    // protected void unreadBuffer(ICharacterScanner scanner) {
    // for (int i= fBuffer.length() - 1; i >= 0; i--)
    // scanner.unread();
    // }
    //		
    //
    // public IToken evaluate(ICharacterScanner scanner, boolean resume) {
    // return evaluate(scanner);
    // }
  }

}
