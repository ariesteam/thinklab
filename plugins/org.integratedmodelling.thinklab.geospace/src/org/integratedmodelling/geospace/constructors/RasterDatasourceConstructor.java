package org.integratedmodelling.geospace.constructors;

import org.integratedmodelling.geospace.datasources.RegularRasterGridDataSource;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.extensions.InstanceImplementationConstructor;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;

public class RasterDatasourceConstructor implements
		InstanceImplementationConstructor {

	public IInstanceImplementation construct(IInstance instance)
			throws ThinklabException {

		return new RegularRasterGridDataSource();
	}

}
