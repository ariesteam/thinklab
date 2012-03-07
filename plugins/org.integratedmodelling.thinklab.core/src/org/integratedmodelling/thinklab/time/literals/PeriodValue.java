/**
 * PeriodValue.java
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

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;
import org.integratedmodelling.thinklab.literals.Value;
import org.integratedmodelling.thinklab.time.TimePlugin;
import org.joda.time.Interval;

@LiteralImplementation(concept="time:PeriodValue")
public class PeriodValue extends Value implements IParseable {

    Interval interval;
    
    public void wrap(Object o) {
    	interval = (Interval)o;
    }

    
    private static IConcept getBaseTimeConcept() throws ThinklabException {
        return 
            KnowledgeManager.get().
            requireConcept(TimePlugin.PERIOD_TYPE_ID);
    }
    
    @Override
    public void parse(String s) throws ThinklabValidationException {
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
        parse(s);
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
    
    public Object clone() {
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
