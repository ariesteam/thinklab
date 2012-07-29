package org.integratedmodelling.thinklab.modelling.lang;

import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionCall;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.modelling.ModelManager;

@Concept(NS.FUNCTION_DEFINITION)
public class FunctionCall extends ModelObject<FunctionCall> implements IFunctionCall {

	@Property(NS.HAS_TYPE)
	String _type;
	@Property(NS.HAS_PARAMETERS)
	HashMap<String, Object> _parameters;
	
	IProject _project;
	
	@Override
	public void set(String type, Map<String, Object> parms) {
		_type = type;
		_parameters  = new HashMap<String, Object>();
		_parameters.putAll(parms);
	}

	@Override
	public FunctionCall demote() {
		return this;
	}

	@Override
	public Map<String, Object> getParameters() {
		return _parameters;
	}

	@Override
	public String getId() {
		return _type;
	}
	
	public void setProject(IProject project) {
		_project = project;
	}

	@Override
	public Object call() throws ThinklabException {
		ModelManager mm = (ModelManager) Thinklab.get().getModelManager();
		IExpression exp = mm.getExpressionForFunctionCall(this);
		if (exp != null) {
			exp.setProjectContext(_project);
			return exp.eval(_parameters);
		}
		return null;
	}
}
