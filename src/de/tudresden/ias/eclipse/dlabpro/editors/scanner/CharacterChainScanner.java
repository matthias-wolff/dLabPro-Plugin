/*
 * Created on 01.09.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * this class inherits the abstract class <code>AbstractScanner</code> and implements a scanner
 * for character chains
 * 
 * @author Xian
 * 
 */
public class CharacterChainScanner extends AbstractScanner
{

  public CharacterChainScanner(IColorManager manager, IPreferenceStore store)
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
    List list = new ArrayList();
    IToken characterChainToken = getToken(IPreferenceConstants.P_CLR_STRING);
    list.add(new PatternRule("\"", "\"", characterChainToken, '\\', true, true, true));
    // list.add(new PatternRule("'", "'", characterChainToken, '\\', true, true, true));
    setDefaultReturnToken(getToken(IPreferenceConstants.DEFAULT));
    return list;
  }

  /**
   * this method overrides initTokenProperties() from the superclass
   * 
   * @see AbstractScanner#initTokenProperties()
   */
  protected void initTokenProperties()
  {
    addTokenProperty(IPreferenceConstants.P_CLR_STRING);
  }

  /*
   * private class CharacterLineRule extends SingleLineRule {
   * 
   * public CharacterLineRule(String startSequence, String endSequence, IToken token) {
   * super(startSequence, endSequence, token); this.startSequence = startSequence; this.endSequence =
   * endSequence; fToken = token; } String startSequence; String endSequence; IToken fToken; //
   * private char[][] fLineDelimiters; // private char[][] fSortedLineDelimiters; // private
   * Comparator fLineDelimiterComparator= new DecreasingCharArrayLengthComparator(); // private
   * class DecreasingCharArrayLengthComparator implements Comparator { // public int compare(Object
   * o1, Object o2) { // return ((char[]) o2).length - ((char[]) o1).length; // } // } protected
   * IToken doEvaluate(ICharacterScanner scanner, boolean resume) { if (resume) {
   * 
   * if (endSequenceDetected(scanner)) return fToken; } else {
   * 
   * int c = scanner.read(); if (c == startSequence.charAt(0)) { if (endSequenceDetected(scanner))
   * return fToken; } }
   * 
   * scanner.unread(); return getToken(IPreferenceConstants.DEFAULT); }
   * 
   * protected boolean endSequenceDetected(ICharacterScanner scanner) { int c; int counter = 0;
   * while ((c = scanner.read()) != ICharacterScanner.EOF) {
   * 
   * if (c == endSequence.charAt(0)) return true;
   * 
   * if (c == (char)0) { // scanner.unread(); // scanner.unread(); // c = scanner.read(); //
   * scanner.read(); // if(!(c == '\\')) return true; } // if(c == 13){ // c = scanner.read(); //
   * if(c == 10){ // scanner.unread(); // scanner.unread(); // scanner.unread(); // c =
   * scanner.read(); // scanner.read(); // scanner.read(); // if(!(c == '\\')) // return true; // } //
   * scanner.unread(); // } } scanner.unread(); return false; } }
   */
}
