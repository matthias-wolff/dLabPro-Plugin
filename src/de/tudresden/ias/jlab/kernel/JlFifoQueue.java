/* jLab type JlFifoQueue
 * - Synchronized FIFO queue
 *
 * AUTHOR  : Matthias Wolff
 * UPDATE  : $Date: 2011-11-11 16:31:20 +0100 (Fr, 11 Nov 2011) $, $Author: wolff $
 *           $Revision: 171 $
 * PACKAGE : de.tudresden.ias.jlab.kernel
 * RCS-ID  : $Id: JlFifoQueue.java 171 2011-11-11 15:31:20Z wolff $
 */

package de.tudresden.ias.jlab.kernel;

import java.util.Vector;

/**
 * @author Matthias Wolff
 */
public abstract class JlFifoQueue extends JlAsyncObservable implements Runnable
{
  public static final int HEAD         = 0;     // Index of the first object in the queue
  public static final int TAIL         = -1;    // Index of the last object in the queue
  private Vector<Object>  iQueue       = null;  // Object queue
  private Thread          iProcessor   = null;  // Processor thread
  private int             nLength      = 0;     // Maximal length of queue
  private boolean         bRun         = false; // Processor thread's run flag
  private boolean         bBypass      = false; // Bypass flag
  private int             nChanged     = 0;     // Incr. by put, decr. after call to process
  protected JlFifoQueue   iOutputQueue = null;  // Output queue

  /**
   * Creates and starts a new FIFO object queue.
   * 
   * @param nCapacity
   *          The maximal number of objects to be held in the queue (0 for infinite).
   */
  public JlFifoQueue(int nCapacity)
  {
    this(nCapacity, null);
  }

  /**
   * Creates and starts a new FIFO object queue.
   * 
   * @param nCapacity
   *          The maximal number of objects to be held in the queue (0 for infinite).
   * @param iOutputQueue
   *          The output queue (may be <code>null</code>)
   */
  public JlFifoQueue(int nCapacity, JlFifoQueue iOutputQueue)
  {
    this.iQueue = new Vector<Object>(nCapacity);
    this.nLength = nCapacity;
    this.iOutputQueue = iOutputQueue;
    start();
  }

  /**
   * Starts the FIFO object queue. When started, the queue will invoke the
   * <code>process</code> method whenever a new object was enqueued using the
   * <code>put</code> method.
   */
  public void start()
  {
    if (iProcessor != null && iProcessor.isAlive()) return;
    bRun = true;
    // nChanged = 0;
    iProcessor = new Thread(this);
    iProcessor.start();
  }

  /**
   * Stops the FIFO object queue. The method blocks (for at most 1000 milliseconds) until the queue thread terminates.
   * After the queue thread has terminated, the method calls the <code>process</code> method with the argument
   * <code>bFlush=true</code>. After calling <code>process</code> the object queue is destroyed.
   */
  public void stop()
  {
    if (iProcessor == null) return;
    bRun = false;
    iProcessor.interrupt();
    try
    {
      iProcessor.join(1000);
    }
    catch (InterruptedException e)
    {
    }
    iProcessor = null;
    flush();
  }

  /**
   * Changes the processing priority of this queue.
   * 
   * @param nNewPriority
   *          Priority the set processor thread to
   */
  public void setPriority(int nNewPriority)
  {
    if (iProcessor == null) return;
    iProcessor.setPriority(nNewPriority);
  }

  /**
   * Sets a new output queue.
   * 
   * @param iOutputQueue
   *          The new output queue (may be <code>null</code>.
   */
  public void setOutputQueue(JlFifoQueue iOutputQueue)
  {
    this.iOutputQueue = iOutputQueue;
  }

  /**
   * Returns the current output queue.
   * 
   * @return The output queue
   */
  public JlFifoQueue getOutputQueue()
  {
    return this.iOutputQueue;
  }
  
  /**
   * Flushes the FIFO object queue. If (and only if) there are objects left in the queue, the method calls
   * <code>process</code> with the <code>bFlush</code> argument set to <code>true</code> and blocks until
   * <code>process</code> returns.
   */
  public synchronized void flush()
  {
    if (length() == 0) return;
    process(true);
    clear();
  }

  /**
   * Appends one object to the queue.
   * 
   * @param obj
   *          The object to append
   */
  public synchronized void put(Object obj) throws JlFifoQueueOverflowException
  {
    // JlInstance.log(" Put in queue "+this.getClass().getName());
    if (nLength > 0 && iQueue.size() >= nLength)
    {
      String sMsg =
        getClass().toString() + ": actual length=" + iQueue.size() +
        ", max length=" + nLength;
      throw new JlFifoQueueOverflowException(sMsg);
    }
    iQueue.addElement(obj);
    nChanged++;
    notifyAll();
  }
  
  /**
   * Retrieves one object from the queue.
   * 
   * @param nIndex
   *          Index of the object to be retrieved
   * @return The object
   */
  protected synchronized Object get(int nIndex)
  {
    if (nIndex==TAIL) nIndex = iQueue.size()-1;
    return (nIndex>=0&&nIndex<iQueue.size()) ? iQueue.elementAt(nIndex) : null;
  }

  /**
   * Returns the number of objects in the queue.
   * 
   * @return The length of the queue
   */
  public synchronized int length()
  {
    return iQueue.size();
  }

  /**
   * Removes one object from the queue.
   * 
   * @param nIndex
   *          Index of the object to be removed.
   */
  protected synchronized void remove(int nIndex)
  {
    if (nIndex == TAIL) nIndex = iQueue.size() - 1;
    if (nIndex>=0 && nIndex<iQueue.size()) iQueue.removeElementAt(nIndex);
  }

  /**
   * Clears the queue.
   */
  public synchronized void clear()
  {
    iQueue.clear();
    nChanged = 0;
  }

  /**
   * Processes the objects contained in the FIFO queue. The method will be called exactly once per enqueued object with
   * the argument <code>bFlush</code> set to <code>false</code>. The method will additionally be called exactly
   * once with the argument <code>bFlush</code> set to <code>true</code> when the queue is being stopped using the
   * <code>stop</code> method.
   * 
   * @param bFlush
   *          If <code>true</code>, the implementation must process all objects left in the queue. The queue and all
   *          enqueued objects will be destroyed immediately after <code>process</code> returns.
   */
  protected abstract void process(boolean bFlush);

  /**
   * This method is called instead of process if bypass mode is set by <code>setBypass()</code>. In bypass mode all 
   * objects in the queue are fed to the output queue without modification. 
   * 
   * @param bFlush true enables, false disables bypass 
   *
   * @see isBypass()  
   * @see setBypass()  
   */
  private void bypass(boolean bFlush)
  {
    while (length() > 0)
    {
      iOutputQueue.put(get(HEAD));
      remove(HEAD);
      if(!bFlush) break;
    }
  }

  /**
   * Waits until the queue has been changed and asynchroneously calls <code>process</code> or <code>bypass</code>. 
   */
  public void run()
  {
    while (bRun)
    {
      while (bRun && nChanged == 0)
        synchronized(this)
        {
          try
          {
            wait();
          }
          catch (InterruptedException e)
          {
          }
        }
      while (bRun && nChanged > 0)
      {
        nChanged--;
        if (bBypass) bypass(false);
        else process(false);
      }
    }
  }

  public boolean isBypass()
  {
    return bBypass;
  }

  public void setBypass(boolean bypass)
  {
    bBypass = bypass;
  }
}

/* EOF */