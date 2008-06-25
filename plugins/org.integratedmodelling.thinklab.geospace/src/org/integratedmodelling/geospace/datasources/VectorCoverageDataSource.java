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
package org.integratedmodelling.geospace.datasources;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationContextState;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.cmodel.FeatureCoverageModel;
import org.integratedmodelling.geospace.cmodel.RegularRasterModel;
import org.integratedmodelling.geospace.cmodel.SubdividedCoverageConceptualModel;
import org.integratedmodelling.geospace.coverage.CoverageFactory;
import org.integratedmodelling.geospace.coverage.ICoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.extents.ShapeExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IRelationship;
import org.integratedmodelling.thinklab.interfaces.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.Pair;

/**
 * TODO it's just a verbatim copy of the raster one for now.
 * @author Ferdinando
 *
 */
public class VectorCoverageDataSource extends CoverageDataSource {

	/**
	 * The conceptual model that defines the data we need to return, saved at handshaking
	 */
	private IConceptualModel dataCM = null;
	private ICoverage coverage = null;

	/* we can have either of these, but not both */
	private RegularRasterModel rasterCM = null;
	private FeatureCoverageModel vectorCM = null;

	/* same here - these are overall extents that we need to conform to */
	private GridExtent gridExtent;
	private ShapeExtent shapeExtent;
	
	public boolean handshake(IConceptualModel cm,
			IObservationContext observationContext,
			IObservationContext overallContext)
			throws ThinklabException {
		
		dataCM = cm;
		
		IExtent extent = overallContext.getExtent(Geospace.SubdividedSpaceObservable());
		IConceptualModel cmodel = extent.getConceptualModel();
		
		System.out.println(extent);

		/*
		 * See what we have to deal with overall. 
		 */
		if (cmodel instanceof RegularRasterModel) {

			this.rasterCM = (RegularRasterModel) cmodel;
			gridExtent = (GridExtent)extent;
			
		} else if (cmodel instanceof FeatureCoverageModel) {
			
			this.vectorCM = (FeatureCoverageModel) cmodel;
			this.shapeExtent = (ShapeExtent)extent;
			
			// communicate the features to the extent, so that we can compute multiplicity and overall shape
			this.shapeExtent.setFeatures(((VectorCoverage)coverage).getFeatures(), coverage.getSourceUrl());

		} else {
			throw new ThinklabValidationException("raster datasource cannot work with conceptual model " + cm.getClass());
		}
		
		/*
		 * if raster, we may need to adjust the coverage to the extent for CRS, bounding box, and resolution.
		 */
		if (gridExtent != null) {
			
			/*
			 * this will rasterize our vector coverage. If it's a raster, we should then fall back to
			 * raster methods when data are accessed. For the way this is defined, the coverage class
			 * should automatically take care of that.
			 */
			coverage = coverage.requireMatch(gridExtent, true);

			/*
			 * ask for the main extent's activation layer (creating an inactive
			 * default if not there) and AND our active areas with it.
			 */
			defineActivationLayer(
					gridExtent.requireActivationLayer(true), gridExtent);
		}
		
		// if we get to handshaking, we need to load the data
		coverage.loadData();
		
		// whatever happens, we can definitely use indexes here, so return true.
		return true;
	}

	public void initialize(IInstance i) throws ThinklabException {

		// these are compulsory
		String sourceURL = null;
		String valueAttr = null;

		// these are only needed if we use an external attribute table
		String dataURL = null;
		String sourceAttr = null;
		String targetAttr = null;
		
		// read requested parameters from properties
		for (IRelationship r : i.getRelationships()) {
			
			if (r.isLiteral()) {
				
				if (r.getProperty().equals(Geospace.COVERAGE_SOURCE_URL)) {
					sourceURL = r.getValue().toString();
				} else if (r.getProperty().equals(Geospace.HAS_SOURCE_LINK_ATTRIBUTE)) {
					sourceAttr = r.getValue().toString();
				} else if (r.getProperty().equals(Geospace.HAS_TARGET_LINK_ATTRIBUTE)) {
					targetAttr = r.getValue().toString();
				} else if (r.getProperty().equals(Geospace.HAS_VALUE_ATTRIBUTE)) {
					valueAttr = r.getValue().toString();
				} else if (r.getProperty().equals(Geospace.HAS_ATTRIBUTE_URL)) {
					dataURL = r.getValue().toString();
				}
			}
		}

		// check data
		if (sourceURL == null || valueAttr == null)
			throw new ThinklabValidationException("vector coverage: specifications are invalid (source url or value attribute missing)");

		if (dataURL != null && ( sourceAttr == null || targetAttr == null))
			throw new ThinklabValidationException("vector coverage: specifications are invalid (no link attributes for external data table)");

		try {

			Properties properties = new Properties();
			
			properties.setProperty(CoverageFactory.VALUE_ATTRIBUTE_PROPERTY, valueAttr);
			
			if (dataURL != null) {
				properties.setProperty(CoverageFactory.ATTRIBUTE_URL_PROPERTY, dataURL);
				properties.setProperty(CoverageFactory.SOURCE_LINK_ATTRIBUTE_PROPERTY, sourceAttr);
				properties.setProperty(CoverageFactory.TARGET_LINK_ATTRIBUTE_PROPERTY, targetAttr);
			}
			
			this.coverage = CoverageFactory.requireCoverage(new URL(sourceURL), properties);
			
		} catch (MalformedURLException e) {
			throw new ThinklabIOException(e);
		}
		
	}

	public Pair<IValue, IUncertainty> getInitialValue() {
		return null;
	}

	public Pair<IValue, IUncertainty> getValue(
			IObservationContextState context, IConcept concept,
			boolean useExtentIndex) throws ThinklabValidationException {
		
		IValue val = null;
		// TODO
		IUncertainty unc = null;
		if (useExtentIndex) {
			
			// FIXME harmonize the long/int thing here
			val = coverage.getSubdivisionValue(
					(int) context.getLinearIndex(), 
					dataCM, 
					gridExtent == null ? shapeExtent : gridExtent);
			
		} else {
			
			// TODO
			throw new ThinklabValidationException("vector coverage doesn't yet support non-indexed access");
			
		}
		
		return new Pair<IValue, IUncertainty>(val, unc);
	}

	public Pair<String, IUncertainty> getValueLiteral(
			IObservationContextState context, IConcept concept,
			boolean useExtentIndex) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueType getValueType() {
		// TODO check if we need to return literals conditionally
		return IDataSource.ValueType.IVALUE;
	}

	public void validate(IInstance i) throws ThinklabException {

		if (coverage != null) {
			
		} else {
			
			// TODO we should support inline data
			throw new ThinklabValidationException("raster datasource: no coverage specified");		}
		
	}

	private SubdividedCoverageConceptualModel getConceptualModel() {
		return vectorCM == null ? rasterCM : vectorCM;
	}

}
