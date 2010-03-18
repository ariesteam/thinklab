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
package org.integratedmodelling.time.literals;

import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.SECOND;

import javax.measure.quantity.Duration;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;
import org.integratedmodelling.time.TimePlugin;
import org.jscience.physics.amount.Amount;

/**
 * A parsed literal representing a time duration. Linked to the DurationValue OWL class to be used as an
 * extended literal for it. Maximum resolution is milliseconds. Can be initialized
 * by a string expressing number with units; unit must express time and the syntax is the one loaded in the 
 * JScience framework by the CoreScience plugin.
 *   
 * @author Ferdinando Villa
 *
 */
@LiteralImplementation(concept="time:DurationValue")
public class DurationValue extends ParsedLiteralValue {

    long value = 0l;
    String literal = null;
    
    
    public DurationValue() throws ThinklabException {
        super();
        concept = TimePlugin.Duration();
    }

    @Override
    public void parseLiteral(String s) throws ThinklabValidationException {
        try  {
        	/* oh do I like this */
        	literal = s;
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
    
    public DurationValue(String s) throws ThinklabValidationException, ThinklabNoKMException {
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

    public String toString() {
        return 
        	literal == null ?
        		(value + " ms"):
        		literal;
    }
    
    public IValue clone() {
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
    
	@Override
	public Object demote() {
		return value;
	}

}
