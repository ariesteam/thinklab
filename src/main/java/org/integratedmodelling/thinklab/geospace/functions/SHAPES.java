package org.integratedmodelling.thinklab.geospace.functions;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;

@Function(id="shapes", parameterNames= { "file", "shapes" })
public class SHAPES implements IExpression {

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
