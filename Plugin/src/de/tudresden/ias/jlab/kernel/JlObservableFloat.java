/* jLab type JlObservableFloat
 * - An observable float buffer
 *
 * AUTHOR  : Matthias Wolff
 * UPDATE  : $Date: 2007-08-07 11:01:10 +0200 (Di, 07 Aug 2007) $, $Author: wolff $
 *           $Revision: 121 $
 * PACKAGE : de.tudresden.ias.jlab.kernel
 * RCS-ID  : $Id: JlObservableFloat.java 121 2007-08-07 09:01:10Z wolff $
 */

package de.tudresden.ias.jlab.kernel;

/**
 * @author Matthias Wolff
 */
public class JlObservableFloat extends JlAsyncObservable
{
  protected float nValue = 0f;
  
  public void set(float n)
  {
    nValue=n;
    setChanged();
    notifyObserversAsync(null);
  }
  
  public float get()
  {
    return nValue;
  }
}

/* EOF */