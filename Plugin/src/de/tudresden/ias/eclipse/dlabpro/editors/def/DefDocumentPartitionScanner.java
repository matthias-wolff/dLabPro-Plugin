/*
 * Created on 21.08.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.def;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonDocumentPartitionScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.rules.PreprocessorWithLeadingDollarRule;

/**
 * @author Christian Feig
 * @deprecated just used from deprecated class
 *             {@link de.tudresden.ias.eclipse.dlabpro.editors.def.DefDocumentProvider}
 */
public class DefDocumentPartitionScanner extends CommonDocumentPartitionScanner
{

  private static final IToken preprocessorWithLeading$Token = new Token(PREPROCESSOR_WITH_LEADING$);
  private final String[]      PARTITION_TYPES               =
                                                            { PREPROCESSOR_WITH_LEADING$, P_CLR_KEYWORD };

  public DefDocumentPartitionScanner()
  {
    super();
  }

  protected List createRules()
  {
    List rules = super.createRules();
    rules.add(new PreprocessorWithLeadingDollarRule(preprocessorWithLeading$Token));
    return rules;
  }

  public String[] getPartitionTypes()
  {
    String[] superTypes = super.getPartitionTypes();
    ArrayList list = new ArrayList();
    for (int i = 0; i < superTypes.length; i++)
      list.add(superTypes[i]);
    for (int i = 0; i < PARTITION_TYPES.length; i++)
      list.add(PARTITION_TYPES[i]);
    String[] returnArray = new String[list.size()];
    list.toArray(returnArray);
    return returnArray;
  }
}
