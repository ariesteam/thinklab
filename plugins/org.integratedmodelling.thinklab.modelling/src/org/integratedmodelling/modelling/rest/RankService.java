package org.integratedmodelling.modelling.rest;

import org.integratedmodelling.clojure.ClojureInterpreter;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.restlet.representation.Representation;

@RESTResourceHandler(id="rank",
		 arguments="concept,context,scenario",
		 options="output,visualize,dump",
		 description="build and run a ranking for a concept in a given context")
public class RankService extends DefaultAbstractModelService {

	@Override
	public Representation run() throws ThinklabException {

		if (_concept == null || _context == null) {
			throw new ThinklabValidationException("rank: model or context are null");
		}
		
		String clj = 
			"(modelling/model '" + 
			_concept + 
			" (modelling/ranking '" + _concept + "))";
					
		_model = (Model) new ClojureInterpreter().evalRaw(clj, "user", null);
		
		return enqueue(new ModelThread(_kbox, _model, _context, getSession(), null));	
	}

}
