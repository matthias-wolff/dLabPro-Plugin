/*
 * Created on 21.08.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.IKeywordConstants;
import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * @author Xian TODO temporarely not used, use it
 */
public class SyntaxOffEolKeywordScanner extends AbstractScanner
{

  public SyntaxOffEolKeywordScanner(IColorManager manager, IPreferenceStore store)
  {
    super(manager, store);

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner#createRules()
   */
  protected List createRules()
  {
    List list = new ArrayList();
    list.add(createSyntaxOffKeywordRule());
    return list;
  }

  /**
   * 
   * @return a new IRule for IKeywordConstants.SINGLE_SEGMENT_KEYWORDS elements
   */
  private IRule createSyntaxOffKeywordRule()
  {

    IToken keywordToken = getToken(IPreferenceConstants.P_CLR_KEYWORD);

    SyntaxOffWordRule wordRule = new SyntaxOffWordRule();
    for (int i = 0; i < IKeywordConstants.SINGLE_SEGMENT_KEYWORDS.length; i++)
      wordRule.addWord(IKeywordConstants.SINGLE_SEGMENT_KEYWORDS[i], keywordToken);
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

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
         */
        public boolean isWordStart(char c)
        {
          return Character.isJavaIdentifierStart(c);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
         */
        public boolean isWordPart(char c)
        {
          if (c == ':') return true;
          else return Character.isJavaIdentifierPart(c);
        }
      });
    }

    // private ArrayList exceptedWords = new ArrayList();
    //		
    // protected void addExceptedWord(String word, IToken token){
    // this.addWord(word, token);
    // exceptedWords.add(word);
    // }

    // IToken keywordToken = getToken(IPreferenceConstants.KEYWORD);
    private StringBuffer fBuffer      = new StringBuffer();

    private boolean      keywordFound = false;

    // private boolean exceptedWordFound = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
     */
    public IToken evaluate(ICharacterScanner scanner)
    {
      int c = scanner.read();
      if (fDetector.isWordStart((char)c))
      {
        if (fColumn == UNDEFINED) keywordFound = false;
        if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1))
        {

          fBuffer.setLength(0);
          do
          {
            fBuffer.append((char)c);
            c = scanner.read();
          }
          while (c != ICharacterScanner.EOF && fDetector.isWordPart((char)c));
          scanner.unread();

          IToken token = (IToken)fWords.get(fBuffer.toString());
          // if (token != null && !exceptedWordFound && exceptedWords.contains(fBuffer.toString())){
          // exceptedWordFound = true;
          // return token;
          // }

          if (token != null && !keywordFound)
          {
            keywordFound = true;
            return token;
          }

          if (fDefaultToken.isUndefined()) unreadBuffer(scanner);

          return fDefaultToken;
        }

      }

      scanner.unread();
      return Token.UNDEFINED;
    }

  }

}
