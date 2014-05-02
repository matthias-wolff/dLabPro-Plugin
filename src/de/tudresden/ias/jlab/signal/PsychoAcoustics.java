// jLab

package de.tudresden.ias.jlab.signal;

/**
 * This class provides psychoacoustic functions.
 * 
 * @author Matthias Wolff
 */
public class PsychoAcoustics
{

  /**
   * Returns the absolute threshold of hearing (ATH) at a given frequency.
   * 
   * @param f
   *          The frequency in Hz to return the ATH for.
   * @return The absolute threshold of hearing measured in dB.
   */
  public static double getATH(double f)
  {
    f /= 1000.; // Convert f to kHz
    if (f<=0) return 0.;
    return 3.64*Math.pow(f,-0.8)
           - 6.5*Math.exp(-0.6*(f-3.3)*(f-3.3))
           + 0.001*f*f*f*f;
  }

  /**
   * Returns the masked threshold at a given frequency for a given sinusoidal masker.
   * 
   * @param f
   *          The frequency in Hz to return the ATH for.
   * @param fm
   *          The frequency of the masking tone in Hz.
   * @param Lm
   *          The level of the masking tone in dB.
   * @return The masked threshold measured in dB.
   */
  public static double getMT(double f, double fm, double Lm)
  {
    double nS  = f<fm ? 27. : (24. + 230./f - 0.2*Lm);
    double nMt = Lm - Math.abs(freqToTonheit(f)-freqToTonheit(fm))*nS;
    return Math.max(nMt,getATH(f));
  }

  /**
   * Converts from frequency (in Hz) to honheit (in Bark).
   * 
   * @param f
   *          The frequency in Hz.
   * @return The tonheit in Bark.
   */
  public static double freqToTonheit(double f)
  {
    return 13.*Math.atan(0.00076*f)+3.5*Math.atan((f/7500.)*(f/7500.));
  }
  
}

// EOF

