/* jLab type JlFifoQueueOverflowException
 * - Thrown on FIFO queue overflows 
 *
 * AUTHOR  : Matthias wolff
 * UPDATE  : $Date: 2011-07-31 21:47:13 +0200 (So, 31 Jul 2011) $, $Author: wolff $
 *           $Revision: 162 $
 * PACKAGE : de.tudresden.ias.jlab.kernel
 * RCS-ID  : $Id: JlFifoQueueOverflowException.java 162 2011-07-31 19:47:13Z wolff $
 */

package de.tudresden.ias.jlab.kernel;

/**
 * Thrown on FIFO queue overflows.
 * 
 * @author Matthias Wolff
 */
public class JlFifoQueueOverflowException extends RuntimeException
{
  private static final long serialVersionUID = -2764659432091608088L;

  public JlFifoQueueOverflowException()
  {
    super();
  }

  public JlFifoQueueOverflowException(String message)
  {
    super(message);
  }
}

/* EOF */