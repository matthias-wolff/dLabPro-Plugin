// jLab

package de.tudresden.ias.jlab.signal;

/**
 * Basic FFT-based signal processing.
 * 
 * @author Matthias Wolff
 */
public class FFT
{
  // -- Public methods --
  
  /**
   * Bare FFT implementation.
   * <p>
   * This method is based on code by Orlando Selenu (see <a href=
   * "http://www.wikijava.org/wiki/The_Fast_Fourier_Transform_in_Java_(part_1)" >http
   * ://www.wikijava.org/wiki/The_Fast_Fourier_Transform_in_Java_(part_1)</a>).
   * </p>
   * 
   * @param xReal
   *          Real part of input, overwritten with real part of output
   * @param xImag
   *          Imaginary part of input, overwritten with imaginary part of output
   * @param bDirect
   *          <code>true</code> for FFT, <code>false</code> for inverse FFT
   * @throws SignalProcessingError
   *           Thrown on any problems, message contains details
   */
  public static void fft(double[] xReal, double[] xImag, boolean bDirect)
  {
    int nDim = xReal.length; 
    int nOrd = (int)(Math.log(nDim)/Math.log(2.));
    if (1<<nOrd!=nDim)
      throw new SignalProcessingError("xReal.length (" + xReal.length
        + ") is not a power of 2");
    if (xImag.length!=nDim)
      throw new SignalProcessingError("xImag.length (" + xImag.length
        + ")!=xReal.length (" + xReal.length + ")");

    int n2 = nDim/2;
    int nu1 = nOrd-1;
    double tReal, tImag, p, arg, c, s;

    double constant;
    if (bDirect)
      // Direct FFT
      constant = -2f*Math.PI;
    else
      // Inverse FFT
      constant = 2f*Math.PI;

    // First phase - calculation
    int k = 0;
    for (int l = 1; l<=nOrd; l++)
    {
      while (k<nDim)
      {
        for (int i = 1; i<=n2; i++)
        {
          p = bitReversal(k>>nu1,nOrd);
          arg = constant*p/nDim;
          c = Math.cos(arg);
          s = Math.sin(arg);
          tReal = xReal[k+n2]*c+xImag[k+n2]*s;
          tImag = xImag[k+n2]*c-xReal[k+n2]*s;
          xReal[k+n2] = xReal[k]-tReal;
          xImag[k+n2] = xImag[k]-tImag;
          xReal[k] += tReal;
          xImag[k] += tImag;
          k++;
        }
        k += n2;
      }
      k = 0;
      nu1--;
      n2 /= 2;
    }

    // Second phase - recombination
    k = 0;
    int r;
    while (k<nDim)
    {
      r = bitReversal(k,nOrd);
      if (r>k)
      {
        tReal = xReal[k];
        tImag = xImag[k];
        xReal[k] = xReal[r];
        xImag[k] = xImag[r];
        xReal[r] = tReal;
        xImag[r] = tImag;
      }
      k++;
    }

    // Third phase - normalization
    for (int i = 0; i<nDim; i++)
    {
      xReal[i] /= nDim;
      xImag[i] /= nDim;
    }
  }

  /**
   * Computes the technical amplitude spectrum <i>A</i>(<i>f</i>). "Technical" means that amplitude
   * values will only be calculated for non-negative frequencies. Except for the direct component,
   * the amplitude of the technical spectrum is twice the amplitude of the mathematical spectrum:
   * <p style="margin-left:1cm">
   * <i>A</i>(<i>f</i>) = |<i><u>X</u></i>(<i>f</i>)| + |<i><u>X</u></i>(-<i>f</i>)| = 2
   * |<i><u>X</u></i>(<i>f</i>)|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<i>f</i>&gt;0)<br>
   * <i>A</i>(0) = |<i><u>X</u></i>(0)|
   * </p>
   * 
   * @param x
   *          The signal to be analyzed. <code>x.length</code> must be a power of 2, otherwise
   *          a {@link SignalProcessingException} will be thrown.
   * @return A newly allocated <code>double</code> array with <code>x.length</code>/2 elements
   *         containing the technical amplitude spectrum.
   * @throws SignalProcessingError
   *           Thrown on any problems, message contains details
   */
  public static double[] amp(double[] x)
  {
    double[] xImag = new double[x.length]; 
    for (int i=0; i<x.length; i++) xImag[i] = 0.;
    fft(x,xImag,true);
    double[] y = new double[x.length/2];
    y[0]=Math.sqrt(x[0]*x[0]+xImag[0]*xImag[0]);
    for (int i=1; i<y.length; i++)
      y[i]=2*Math.sqrt((x[i]*x[i]+xImag[i]*xImag[i]));
    return y;
  }

  /**
   * TODO: Write JavaDoc
   * 
   * @param x
   * @return
   */
  public static float[] amp(float[] x)
  {
    double[] xd = new double[x.length];
    for (int i=0; i<xd.length; i++) xd[i]=x[i];
    double[] yd = amp(xd);
    float[] y = new float[yd.length];
    for (int i=0; i<y.length; i++) y[i]=(float)yd[i];
    return y;
  }

  /**
   * TODO: Write JavaDoc
   * 
   * @param x
   * @return
   */
  public static double[] ampSym(double[] x)
  {
    double[] xImag = new double[x.length]; 
    for (int i=0; i<x.length; i++) xImag[i] = 0.;
    fft(x,xImag,true);
    double[] y = new double[x.length];
    for (int i=0; i<y.length/2; i++)
    {
      y[y.length/2+i]=Math.sqrt((x[i]*x[i]+xImag[i]*xImag[i]));
      y[y.length/2-i]=y[y.length/2+i];
    }
    return y;
  }

  /**
   * TODO: Write JavaDoc
   * 
   * @param x
   * @return
   */
  public static float[] ampSym(float[] x)
  {
    double[] xd = new double[x.length];
    for (int i=0; i<xd.length; i++) xd[i]=x[i];
    double[] yd = ampSym(xd);
    float[] y = new float[yd.length];
    for (int i=0; i<y.length; i++) y[i]=(float)yd[i];
    return y;
  }

  /**
   * Computes the technical effective value spectrum <i>E</i>(<i>f</i>). "Technical" means that
   * effective values will only be calculated for non-negative frequencies. Except for the direct
   * component, the effective value of the technical spectrum is twice the effective value of the
   * mathematical spectrum:
   * <p style="margin-left:1cm">
   * <i>E</i>(<i>f</i>) = 1/&radic;<span style="text-decoration:overline">2</span> <font
   * size="3">[</font> |<i><u>X</u></i>(<i>f</i>)| + |<i><u>X</u></i>(-<i>f</i>)| <font
   * size="3">]</font> = &radic;<span style="text-decoration:overline">2</span>
   * |<i><u>X</u></i>(<i>f</i>)|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<i>f</i>&gt;0)<br>
   * <i>E</i>(0) = |<i><u>X</u></i>(0)|
   * </p>
   * 
   * @param x
   *          The signal to be analyzed. <code>x.length</code> must be a power of 2, otherwise a
   *          {@link SignalProcessingException} will be thrown.
   * @return A newly allocated <code>double</code> array with <code>x.length</code>/2 elements
   *         containing the technical effective value spectrum.
   * @throws SignalProcessingError
   *           Thrown on any problems, message contains details
   */
  public static double[] evs(double[] x)
  {
    double[] xImag = new double[x.length]; 
    for (int i=0; i<x.length; i++) xImag[i] = 0.;
    fft(x,xImag,true);
    double[] y = new double[x.length/2];
    y[0]=Math.sqrt(x[0]*x[0]+xImag[0]*xImag[0]);
    for (int i=1; i<x.length/2; i++)
      y[i]=Math.sqrt(2*(x[i]*x[i]+xImag[i]*xImag[i]));
    return y;
  }

  /**
   * Float wrapper of {@link #evs(double[])}. Internal computation is done with double precision.
   * 
   * @param x
   *          The signal to be analyzed. <code>x.length</code> must be a power of 2, otherwise a
   *          {@link SignalProcessingException} will be thrown.
   * @return A newly allocated <code>float</code> array with <code>x.length</code>/2 elements
   *         containing the technical effective value spectrum.
   * @throws SignalProcessingError
   *           Thrown on any problems, message contains details
   */
  public static float[] evs(float[] x)
  {
    double[] xd = new double[x.length];
    for (int i=0; i<xd.length; i++) xd[i]=x[i];
    double[] yd = evs(xd);
    float[] y = new float[yd.length];
    for (int i=0; i<y.length; i++) y[i]=(float)yd[i];
    return y;
  }

  /**
   * TODO: Write JavaDoc!
   * 
   * @param x
   * @return
   */
  public static double[] evsSym(double[] x)
  {
    double[] xImag = new double[x.length]; 
    for (int i=0; i<x.length; i++) xImag[i] = 0.;
    fft(x,xImag,true);
    double[] y = new double[x.length];
    y[y.length/2]=Math.sqrt((x[0]*x[0]+xImag[0]*xImag[0]));
    for (int i=1; i<y.length/2; i++)
    {
      y[y.length/2+i]=Math.sqrt((x[i]*x[i]+xImag[i]*xImag[i])/2);
      y[y.length/2-i]=y[y.length/2+i];
    }
    return y;
  }

  // -- Workers --
  
  /**
   * Bit-reversal for FFT.
   * 
   * @author Orlando Selenu
   */
  private static int bitReversal(int j, int nu)
  {
    int j2;
    int j1 = j;
    int k = 0;
    for (int i = 1; i<=nu; i++)
    {
      j2 = j1/2;
      k = 2*k+j1-2*j2;
      j1 = j2;
    }
    return k;
  }

}

// EOF
