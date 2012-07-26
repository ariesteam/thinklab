package org.integratedmodelling.thinklab.modelling.functions;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;
import org.integratedmodelling.thinklab.modelling.bayes.BayesianAccessor;

@Function(id="bayesian", parameterNames={"import"})
public class BAYESIAN implements IExpression {

	private IProject project;

	@Override
	public Object eval(Map<String, Object> parameters) throws ThinklabException {
		return new BayesianAccessor(parameters.get("import").toString(), project.getLoadPath());
	}

	@Override
	public void setProjectContext(IProject project) {
		this.project = project;
	}

}
