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
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.geospace.coverage.AbstractRasterCoverage;
import org.integratedmodelling.thinklab.geospace.coverage.WCSCoverage;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;

@Concept("geospace:WCSDataSource")
public class WCSGridDataSource extends RegularRasterGridDataSource {

	private GridExtent finalExtent = null;
	
	public WCSGridDataSource(String service, String id, double[] noData) throws ThinklabException {

		Properties p = new Properties();
		p.put(WCSCoverage.WCS_SERVICE_PROPERTY, service);
		
		for (double d : noData) {
			if (!Double.isNaN(d)) {
				String s = p.getProperty(AbstractRasterCoverage.NODATA_PROPERTY, "");
				if (s.length() > 0)
					s += ",";
				s += d;
				p.put(AbstractRasterCoverage.NODATA_PROPERTY, s);
			}
		}
			
		Thinklab.get().logger().info("reading WCS source " + service + "#" + id);

		this.coverage = new WCSCoverage(id, p);
	}

	
//	@Override
//	public IDataSource<?> transform(IDatasourceTransformation transformation)
//			throws ThinklabException {
//		
//		if (transformation instanceof Resample) {
//			// just set the extent
//			finalExtent = ((Resample)transformation).getExtent();
//		} else {
//			throw new ThinklabValidationException(
//					"WCS datasource: cannot process transformation of class " + transformation.getClass());
//		}
//		return this;
//	}
//
//	@Override
//	public void postProcess(IObservationContext context)
//			throws ThinklabException {
//		
//		if (finalExtent == null) {
//			throw new ThinklabValidationException(
//					"WCS datasource: no subsetting of data, probably being used without a spatial extent");
//		}
//		coverage.requireMatch(finalExtent, false);
//		coverage.loadData();
//		finalExtent.requireActivationLayer(true);
//		setGridExtent(finalExtent);
//	}
//
//	@Override
//	public void preProcess(IObservationContext context)
//			throws ThinklabException {
//	}

}
