/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.time.literals;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

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