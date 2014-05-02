
package de.tudresden.ias.eclipse.dlabpro.editors.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 * @author Christian Feig
 * 
 */
public class DLabProNumberRule extends NumberRule
{

  /**
   * Constructor
   * 
   * @param token
   */
  public DLabProNumberRule(IToken token)
  {
    super(token);

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
   */
  public IToken evaluate(ICharacterScanner scanner)
  {
    int c = scanner.read();
    IToken returnToken = Token.UNDEFINED;

    if (Character.isWhitespace((char)c) || (char)c == ',' || (char)c == ';' || (char)c == ':')
    {
      c = scanner.read();
      returnToken = doEvaluate(c, scanner);
      if (returnToken != null) return returnToken;
      scanner.unread();
    }
    else if (scanner.getColumn() == 1)
    {
      returnToken = doEvaluate(c, scanner);
      if (returnToken != null) return returnToken;
    }

    scanner.unread();
    return Token.UNDEFINED;
  }

  /**
   * performs the evaluation of the given character c with the given scanner
   * 
   * @param c -
   *          the character to evaluate
   * @param scanner -
   *          the scanner to perform the evaluation with
   * @return
   */
  private IToken doEvaluate(int c, ICharacterScanner scanner)
  {
    if (Character.isDigit((char)c))
    {
      if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1))
      {
        do
        {
          c = scanner.read();
        }
        while (Character.isDigit((char)c) || doEvaluatePoint(c, scanner) != null
            || doEvaluateX(c, scanner) != null || doEvaluateE(c, scanner) != null);
        scanner.unread();
        return fToken;
      }
    }
    else if ((char)c == '.')
    {
      if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) return doEvaluatePoint(c,
          scanner);
    }
    else if ((char)c == '+' || (char)c == '-') if (fColumn == UNDEFINED
        || (fColumn == scanner.getColumn() - 1)) return doEvaluatePlusMinus(c, scanner);
    else if ((char)c == 'e') if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) return doEvaluateE(
        c, scanner);
    return null;
  }

  /**
   * 
   * evaluates wether the given character c is of kind 'x' and following characters are valid or not
   * 
   * @param c -
   *          the character to evaluate
   * @param scanner -
   *          the scanner to perform the evaluation with
   * @return the token or null if c is not of type 'x' or following characters are not valid
   */
  private IToken doEvaluateX(int c, ICharacterScanner scanner)
  {
    if ((char)c == 'x')
    {
      c = scanner.read();
      if (Character.isDigit((char)c))
      {
        do
        {
          c = scanner.read();
        }
        while (Character.isDigit((char)c));
        scanner.unread();
        return fToken;
      }
      else
      {
        scanner.unread();
        return null;
      }
    }
    return null;
  }

  /**
   * evaluates wether the given character c is of kind '+' or '-' and following characters are valid
   * or not
   * 
   * @param c -
   *          the character to evaluate
   * @param scanner -
   *          the scanner to perform the evaluation with
   * @return the token or null if c is not of type '+' or '-' or following characters are not valid
   */
  private IToken doEvaluatePlusMinus(int c, ICharacterScanner scanner)
  {
    if ((char)c == '+' || (char)c == '-')
    {
      c = scanner.read();
      if (Character.isDigit((char)c) || doEvaluatePoint(c, scanner) != null
          || doEvaluateE(c, scanner) != null)
      {
        do
        {
          c = scanner.read();
        }
        while (Character.isDigit((char)c) || doEvaluateE(c, scanner) != null
            || doEvaluatePoint(c, scanner) != null);
        scanner.unread();
        return fToken;
      }
      else
      {
        scanner.unread();
        return null;
      }
    }
    return null;
  }

  /**
   * evaluates wether the given character c is a '.' and following characters are valid or not
   * 
   * @param c -
   *          the character to evaluate
   * @param scanner -
   *          the scanner to perform the evaluation with
   * @return the token or null if c is not of type '.' or following characters are not valid
   */
  private IToken doEvaluatePoint(int c, ICharacterScanner scanner)
  {
    if ((char)c == '.')
    {
      c = scanner.read();
      if (Character.isDigit((char)c))
      {
        do
        {
          c = scanner.read();
        }
        while (Character.isDigit((char)c));
        scanner.unread();
        return fToken;
      }
      else
      {
        scanner.unread();
        return null;
      }
    }
    return null;
  }

  /**
   * evaluates wether the given character c and following characters represents a valid 'e'-notation
   * or not
   * 
   * @param c -
   *          the character to evaluate
   * @param scanner -
   *          the scanner to perform the evaluation with
   * @return the token or null if c and following characters does not represents a valid
   *         'e'-notation
   */
  private IToken doEvaluateE(int c, ICharacterScanner scanner)
  {
    if ((char)c == 'e')
    {
      c = scanner.read();
      if (Character.isDigit((char)c) || doEvaluatePlusMinus(c, scanner) != null
          || doEvaluatePoint(c, scanner) != null)
      {
        do
        {
          c = scanner.read();
        }
        while (Character.isDigit((char)c) || doEvaluatePlusMinus(c, scanner) != null
            || doEvaluatePoint(c, scanner) != null);
        scanner.unread();
        return fToken;
      }
      else
      {
        scanner.unread();
        return null;
      }
    }
    return null;
  }

}
