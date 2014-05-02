/* jLab type JlDataLogger
 * - Log-to-JlData FIFO queue
 *
 * AUTHOR  : Matthias
 * UPDATE  : $Date: 2011-07-31 21:47:13 +0200 (So, 31 Jul 2011) $, $Author: wolff $
 *           $Revision: 162 $
 * PACKAGE : de.tudresden.ias.jlab.kernel
 * RCS-ID  : $Id: JlDataLogger.java 162 2011-07-31 19:47:13Z wolff $
 */

package de.tudresden.ias.jlab.kernel;

import java.lang.reflect.Array;

/**
 * Logs buffers into a <code>JlData</code> instance.
 */
public class JlDataLogger extends JlFifoQueue
{
  private static final long serialVersionUID = -4722433480133948870L;
  private JlData            iData            = null;
  private int               nComps           = 0;
  private int               nIncrement       = 0;

  public JlDataLogger(Class<?> cType, int nComps, int nCapacity)
  {
    super(nCapacity);
    this.iData = new JlData();
    if (nComps>0 && cType!=null)
    {
      this.nComps = nComps;
      iData.addNComps(cType, nComps);
    }
    setPriority(Thread.MIN_PRIORITY);
  }

  public JlDataLogger(int nCapacity)
  {
    this(null,0,nCapacity);
  }

  /**
   * Sets the memory reallocation increment for this data logger.
   * 
   * @param nIncrement
   *          Minimal number of records per reallocation
   */
  public void setIncrement(int nIncrement)
  {
    this.nIncrement = nIncrement;
  }
  
  protected void process(boolean bFlush)
  {
    while (length() > 0)
    {
      // Get buffer from queue
      Object aBuffer = get(HEAD);
      remove(HEAD);
      if (aBuffer == null)
      {
        //JlObject.log("\n ::: JlDataLogger.process - null :::");
        setChanged();
        notifyObserversAsync("EOS");
        continue;
      }

      if (aBuffer instanceof JlData)
      {
        // Append data instance
        JlData idSrc = (JlData)aBuffer;
        if (idSrc!=null)
          iData.cat(idSrc,0,idSrc.getLength(),nIncrement);
      }
      else
      {
        // Calculate and allocate required additional memory
        int nLength = Array.getLength(aBuffer);
        Class<?> cBufferType = aBuffer.getClass().getComponentType();
        Class<?> cDataType   = iData.getCompType(0);
        if (!cBufferType.equals(cDataType))
          throw new Error("Invalid buffer type (" +
            cBufferType.getSimpleName() + "[]) for this logger (should be " +
            cDataType.getSimpleName() + "[])");
        int nFR = iData.getLength();
        int nXR = nLength / nComps;
        if (nXR * nComps < nLength) nXR++;
        iData.allocate(nFR + Math.max(nXR,nIncrement));
        iData.setNRecs(nFR + nXR);
        //JlObject.log("\n ::: queLen=" + length() + ", bufLen=" + nLength +
        //  ", bufType=" + aBuffer.getClass().getSimpleName() +
        //  ", nFR=" + nFR + ", nXR=" + nXR + ", capacity=" +
        //  iData.getCapacity());
  
        // Copy data
        for (int nR = nFR, nB = 0; nR < nFR + nXR; nR++)
          for (int nC = 0; nC < nComps && nB < nLength; nC++, nB++)
            System.arraycopy(aBuffer, nB, iData.getComp(nC), nR, 1);
      }
    }
  }

  @Override
  public synchronized void flush()
  {
    if (length() == 0) return;
    process(true);
  }

  /**
   * Clears the FIFO queue and the log <code>JlData</code> instance.
   */
  public void clear()
  {
    super.clear();
    iData.allocate(0);
  }

  public JlData getData()
  {
    return iData;
  }
}

/* EOF */