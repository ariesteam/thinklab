/**
 * RegularRasterModel.java
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
 * ${project_name} is distributed in the hope that it will be useful,
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
 * @author    Ferdinando Villa
 * @date      Feb 18, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/

package org.integratedmodelling.geospace.implementations.cmodels;

import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.implementations.data.RasterCoverageAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

@InstanceImplementation(concept=Geospace.RASTER_CONCEPTUAL_MODEL)
public class RegularRasterModel extends SubdividedCoverageConceptualModel {

	int xRangeOffset = 0;
	int xRangeMax = 0;
	int yRangeOffset = 0;
	int yRangeMax = 0;
	
	RasterCoverage coverage = null;
	GridExtent extent = null;

	/**
	 * The laborious manual constructor for raster model
	 * 
	 * @param xRangeOffset
	 * @param xRangeMax
	 * @param yRangeOffset
	 * @param yRangeMax
	 * @param latLowerBound
	 * @param latUpperBound
	 * @param lonLowerBound
	 * @param lonUpperBound
	 * @param crsID
	 */
	public RegularRasterModel(
			int xRangeOffset, int xRangeMax, 
			int yRangeOffset, int yRangeMax, 
			double latLowerBound, double latUpperBound, 
			double lonLowerBound, double lonUpperBound,
			CoordinateReferenceSystem crs) {

		this.xRangeOffset = xRangeOffset;
		this.xRangeMax = xRangeMax;
		this.yRangeOffset = yRangeOffset;
		this.yRangeMax = yRangeMax;		

		setBoundary(latUpperBound, latLowerBound, lonUpperBound, lonLowerBound);
		setCRS(crs);
	
		extent = new GridExtent(this, crs,
				lonLowerBound, latLowerBound, lonUpperBound, latUpperBound, 
				xRangeMax - xRangeOffset, yRangeMax - yRangeOffset);
		
	}

	public RegularRasterModel() {
		// TODO Auto-generated constructor stub
	}

	public void initialize(IInstance i, Properties properties) throws ThinklabException {

		super.initialize(i, properties);
		
		// read requested parameters from properties
		for (IRelationship r : i.getRelationships()) {
			
			/* for speed */
			if (r.isLiteral()) {
				
				if (r.getProperty().equals(Geospace.X_RANGE_OFFSET)) {
					xRangeOffset = r.getValue().asNumber().asInteger();
				} else if (r.getProperty().equals(Geospace.X_RANGE_MAX)) {
					xRangeMax = r.getValue().asNumber().asInteger();
				} else if (r.getProperty().equals(Geospace.Y_RANGE_OFFSET)) {
					yRangeOffset = r.getValue().asNumber().asInteger();
				} else if (r.getProperty().equals(Geospace.Y_RANGE_MAX)) {
					yRangeMax = r.getValue().asNumber().asInteger();
				}
			}
		}
		
		setBoundary(latUpperBound, latLowerBound, lonUpperBound, lonLowerBound);
	
		extent = new GridExtent(this, getCRS(),
				lonLowerBound, latLowerBound, lonUpperBound, latUpperBound, 
				xRangeMax - xRangeOffset, yRangeMax - yRangeOffset);
	}

	public IExtent getExtent() throws ThinklabException {
		return extent;
	}

	public IExtentMediator getExtentMediator(IExtent extent)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArealExtent createMergedExtent(ArealExtent orextent,
			ArealExtent otextent, CoordinateReferenceSystem ccr,
			Envelope common, boolean isConstraint) throws ThinklabException {

		if ( !(orextent instanceof GridExtent && otextent instanceof GridExtent)) {
			throw new ThinklabUnimplementedFeatureException("RasterModel: cannot yet merge extents of different types");
		}

		GridExtent orext = (GridExtent)orextent;
		GridExtent otext = (GridExtent)otextent;
		
		/* we'll fix the resolution later  */
		GridExtent nwext = 
			new GridExtent(this, ccr, common.getMinX(), common.getMinY(), common.getMaxX(), common.getMaxY(), 1, 1);

		// this is the constrained resolution; we recompute it below if we are free to choose
		int xc = otext.getXCells();
		int yc = otext.getYCells();
		
		// phase errors in both directions due to choosing resolutions that do not
		// match exactly. This is computed but not used right now. We could set what to 
		// do with it as a property: e.g., log a warning or even raise an exception if nonzero.
		double errx = 0.0;
		double erry = 0.0;
		
		if (!isConstraint) {
		
			/* choose the smallest of the reprojected cells unless we're constrained to accept otextent */
			Envelope cor = null;
			Envelope cot = null; 
		
			try {
			
				cor = orext.getCellEnvelope(0, 0).transform(ccr, true, 10);
				cot = otext.getCellEnvelope(0, 0).transform(ccr, true, 10);
						
			} catch (Exception e) {
				throw new ThinklabValidationException(e);
			}
		
			double aor = cor.getHeight() * cor.getWidth();
			double aot = cot.getHeight() * cot.getWidth();
		
			/*
			 * We take the finest res
			 */
			Envelope cell = aor < aot ? cor : cot;
	
			// System.out.println("cells are " + cor + " and " + cot + "; chosen " + cell + " because areas are " + aor + " and " + aot);
		
			/* recompute the number of cells in the new extent */
			xc = (int)Math.floor(nwext.getEnvelope().getWidth()/cell.getWidth());
			yc = (int)Math.floor(nwext.getEnvelope().getHeight()/cell.getHeight());

			errx = nwext.getEnvelope().getWidth() - (cell.getWidth() * xc);
			erry = nwext.getEnvelope().getHeight() - (cell.getHeight() * yc);

		} else {
			
			/* 
			 * TODO
			 * just compute the error, which we're not using right now;
			 * so compute it another time.
			 */
			
		}
		
		// System.out.println("new cell size is " + xc + "," + yc);
		
		// TODO use the error, make sure it's not larger than too much
		// System.out.println("errors are " + errx + "," + erry);
		
		nwext.setResolution(xc, yc);
		
		System.out.println("extent is now " + nwext);
		
		
		return nwext;
	}

	public int getColumns() {
		return xRangeMax - xRangeOffset;
	}

	public int getRows() {
		return yRangeMax - yRangeOffset;
	}

	@Override
	public IStateAccessor getStateAccessor(IConcept stateType, IObservationContext context) {
		// TODO Auto-generated method stub - should return an accessor to grid extents
		return null;
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
		
		// TODO get the coverage from the ds; make sure dimensions are
		// coherent
		
	}

}
