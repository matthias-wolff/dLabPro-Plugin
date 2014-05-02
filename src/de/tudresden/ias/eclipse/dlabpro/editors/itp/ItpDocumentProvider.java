/*
 * Created on 23.08.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.itp;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonDocumentProvider;
import de.tudresden.ias.eclipse.dlabpro.editors.IDocumentPartitionScanner;

/**
 * this class implements the DocumentProvider for itp documents extending the common class
 * CommonDocumentProvider
 * 
 * @author Christian Feig
 */
public class ItpDocumentProvider extends CommonDocumentProvider
{
  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonDocumentProvider#getScanner()
   */
  protected IDocumentPartitionScanner getScanner()
  {
    return new ItpDocumentPartitionScanner();
  }
}
