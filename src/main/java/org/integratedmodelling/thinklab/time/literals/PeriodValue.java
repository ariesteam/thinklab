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
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticLiteral;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.api.modelling.ITopologicallyComparable;
import org.integratedmodelling.thinklab.time.TimePlugin;
import org.joda.time.Interval;

@Literal(concept="time:PeriodValue", javaClass=Interval.class, datatype="")
public class PeriodValue 
	extends SemanticLiteral<Interval> 
	implements IParseable, ITopologicallyComparable<PeriodValue> {
    
    private static IConcept getBaseTimeConcept() throws ThinklabException {
        return Thinklab.c(TimePlugin.PERIOD_TYPE_ID);
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
        super(c, interval);
    }

    public PeriodValue(Interval interval) throws ThinklabException {
        super(getBaseTimeConcept(), interval);
    }

    public PeriodValue(String s) throws ThinklabException {
        parse(s);
    }
    
    public PeriodValue(long x, long x2) throws ThinklabException {
        super(getBaseTimeConcept(), new Interval(x, x2));
    }

    public String toString() {
        return value.toString();
    }
    
    public Object clone() {
        PeriodValue ret = null;
        try {
            ret = new PeriodValue(concept, value);
        } catch (ThinklabException e) {
        }
        return ret;
    }
    
    public Interval getInterval() {
    	return value;
    }
    
    public long getStart() {
    	return value.getStartMillis();
    }
    
    public long getEnd() {
    	return value.getEndMillis();
    }


	@Override
	public boolean is(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String asText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(PeriodValue o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean overlaps(PeriodValue o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(PeriodValue o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

}
