package org.integratedmodelling.thinklab.modelling.functions;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;
import org.integratedmodelling.thinklab.modelling.span.SPANAccessor;

/*
 * TODO move to some plugin
 */
@Function(id="span", parameterNames={"method"})
public class SPAN implements IExpression {

	@Override
	public Object eval(Map<String, Object> parameters) throws ThinklabException {
		// TODO everything
		return new SPANAccessor();
	}

	@Override
	public void setProjectContext(IProject project) {
		// TODO Auto-generated method stub
		
	}

}
