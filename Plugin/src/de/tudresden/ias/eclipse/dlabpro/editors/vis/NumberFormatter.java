package de.tudresden.ias.eclipse.dlabpro.editors.vis;

import de.tucottbus.kt.jlab.datadisplays.utils.DdUtils;

/**
 * Klasse dient zum Formatieren der Beschriftung eines Lineals.
 * 
 * @author Stephan Larws
 * 
 */
public class NumberFormatter {

	private double mLowVal;

	private double mHighVal;

	private double mLabelInterval;

	private double mSecondLabelValue;

	private double mOldValue;

	private boolean mUseOldValue;

	private boolean mUseAbriviation;

	private boolean mFirstLabelSent;

	private int mLabelIntervalExponent;

	private int mScientificNotationExponent;

	public NumberFormatter(double lowVal, double highVal,
			double secondLabelValue, double labelInterval) {
		mLowVal = lowVal;
		mHighVal = highVal;
		mSecondLabelValue = secondLabelValue;
		mLabelInterval = labelInterval;
		mLabelIntervalExponent = computeLabelIntervalExponent();
		mScientificNotationExponent = 0;
		determineNecessityForSN();
		mOldValue = mSecondLabelValue;
		mUseOldValue = false;
		mUseAbriviation = false;
		mFirstLabelSent = false;
	}

	/**
	 * Returns the first shown number of the ruler, if the low value of the ruler is a multiple of the
	 * label interval. Otherwise this method is not called and the labeling starts at the second label
	 * line.
	 * 
	 * @return first shown number of the ruler
	 */
	public String getFirstLabel() {
		String s = "";
		int first = 0;
		int sizeOfNumbers = 0;

		if (mLabelInterval > 1.0) {
			first = (int) mLowVal;
			s = "" + first;
			sizeOfNumbers = s.length();
		}
		if (mLabelInterval <= 1.0) {
			int exp = mLabelIntervalExponent;
			double t = mLowVal;
			int fpos = ("" + mLowVal).indexOf('.');
			for (; exp <= 0; exp++) {
				t *= 10.0;
			}
			first = (int) Math.round(t);
			String str = "" + first;
			if (fpos != str.length()) {
				s = str.substring(0, fpos) + "." + str.substring(fpos);
			} else {
				s = str.substring(0, fpos);
			}
			s = getRidOfLastZeroes(s);
			sizeOfNumbers = s.length() - 1;
		}

		if (sizeOfNumbers > DdUtils.NUMBER_OF_DIGITS_FOR_WHOLE_NUMBER)
			mUseAbriviation = true;

		mFirstLabelSent = true;
		return s;
	}

	/**
	 * Returns the labels following the first one. Needs to be called for every label separately.
	 * 
	 * @return the labels following the first one
	 */
	public String getNextLabel() {
		String s = "";
		double value;
		int fpos = 0;
		int exp = mLabelIntervalExponent;
		int sizeOfNumbers = 0;

		if (mUseOldValue) {
			value = mOldValue + mLabelInterval;
		} else {
			value = mSecondLabelValue;
		}

		s = "" + value;
		fpos = s.indexOf('.');

		if (mFirstLabelSent) {
			int num;
			String front = "0.";

			// If the labelinterval is bigger than 0
			// the digits after the decimal point are
			// not of interest
			if (mLabelInterval > 1.0) {
				num = (int) value;
				s = "" + num;
			}
			// otherwise the are
			if (mLabelInterval <= 1.0) {
				int i = exp;
				int v_exp = determineMagnitude(value);
				// if the value is smaller than 1 the
				// floating point position will disappear
				// in the following transformations thus
				// it needs to be handled separately
				if (v_exp < 0) {
					fpos = 0;
					for (int j = -1; j > v_exp; j--) {
						front += "0";
					}
				}
				double t = value;
				for (; i <= 0; i++) {
					t *= 10.0;
				}
				num = (int) Math.round(t);
				String str = "" + num;
				s = str.substring(0, fpos) + "." + str.substring(fpos);
				if (fpos == 0) {
					s = front + str.substring(fpos);
				}
				if (fpos >= str.length()) {

				}
			}
		} else {
			int first;
			mFirstLabelSent = true;

			if (mLabelInterval > 1.0) {
				first = (int) value;
				s = "" + first;
				sizeOfNumbers = s.length();
			}
			if (mLabelInterval <= 1.0) {
				double t = value;
				for (; exp <= 0; exp++) {
					t *= 10.0;
				}
				first = (int) Math.round(t);
				String str = "" + first;
				if (fpos != str.length()) {
					s = str.substring(0, fpos) + "." + str.substring(fpos);
				} else {
					s = str.substring(0, fpos);
				}
				sizeOfNumbers = s.length() - 1;
			}
		}
		s = getRidOfLastZeroes(s);

		if (mUseAbriviation
				&& s.length() > DdUtils.NUMBER_OF_DIGITS_FOR_WHOLE_NUMBER) {
			s = ".." + s.substring(s.length() - 4);
		}

		mOldValue = value;
		mUseOldValue = true;

		if (sizeOfNumbers > DdUtils.NUMBER_OF_DIGITS_FOR_WHOLE_NUMBER)
			mUseAbriviation = true;

		return s;
	}

	/**
	 * This method should be called after the creation of a ruler. It returns
	 * the exponent for the scientific notation
	 * 
	 * @return the exponent for scientific notation
	 */
	public int getSNExponent() {
		return mScientificNotationExponent;
	}

	/**
	 * Calling this method reset the variables to their original values, but negates
	 * their prefixes. This method is needed when drawing the lower part of a vertical
	 * ruler
	 */
	public void reverse() {
		mSecondLabelValue = -mSecondLabelValue;
		mLabelInterval = -mLabelInterval;
		mOldValue = mSecondLabelValue;

		mUseOldValue = false;
		mUseAbriviation = false;
	}

	/**
	 * Calculates the exponent for one lable interval. If for example the size of the 
	 * interval is 100 this method should return 2.
	 * 
	 * @return the exponent for the label interval
	 */
	private int computeLabelIntervalExponent() {
		int res = 0;
		double t = mLabelInterval;
		if (mLabelInterval < 1d) {
			while (t < 1d) {
				t *= 10d;
				res--;
			}
		}
		if (mLabelInterval > 1d) {
			while (t > 1d) {
				t /= 10d;
				res++;
			}
		}
		if (mLabelInterval == 1d)
			res = 0;

		return res;
	}

	/**
	 * Determines whether scientific notation is needed. If it is necessary to do so,
	 * the start and end values of the ruler are also converted.
	 */
	private void determineNecessityForSN() {
		int magHighVal = determineMagnitude(mHighVal);
		int magLowVal = determineMagnitude(mLowVal);
		;

		if (magLowVal > magHighVal) {
			if (magLowVal <= DdUtils.EXPONENT_FOR_SCIENTIFIC_NOTATION_LOW
					|| magLowVal >= DdUtils.EXPONENT_FOR_SCIENTIFIC_NOTATION_HIGH) {
				mLowVal = switchToSN(magLowVal, mLowVal);
				mHighVal = switchToSN(magLowVal, mHighVal);
				mSecondLabelValue = switchToSN(magLowVal, mSecondLabelValue);

				mLabelInterval = switchToSN(magLowVal, mLabelInterval);
				mLabelIntervalExponent -= magLowVal;

				mScientificNotationExponent = magLowVal;
			}
		} else {
			if (magHighVal <= DdUtils.EXPONENT_FOR_SCIENTIFIC_NOTATION_LOW
					|| magHighVal >= DdUtils.EXPONENT_FOR_SCIENTIFIC_NOTATION_HIGH) {
				mLowVal = switchToSN(magHighVal, mLowVal);
				mHighVal = switchToSN(magHighVal, mHighVal);
				mSecondLabelValue = switchToSN(magHighVal, mSecondLabelValue);

				mLabelInterval = switchToSN(magHighVal, mLabelInterval);
				mLabelIntervalExponent -= magHighVal;

				mScientificNotationExponent = magHighVal;
			}
		}
	}

	/**
	 * Determines the magnitude of a number
	 * 
	 * @param d the value for which the magnitude needs to be determined
	 * 
	 * @return the magnitude of d
	 */
	private int determineMagnitude(double d) {
		int mag = 0;

		if (d < 0.0)
			d = -d;

		if (d >= 10d) {
			while (d >= 10d) {
				d /= 10d;
				mag++;
			}
		}
		if (d > 0d && d < 1d) {
			while (d > 0d && d < 1d) {
				d *= 10d;
				mag--;
			}
			int a = (int) Math.round(d);
			if (a == 10) {
				mag++;
			}
		}

		return mag;
	}

	/**
	 * Converts a number into scientific notation (SN). d = return value * mag
	 * 
	 * @param mag	the exponent/magnitude
	 * @param d		the value to be converted
	 * @return 		the converted value
	 */
	private double switchToSN(int mag, double d) {
		if (mag < 0) {
			while (mag < 0) {
				d *= 10;
				mag++;
			}
		} else {
			while (mag > 0) {
				d /= 10;
				mag--;
			}
		}

		return d;
	}

	/**
	 * Remove unnecessary zeroes at the end of interval label strings.
	 * 
	 * @param str	string representation of the number
	 * @return		string representation of the number without the ending zeroes
	 */
	private String getRidOfLastZeroes(String str) {
		if (str.indexOf('.') < 0)
			return str;

		int pos = str.length() - 1;
		while (str.charAt(pos) == '0') {
			pos--;
		}
		if (str.charAt(pos) == '.')
			pos--;

		return str.substring(0, pos + 1);
	}

	public String toString() {
		String s = "";
		String del = "--------------------------------------\n";
		String label = "Label-Intervall: " + mLabelInterval
				+ " | Label-Intervall Exponent: " + mLabelIntervalExponent
				+ "\n";
		String values = "Low: " + mLowVal + " | High: " + mHighVal + "\n";

		s = del + label + values + del;
		return s;
	}
}
