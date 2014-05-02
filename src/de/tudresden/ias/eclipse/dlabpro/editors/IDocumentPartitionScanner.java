/*
 * Created on 21.08.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

/**
 * @author Xian
 * 
 */
public interface IDocumentPartitionScanner extends IPartitionTokenScanner
{
  public String[] getPartitionTypes();
}
