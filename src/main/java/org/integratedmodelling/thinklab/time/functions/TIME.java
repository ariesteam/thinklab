package org.integratedmodelling.thinklab.time.functions;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;

@Function(id="time", parameterNames= { "start", "end", "duration", "period", "resolution"})
public class TIME implements IExpression {

	@Override
	public Object eval(Map<String, Object> parameters) throws ThinklabException {
		// TODO create a WCS datasource
		return null;
	}

	@Override
	public void setProjectContext(IProject project) {
		// TODO Auto-generated method stub
		
	}

}
