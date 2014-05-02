
package de.tudresden.ias.eclipse.dlabpro.preferences;


public interface IPreferenceConstants
{
  // General
  public static final String  P_CNS_RECYCLE             = "dlabpro.console.recycle";
  public static final String  P_CNS_SHOW                = "dlabpro.console.show";
  public static final String  P_DLG_RUN                 = "dlabpro.dialog.run";
  public static final String  P_DLG_RERUN               = "dlabpro.dialog.rerun";
  
  // Script Code folding
  public static final String  P_FLD_XTPJAVADOC          = "dlabpro.codefolding.javadoc";
  public static final String  P_FLD_XTPFUNCTION         = "dlabpro.codefolding.functions";
  public static final String  P_FLD_DEFMAN              = "dlabpro.codefolding.man";
  public static final String  P_FLD_DEFCODE             = "dlabpro.codefolding.code";

  // Script Syntax Coloring
  public static final String  P_CLR_COMMENT             = "DLABPRO.commentColor";
  public static final String  P_CLR_STRING              = "DLABPRO.characterChain";
  public static final String  P_CLR_CONSTANTS           = "DLABPRO.constantsColor";
  public static final String  P_CLR_PREPROC             = "DLABPRO.preprocessorColor";
  public static final String  P_CLR_KEYWORD             = "DLABPRO.keywordColor";
  public static final String  P_CLR_FORMULA             = "DLABPRO.formelInterpreterColor";
  public static final String  P_SUFFIX_BOLD             = "_bold";
  public static final String  P_SUFFIX_ITALIC           = "_italic";

  // Programs
  public static final String P_PRG_DLPHOME              = "DLABPRO_HOME";
  public static final String P_PRG_DLPEXE               = "dlabproPreferenceExecutive";
  public static final String P_PRG_DLPDOC               = "dlabproPreferenceDocumentation";
  public static final String P_PRG_DLPARGS              = "dlabproPreferenceArguments";
  public static final String P_PRG_CGENEXE              = "cGenPreferenceExecutive";
  public static final String P_PRG_CGENARGS             = "cGenPreferenceArguments";
  public static final String P_PRG_UASRHOME             = "UASR_HOME";
  public static final String P_PRG_UASRDOC              = "uasrPreferenceDocumentation";

  // Other constants...
  // TODO: ... which do not belong here!!
  public static final String DEFAULT                    = "DLABPRO.default";
  public static final String PREPROCESSOR_WITH_LEADING$ = "DLABPRO.preprocessorColorWithLeading$";
  public static final String PREPROCESSOR_WITH_BRACKETS = "DLABPRO.preprocessorColorWithBrackets";
  public static final String SYNTAXOF_SEGMENT           = "DLABPRO.syntaxoffSegmentKeyword";
  public static final String SYNTAXOFF_EOL              = "DLABPRO.syntaxoffEolKeyword";

}
