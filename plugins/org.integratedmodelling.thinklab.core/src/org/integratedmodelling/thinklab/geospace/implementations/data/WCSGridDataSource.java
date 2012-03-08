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
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.integratedmodelling.thinklab.geospace.coverage.AbstractRasterCoverage;
import org.integratedmodelling.thinklab.geospace.coverage.WCSCoverage;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.interpreter.mvel.MVELExpression;

@Concept("geospace:WCSDataSource")
public class WCSGridDataSource extends RegularRasterGridDataSource {

	private GridExtent finalExtent = null;
	
	public void initialize(ISemanticObject i) throws ThinklabException {
		
//		Properties p = new Properties();
//		p.putAll(Thinklab.get().getProperties());
//		ISemanticObject server = i.get("geospace:hasServiceUrl");
//		if (server != null)
//			p.put(WCSCoverage.WCS_SERVICE_PROPERTY, server.toString());
//		ISemanticObject format = i.get("geospace:hasImageFormat");
//		if (format != null)
//			p.put(WCSCoverage.WCS_FORMAT_PROPERTY, format.toString());
//		
//		for (IRelationship r : i.getRelationships("geospace:hasNodataValue")) {
//			ISemanticLiteral nodata = r.getValue();
//			if (nodata != null) {
//				String s = p.getProperty(AbstractRasterCoverage.NODATA_PROPERTY, "");
//				if (s.length() > 0)
//					s += ",";
//				s += nodata.toString();
//				p.put(AbstractRasterCoverage.NODATA_PROPERTY, nodata.toString());
//			}
//		}
//			
//			ISemanticLiteral transf = i.get(Geospace.HAS_TRANSFORMATION_EXPRESSION);
//		if (transf != null) {
//			this.transformation = new MVELExpression(transf.toString());
//		}
//		String rid = i.get("geospace:hasCoverageId").toString();
//		
//		Thinklab.get().logger().info("reading WCS source " + server + "#" + rid);
//
//		this.coverage = 
//			new WCSCoverage(rid, p);
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
