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
package org.integratedmodelling.corescience.literals;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.literals.IntervalValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;

public class MappedInterval extends ParsedLiteralValue {

	IntervalValue interval = null;
	String sform = null;
	
	public MappedInterval(String s) throws ThinklabValidationException {
		parseLiteral(s);
	}

	public MappedInterval(IConcept concept, IntervalValue val) {
		setConceptWithoutValidation(concept);
		interval = val;
	}

	@Override
	public void parseLiteral(String s) throws ThinklabValidationException {

		sform = s;
		
		int idx = s.indexOf(":");
		if (idx < 0)
			throw new ThinklabValidationException("invalid mapped interval syntax: " + s);
		
		String cname = s.substring(0, idx).trim();
		String intvs = s.substring(idx+1).trim();
		
		interval = new IntervalValue(intvs);
		try {
			setConceptWithoutValidation(KnowledgeManager.get().requireConcept(cname));
		} catch (ThinklabException e) {
			throw new ThinklabValidationException(
					"invalid mapped interval: " + s + ": " + e.getMessage());
		}

	}

	public IntervalValue getInterval() {
		return interval;
	}
	
	public boolean contains(double d) {
		return interval.contains(d);
	}

}
