package org.integratedmodelling.thinklab.modelling.functions;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionCall;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;

/*
 * Creates the proper observable for the uncertainty of another. Provided to
 * avoid the need for a complicated instance definition when uncertainty is wanted
 * from a model.
 */
@Function(id="uncertainty", parameterNames={IFunctionCall.DEFAULT_PARAMETER_NAME})
public class UNCERTAINTY implements IExpression {

	IProject _project;
	
	@Override
	public Object eval(Map<String, Object> parameters) throws ThinklabException {
		
		/*
		 * TODO make list and return the instantiated semantic object. Modeling
		 * ontology still needs the concepts.
		 * 
		 * The argument may be a concept or a list, which will be matched to
		 * another observable in the observable list of a model.
		 */
		Object obs = parameters.get(IFunctionCall.DEFAULT_PARAMETER_NAME);
		
		if (obs instanceof String) {
			if (((String) obs).trim().startsWith("(")) {
				obs = PolyList.parse(obs.toString());
			} else {
				obs = PolyList.list(Thinklab.c(obs.toString()));
			}
		} else if (obs instanceof IConcept) {
			obs = PolyList.list(obs);
		} else if (! (obs instanceof IList)) {
			throw new ThinklabValidationException(
					"observable for uncertainty function unrecognized: should be a concept or an instance list");
		}
		
		return PolyList.list(NS.UNCERTAINTY, PolyList.list(NS.HAS_OBSERVABLE, obs));
	}

	@Override
	public void setProjectContext(IProject project) {
		/*
		 * not necessary for now, but we may want to condition the display of
		 * uncertainty to some project-level configuration.
		 */
		_project = project;
	}

}
