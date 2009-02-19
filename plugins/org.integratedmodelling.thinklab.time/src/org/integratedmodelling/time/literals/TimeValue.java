/**
 * TimeValue.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabTimePlugin.
 * 
 * ThinklabTimePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabTimePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.time.literals;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;
import org.integratedmodelling.time.TimePlugin;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * A parsed literal representing one point in time, parsed from a string that
 * satisfies the JODA Time parser. The typical input is an ISO8601 date/time
 * string, such as "2006-12-13T21:39:45.618-08:00". Linked to the DateTimeValue
 * type as an extended literal. The same syntax can be used to define an entire
 * time observation of type TemporalRecord using a literal.
 * 
 * TODO parser should support yyyy, mm-yyyy and dd-mm-yyyy by simple pattern
 * recognition, and set correspondent PRECISION values (YEAR, MONTH, DAY).
 * Whatever doesn't match should be given to Yoda for ISO parsing and assumed
 * MILLISECOND precision.
 * 
 * @author Ferdinando Villa
 * 
 */
@LiteralImplementation(concept="time:DateTimeValue")
public class TimeValue extends ParsedLiteralValue {

	public enum Precision {
		MILLISECOND, DAY, MONTH, YEAR
	}

	DateTime value;

	Precision precision = Precision.MILLISECOND;

	public static String matchYear = "[0-2][0-9][0-9][0-9]";

	public static String matchMonthYear = "[0-1][0-9]-[0-2][0-9][0-9][0-9]";

	public static String matchDayMonthYear = "[0-3][0-9]-[0-1][0-9]-[0-2][0-9][0-9][0-9]";

	public TimeValue() throws ThinklabException {
		super();
		// "now" value
		value = new DateTime();
		concept = TimePlugin.DateTime();
	}

	@Override
	public void parseLiteral(String s) throws ThinklabValidationException {
		try {

			if (s.matches(matchYear)) {
				DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy");
				value = fmt.parseDateTime(s);
				precision = Precision.YEAR;
			} else if (s.matches(matchMonthYear)) {
				DateTimeFormatter fmt = DateTimeFormat.forPattern("MM-yyyy");
				value = fmt.parseDateTime(s);
				precision = Precision.MONTH;
			} else if (s.matches(matchDayMonthYear)) {
				DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
				value = fmt.parseDateTime(s);
				precision = Precision.DAY;
			} else {
				value = new DateTime(s);
			}
			concept = TimePlugin.DateTime();
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
	}

	public TimeValue(IConcept c) throws ThinklabException {
		super(c);
		value = new DateTime();
	}

	public TimeValue(String s) throws ThinklabValidationException,
			ThinklabNoKMException {
		parseLiteral(s);
	}

	public boolean isNumber() {
		return false;
	}

	public boolean isText() {
		return false;
	}

	public boolean isBoolean() {
		return false;
	}

	public boolean isClass() {
		return false;
	}

	public boolean isObject() {
		return false;
	}

	public boolean isLiteral() {
		return true;
	}

	public IValue clone() {
		TimeValue ret = null;
		try {
			ret = new TimeValue(concept);
			ret.value = new DateTime(value);
		} catch (ThinklabException e) {
		}
		return ret;
	}

	public int getDimensionsCount() {
		return 0;
	}

	public int getGranularity(int dimensionIndex) {
		return 0;
	}

	public IValue getValue(int idx, IConcept concept)
			throws ThinklabValidationException {
		return this;
	}

	public int getValuesCount() {
		return 1;
	}

	public DateTime getTimeData() {
		return value;
	}

	public Precision getPrecision() {
		return precision;
	}

	/**
	 * Compare at the resolution intrinsic in the date. Code should read easily.
	 *
	 * TODO no normalization of time zones is done - we should ensure both TZ are the same before
	 * comparing.
	 * 
	 * @param v1
	 * @return
	 */
	public boolean comparable(TimeValue v1) {
				
		if (precision.equals(Precision.YEAR)) {
			return 
				value.year().getMaximumValue() == v1.value.year().getMaximumValue();
			
		} else if (precision.equals(Precision.MONTH)) {
			
			return 
				value.year().equals(v1.value.year()) &&
				value.monthOfYear().equals(v1.value.monthOfYear());
			
		} else if (precision.equals(Precision.DAY)) {
			return 
			value.year().equals(v1.value.year()) &&
			value.monthOfYear().equals(v1.value.monthOfYear()) &&
			value.dayOfMonth().equals(v1.value.dayOfMonth());
		}
		
		return v1.value.equals(value);
	}

	public String toString() {

		if (precision.equals(Precision.YEAR))
			return value.toString("yyyy");
		else if (precision.equals(Precision.MONTH))
			return value.toString("MM-yyyy");
		else if (precision.equals(Precision.DAY))
			return value.toString("dd-MM-yyyy");
		return value.toString();
	}
	
	public boolean isIdentical(TimeValue obj) {
		
		return
			(obj instanceof TimeValue) &&
			(precision == ((TimeValue)obj).precision) &&
			value.equals(((TimeValue)obj).value);
		
	}

	public TimeValue leastPrecise(TimeValue v) {

		if (comparable(v)) {
			return precision.compareTo(v.precision) > 0 ? this : v;
		}	
		return null;
	}

	public TimeValue mostPrecise(TimeValue v) {

		if (comparable(v)) {
			return precision.compareTo(v.precision) > 0 ? v : this;
		}
		return null;
	}

	public IValue getValue(int[] idx, IObservationContextState context, IConcept concept) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean handshake(IConceptualModel cm, IObservationContext observationContext, IObservationContext overallContext) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return false;
	}

	public int getMonth() {
		return value.getMonthOfYear();
	}

	public int getYear() {
		return value.getYear();
	}
	
	@Override
	public Object demote() {
		return value;
	}

// I don't know why it doesn't get called.
//	public boolean equals(Object other) {
//		
//		System.out.println("Called");
//		
//		boolean ret = (other instanceof TimeValue);
//
//		if (ret)
//			ret = precision == ((TimeValue)other).precision;
//		
//		if (ret)
//			ret = value.equals(((TimeValue)other).value);
//		return ret;
//	}

}
