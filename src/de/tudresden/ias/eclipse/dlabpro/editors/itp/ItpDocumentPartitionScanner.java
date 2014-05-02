
package de.tudresden.ias.eclipse.dlabpro.editors.itp;

import java.util.List;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonDocumentPartitionScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.rules.FormularRule;

/**
 * this class implements the DocumentPartitionScanner for itp documents extending the common class
 * CommonDocumentPartitionScanner
 * 
 * @author Christian Feig
 * 
 */
public class ItpDocumentPartitionScanner extends CommonDocumentPartitionScanner
{

  private static final IToken formelInterpreterToken = new Token(P_CLR_FORMULA);

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.IDocumentPartitionScanner#getPartitionTypes()
   */
  public String[] getPartitionTypes()
  {
    String[] array = super.getPartitionTypes();
    String[] returnArray = new String[array.length + 1];
    for (int i = 0; i < array.length; i++)
      returnArray[i] = array[i];
    returnArray[array.length] = P_CLR_FORMULA;
    return returnArray;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonDocumentPartitionScanner#createRules()
   */
  protected List createRules()
  {
    List list = super.createRules();
    list.add(new FormularRule(formelInterpreterToken));
    return list;
  }
}
