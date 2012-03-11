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
package org.integratedmodelling.thinklab.literals;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.knowledge.SemanticLiteral;

/**
 * This is what happens if we annotate a concept.
 * @author Ferd
 *
 */
@Literal(concept=NS.THING, datatype="thinklab:concept", javaClass=IConcept.class)
public class ConceptValue extends SemanticLiteral implements IParseable {
    
    public ConceptValue() {
        super(Thinklab.NOTHING);
    }
    
    public ConceptValue(IConcept c)  {
        super(c);
    }
    
	@Override
	public Object getObject() {
		return getDirectType();
	}

	@Override
	public boolean is(Object object) {
		return object instanceof IConcept && ((IConcept)object).is(getDirectType());
	}

	@Override
	public void parse(String string) throws ThinklabException {
		setConcept(Thinklab.c(string));
	}

	@Override
	public String toString() {
		return getDirectType().toString();
	}
	
	@Override
	public String asText() {
		return getDirectType().toString();
	}
}
