/**
 * RegularRasterGridDataSource.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Feb 18, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
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
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Feb 18, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.implementations.data;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.CoverageFactory;
import org.integratedmodelling.geospace.coverage.ICoverage;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interpreter.mvel.MVELExpression;
import org.integratedmodelling.utils.URLUtils;
import org.mvel2.MVEL;

@InstanceImplementation(concept="geospace:ExternalRasterDataSource")
public class RegularRasterGridDataSource implements IDataSource<Object>, IInstanceImplementation {

	protected ICoverage coverage = null;

	/* same here - these are overall extents that we need to conform to */
	private GridExtent gridExtent;
	/* any expression to transform with gets compiled into this */
	protected MVELExpression transformation = null;
	
	public RegularRasterGridDataSource() {
	}
	
	public RegularRasterGridDataSource(ICoverage coverage,
			GridExtent gridExtent) {
		this.coverage = coverage;
		this.gridExtent = gridExtent;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		String sourceURL = null;
		String valueAttr = null;
		
		// read requested parameters from properties
		// TODO read class mappings if any - could be to concepts or to instances
		for (IRelationship r : i.getRelationships()) {
			
			if (r.isLiteral()) {
				
				if (r.getProperty().equals(Geospace.COVERAGE_SOURCE_URL)) {
					
					/*
					 * this can also point to a vector source, as long as the value attribute is
					 * provided.
					 */
					sourceURL = URLUtils.resolveUrl(
							r.getValue().toString(),
							Geospace.get().getProperties());
					
				} else if (r.getProperty().equals(Geospace.HAS_VALUE_ATTRIBUTE)) {
					valueAttr = r.getValue().toString();
				} else if (r.getProperty().equals(Geospace.HAS_TRANSFORMATION_EXPRESSION)) {
					this.transformation = new MVELExpression(r.getValue().toString());
				}
			} 
		}

		try {
			
			Properties p = new Properties();
			if (valueAttr != null)	
				p.setProperty(CoverageFactory.VALUE_ATTRIBUTE_PROPERTY, valueAttr);
			this.coverage = CoverageFactory.requireCoverage(new URL(sourceURL), p);
			
		} catch (MalformedURLException e) {
			throw new ThinklabIOException(e);
		}

		
	}

	@Override
	public void validate(IInstance i) throws ThinklabException {

		if (coverage != null) {
			
		} else {
			
			// TODO we should support inline data
			throw new ThinklabValidationException("raster datasource: no coverage specified");		
		}
		
	}


	@Override
	public Object getValue(int index, Object[] parameters) {
		
		/*
		 * TODO reinterpret through classification lookup table if any is provided
		 */
		try {
			Object ret = coverage.getSubdivisionValue(index, gridExtent);
			Double nd = coverage.getNodataValue();
			if (nd != null && ret != null && (ret instanceof Double) && ((Double)ret).equals(nd)) {
				ret = Double.NaN;
			}
			if (this.transformation != null && ret != null && !(ret instanceof Double && Double.isNaN((Double)ret)) ) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				parms.put("self", ret);
				ret = this.transformation.eval(parms);
			}
			return ret;
		} catch (ThinklabValidationException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	protected void setGridExtent(GridExtent extent) {
		this.gridExtent = extent;
	}

	@Override
	public IConcept getValueType() {
		// TODO could be numbers or knowledge
		return null;
	}

	@Override
	public Object getInitialValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataSource<?> transform(IDatasourceTransformation transformation)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void postProcess(IObservationContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preProcess(IObservationContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<String, Object> getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}


}
