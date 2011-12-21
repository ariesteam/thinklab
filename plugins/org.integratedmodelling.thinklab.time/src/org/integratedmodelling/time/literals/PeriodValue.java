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

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;
import org.integratedmodelling.time.TimePlugin;
import org.joda.time.Interval;

@LiteralImplementation(concept="time:PeriodValue")
public class PeriodValue extends ParsedLiteralValue {

    Interval interval;
    
    private static IConcept getBaseTimeConcept() throws ThinklabException {
        return 
            KnowledgeManager.get().
            requireConcept(TimePlugin.PERIOD_TYPE_ID);
    }
    
    @Override
    public void parseLiteral(String s) throws ThinklabValidationException {
        try {
        	/* literal is two dates separated by a pound sign */
        	concept = getBaseTimeConcept();
        } catch (Exception e) {
            throw new ThinklabValidationException(e);
        }
    }

    public PeriodValue(IConcept c, Interval interval) throws ThinklabException {
        super(c);
        this.interval = interval;
    }

    public PeriodValue(Interval interval) throws ThinklabException {
        super(getBaseTimeConcept());
        this.interval = interval;
    }

    public PeriodValue(String s) throws ThinklabException {
        parseLiteral(s);
    }
    
    public PeriodValue(long x, long x2) throws ThinklabException {
        super(getBaseTimeConcept());
        interval = new Interval(x, x2);
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
        return interval.toString();
    }
    
    public IValue clone() {
        PeriodValue ret = null;
        try {
            ret = new PeriodValue(concept, interval);
        } catch (ThinklabException e) {
        }
        return ret;
    }
    
    public Interval getInterval() {
    	return interval;
    }
    
    public long getStart() {
    	return interval.getStartMillis();
    }
    
    public long getEnd() {
    	return interval.getEndMillis();
    }

	@Override
	public Object demote() {
		return interval;
	}

}
