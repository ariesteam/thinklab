package org.integratedmodelling.thinklab.geospace.functions;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.geospace.implementations.data.WCSGridDataSource;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;

@Function(id="wcs", parameterNames= { "service", "id", "no-data"})
public class WCS implements IExpression {

	@Override
	public Object eval(Map<String, Object> parameters) throws ThinklabException {

		String service = parameters.get("service").toString();
		String id = parameters.get("id").toString();
		double noData = Double.NaN;

		/*
		 * TODO support a list of nodata values
		 */
		if (parameters.containsKey("no-data")) {
			noData = Double.parseDouble(parameters.get("no-data").toString());
		}
		
		return new WCSGridDataSource(service, id, new double[]{noData});
	}

	@Override
	public void setProjectContext(IProject project) {
		// TODO Auto-generated method stub
		
	}

}
