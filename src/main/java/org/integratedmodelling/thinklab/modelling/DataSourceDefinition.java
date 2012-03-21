package org.integratedmodelling.thinklab.modelling;

import java.util.Map;

import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.lang.parsing.IDataSourceDefinition;

public class DataSourceDefinition extends ModelObject implements IDataSourceDefinition {

	String _type;
	IExpression _expression;
	Map<String, Object> _parameters;
	
	@Override
	public void setType(String type) {
		_type = type;
	}

	@Override
	public void setResolvedExpression(IExpression expression) {
		_expression = expression;
	}

	@Override
	public void setParameters(Map<String, Object> parameters) {
		_parameters = parameters;
	}

}
