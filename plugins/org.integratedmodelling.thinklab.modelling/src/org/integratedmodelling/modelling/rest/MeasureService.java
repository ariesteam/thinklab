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

@RESTResourceHandler(id="measure",
		 arguments="concept,units,context,scenario",
		 options="output,visualize,dump",
		 description="build and run a measurement for a concept in a given context")
public class MeasureService extends DefaultAbstractModelService {

	@Override
	public Representation run() throws ThinklabException {

		if (_concept == null || _context == null || getArgument("units") == null) {
			throw new ThinklabValidationException("measure: model, context or unit are null");
		}
		
		String units = getArgument("units");
		String clj = 
			"(modelling/model '" + 
			_concept + 
			" (modelling/measurement '" + _concept + " \"" + units + "\"))";
					
		_model = (Model) new ClojureInterpreter().evalRaw(clj, "user", null);

		return enqueue(new ModelThread(_kbox, _model, _context, getSession(), null));	
	}

}
