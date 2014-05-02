/*
 * Created on 21.08.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * this class implements a rule for preprocessors with leading $
 * 
 * @author Xian
 * 
 */
public class PreprocessorWithLeadingDollarRule extends WordRule implements IPredicateRule
{
  private StringBuffer fBuffer = new StringBuffer();
  private String       startSequence;

  public PreprocessorWithLeadingDollarRule(IToken preprocessorToken)
  {
    super(new IWordDetector()
    {

      public boolean isWordStart(char c)
      {
        if (c == '$') return true;
        else return false;
      }

      public boolean isWordPart(char c)
      {
        int value = -1;
        try
        {
          value = Integer.valueOf(String.valueOf(c)).intValue();
        }
        catch (NumberFormatException e)
        {
          // e.printStackTrace();
        }
        if (value >= 0) return true;
        else return false;
      }
    });
    this.startSequence = "$";
    this.addWord(startSequence, preprocessorToken);
  }

  public IToken evaluate(ICharacterScanner scanner)
  {
    int c = scanner.read();
    if (fDetector.isWordStart((char)c))
    {
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

        if (fBuffer.length() > 1)
        {
          int value = -1;
          try
          {
            value = Integer.valueOf(fBuffer.substring(1)).intValue();
          }
          catch (NumberFormatException e)
          {
            // e.printStackTrace();
          }
          if ((value >= 0 && value <= 99) || fBuffer.toString().equals("$$"))
          {
            IToken token = getSuccessToken();
            if (token != null) return token;
          }
        }

        if (fDefaultToken.isUndefined()) unreadBuffer(scanner);

        return fDefaultToken;
      }
    }

    scanner.unread();
    return Token.UNDEFINED;
  }

  protected void unreadBuffer(ICharacterScanner scanner)
  {
    for (int i = fBuffer.length() - 1; i >= 0; i--)
      scanner.unread();
  }

  public IToken getSuccessToken()
  {
    return (IToken)fWords.get(startSequence);
  }

  public IToken evaluate(ICharacterScanner scanner, boolean resume)
  {
    return evaluate(scanner);
  }

}
