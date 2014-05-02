
package de.tudresden.ias.eclipse.dlabpro.editors.rules;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;

public class EolRule extends PatternRule
{

  public EolRule(String startSequence, IToken token)
  {
    super(startSequence, null, token, '#', true, true, false);
  }

  public EolRule(String startSequence, IToken token, char escapeCharacter)
  {
    super(startSequence, null, token, escapeCharacter, true, true, false);
  }

  /**
   * Cached line delimiters.
   * 
   * @since 3.1
   */
  private char[][]   fLineDelimiters;
  /**
   * Cached sorted {@linkplain #fLineDelimiters}.
   * 
   * @since 3.1
   */
  private char[][]   fSortedLineDelimiters;

  /**
   * Line delimiter comparator which orders according to decreasing delimiter length.
   * 
   * @since 3.1
   */
  private Comparator fLineDelimiterComparator = new DecreasingCharArrayLengthComparator();

  /**
   * Comparator that orders <code>char[]</code> in decreasing array lengths.
   * 
   * @since 3.1
   */
  private static class DecreasingCharArrayLengthComparator implements Comparator
  {
    public int compare(Object o1, Object o2)
    {
      return ((char[])o2).length - ((char[])o1).length;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
   */
  protected boolean endSequenceDetected(ICharacterScanner scanner)
  {
    char[][] originalDelimiters = scanner.getLegalLineDelimiters();
    int count = originalDelimiters.length;
    if (fLineDelimiters == null || originalDelimiters.length != count)
    {
      fSortedLineDelimiters = new char[count][];
    }
    else
    {
      while (count > 0 && fLineDelimiters[count - 1] == originalDelimiters[count - 1])
        count--;
    }
    if (count != 0)
    {
      fLineDelimiters = originalDelimiters;
      System.arraycopy(fLineDelimiters, 0, fSortedLineDelimiters, 0, fLineDelimiters.length);
      Arrays.sort(fSortedLineDelimiters, fLineDelimiterComparator);
    }

    int c;
    while ((c = scanner.read()) != ICharacterScanner.EOF)
    {
      if (c == fEscapeCharacter)
      {
        scanner.unread();
        return true;

      }
      else if (fEndSequence.length > 0 && c == fEndSequence[0])
      {
        // Check if the specified end sequence has been found.
        if (sequenceDetected(scanner, fEndSequence, true)) return true;
      }
      else if (fBreaksOnEOL)
      {
        // Check for end of line since it can be used to terminate the pattern.
        for (int i = 0; i < fSortedLineDelimiters.length; i++)
        {
          if (c == fSortedLineDelimiters[i][0]
              && sequenceDetected(scanner, fSortedLineDelimiters[i], true)) return true;
        }
      }
    }
    if (fBreaksOnEOF) return true;
    scanner.unread();
    return false;
  }

}
