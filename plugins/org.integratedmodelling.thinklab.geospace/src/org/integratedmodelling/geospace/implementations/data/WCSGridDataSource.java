/**
 * WCSGridDataSource.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2009 www.integratedmodelling.org
 * Created: Apr 9, 2009
 *
 * ----------------------------------------------------------------------------------
 * This file is part of thinklab.
 * 
 * thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2009 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Apr 9, 2009
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.implementations.data;

import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.AbstractRasterCoverage;
import org.integratedmodelling.geospace.coverage.WCSCoverage;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.transformations.Resample;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interpreter.mvel.MVELExpression;

@InstanceImplementation(concept="geospace:WCSDataSource")
public class WCSGridDataSource extends RegularRasterGridDataSource {

	private GridExtent finalExtent = null;
	
	public void initialize(IInstance i) throws ThinklabException {
		
		Properties p = new Properties();
		p.putAll(Geospace.get().getProperties());
		IValue server = i.get("geospace:hasServiceUrl");
		if (server != null)
			p.put(WCSCoverage.WCS_SERVICE_PROPERTY, server.toString());
		IValue format = i.get("geospace:hasImageFormat");
		if (format != null)
			p.put(WCSCoverage.WCS_FORMAT_PROPERTY, format.toString());
		IValue nodata = i.get("geospace:hasNodataValue");
		if (nodata != null)
			p.put(AbstractRasterCoverage.NODATA_PROPERTY, nodata.toString());
		IValue transf = i.get(Geospace.HAS_TRANSFORMATION_EXPRESSION);
		if (transf != null) {
			this.transformation = new MVELExpression(transf.toString());
		}
		String rid = i.get("geospace:hasCoverageId").toString();
		
		Geospace.get().logger().info("reading WCS source " + server + "#" + rid);

		this.coverage = 
			new WCSCoverage(rid, p);
	}
	
	@Override
	public IDataSource<?> transform(IDatasourceTransformation transformation)
			throws ThinklabException {
		
		if (transformation instanceof Resample) {
			// just set the extent
			finalExtent = ((Resample)transformation).getExtent();
		} else {
			throw new ThinklabValidationException(
					"WCS datasource: cannot process transformation of class " + transformation.getClass());
		}
		return this;
	}

	@Override
	public void postProcess(IObservationContext context)
			throws ThinklabException {
		
		if (finalExtent == null) {
			throw new ThinklabValidationException(
					"WCS datasource: no subsetting of data, probably being used without a spatial extent");
		}
		coverage.requireMatch(finalExtent, false);
		coverage.loadData();
		finalExtent.requireActivationLayer(true);
		setGridExtent(finalExtent);
	}

	@Override
	public void preProcess(IObservationContext context)
			throws ThinklabException {
	}

}
