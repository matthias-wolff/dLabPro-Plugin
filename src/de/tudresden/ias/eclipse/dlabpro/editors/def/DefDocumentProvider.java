/*
 * Created on 21.08.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors.def;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonDocumentProvider;
import de.tudresden.ias.eclipse.dlabpro.editors.IDocumentPartitionScanner;

/**
 * @author Christian Feig
 * @deprecated never used locally
 */
public class DefDocumentProvider extends CommonDocumentProvider
{

  protected IDocumentPartitionScanner getScanner()
  {
    return new DefDocumentPartitionScanner();
  }
}
