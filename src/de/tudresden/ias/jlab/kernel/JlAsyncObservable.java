package de.tudresden.ias.jlab.kernel;

import java.util.Observable;

/**
 * {@link Observable} providing a non-blocking
 * {@link #notifyObserversAsync(Object)} method.
 * 
 * @author Matthias Wolff
 */
public class JlAsyncObservable extends Observable
{
  protected class NotifyThread extends Thread
  {
    protected Object iArg;

    public NotifyThread(Object iArg)
    {
      this.iArg = iArg;
    }
    
    public void run()
    {
      notifyObservers(this.iArg);
    }
  }
  
  /**
   * Asynchroneous (non-blocking) implementation of the
   * {@link Observable#notifyObservers(Object)} method.
   * @param arg
   *          any object.
   */
  public void notifyObserversAsync(Object arg)
  {
    (new NotifyThread(arg)).start();
  }
  
}
