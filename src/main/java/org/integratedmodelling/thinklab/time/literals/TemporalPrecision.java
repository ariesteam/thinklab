/**
 * 
 */
package org.integratedmodelling.thinklab.time.literals;

import org.integratedmodelling.exceptions.ThinklabValidationException;

public class TemporalPrecision {
	
	public static final int MILLISECOND = 1;
	public static final int SECOND = 2;
	public static final int MINUTE = 3;
	public static final int HOUR = 4;
	public static final int DAY = 5;
	public static final int MONTH = 6;
	public static final int YEAR = 7;
	
	/**
	 * Check the unit expressed in the passed string (which must end with
	 * a known SI unit of time) and return the corresponding precision.
	 * If the unit is absent, precision is milliseconds; if the unit is
	 * present and unrecognized, an exception is thrown.
	 * 
	 * As a reminder, the units for millisecond, second, minute, hour,
	 * day, month and year are ms, s, min, h, day, month, year.
	 *  
	 * @param s
	 * @return
	 */
	public static int getPrecisionFromUnit(String s) throws ThinklabValidationException {
		
		int ret = MILLISECOND;
		s = s.trim();
		
		if (Character.isLetter(s.charAt(s.length() - 1))) {
			
			if (s.endsWith("s") && !s.endsWith("ms")) {
				ret = SECOND;
			} else if (s.endsWith("min")) {
				ret = MINUTE;
			} else if (s.endsWith("h")) {
				ret = HOUR;
			} else if (s.endsWith("day")) {
				ret = DAY;
			} else if (s.endsWith("month")) {
				ret = MONTH;
			} else if (s.endsWith("year")) {
				ret = YEAR;
			} else if (!s.endsWith("ms")) {
				throw new ThinklabValidationException("time unit unrecognized in " + s);
			}
		}
		
		return ret;
	}
	
	
}