// jLab

package de.tudresden.ias.jlab.signal;

/**
 * This class provides utilities for (sound pressure) level computation.
 * 
 * @author Matthias Wolff
 */
public class Level
{
  /**
   * The reference sound pressure ({@value #REF_SOUND_PRESSURE} Pa).
   */
  public static final float REF_SOUND_PRESSURE = 2E-5f;
  
  // -- Amplitude-Level Conversions --
  
  /**
   * Computes the level of an amplitude value.
   * 
   * @param nAmp
   *          The amplitude value.
   * @param nRef
   *          The reference value (0 dB).
   * @param nMin
   *          The minimal level in dB.
   * @return The level.
   */
  public static double ampToLevel(double nAmp, double nRef, double nMin)
  {
    return Math.max(20*Math.log10(nAmp/nRef),nMin);
  }

  /**
   * Computes the level of an amplitude value. Equal to {@link #ampToLevel(double, double, double)
   * ampToLevel(nAmp,nRef,-96)}.
   * 
   * @param nAmp
   *          The amplitude value.
   * @param nRef
   *          The reference value (0 dB).
   * @return The level.
   */
  public static double ampToLevel(double nAmp, double nRef)
  {
    return ampToLevel(nAmp,nRef,-96);
  }
  
  /**
   * Computes the levels of an array amplitude values.
   * 
   * @param aAmp
   *          The array of amplitude values, will be overwritten with the level values.
   * @param nRef
   *          The reference value (0 dB).
   * @param nMin
   *          The minimal level in dB.
   * @return <code>aAmp</code> filled with the level values.
   */
  public static double[] ampToLevel(double[] aAmp, double nRef, double nMin)
  {
    if (aAmp==null) return null;
    for (int i=0; i<aAmp.length; i++)
      aAmp[i]=ampToLevel(aAmp[i],nRef,nMin);
    return aAmp;
  }
  
  /**
   * Computes the levels of an array amplitude values.
   * 
   * @param aAmp
   *          The array of amplitude values, will be overwritten with the level values.
   * @param nRef
   *          The reference value (0 dB).
   * @param nMin
   *          The minimal level in dB.
   * @return <code>aAmp</code> filled with the level values.
   */
  public static float[] ampToLevel(float[] aAmp, float nRef, float nMin)
  {
    if (aAmp==null) return null;
    for (int i=0; i<aAmp.length; i++)
      aAmp[i]=(float)ampToLevel(aAmp[i],nRef,nMin);
    return aAmp;
  }
  
  /**
   * Computes the levels of an array amplitude values. Equal to {@link #ampToLevel(double[], double, double)
   * ampToLevel(aAmp,nRef,-96)}.
   * 
   * @param aAmp
   *          The array of amplitude values, will be overwritten with the level values.
   * @param nRef
   *          The reference value (0 dB).
   * @return <code>aAmp</code> filled with the level values.
   */
  public static double[] ampToLevel(double[] aAmp, double nRef)
  {
    return ampToLevel(aAmp,nRef,-96);
  }
  
  /**
   * Computes the levels of an array amplitude values. Equal to {@link #ampToLevel(double[], double, double)
   * ampToLevel(aAmp,nRef,-96)}.
   * 
   * @param aAmp
   *          The array of amplitude values, will be overwritten with the level values.
   * @param nRef
   *          The reference value (0 dB).
   * @return <code>aAmp</code> filled with the level values.
   */
  public static float[] ampToLevel(float[] aAmp, float nRef)
  {
    return ampToLevel(aAmp,nRef,-96);
  }

  /**
   * Computes the amplitude value of a level.
   * 
   * @param nLevel
   *          The level.
   * @param nRef
   *          The reference value (0 dB).
   * @return The amplitude value.
   */
  public static double levelToAmp(double nLevel, double nRef)
  {
    return nRef*Math.pow(10,nLevel/20);
  }
  
  // -- Level-Level Conversions --

  /**
   * Converts the from ordinary to A-weighted amplitude level. This conversion is only meaningful
   * for levels of narrow-band signals.
   * 
   * @param nLevel
   *          The ordinary level
   * @param nFrequency
   *          The center frequency of the signal band.
   * @param nRef
   *          The reference value (0 dB)
   * @return The A-weighted level in dB(A)
   */
  public static double ampBbToDba(double nLevel, double nFrequency, double nRef)
  {
    double nAmp = levelToAmp(nLevel,nRef);
    nAmp *= weightingA(nFrequency);
    return ampToLevel(nAmp,nRef);
  }
  
  // -- Weighting Filters --

  /**
   * Returns the A-weighting factor at a given frequency.
   * 
   * @param f
   *          The frequency in Hz
   * @return The A-weighting factor.
   */
  public static double weightingA(double f)
  {
    final double a0 = Math.pow(10.,2./20); // Gain correction: +2 dB
    final double c1 = Math.pow(12200.0,2);
    final double c2 = Math.pow(   20.6,2);
    final double c3 = Math.pow(  107.7,2);
    final double c4 = Math.pow(  737.9,2);
    double       f2 = f*f;
    double       a1 = c1*f2*f2;
    double       a2 = (f2+c2)*(f2+c1)*Math.sqrt(f2+c3)*Math.sqrt(f2+c4);
    return a0*a1/a2;
  }
  
  /**
   * Returns the frequency response of an A-weighting filter.
   * 
   * @param nFinc
   *          The frequency increment between subsequent A-weights in Hz.   
   * @param nCount
   *          The number of A-weights to compute.
   * @return An array containing the A-weights.
   */
  public static double[] weightingA(double nFinc, int nCount)
  {
    if (nCount<=0) return new double[0];
    double[] aW = new double[nCount];
    double   f  = 0.;
    for (int i=0; i<aW.length; i++, f+=nFinc)
      aW[i] = weightingA(f);
    return aW;
  }

  /**
   * Returns the B-weighting factor at a given frequency.
   * 
   * @param f
   *          The frequency in Hz
   * @return The B-weighting factor.
   */
  public static double weightingB(double f)
  {
    final double a0 = Math.pow(10.,0.17/20); // Gain correction: +0.17 dB
    final double c1 = Math.pow(12200.0,2);
    final double c2 = Math.pow(   20.6,2);
    final double c3 = Math.pow(  158.5,2);
    double       f2 = f*f;
    double       a1 = c1*f2*f;
    double       a2 = (f2+c2)*(f2+c1)*Math.sqrt(f2+c3);
    return a0*a1/a2;
  }
  
  /**
   * Returns the frequency response of an B-weighting filter.
   * 
   * @param nFofs
   *          The frequency of the first B-weight in Hz.
   * @param nFinc
   *          The frequency increment between subsequent B-weights in Hz.   
   * @param nCount
   *          The number of B-weights to compute.
   * @return An array containing the B-weights.
   */
  public static double[] weightingB(double nFinc, int nCount)
  {
    if (nCount<=0) return new double[0];
    double[] aW = new double[nCount];
    double   f  = 0.;
    for (int i=0; i<aW.length; i++, f+=nFinc)
      aW[i] = weightingB(f);
    return aW;
  }

  /**
   * Returns the C-weighting factor at a given frequency.
   * 
   * @param f
   *          The frequency in Hz
   * @return The C-weighting factor.
   */
  public static double weightingC(double f)
  {
    final double a0 = Math.pow(10.,0.06/20); // Gain correction: +0.06 dB
    final double c1 = Math.pow(12200.0,2);
    final double c2 = Math.pow(   20.6,2);
    double       f2 = f*f;
    double       a1 = c1*f2;
    double       a2 = (f2+c2)*(f2+c1);
    return a0*a1/a2;
  }

  /**
   * Returns the frequency response of an C-weighting filter.
   * 
   * @param nFofs
   *          The frequency of the first C-weight in Hz.
   * @param nFinc
   *          The frequency increment between subsequent C-weights in Hz.   
   * @param nCount
   *          The number of C-weights to compute.
   * @return An array containing the C-weights.
   */
  public static double[] weightingC(double nFinc, int nCount)
  {
    if (nCount<=0) return new double[0];
    double[] aW = new double[nCount];
    double   f  = 0.;
    for (int i=0; i<aW.length; i++, f+=nFinc)
      aW[i] = weightingC(f);
    return aW;
  }
  
}

// EOF
