package org.integratedmodelling.thinklab.geospace.functions;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.geospace.implementations.data.WFSCoverageDataSource;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;

@Function(id="wfs", parameterNames= { "service", "id", "attribute", "filter", "value-type", "default-value" })
public class WFS implements IExpression {

	@Override
	public Object eval(Map<String, Object> parameters) throws ThinklabException {
		
		String service = parameters.get("service").toString();
		String id = parameters.get("id").toString();
		String attribute = 
				parameters.containsKey("attribute") ? 
						parameters.get("attribute").toString() : null;
		String filter =				
				parameters.containsKey("filter") ? 
						parameters.get("filter").toString() : null;
		String valueType = 
				parameters.containsKey("value-type") ? 
						parameters.get("value-type").toString() : null;
		String valueDefault = 	
				parameters.containsKey("default-value") ? 
						parameters.get("default-value").toString() : null;

		return new WFSCoverageDataSource(service, id, attribute, filter, valueType, valueDefault);
	}

	@Override
	public void setProjectContext(IProject project) {
		// TODO Auto-generated method stub
		
	}

}
