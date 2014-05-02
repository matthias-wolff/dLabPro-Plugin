/*
 * Created on 21.08.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * @author Christian Feig
 * 
 */
public class CommonDocumentProvider extends FileDocumentProvider
{

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createDocument(java.lang.Object)
   */
  protected IDocument createDocument(Object element) throws CoreException
  {
    IDocument document = super.createDocument(element);
    if (document != null)
    {
      IDocumentPartitionScanner scanner = getScanner();
      IDocumentPartitioner partitioner = new FastPartitioner(scanner, scanner.getPartitionTypes());
      document.setDocumentPartitioner(partitioner);
      partitioner.connect(document);
    }
    return document;
  }

  /**
   * @return the scanner to be used
   */
  protected IDocumentPartitionScanner getScanner()
  {
    return new CommonDocumentPartitionScanner();
  }
}
