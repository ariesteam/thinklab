package org.integratedmodelling.modelling.rest;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.restlet.representation.Representation;

@RESTResourceHandler(id="model",
					 arguments="model,context,scenario",
					 options="output,visualize,dump",
					 description="run a model in a given context")
public class ModelService extends DefaultAbstractModelService {


	@Override
	public Representation run() throws ThinklabException {

		if (_model == null || _context == null) {
			throw new ThinklabValidationException("model service: model or context are null");
		}
		
		return enqueue(new ModelThread(_kbox, _model, _context, getSession(), null));
	}
}
