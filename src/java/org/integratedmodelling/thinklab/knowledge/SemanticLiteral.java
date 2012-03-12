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
package org.integratedmodelling.thinklab.knowledge;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;

/**
 * Base class for a general literal object.
 * 
 * @author Ferd
 *
 */
public abstract class SemanticLiteral implements ISemanticObject {
	
	public IConcept concept;
	
    public SemanticLiteral()  {
        concept = Thinklab.THING;
    }

    public SemanticLiteral(IConcept c) {
        concept = c;
    }

    /* (non-Javadoc)
     * @see org.integratedmodelling.ima.core.value.IValue#toString()
     */
    @Override
	public String toString() {
		return concept.toString();
	}

    /* (non-Javadoc)
     * @see org.integratedmodelling.ima.core.value.IValue#setConceptWithoutValidation(org.integratedmodelling.ima.core.IConcept)
     */
    public void setConcept(IConcept concept) {
        this.concept = concept;
    }
    
	@Override
	public Semantics getSemantics() {
		return new Semantics(PolyList.list(concept, getObject()), Thinklab.get());
	}
    
	@Override
	public IConcept getDirectType() {
		return concept;
	}

	@Override
	public boolean is(Object concept) {
		
		return false; // this.concept.is(concept);
	}

	@Override
	public ISemanticObject get(IProperty property) {
		return null;
	}

	@Override
	public List<ISemanticObject> getAll(IProperty property) {
		return new ArrayList<ISemanticObject>();
	}

	@Override
	public boolean isLiteral() {
		return true;
	}

	@Override
	public boolean isConcept() {
		return false;
	}

	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public void validate() throws ThinklabValidationException {
	}

	@Override
	public boolean asBoolean() {
		return false;
	}

	@Override
	public int asInteger() {
		return 0;
	}

	@Override
	public double asDouble() {
		return Double.NaN;
	}

	@Override
	public float asFloat() {
		return Float.NaN;
	}

	@Override
	public String asString() {
		return null;
	}

	
}