package org.integratedmodelling.modelling.rest;

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
		
		/*
		 * TODO create model for concept
		 */
		
		return enqueue(new ModelThread(_kbox, _model, _context, getSession(), null));	
	}

}
