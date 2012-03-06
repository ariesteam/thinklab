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

import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.geospace.coverage.ICoverage;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.geospace.extents.ShapeExtent;
import org.integratedmodelling.thinklab.interpreter.mvel.MVELExpression;

/**
 * @author Ferdinando
 */
public class VectorCoverageDataSource implements IDataSource {


	protected ICoverage coverage = null;

	/* same here - these are overall extents that we need to conform to */
	private GridExtent gridExtent;
	private ShapeExtent shapeExtent;

	protected MVELExpression transformation;
	
//	@Override
//	public void initialize(IInstance i) throws ThinklabException {
//
//		// these are compulsory
//		String sourceURL = null;
//		String valueAttr = null;
//		String filterExpression = null;
//
//		// these are only needed if we use an external attribute table
//		String dataURL = null;
//		String sourceAttr = null;
//		String targetAttr = null;
//		
//		// read requested parameters from properties
//		for (IRelationship r : i.getRelationships()) {
//			
//			if (r.isLiteral()) {
//				
//				if (r.getProperty().equals(Geospace.COVERAGE_SOURCE_URL)) {
//					sourceURL = URLUtils.resolveUrl(
//							r.getValue().toString(),
//							Geospace.get().getProperties());
//				} else if (r.getProperty().equals(Geospace.HAS_SOURCE_LINK_ATTRIBUTE)) {
//					sourceAttr = r.getValue().toString();
//				} else if (r.getProperty().equals(Geospace.HAS_TARGET_LINK_ATTRIBUTE)) {
//					targetAttr = r.getValue().toString();
//				} else if (r.getProperty().equals(Geospace.HAS_VALUE_ATTRIBUTE)) {
//					valueAttr = r.getValue().toString();
//				} else if (r.getProperty().equals(Geospace.HAS_ATTRIBUTE_URL)) {
//					dataURL = r.getValue().toString();
//				} else if (r.getProperty().equals(Geospace.HAS_FILTER_PROPERTY)) {
//					filterExpression = r.getValue().toString();
//				} else if (r.getProperty().equals(Geospace.HAS_TRANSFORMATION_EXPRESSION)) {
//					this.transformation = new MVELExpression(r.getValue().toString());
//				}
//			}
//		}
//
//		// check data
//		if (sourceURL == null || valueAttr == null)
//			throw new ThinklabValidationException("vector coverage: specifications are invalid (source url or value attribute missing)");
//
//		if (dataURL != null && ( sourceAttr == null || targetAttr == null))
//			throw new ThinklabValidationException("vector coverage: specifications are invalid (no link attributes for external data table)");
//
//		try {
//
//			Properties p = new Properties();
//			p.setProperty(CoverageFactory.VALUE_ATTRIBUTE_PROPERTY, valueAttr);
//			
//			if (dataURL != null) {
//				p.setProperty(CoverageFactory.ATTRIBUTE_URL_PROPERTY, dataURL);
//				p.setProperty(CoverageFactory.SOURCE_LINK_ATTRIBUTE_PROPERTY, sourceAttr);
//				p.setProperty(CoverageFactory.TARGET_LINK_ATTRIBUTE_PROPERTY, targetAttr);
//			}
//			
//			if (filterExpression != null) {
//				p.setProperty(CoverageFactory.CQL_FILTER_PROPERTY, filterExpression);
//			}
//			
//			this.coverage = 
//				CoverageFactory.requireCoverage(new URL(sourceURL), p);
//			
//		} catch (MalformedURLException e) {
//			throw new ThinklabIOException(e);
//		}
//	}
//
//	public Object getInitialValue() {
//		return null;
//	}
//
//	@Override
//	public void validate(IInstance i) throws ThinklabException {
//
//		if (coverage != null) {
//			
//		} else {	
//			// TODO we should support inline data
//			throw new ThinklabValidationException("vector datasource: no coverage specified");		
//		}
//	}

//	@Override
	public Object getValue(int index, Object[] parameters) {
		
		Object ret = null;
		try {
			ret = coverage.getSubdivisionValue(
					index, 
					gridExtent == null ? shapeExtent : gridExtent);
			
			if (this.transformation != null && ret != null && !(ret instanceof Double && Double.isNaN((Double)ret)) ) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				parms.put("self", ret);
				ret = this.transformation.eval(parms);
			}
			
			/*
			 * TODO if there's any table linked, use the result as
			 * a key into it.
			 */
			
		} catch (ThinklabValidationException e) {
			throw new ThinklabRuntimeException(e);
		}
		return ret;
	}

	@Override
	public IConcept getValueType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyTargetContext(IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IDataSource transform(IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public IDataSource transform(IDatasourceTransformation transformation)
//			throws ThinklabException {
//		
//		IDataSource ret = this;
//		
//		if (transformation instanceof Rasterize) {
//			gridExtent = ((Rasterize)transformation).getExtent();
//			gridExtent.requireActivationLayer(true);
//			coverage = coverage.requireMatch(gridExtent, true);
//			coverage.loadData();
//			ret = new RegularRasterGridDataSource(coverage, gridExtent, this.transformation);
//		} else {
//			throw new ThinklabValidationException(
//					"vector datasource: don't know how to deal with " + transformation);
//		}
//		
//		return ret;
//	}

}
