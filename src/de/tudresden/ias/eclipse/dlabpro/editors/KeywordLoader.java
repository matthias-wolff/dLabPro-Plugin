
package de.tudresden.ias.eclipse.dlabpro.editors;

import java.util.ResourceBundle;

public class KeywordLoader
{

  static ResourceBundle res = ResourceBundle
                                .getBundle("de.tudresden.ias.eclipse.dlabpro.editors.Keyword");

  public static String[] getDefaultCommonKeywords()
  {
    if (res != null) return res.getString("DefaultCommonKeywords").split("\\s+");
    else return new String[]
    { "" };
  }

  public static String[] getCommonKeywords()
  {
    return getDefaultDefKeywords();
  }

  public static String[] getDefaultDefKeywords()
  {
    if (res != null) return res.getString("DefaultDefKeywords").split("\\s+");
    else return new String[]
    { "" };
  }

  public static String[] getDefaultItpKeywords()
  {
    if (res != null) return res.getString("DefaultItpKeywords").split("\\s+");
    else return new String[]
    { "" };
  }

  public static String[] getSyntaxOffSegmentStartKeywords()
  {
    if (res != null) return res.getString("SyntaxOffSegmentStartKeywords").split("\\s+");
    else return new String[]
    { "" };
  }

  public static String[] getSyntaxOffSegmentEndKeywords()
  {
    if (res != null) return res.getString("SyntaxOffSegmentEndKeywords").split("\\s+");
    else return new String[]
    { "" };
  }

  public static String[] getSyntaxOffEolKeywords()
  {
    if (res != null) return res.getString("SyntaxOffEolKeywords").split("\\s+");
    else return new String[]
    { "" };
  }

  public static String[] getDefKeywords()
  {
    return getDefaultDefKeywords();
  }

}
