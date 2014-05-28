/*
 * Created on 06.09.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * this classis temporarely not used and shall be an implementation for a StartOfLineRule which will
 * be used by the <code>FormelInterpreterScanner</code>
 * 
 * @author Xian
 * 
 */
public class StartOfLineRule extends SingleLineRule
{

  public StartOfLineRule(String endSequence, IToken token)
  {
    super("^", endSequence, token);
  }

  protected IToken doEvaluate(ICharacterScanner scanner, boolean resume)
  {
    if (resume)
    {

      if (endSequenceDetected(scanner)) return fToken;

    }
    else
    {

      // int c = scanner.read();
      scanner.read();
      // if (c == fStartSequence[0]) {
      if (scanner.getColumn() == 0)
      {
        if (endSequenceDetected(scanner)) return fToken;
      }
    }

    scanner.unread();
    return Token.UNDEFINED;
  }

  public IToken evaluate(ICharacterScanner scanner, boolean resume)
  {
    if (fColumn == UNDEFINED) return doEvaluate(scanner, resume);

    // int c = scanner.read();
    // scanner.read();
    scanner.unread();
    // if (c == fStartSequence[0])
    if (scanner.getColumn() == 0) return (fColumn == scanner.getColumn() ? doEvaluate(scanner,
        resume) : Token.UNDEFINED);
    else return Token.UNDEFINED;
  }

  protected boolean endSequenceDetected(ICharacterScanner scanner)
  {
    int c;
    // char[][] delimiters= scanner.getLegalLineDelimiters();
    // boolean previousWasEscapeCharacter = false;
    while ((c = scanner.read()) != ICharacterScanner.EOF)
    {
      if (c == fEscapeCharacter)
      {
        // Skip the escaped character.
        scanner.read();
      }
      else if (fEndSequence.length > 0 && c == fEndSequence[0])
      {
        // Check if the specified end sequence has been found.
        if (sequenceDetected(scanner, fEndSequence, true)) return true;
      }/*
         * else if (fBreaksOnEOL) { // Check for end of line since it can be used to terminate the
         * pattern. for (int i= 0; i < delimiters.length; i++) { if (c == delimiters[i][0] &&
         * sequenceDetected(scanner, delimiters[i], true)) { if (!fEscapeContinuesLine ||
         * !previousWasEscapeCharacter) return false; } } } previousWasEscapeCharacter = (c ==
         * fEscapeCharacter);
         */
    }
    if (fBreaksOnEOF) return true;
    scanner.unread();
    return false;
  }

}
