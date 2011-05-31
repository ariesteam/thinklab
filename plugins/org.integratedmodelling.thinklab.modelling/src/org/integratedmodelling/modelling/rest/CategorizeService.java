package org.integratedmodelling.modelling.rest;

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
		
		/*
		 * TODO create model for concept
		 */
		return enqueue(new ModelThread(_kbox, _model, _context, getSession(), null));	
	}

}
