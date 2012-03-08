/**
 * DurationValue.java
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
package org.integratedmodelling.thinklab.time.literals;

import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.SECOND;

import javax.measure.quantity.Duration;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.Semantics;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.knowledge.SemanticLiteral;
import org.integratedmodelling.thinklab.time.TimePlugin;
import org.integratedmodelling.utils.MiscUtilities;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.jscience.physics.amount.Amount;

/**
 * A parsed literal representing a time duration. Linked to the DurationValue OWL class to be used as an
 * extended literal for it. Maximum resolution is milliseconds. Can be initialized
 * by a string expressing number with units; unit must express time and the syntax is the one
 * supported by the implementation of IUnit.
 *   
 * @author Ferdinando Villa
 * FIXME make it wrap something real
 */
@Literal(concept="time:DurationValue", javaClass=DurationValue.class, datatype="")
public class DurationValue extends SemanticLiteral implements IParseable {

    long value = 0l;
    String literal = null;
    int precision = TemporalPrecision.MILLISECOND;
    int origQuantity = 1;
    String origUnit = "";
    
    public DurationValue() throws ThinklabException {
        super();
        concept = TimePlugin.Duration();
    }

    @Override
    public void parse(String s) throws ThinklabValidationException {
    	
    	/*
    	 * insert a space to break strings like 1s or 2year
    	 */
    	int brk = -1;
    	if (!s.contains(" ")) {
    		for (int i = 0; i < s.length(); i++) {
    			if (!Character.isDigit(s.charAt(i))) {
    				brk = i;
    				origUnit = s.substring(i);
    				break;
    			}
    		}
    	} else {
    		origUnit = s.substring(s.indexOf(" ") + 1);
    	}
    	
    	if (brk > 0) {
    		s =
    			s.substring(0,brk) + 
    			" " +
    			s.substring(brk);
    	}
    	
		if (Character.isDigit(s.charAt(0)))
			this.origQuantity = MiscUtilities.readIntegerFromString(s);

        try  {
        	precision = TemporalPrecision.getPrecisionFromUnit(s);
        	literal = s;
        	/* oh do I like this */
        	Amount<Duration> duration = Amount.valueOf(s).to(MILLI(SECOND));
        	value = duration.getExactValue();
        	concept = TimePlugin.Duration();
        } catch (Exception e) {
            throw new ThinklabValidationException(e);
        }
    }

    public DurationValue(IConcept c) throws ThinklabException {
        super(c);
        value = 0l;
    }
    
    public void wrap(Object o) {
    	value = ((Number)o).longValue();
    }

    
    public DurationValue(String s) throws ThinklabValidationException {
        parse(s);
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

    public String toString() {
        return 
        	literal == null ?
        		(value + " ms"):
        		literal;
    }
    
    public Object clone() {
        DurationValue ret = null;
        try {
            ret = new DurationValue(concept);
            ret.value = value;
        } catch (ThinklabException e) {
        }
        return ret;
    }

	public long getMilliseconds() {
		return value;
	}

	/**
	 * Localize a duration to an extent starting at the current moment
	 * using the same resolution that was implied in the generating 
	 * text. For example, if the duration was one year, localize to the 
	 * current year (jan 1st to dec 31st). Return the start and end points
	 * of the extent.
	 * 
	 * @return
	 */
	public Pair<TimeValue, TimeValue> localize() {
		
		DateTime date = new DateTime();
		TimeValue start = null, end = null;
		long val = value;
		
		switch (precision) {
		
			case TemporalPrecision.MILLISECOND:
				start = new TimeValue(date);
				end = new TimeValue(date.plus(val));
				break;
			case TemporalPrecision.SECOND:
				val = value/DateTimeConstants.MILLIS_PER_SECOND;
				start = 
					new TimeValue(
						new DateTime(
							date.getYear(),
							date.getMonthOfYear(),
							date.getDayOfMonth(),
							date.getHourOfDay(),
							date.getMinuteOfHour(),
							date.getSecondOfMinute(),
							0));
				end = new TimeValue(start.getTimeData().plusSeconds((int)val));
				break;
			case TemporalPrecision.MINUTE:
				val = value/DateTimeConstants.MILLIS_PER_MINUTE;
				start = 
					new TimeValue(
						new DateTime(
							date.getYear(),
							date.getMonthOfYear(),
							date.getDayOfMonth(),
							date.getHourOfDay(),
							date.getMinuteOfHour(),
							0,
							0));
				end = new TimeValue(start.getTimeData().plusMinutes((int)val));
				break;
			case TemporalPrecision.HOUR:
				val = value/DateTimeConstants.MILLIS_PER_HOUR;
				start = 
					new TimeValue(
						new DateTime(
							date.getYear(),
							date.getMonthOfYear(),
							date.getDayOfMonth(),
							date.getHourOfDay(),
							0,
							0,
							0));
				end = new TimeValue(start.getTimeData().plusHours((int)val));
				break;
			case TemporalPrecision.DAY:
				val = value/DateTimeConstants.MILLIS_PER_DAY;
				start = 
					new TimeValue(
						new DateTime(
							date.getYear(),
							date.getMonthOfYear(),
							date.getDayOfMonth(),
							0,
							0,
							0,
							0));
				end = new TimeValue(start.getTimeData().plusDays((int)val));
				break;
			case TemporalPrecision.MONTH:
				start = 
					new TimeValue(
						new DateTime(
							date.getYear(),
							date.getMonthOfYear(),
							1,
							0,
							0,
							0,
							0));
				end = new TimeValue(start.getTimeData().plusMonths(origQuantity));
				break;
			case TemporalPrecision.YEAR:
				start = 
					new TimeValue(
						new DateTime(
							date.getYear(),
							1,
							1,
							0,
							0,
							0,
							0));
				end = new TimeValue(start.getTimeData().plusYears(origQuantity));
				break;		
		}
		
		return new Pair<TimeValue, TimeValue>(start, end);
	}
	
	public int getOriginalQuantity() {
		return origQuantity;
	}
	
	public String getOriginalUnit() {
		return origUnit;
	}

	@Override
	public Semantics getSemantics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean is(ISemanticObject object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String asText() {
		// TODO Auto-generated method stub
		return null;
	}
}
