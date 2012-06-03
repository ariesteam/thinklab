/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.geospace.implementations.data;

import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.geospace.coverage.AbstractRasterCoverage;
import org.integratedmodelling.thinklab.geospace.coverage.ICoverage;
import org.integratedmodelling.thinklab.geospace.coverage.WCSCoverage;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.geospace.literals.PolygonValue;
import org.integratedmodelling.thinklab.interfaces.IStorageMetadataProvider;
import org.integratedmodelling.thinklab.modelling.lang.Metadata;

public class WCSGridDataSource extends RegularRasterGridDataSource implements IStorageMetadataProvider {

	private Properties _properties = new Properties();
	private String _service;
	private String _id;
	
	public WCSGridDataSource(String service, String id, double[] noData) throws ThinklabException {

		_service = service;
		_id = id;
		
		_properties.put(WCSCoverage.WCS_SERVICE_PROPERTY, service);
		
		for (double d : noData) {
			if (!Double.isNaN(d)) {
				String s = _properties.getProperty(AbstractRasterCoverage.NODATA_PROPERTY, "");
				if (s.length() > 0)
					s += ",";
				s += d;
				_properties.put(AbstractRasterCoverage.NODATA_PROPERTY, s);
			}
		}	
	}
	
	@Override
	protected ICoverage readData() throws ThinklabException {

		if (this._coverage == null) {
			Thinklab.get().logger().info("reading WCS source " + _service + "#" + _id);
			this._coverage = new WCSCoverage(_id, _properties);
		}
		return this._coverage;
	}


	@Override
	public void addStorageMetadata(IMetadata metadata) {
		try {
			readData();
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
		
		((Metadata)metadata).put(
				NS.GEOSPACE_HAS_SHAPE, 
				new PolygonValue(_coverage.getEnvelope()));
	}

	@Override
	protected GridExtent getFinalExtent(IContext context)
			throws ThinklabException {

		IExtent space = context.getSpace();
		
		if ( !(space instanceof GridExtent)) {
			throw new ThinklabValidationException("cannot compute a WCS datasource in a non-grid context");
		}

		if (space.getMultiplicity() != context.getMultiplicity()) {
			throw new ThinklabValidationException("extents requested to WCS datasource span more domains than space");			
		}
		
		return (GridExtent)space;
	}
	
	
}
