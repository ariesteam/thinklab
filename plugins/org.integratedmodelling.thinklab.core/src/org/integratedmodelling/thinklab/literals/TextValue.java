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
import org.integratedmodelling.lang.Semantics;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;
import org.integratedmodelling.thinklab.knowledge.Value;

@LiteralImplementation(concept="thinklab-core:Text")
public class TextValue extends Value implements IParseable {
    
    public String value;
    
    public TextValue() {
        super(KnowledgeManager.Text());
        value = "";
    }
    
    public TextValue(String s)  {
        super(KnowledgeManager.Text());
        value = s;
    }
    
    public void wrap(Object o) {
    	value = (String)o;
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
	public void parse(String string) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String asText() {
		// TODO Auto-generated method stub
		return null;
	}
}
