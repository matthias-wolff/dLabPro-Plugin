
package de.tudresden.ias.eclipse.dlabpro.editors.rules;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;

public class FormularRule extends PatternRule
{

  char[]           endSequence1 = ":".toCharArray();
  char[]           endSequence2 = ";".toCharArray();

  /**
   * replaces the same named private field of class PatternRule
   */
  private char[][] fLineDelimiters;

  /**
   * replaces the same named private field of class PatternRule
   */
  private char[][] fSortedLineDelimiters;

  /**
   * replaces the same named private class of class PatternRule
   */
  private static class DecreasingCharArrayLengthComparator implements Comparator
  {
    public int compare(Object o1, Object o2)
    {
      return ((char[])o2).length - ((char[])o1).length;
    }
  }
  /**
   * replaces the same named private field of class PatternRule
   */
  private Comparator fLineDelimiterComparator = new DecreasingCharArrayLengthComparator();

  /**
   * Constructor
   * 
   * @param token
   */
  public FormularRule(IToken token)
  {
    super(":", ":", token, (char)0, false, true);
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
        // Skip escaped character(s)
        if (fEscapeContinuesLine)
        {
          c = scanner.read();
          for (int i = 0; i < fSortedLineDelimiters.length; i++)
          {
            if (c == fSortedLineDelimiters[i][0]
                && sequenceDetected(scanner, fSortedLineDelimiters[i], true)) break;
          }
        }
        else scanner.read();

      }
      else if (endSequence1.length > 0 && c == endSequence1[0])
      {
        // Check if the specified end sequence has been found.
        if (sequenceDetected(scanner, endSequence1, true)) return true;

      }
      else if (endSequence2.length > 0 && c == endSequence2[0])
      {
        if (sequenceDetected(scanner, endSequence2, true)) return true;
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
