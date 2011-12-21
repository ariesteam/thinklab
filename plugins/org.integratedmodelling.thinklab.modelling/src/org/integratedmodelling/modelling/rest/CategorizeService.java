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
package org.integratedmodelling.modelling.rest;

import org.integratedmodelling.clojure.ClojureInterpreter;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.restlet.representation.Representation;

@RESTResourceHandler(id="categorize",
		 arguments="concept,context,scenario",
		 options="output,visualize,dump",
		 description="build and run a categorization for a concept in a given context")
public class CategorizeService extends DefaultAbstractModelService {

	@Override
	public Representation run() throws ThinklabException {

		if (_concept == null || _context == null) {
			throw new ThinklabValidationException("categorize: model or context are null");
		}
		
		String clj = 
			"(modelling/model '" + 
			_concept + 
			" (modelling/categorization '" + _concept + "))";
					
		_model = (Model) new ClojureInterpreter().evalRaw(clj, "user", null);
		
		return enqueue(new ModelThread(_kbox, _model, _context, getSession(), null));	
	}

}
