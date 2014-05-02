/*
 * Created on 21.08.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import de.tudresden.ias.eclipse.dlabpro.editors.rules.EolRule;
import de.tudresden.ias.eclipse.dlabpro.editors.rules.PreprocessorWithLeadingDollarRule;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * @author Christian Feig
 * 
 */
public class CommonDocumentPartitionScanner extends RuleBasedPartitionScanner implements
    IPreferenceConstants, IDocumentPartitionScanner
{

  private static final IToken commentToken                  = new Token(P_CLR_COMMENT);
  // private static final IToken defaultToken = new Token(DEFAULT);
  private static final IToken preprocessorWithLeading$Token = new Token(PREPROCESSOR_WITH_LEADING$);
  private static final IToken preprocessorWithBracketsToken = new Token(PREPROCESSOR_WITH_BRACKETS);
  private static final IToken syntaxOffToken                = new Token(SYNTAXOF_SEGMENT);
  private static final IToken syntaxOffEolToken             = new Token(SYNTAXOFF_EOL);
  private static final IToken characterChainToken           = new Token(P_CLR_STRING);
  // private static final IToken formelInterpreterToken = new
  // Token(FORMELINTERPRETER);

  /**
   * here you have to add all partition types which shall be handled
   */
  private final String[]      PARTITION_TYPES               =
                                                            { SYNTAXOF_SEGMENT, SYNTAXOFF_EOL,
      P_CLR_STRING, P_CLR_COMMENT, DEFAULT, PREPROCESSOR_WITH_LEADING$, PREPROCESSOR_WITH_BRACKETS /*
                                                                                                 * ,
                                                                                                 * FORMELINTERPRETER
                                                                                                 */};

  /**
   * This class partitionizes the document with the given rules. It divides the Document in logical
   * blocks. This makes it more easy to handle for example comment-blocks or something else.<br>
   * To find and mark partitions you have to specify a token. Add the Token Object manually to the
   * list of supported partition types in field <code>PARTITION_TYPES</code>. The same token
   * object is used in <code>CommonSourceViewerConfiguration.getPresentationReconciler()</code> to
   * set the Damager and Repairer which handles damaging and repairing of the marked partitions.
   * Then add the rule to find the partition in method <code>createRules()</code>.
   * 
   */
  public CommonDocumentPartitionScanner()
  {
    initializeRules();
  }

  /**
   * initializes the specified rules
   * 
   */
  protected void initializeRules()
  {
    List rules = createRules();
    if (rules != null)
    {
      IPredicateRule[] result = new IPredicateRule[rules.size()];
      rules.toArray(result);
      setPredicateRules(result);

    }
  }

  /**
   * 
   * @return the list of rules to find document partitions
   */
  protected List createRules()
  {
    List rules = new ArrayList();
    rules.add(new EndOfLineRule("#", commentToken));

    for (int i = 0; i < IKeywordConstants.SINGLE_SEGMENT_KEYWORDS.length; i++)
      rules.add(new EolRule(IKeywordConstants.SINGLE_SEGMENT_KEYWORDS[i], syntaxOffEolToken));

    rules.add(new SingleLineRule("${", "}", preprocessorWithBracketsToken/* ,'\\' */));
    rules.add(new SingleLineRule("$[", "]", preprocessorWithBracketsToken/* ,'\\' */));
    rules.add(new PreprocessorWithLeadingDollarRule(preprocessorWithLeading$Token));

    rules.add(new PatternRule("\"", "\"", characterChainToken, '\\', true, true, true));
    // rules.add(new PatternRule("'", "'", characterChainToken, '\\', true, true, true));

    // rules.add(new FormularRule(formelInterpreterToken));

    // rules
    // .add(new SingleLineRule("${", "}", preprocessorWithBracketsToken/* ,'\\' */));
    // rules
    // .add(new SingleLineRule("$[", "]", preprocessorWithBracketsToken/* ,'\\' */));
    // rules.add(new PreprocessorWithLeadingDollarRule(
    // preprocessorWithLeading$Token));

    rules.add(new CodeRule(syntaxOffToken));
    for (int i = 0; i < IKeywordConstants.SYNTAX_OFF_SEGMENT_START_KEYWORDS.length; i++)
      if (IKeywordConstants.SYNTAX_OFF_SEGMENT_START_KEYWORDS[i].endsWith("CODE:")
          && !IKeywordConstants.SYNTAX_OFF_SEGMENT_START_KEYWORDS[i].equals("CODE:")) rules
          .add(new MultiLineRule(IKeywordConstants.SYNTAX_OFF_SEGMENT_START_KEYWORDS[i],
              "END_CODE", syntaxOffToken));

    // rules.add(new MultiLineRule("CODE:", "END_CODE", syntaxOffToken));
    rules.add(new MultiLineRule("MAN:", "END_MAN", syntaxOffToken));
    rules.add(new MultiLineRule("<%", "%>", syntaxOffToken));
    // rules.add(new StartOfLineRule(";",formelInterpreterToken));

    return rules;
  }

  /**
   * returns the supported partition types specified in field <code>PARTITION_TYPES</code>
   */
  public String[] getPartitionTypes()
  {
    return PARTITION_TYPES;
  }

  /**
   * this class represents a type MultiLineRule to detect and handle so called CODE-segments
   * starting with CODE: and ending with END_CODE
   * 
   * @author Christian Feig
   * 
   */
  private class CodeRule extends MultiLineRule
  {
    static final String STARTSEQUENCE = "CODE:";
    static final String ENDSEQUENCE   = "END_CODE";

    public CodeRule(IToken token)
    {
      super(STARTSEQUENCE, ENDSEQUENCE, token);
    }

    /**
     * Returns whether the next characters to be read by the character scanner are an exact match
     * with the given sequence. No escape characters are allowed within the sequence. If specified
     * the sequence is considered to be found when reading the EOF character.
     * 
     * @param scanner
     *          the character scanner to be used
     * @param sequence
     *          the sequence to be detected
     * @param eofAllowed
     *          indicated whether EOF terminates the pattern
     * @return true id sequence detected, false else
     */
    private boolean detectSequence(ICharacterScanner scanner, char[] sequence, boolean eofAllowed)
    {
      for (int i = 1; i < sequence.length; i++)
      {
        int c = scanner.read();
        if (c == ICharacterScanner.EOF && eofAllowed)
        {
          return true;
        }
        else if (c != sequence[i])
        {
          // Non-matching character detected, rewind the scanner back to the
          // start.
          // Do not unread the first character.
          scanner.unread();
          for (int j = i - 1; j > 0; j--)
            scanner.unread();
          return false;
        }
      }

      return true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
     */
    protected boolean endSequenceDetected(ICharacterScanner scanner)
    {
      return super.endSequenceDetected(scanner);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.text.rules.PatternRule#sequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner,
     *      char[], boolean)
     */
    protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence,
        boolean eofAllowed)
    {
      if (String.copyValueOf(sequence).equals(STARTSEQUENCE))
      {
        scanner.unread();
        scanner.unread();
        int pre = scanner.read();
        scanner.read();
        if (pre == ' ' | pre == '\r' | pre == '\n') return detectSequence(scanner, sequence,
            eofAllowed);

        return false;
      }
      else return detectSequence(scanner, sequence, eofAllowed);

    }
  }

}
