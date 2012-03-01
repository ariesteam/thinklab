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
package org.integratedmodelling.thinklab.metadata;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.SemanticAnnotation;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;

public class Metadata implements IMetadata {

	HashMap<String, Object> _md = new HashMap<String, Object>();
	
	@Override
	public Object get(String string) {
		return _md.get(string);
	}

	@Override
	public SemanticAnnotation conceptualize() throws ThinklabException {

		// recognize strings that are known properties and conceptualize to them; if
		// object is a conceptualizable use an object property, otherwise annotation.
		ArrayList<Object> ret = new ArrayList<Object>();
		
		for (String s : _md.keySet()) {
			IProperty p = Thinklab.get().getProperty(s);
			if (p != null) {
				
				Object v = _md.get(s);
				
				if (v instanceof IConceptualizable) {
					v = ((IConceptualizable)v).conceptualize();
				}
				
				ret.add(PolyList.list(p, v));
				
			}
		}
		return new SemanticAnnotation(PolyList.fromCollection(ret), Thinklab.get());
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

	@Override
	public void define(SemanticAnnotation conceptualization) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	
}
