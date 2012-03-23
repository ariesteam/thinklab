package org.integratedmodelling.thinklab.functions.geospace;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;

@Function(id="shape", parameterNames= { "wkt" })
public class SHAPE implements IExpression {

	@Override
	public String getLanguage() {
		// TODO Auto-generated method stub
		return "TQL";
	}

	@Override
	public Object eval(Map<String, Object> parameters) throws ThinklabException {
		// TODO create a WCS datasource
		return null;
	}

}
