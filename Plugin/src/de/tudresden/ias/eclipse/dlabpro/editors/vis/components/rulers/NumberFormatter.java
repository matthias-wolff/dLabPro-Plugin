package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;

/**
 * Class for formatting the labels of a ruler
 * 
 * @author Stephan Larws
 * 
 */
public class NumberFormatter {
	
	/**
	 * Determines the magnitude of a number
	 * 
	 * @param num
	 * 			the value for which the magnitude needs to be determined
	 * 
	 * @return 
	 * 			the magnitude of <code>num</code>
	 */
	public static int getMagnitude(double num) {
	  if (Double.isInfinite(num)) return 308;
	  if (Double.isNaN(num)) return 0;
	  double d = num;
		if(d < 0.0)
			d = -d;
		int mag = 0;
		if(d >= 1.0) {
			if(d < 10.0)
				return mag;
			for(mag = 0; d >= 10.0; mag++)
				d /= 10.0;
		}
		else {
			for(mag = 0; d > 0.0 && d < 1.0; mag--)
				d *= 10.0;
		}
		
		return mag;
	}
	
	/**
	 * Formats a value so it will be displayed correctly on the ruler
	 * 
	 * @param val
	 * 			The value that needs to be formatted
	 * @param mag
	 * 			The magnitude the value should be fitted to
	 * @param first
	 * 			Flag whether or not this value is the first value of a series
	 * @return
	 * 			The formatted value as a {@link String}
	 */
	public static String format(double val, int mag, boolean first) {
		String s = "";
		
		if (mag <= VIS.EXPONENT_FOR_SCIENTIFIC_NOTATION_LOW
				|| mag >= VIS.EXPONENT_FOR_SCIENTIFIC_NOTATION_HIGH) {
			int multiplier = 1;
			int exp = mag;
			if (mag < 0)
				exp = -mag;
			for (int i = exp; i > 0; i--) {
				multiplier *= 10;
			}
			if(mag < 0) {
				val *= multiplier;
			}
			else {
				val /= multiplier;
			}
		}
		
		s = String.format("%g", new Object[] { Double.valueOf(val) });
		int pi = s.indexOf('.');
		//Get rid of trailing zeroes
		for(int i = s.length() - 1; i > 0; i--) {
			if(s.charAt(i) == '.') {
				s = s.substring(0, i);
				break;
			}
			if(s.charAt(i) == '0' && pi < i && pi != -1) {
				s = s.substring(0, i);
			}
			else
				break;
		}
		
		return s;
	}
	
	/**
	 * Formats a value and if necessary converts it to scientific notation
	 * 
	 * @param val
	 * 				The value that needs to be formatted
	 * @return
	 * 				The value as a formatted {@link String}
	 */
	public static String formatAndAdjust(double val) {
		int mag = getMagnitude(val);
		String sVal = format(val, mag, true);
		if (mag <= VIS.EXPONENT_FOR_SCIENTIFIC_NOTATION_LOW
				|| mag >= VIS.EXPONENT_FOR_SCIENTIFIC_NOTATION_HIGH) {
			sVal = sVal + " x10e" + mag;
		}
		
		return sVal;
	}
	
  /**
   * Formats a value and if necessary converts it to scientific notation
   * 
   * @param nVal
   *        the value that needs to be formatted
   * @param nSig
   *        number of significant positions
   * @return
   *        the value as a formatted {@link String}
   */
	public static String formatAndAdjust(double nVal, int nSig)
	{
    double e = Math.pow(10,nSig);
    return formatAndAdjust(Math.round(nVal*e)/e);
	}
}
