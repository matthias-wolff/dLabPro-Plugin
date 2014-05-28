package de.tudresden.ias.jlab.kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.Timer;


public class JlDataStreamer extends Observable implements ActionListener
{
  private Timer       timer;
  private boolean     bPause;
  private JlFifoQueue target;
  private int         nBlockLength;
  private JlData      idSrc;
  private int         nComp;
  private int         nRec;
  
  /**
   * Initializes this data streamer. 
   * 
   * @param target
   *          The queue to stream the audio to
   * @param nBlockLength
   *          Number of records to stream at a time
   */
  public void init(JlFifoQueue target, int nBlockLength)
  {
    this.target       = target;
    this.nBlockLength = Math.max(nBlockLength,1);
  }

  // -- Getters and setters --
  
  /**
   * Returns <code>true</code> if this streamer is playing or paused.
   * 
   * @see #isPaused()
   */
  public boolean isActive()
  {
    if (timer==null) return false;
    return timer.isRunning();
  }
  
  /**
   * Returns <code>true</code> is this streamer is paused.
   * 
   * @see #isActive()
   */
  public boolean isPaused()
  {
    if (timer==null) return false;
    return bPause;
  }
  
  // -- Operations --

  /**
   * Streams a {@link JlData} instance.
   * 
   * @param idSrc
   *          The data to be streamed
   * @param nComp
   *          The zero-based index in <code>idData</code> if the component to
   *          be streamed 
   * @throws IllegalThreadStateException
   *          if streaming is already in progress
   * @throws IllegalArgumentException
   *          if <code>nComp</code> is not a valid component index 
   */
  public void stream(JlData idSrc, int nComp)
  throws IllegalThreadStateException, IllegalArgumentException
  {
    JlObject.log("\n   JlDataStreamer.stream(idSrc,"+nComp+");");
    JlObject.log("\n   - Block length      : "+this.nBlockLength+" records");
    
    if (idSrc==null) return;
    if (nComp<0 || nComp>=idSrc.getDimension())
      throw new IllegalArgumentException("Invalid component index "+nComp);
    if (timer!=null)
      throw new IllegalThreadStateException("Already streaming");
    
    this.bPause = false;
    this.idSrc  = idSrc;
    this.nComp  = nComp;
    this.nRec   = 0;
    int nDelay  = 0;
    if ("s".equals(new String(idSrc.runit)))
      nDelay = (int)(this.nBlockLength*idSrc.rinc*1000);
    else if ("ms".equals(new String(idSrc.runit)))
      nDelay = (int)(this.nBlockLength*idSrc.rinc);
    else
    {
      JlObject.WARNING("Cannot stream in real time (unknown runit \"" + 
        (new String(idSrc.runit)) + "\")");
      nDelay = 1;
    }
    if (nDelay<=0)
    {
      JlObject.WARNING("Cannot stream in real time. Increase buffer length!");
      nDelay = 1;
    }
    JlObject.log("\n   - Timer delay       : "+nDelay+" ms");
    timer = new Timer(nDelay,this);
    timer.start();
    setChanged();
    notifyObservers();
  }

  /**
   * Stops streaming;
   */
  public synchronized void stop()
  {
    JlObject.log("\n\n   JlDataStreamer.stop()");
    if (timer==null) return;
    timer.stop();
    if (target!=null) target.put(null);
    timer = null;
    idSrc = null;
    nComp = -1;
    nRec = -1;
    bPause = false;
    setChanged();
    notifyObservers();
  }

  /**
   * Pauses this streamer. The method does nothing if the streamer
   * <ul>
   *   <li>is not active (i.e. if {@link #isActive()} returns <code>false</code>) or</li>
   *   <li>is already paused (i.e. if {@link #isPaused()} returns <code>true</code>).</li>
   * </ul>
   */
  public void pause()
  {
    if (!isActive()) return;
    if ( isPaused()) return;
    this.bPause = true;
    setChanged();
    notifyObservers();
  }

  /**
   * Resumes this streamer. The method does nothing if the streamer
   * <ul>
   *   <li>is not active (i.e. if {@link #isActive()} returns <code>false</code>) or</li>
   *   <li>is not paused (i.e. if {@link #isPaused()} returns <code>false</code>).</li>
   * </ul>
   */
  public void resume()
  {
    if (!isActive()) return;
    if (!isPaused()) return;
    this.bPause = false;
    setChanged();
    notifyObservers();
  }
  
  // -- Implementation of the ActionListener interface --
  
  public void actionPerformed(ActionEvent e)
  {
    if (timer==null || idSrc==null) return;

    if (!bPause)
      synchronized (this)
      {
        // Stream data
        int nCount = Math.min(nBlockLength,idSrc.getLength()-nRec);
        if (target!=null)
          target.put(idSrc.selectRecs(nRec,nCount).getComp(nComp));
        nRec += nBlockLength;
        
        // At the end of data
        if (nRec>=idSrc.getLength()) stop();
      }
  }
}
