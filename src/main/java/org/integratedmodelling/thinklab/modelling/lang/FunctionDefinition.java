package org.integratedmodelling.thinklab.modelling.lang;

import java.util.Map;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;

@Concept(NS.DATASOURCE_DEFINITION)
public class FunctionDefinition extends ModelObject<FunctionDefinition> implements IFunctionDefinition {

	String _type;
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
