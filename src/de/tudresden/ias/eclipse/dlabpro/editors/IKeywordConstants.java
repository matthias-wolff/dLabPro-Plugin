/*
 * Created on 11.03.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

package de.tudresden.ias.eclipse.dlabpro.editors;

/**
 * this interface definesthe common and def/itp specific keywords
 * 
 * @author Xian
 * 
 */
public interface IKeywordConstants
{
  // public static String[] COMMON_KEYWORDS = {"if", "else", "endif", "not",
  // "quit", "break", "label", "goto", "NULL", "TRUE", "FALSE"};
  public static String[] COMMON_KEYWORDS = KeywordLoader
      .getDefaultCommonKeywords();
  public static String[] DEF_KEYWORDS = KeywordLoader.getDefaultDefKeywords();
  public static String[] ITP_KEYWORDS = KeywordLoader.getDefaultItpKeywords();
  public static String[] SEGMENT_START_KEYWORDS =
  { "CLASSCODE:", "COPYCODE:", "DONECODE:", "HEADERCODE:", "INITCODE:",
      "INSTALLCODE:", "RESETCODE:", "RESTORECODE:", "SAVECODE:" };
  public static String[] SYNTAX_OFF_SEGMENT_START_KEYWORDS = KeywordLoader
      .getSyntaxOffSegmentStartKeywords();
  public static String[] SYNTAX_OFF_SEGMENT_END_KEYWORDS = KeywordLoader
      .getSyntaxOffSegmentEndKeywords();
  // public static String[] SYNTAX_OFF_EOL_KEYWORDS = KeywordLoader
  // .getSyntaxOffEolKeywords();

  public static String[] SEGMENT_END_KEYWORDS =
  { "END_CODE", "END_MAN" };
  public static String[] LIST_KEYWORDS =
  { "DEFINE:", "FILE:", "INCLUDE:", "PINCLUDE:" };
  public static String[] ITP_LIST_KEYWORDS =
  { "include" };
  public static String[] SINGLE_SEGMENT_KEYWORDS =
  { "COMMENT:", "METHOD:", "FIELD:", "OPTION:", "ERROR:", "NOTE:" };

}
