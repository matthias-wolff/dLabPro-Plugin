// jLab

package de.tudresden.ias.jlab.kernel;

/**
 * The class <code>JlMath</code> provides extended mathematical methods such aggregation operations.
 * <p><b style="color:red">NOTE:</b> This implementation is only a stub. More methods are to come on
 * demand.</p>
 * 
 * @author Matthias Wolff, BTU Cottbus
 */
public class JlMath
{

  // -- Simple aggregations --
  
  /**
   * Returns the minimum value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The minimum.
   */
  public static double min(double[] a)
  {
    return aggregate(Op.MIN,a,0,0,-1);
  }
  
  /**
   * Returns the minimum value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The minimum.
   */
  public static float min(float[] a)
  {
    return aggregate(Op.MIN,a,0,0,-1);
  }

  /**
   * Returns the maximum value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The maximum.
   */
  public static double max(double[] a)
  {
    return aggregate(Op.MAX,a,0,0,-1);
  }

  /**
   * Returns the maximum value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The maximum.
   */
  public static float max(float[] a)
  {
    return aggregate(Op.MAX,a,0,0,-1);
  }
  
  /**
   * Returns the minimum absolute value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The minimum absolute value.
   */
  public static double absmin(double[] a)
  {
    return aggregate(Op.ABSMIN,a,0,0,-1);
  }
  
  /**
   * Returns the minimum absolute value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The minimum absolute value.
   */
  public static float absmin(float[] a)
  {
    return aggregate(Op.ABSMIN,a,0,0,-1);
  }
  
  /**
   * Returns the maximum absolute value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The maximum absolute value.
   */
  public static double absmax(double[] a)
  {
    return aggregate(Op.ABSMAX,a,0,0,-1);
  }
  
  /**
   * Returns the maximum absolute value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The maximum absolute value.
   */
  public static float absmax(float[] a)
  {
    return aggregate(Op.ABSMAX,a,0,0,-1);
  }
  
  /**
   * Returns the index of the minimum value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The index of the minimum value.
   */
  public static int imin(double[] a)
  {
    return (int)aggregate(Op.IMIN,a,0,0,-1);
  }
  
  /**
   * Returns the index of the minimum value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The index of the minimum value.
   */
  public static int imin(float[] a)
  {
    return (int)aggregate(Op.IMIN,a,0,0,-1);
  }
  
  /**
   * Returns the index of the maximum value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The index of the maximum value.
   */
  public static int imax(double[] a)
  {
    return (int)aggregate(Op.IMAX,a,0,0,-1);
  }
  
  /**
   * Returns the index of the maximum value of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The index of the maximum value.
   */
  public static int imax(float[] a)
  {
    return (int)aggregate(Op.IMAX,a,0,0,-1);
  }
  
  /**
   * Returns the sum of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The sum.
   */
  public static double sum(double[] a)
  {
    return aggregate(Op.SUMPOW,a,1,0,-1);
  }
  
  /**
   * Returns the sum of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The sum.
   */
  public static float sum(float[] a)
  {
    return aggregate(Op.SUMPOW,a,1,0,-1);
  }

  /**
   * Returns the <code>p</code>-th order norm of array elements. Special cases:
   * <ul>
   * <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul>
   * <h3>Note:</h3>
   * <p> Not all orders <code>p</code> yield a "norm" in the mathematical sense. However, as the
   * obtained values can still be useful, the method does not reject such orders.</p>
   * 
   * @param a
   *          The array.
   * @param p
   *          The order (exponent) of the norm
   * @return The <code>p</code>-th order norm.
   */
  public static double pNorm(double[] a, double p)
  {
    return aggregate(Op.ROOTSUMPOW,a,p,0,-1);
  }

  /**
   * Returns the <code>p</code>-th order norm of array elements. Special cases:
   * <ul>
   * <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul>
   * <h3>Note:</h3>
   * <p> Not all orders <code>p</code> yield a "norm" in the mathematical sense. However, as the
   * obtained values can still be useful, the method does not reject such orders.</p>
   * 
   * @param a
   *          The array.
   * @param p
   *          The order (exponent) of the norm
   * @return The <code>p</code>-th order norm.
   */
  public static float pNorm(float[] a, float p)
  {
    return aggregate(Op.ROOTSUMPOW,a,p,0,-1);
  }
  
  /**
   * Returns the mean (average) of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The mean.
   */
  public static double mean(double[] a)
  {
    return aggregate(Op.MEANPOW,a,1,0,-1);
  }
  
  /**
   * Returns the mean (average) of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The mean.
   */
  public static float mean(float[] a)
  {
    return aggregate(Op.MEANPOW,a,1,0,-1);
  }

  /**
   * Returns the standard deviation of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The standard deviation.
   */
  public static double sdev(double[] a)
  {
    return aggregate(Op.CMOMENT,a,2,0,-1);
  }

  /**
   * Returns the standard deviation of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The standard deviation.
   */
  public static float sdev(float[] a)
  {
    return aggregate(Op.CMOMENT,a,2,0,-1);
  }

  /**
   * Returns the root mean square of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The root mean square.
   */
  public static double rms(double[] a)
  {
    return aggregate(Op.ROOTMEANPOW,a,2,0,-1);
  }

  /**
   * Returns the root mean square of array elements. Special cases:
   * <ul>
   *   <li>If <code>a</code> is <code>null</code>, the result is 0.</li>
   * </ul> 
   * 
   * @param a The array.
   * @return The root mean square.
   */
  public static float rms(float[] a)
  {
    return aggregate(Op.ROOTMEANPOW,a,2,0,-1);
  }
  
  // -- Moving aggregations --

  /**
   * Computes the moving mean (average) of array elements. The returned array has the same size as
   * the input array. Special cases:
   * <ul>
   * <li>If <code>a</code> is <code>null</code>, the result is <code>null</code>.</li>
   * <li>If <code>a</code> is an empty array, result is an empty array as well.</li>
   * </ul>
   * 
   * @param a
   *          The array.
   * @param n
   *          The aggregation window size (number of array elements to aggregate for each output
   *          value).
   * @return A new allocated array containing the moving mean values.
   */
  public static double[] movingMean(double[] a, int n)
  {
    return movingSum(Op.MEANPOW,a,1,n);
  }

  /**
   * Computes the moving mean (average) of array elements. The returned array has the same size as
   * the input array. Special cases:
   * <ul>
   * <li>If <code>a</code> is <code>null</code>, the result is <code>null</code>.</li>
   * <li>If <code>a</code> is an empty array, result is an empty array as well.</li>
   * </ul>
   * 
   * @param a
   *          The array.
   * @param n
   *          The aggregation window size (number of array elements to aggregate for each output
   *          value).
   * @return A new allocated array containing the moving mean values.
   */
  public static float[] movingMean(float[] a, int n)
  {
    return movingSum(Op.MEANPOW,a,1,n);
  }

  /**
   * Computes the moving standard deviation of array elements. The returned array has the same size
   * as the input array. Special cases:
   * <ul>
   * <li>If <code>a</code> is <code>null</code>, the result is <code>null</code>.</li>
   * <li>If <code>a</code> is an empty array, result is an empty array as well.</li>
   * </ul>
   * 
   * @param a
   *          The array.
   * @param n
   *          The aggregation window size (number of array elements to aggregate for each output
   *          value).
   * @return A new allocated array containing the moving standard deviation values.
   */
  public static double[] movingSdev(double[] a, int n)
  {
    return movingAggregate(Op.CMOMENT,a,2,n);
  }

  /**
   * Computes the moving standard deviation of array elements. The returned array has the same size
   * as the input array. Special cases:
   * <ul>
   * <li>If <code>a</code> is <code>null</code>, the result is <code>null</code>.</li>
   * <li>If <code>a</code> is an empty array, result is an empty array as well.</li>
   * </ul>
   * 
   * @param a
   *          The array.
   * @param n
   *          The aggregation window size (number of array elements to aggregate for each output
   *          value).
   * @return A new allocated array containing the moving standard deviation values.
   */
  public static float[] movingSdev(float[] a, int n)
  {
    return movingAggregate(Op.CMOMENT,a,2,n);
  }

  /**
   * Computes the moving root mean square of array elements. The returned array has the same size as
   * the input array. Special cases:
   * <ul>
   * <li>If <code>a</code> is <code>null</code>, the result is <code>null</code>.</li>
   * <li>If <code>a</code> is an empty array, result is an empty array as well.</li>
   * </ul>
   * 
   * @param a
   *          The array.
   * @param n
   *          The aggregation window size (number of array elements to aggregate for each output
   *          value).
   * @return A new allocated array containing the moving root mean square values.
   */
  public static double[] movingRms(double[] a, int n)
  {
    return movingSum(Op.ROOTMEANPOW,a,2,n);
  }

  /**
   * Computes the moving root mean square of array elements. The returned array has the same size as
   * the input array. Special cases:
   * <ul>
   * <li>If <code>a</code> is <code>null</code>, the result is <code>null</code>.</li>
   * <li>If <code>a</code> is an empty array, result is an empty array as well.</li>
   * </ul>
   * 
   * @param a
   *          The array.
   * @param n
   *          The aggregation window size (number of array elements to aggregate for each output
   *          value).
   * @return A new allocated array containing the moving root mean square values.
   */
  public static float[] movingRms(float[] a, int n)
  {
    return movingSum(Op.ROOTMEANPOW,a,2,n);
  }
  
  // -- Aggregation workers --

  /**
   * Aggregation operation codes.
   * 
   * @author Matthias Wolff
   */
  enum Op
  {
    MIN, MAX, ABSMIN, ABSMAX, IMIN, IMAX,
    SUMPOW, MEANPOW, ROOTSUMPOW, ROOTMEANPOW, CMOMENT
  };

  /**
   * Aggregates values of a data vector.
   * 
   * @param o
   *          The aggregation operation.
   * @param a
   *          The vector to aggregate.
   * @param p
   *          The parameter (depends on <code>o</code>).
   * @param f
   *          The zero-based index of the first value to aggregate.
   * @param n
   *          The number of values to aggregate (may be -1 for "all elements").
   * @return the aggregated value.
   */
  private static double aggregate(Op o, double[] a, double p, int f, int n)
  {
    if (a==null || a.length==0 || n==0) return 0.;
    if (n<0) n=a.length;
    int l = f+n;
    if (f<0) f = 0;
    if (l>a.length) l = a.length;
    int    j = -1;
    double v = 0.;
    switch (o)
    {
    case MIN:
      v = Double.MAX_VALUE;
      for (int i=f; i<l; i++) v=Math.min(a[i],v);
      return v;
    case MAX:
      v = Double.MIN_VALUE;
      for (int i=f; i<l; i++) v=Math.max(a[i],v);
      return v;
    case ABSMIN:
      v = Double.MAX_VALUE;
      for (int i=f; i<l; i++) v=Math.min(Math.abs(a[i]),v);
      return v;
    case ABSMAX:
      v = Double.MIN_VALUE;
      for (int i=f; i<l; i++) v=Math.max(Math.abs(a[i]),v);
      return v;
    case IMIN:
      v = Double.MAX_VALUE;
      for (int i=f; i<l; i++)
        if (a[i]<v)
        {
          v = a[i];
          j = i;
        }
      return j;
    case IMAX:
      v = Double.MIN_VALUE;
      for (int i=f; i<l; i++)
        if (a[i]>v)
        {
          v = a[i];
          j = i;
        }
      return j;
    case SUMPOW:
      for (int i=f; i<l; i++) v+=Math.pow(a[i],p);
      return v;
    case MEANPOW:
      for (int i=f; i<l; i++) v+=Math.pow(a[i],p);
      return v/(l-f);
    case ROOTSUMPOW:
      for (int i=f; i<l; i++) v+=Math.pow(a[i],p);
      return Math.pow(v,1./p);
    case ROOTMEANPOW:
      for (int i=f; i<l; i++) v+=Math.pow(a[i],p);
      return Math.pow(v/(l-f),1./p);
    case CMOMENT:
      double m = aggregate(Op.MEANPOW,a,1,f,n);
      for (int i=f; i<l; i++) v+=Math.pow(a[i]-m,p);
      return Math.pow(v/(l-f),1./p);
    default:
      return 0.;
    }
  }

  /**
   * Aggregates values of a data vector.
   * 
   * @param o
   *          The aggregation operation.
   * @param a
   *          The vector to aggregate.
   * @param p
   *          The parameter (depends on <code>o</code>).
   * @param f
   *          The zero-based index of the first value to aggregate.
   * @param n
   *          The number of values to aggregate (may be -1 for "all elements").
   * @return the aggregated value.
   */
  private static float aggregate(Op o, float[] a, float p, int f, int n)
  {
    if (a==null || a.length==0 || n==0) return 0f;
    double[] d = new double[a.length];
    for (int i=0; i<d.length; i++)
      d[i]=a[i];
    return (float)aggregate(o,d,(double)p,f,n);
  }

  /**
   * Moving aggregation of the values of a data vector.
   * 
   * @param o
   *          The aggregation operation.
   * @param a
   *          The vector to aggregate.
   * @param p
   *          The parameter (depends on <code>o</code>).
   * @param n
   *          The aggregation window size (number of array elements to aggregate for each output
   *          value).
   * @return A newly allocated buffer containing the aggregated values.
   */
  private static double[] movingAggregate(Op o, double[] a, double p, int n)
  {
    if (a==null) return null;
    if (a.length==0) return new double[0];
    double[] b = new double[a.length];
    for (int i=0; i<a.length; i++)
      b[i]=aggregate(o,a,p,i-n/2,n);
    return b;
  }

  /**
   * Moving aggregation of the values of a data vector.
   * 
   * @param o
   *          The aggregation operation.
   * @param a
   *          The vector to aggregate.
   * @param p
   *          The parameter (depends on <code>o</code>).
   * @param n
   *          The aggregation window size (number of array elements to aggregate for each output
   *          value).
   * @return A newly allocated buffer containing the aggregated values.
   */
  private static float[] movingAggregate(Op o, float[] a, float p, int n)
  {
    if (a==null) return null;
    if (a.length==0) return new float[0];
    float[] b = new float[a.length];
    for (int i=0; i<a.length; i++)
      b[i]=aggregate(o,a,p,i-n/2,n);
    return b;
  }

  /**
   * Efficient implementation of aggregations summing up (powers of) the array elements.
   * 
   * @param o
   *          The aggregation operation.
   * @param a
   *          The vector to aggregate.
   * @param p
   *          The power of the element to sum.
   * @param n
   *          The aggregation window size (number of array elements to aggregate for each output
   *          value).
   * @return A newly allocated buffer containing the aggregated values.
   * @throws IllegalArgumentException
   *           if <code>o</code> is none of {@link Op#SUMPOW}, {@link Op#MEANPOW},
   *           {@link Op#ROOTSUMPOW}, or {@link Op#ROOTMEANPOW}.
   */
  private static double[] movingSum(Op o, double[] a, double p, int n)
  {
    if (a==null) return null;
    if (a.length==0) return new double[0];
    if (o!=Op.MEANPOW && o!=Op.ROOTMEANPOW && o!=Op.ROOTSUMPOW && o!=Op.SUMPOW)
      throw new IllegalArgumentException();

    double[] b     = new double[a.length];
    int      nWofs = n/2;
    boolean  bMean = (o==Op.MEANPOW || o==Op.ROOTMEANPOW);
    boolean  bRoot = ((o==Op.ROOTSUMPOW || o==Op.ROOTMEANPOW) && p!=1);
    int      nNorm = 0;
    double   nQsum = 0.;
    double[] xQelm = new double[n];
    
    for (int i=0; i<a.length+n-1; i++)
    {
      nQsum -= xQelm[i%n];
      if (i<a.length)
      {
        xQelm[i%n] = Math.pow(a[i],p);
        nQsum += xQelm[i%n];
        nNorm = Math.min(++nNorm,n);
      }
      else
        nNorm--;
      if (i-nWofs>=0 && i-nWofs<a.length)
        if (bRoot)
        {
          if (p==2)
            b[i-nWofs] = Math.sqrt(nQsum/(bMean?nNorm:1.));
          else
            b[i-nWofs] = Math.pow(nQsum/(bMean?nNorm:1.),1./p);
        }
        else
          b[i-nWofs] = nQsum/(bMean?nNorm:1.);
    }
    return b;
  }

  /**
   * Efficient implementation of aggregations summing up (powers of) the array elements.
   * 
   * @param o
   *          The aggregation operation.
   * @param a
   *          The vector to aggregate.
   * @param p
   *          The power of the element to sum.
   * @param n
   *          The aggregation window size (number of array elements to aggregate for each output
   *          value).
   * @return A newly allocated buffer containing the aggregated values.
   * @throws IllegalArgumentException
   *           if <code>o</code> is none of {@link Op#SUMPOW}, {@link Op#MEANPOW},
   *           {@link Op#ROOTSUMPOW}, or {@link Op#ROOTMEANPOW}.
   */
  private static float[] movingSum(Op o, float[] a, float p, int n)
  {
    if (a==null) return null;
    if (a.length==0) return new float[0];
    double[] d = new double[a.length];
    for (int i=0; i<d.length; i++) d[i]=a[i];
    double[] e = movingSum(o,d,(double)p,n);
    float[] b = new float[e.length];
    for (int i=0; i<b.length; i++) b[i]=(float)e[i];
    return b;
  }  
}

// EOF
