package org.integratedmodelling.thinklab.modelling.lang;

import java.util.Map;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;

@Concept(NS.FUNCTION_DEFINITION)
public class FunctionDefinition extends ModelObject<FunctionDefinition> implements IFunctionDefinition {

	@Property(NS.HAS_TYPE)
	String _type;
	@Property(NS.HAS_PARAMETERS)
	Map<String, Object> _parameters;
	
	@Override
	public void set(String type, Map<String, Object> parms) {
		_type = type;
		_parameters  = parms;
	}

	@Override
	public FunctionDefinition demote() {
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
}
