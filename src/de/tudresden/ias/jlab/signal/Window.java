// jLab

package de.tudresden.ias.jlab.signal;

import java.lang.reflect.Field;

import de.tudresden.ias.jlab.kernel.JlMath;


/**
 * This class supplies windowing functions for spectral analysis.
 * 
 * @author Matthias Wolff, BTU Cottbus
 */
public class Window
{
  public static final int RECTANGLE = 0;
  public static final int TRIANGLE  = 1;
  public static final int BLACKMAN  = 2;
  public static final int HAMMING   = 3;
  public static final int HANN      = 4;
  public static final int GAUSS     = 5;
  public static final int SYMEXP    = 6;
  public static final int BARTLETT  = 7;

  /**
   * Returns a windowing function.
   * 
   * @param nType
   *          The window type, one of the <code>Window.XXX</code> constants.
   * @param nSize
   *          The window size in samples.
   * @param bNorm
   *          If <code>true</code>, the window's effective value will be normalized to 1, otherwise
   *          the window's maximal amplitude will be one
   * @return An array with <code>nSize</code> elements containing the window.
   */
  public static double[] get(int nType, int nSize, boolean bNorm)
  {
    double[] aWin = new double[nSize];
    double   c    = 2*Math.PI/(nSize-1);
    
    switch (nType)
    {
    case TRIANGLE:
      for (int i=0; i<nSize/2.; i++)
      {
        aWin[i        ] = c*i;
        aWin[nSize-1-i] = aWin[i];
      }
      break;
    case BLACKMAN:
      for (int i=0; i<nSize; i++)
        aWin[i] = 0.42-0.5*Math.cos(i*c)+0.08*Math.cos(2*i*c);
      break;
    case HAMMING:
      for (int i=0; i<nSize; i++)
        aWin[i] = 0.54-0.46*Math.cos(i*c);
      break;
    case HANN:
      for (int i=0; i<nSize; i++)
        aWin[i] = 0.5-0.5*Math.cos(i*c);
      break;
    case GAUSS:
      c = 1./(nSize/2);
      for (int i=0; i<nSize/2.; i++)
      {
        aWin[(nSize/2)+i  ] = Math.exp(-(2*Math.pow(i*c,2)));
        aWin[(nSize/2)-1-i] = aWin[(nSize/2)+i];
      }
      break;
    case SYMEXP:
      c = 1./(nSize/2);
      for (int i=0; i<nSize/2.; i++)
      {
        aWin[(nSize/2)+i  ] = Math.exp(-(i*c));
        aWin[(nSize/2)-1-i] = aWin[(nSize/2)+i];
      }
      break;
    case BARTLETT:
      for (int i=0; i<nSize/2; i++)
        aWin[i] = (double)i/(double)(nSize/2);
      for (int i=nSize/2; i<nSize; i++)
        aWin[i] = (double)(nSize-1-i)/(double)(nSize/2);     
      break;
    default:
      for (int i=0; i<nSize; i++)
        aWin[i]=1;
    }
    
    if (bNorm)
    {
      double nNorm = JlMath.rms(aWin);
      for (int i=0; i<nSize; i++) aWin[i]/=nNorm;
//      nNorm = 0.;
//      for (int i=0; i<nSize; i++) nNorm+=aWin[i]*aWin[i];
//      System.out.println("\n ::: Window eff. value: "+Math.sqrt(nNorm/nSize)+" :::");
    }

    return aWin;
  }

  /**
   * Returns the window type for a given window name.
   * 
   * @param sName
   *          The name (case insensitive).
   * @return The type (one of the <code>Window.XXX</code> constants) or -1 if name does not denote a
   *         window type.
   */
  public static int typeFromName(String sName)
  {
    if (sName==null) return -1;
    
    Field[] aField = Window.class.getDeclaredFields();
    for (int i=0; i<aField.length; i++)
      if (aField[i].getType().equals(int.class))
        if (aField[i].getName().equals(sName.toUpperCase()))
          try
          {
            return aField[i].getInt(null);
          }
          catch (IllegalAccessException e)
          {
            // Must not happen!
            e.printStackTrace();
          }

    return -1;
  }
}

// EOF
