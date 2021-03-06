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
package org.integratedmodelling.geospace.implementations.observations;

import java.util.Hashtable;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.ObservationFactory;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.gis.ThinklabRasterizer;
import org.integratedmodelling.geospace.interfaces.IGeolocatedObject;
import org.integratedmodelling.geospace.interfaces.IGridMask;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.utils.MalformedListException;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * An observation class that represents a gridded view of space, perfect to serve
 * as the spatial extent observation of another observation. Will create all its
 * conceptual model etc. from the OWL specs, so it's typically all you need to
 * define to provide a raster spatial context to an observation.
 * 
 * @author Ferdinando Villa
 */
@InstanceImplementation(concept="geospace:RasterGrid")
public class RasterGrid extends Observation implements Topology, IGeolocatedObject {

	int xRO, xRM, yRO, yRM;
	double latLB, lonLB, latUB, lonUB;
	CoordinateReferenceSystem crs;
	private String crsId = null;
	
	// public so it can be set through reflection
	public GridExtent extent;
	public IGridMask mask;
	public ShapeValue shape;

	@Override
	public String toString() {
		return ("raster-grid("  + getRows() + " x " + getColumns() +")");
	}
	
	public void initialize(IInstance i) throws ThinklabException {

		/*
		 * link the observable - do it now, so that super.initialize() finds it.
		 * NOTE: if we subclass the observable to a grid-specific one, no 
		 * rasterization of vector coverages will take place.
		 */
		i.addObjectRelationship(
					CoreScience.HAS_OBSERVABLE, 
//					Geospace.get().absoluteRasterGridInstance());
					Geospace.get().absoluteSpatialCoverageInstance(i.getOntology()));
		
				
		// read requested parameters from properties
		for (IRelationship r : i.getRelationships()) {
			
			/* for speed */
			if (r.isLiteral()) {
				
				if (r.getProperty().equals(Geospace.X_RANGE_OFFSET)) {
					xRO = r.getValue().asNumber().asInteger();
				} else if (r.getProperty().equals(Geospace.X_RANGE_MAX)) {
					xRM = r.getValue().asNumber().asInteger();
				} else if (r.getProperty().equals(Geospace.Y_RANGE_OFFSET)) {
					yRO = r.getValue().asNumber().asInteger();
				} else if (r.getProperty().equals(Geospace.Y_RANGE_MAX)) {
					yRM = r.getValue().asNumber().asInteger();
				} else if (r.getProperty().equals(Geospace.LAT_LOWER_BOUND)) {
					latLB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.LON_LOWER_BOUND)) {
					lonLB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.LAT_UPPER_BOUND)) {
					latUB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.LON_UPPER_BOUND)) {
					lonUB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.CRS_CODE)) {
					crsId = r.getValue().toString();
				} 			
			}
		}

		if (crsId != null) 
			crs = Geospace.getCRSFromID(crsId);
		
		super.initialize(i);
		
		// may have been put there through reflection if the grid comes from the conceptualization
		// of a GridExtent. In such cases, it may hold the lineage of the grid in terms of 
		// rasterized shapes and vector coverages.
		if (extent == null) 
			this.extent = new GridExtent(crs,lonLB, latLB, lonUB, latUB, xRM - xRO, yRM - yRO);
		
		// TODO check the shape (which may have been put there through reflection). If there,
		// compute the mask.
		this.extent.shape = this.shape;
		if (shape != null && mask == null)
			mask(shape);
		this.extent.setActivationLayer(this.mask);
		
	}
	
	public int getColumns() {
		return xRM - xRO;
	}

	public int getRows() {
		return yRM - yRO;
	}
	
	/**
	 * Determine the width and height (in cells) of the bounding box for the passed
	 * shape when we want the max linear resolution to be the passed one and the
	 * cells square. Using the envelope converted to meters to keep the cell 
	 * roughly square. 
	 * 
	 * @param shape
	 * @param maxLinearResolution
	 * @return
	 * @throws ThinklabException 
	 */
	public static Pair<Integer, Integer> getRasterBoxDimensions(ShapeValue shape, int majorAxisResolution) throws ThinklabException {
		
		ReferencedEnvelope env = 
				Geospace.get().squareCellsM() ? 
						shape.convertToMeters().getEnvelope() :
						shape.getEnvelope();
		
		int x = 0, y = 0;
		if (env.getWidth() > env.getHeight()) {
			x = majorAxisResolution;
			y = (int)Math.ceil(majorAxisResolution * (env.getHeight()/env.getWidth()));
		} else {
			y = majorAxisResolution;
			x = (int)Math.ceil(majorAxisResolution * (env.getWidth()/env.getHeight()));			
		}
		
		return new Pair<Integer, Integer>(x,y);
	}
	
	/**
	 * Compute the x,y resolution for a grid encompassing the given shape and approximating
	 * a linear X resolution as passed in the string, which represents a length with units
	 * such as 100m or 1km.
	 * 
	 * @param shape
	 * @param linearResolution
	 * @return
	 * @throws ThinklabException 
	 */
	public Pair<Integer, Integer> getSubdivisions(ShapeValue shape, String linearResolution) throws ThinklabException {
		
		int x = 0, y = 0;
		
		int idx = 0;
		for (idx = 0; idx < linearResolution.length(); idx++)
			if (Character.isLetter(linearResolution.charAt(idx)))
				break;
		
		String val = linearResolution.substring(0,idx).trim();
		String uni = linearResolution.substring(idx).trim();
		
		Unit<?> unit = Unit.valueOf(uni);
		UnitConverter converter = unit.getConverterTo(SI.METER);
		double value = Double.parseDouble(val);
		
		double meters = converter.convert(value);
		shape = shape.convertToMeters();
		
		ReferencedEnvelope env = shape.getEnvelope();
		
		if (env.getWidth() > env.getHeight()) {
			x = (int)((env.getMaxX() - env.getMinX())/meters);
			y = (int)(x * (env.getHeight()/env.getWidth()));
		} else {
			y = (int)((env.getMaxY() - env.getMinY())/meters);
			x = (int)(y * (env.getWidth()/env.getHeight()));			
		}
				
		return new Pair<Integer, Integer>(x, y);
	}
	
	/**
	 * Create a raster grid for the given shape using the native resolution specified
	 * in the passed grid.
	 * 
	 * @param shape
	 * @param original
	 * @return
	 * @throws ThinklabException 
	 */
	public static Polylist createRasterGrid(ShapeValue shape, RasterGrid original) throws ThinklabException {
		
		double xsize = (original.getRight() - original.getLeft())/original.getColumns();
		double ysize = (original.getTop() - original.getBottom())/original.getRows();
		
		ReferencedEnvelope env = shape.transform(original.crs).getEnvelope();
		
		int xc = (int) (env.getWidth()/xsize);
		int yc = (int) (env.getHeight()/ysize);
		
		return createRasterGrid(shape, xc, yc);
	}
	
	/**
	 * Create the rastergrid definition that will define the envelope of the passed
	 * shape, with the passed max resolution as the resolution of the longest
	 * dimension and the other dimension defined in order to keep the cells square.
	 * Maximum raster res will be <= maxLinearResolution^2. If resolution is 0,
	 * the grid will have 0 size, meaning that the native resolution of the data
	 * will be used.
	 * 
	 * @param shape
	 * @param maxLinearResolution
	 * @return
	 * @throws ThinklabException
	 */
	public static Polylist createRasterGrid(ShapeValue shape, int maxLinearResolution) throws ThinklabException {

		/*
		 * calculate aspect ratio and define resolution from it
		 */
		Pair<Integer, Integer> xy = 
					getRasterBoxDimensions(shape, maxLinearResolution);
		
		return createRasterGrid(shape, xy.getFirst(), xy.getSecond());
	}


	/**
	 * Create the rastergrid definition that will define the envelope of the passed
	 * shape
	 */
	public static Polylist createRasterGrid(ShapeValue shape, int xcells, int ycells) throws ThinklabException {

		Polylist ret = null;
		
		/*
		 * Create the list representation of a RasterGrid object and substitute the 
		 * envelope values in it.
		 */
		String grid = 
				"(geospace:RasterGrid" + 
				"	(geospace:hasXRangeOffset $xRangeOffset)" + 
				"	(geospace:hasXRangeMax $xRangeMax)" + 
				"	(geospace:hasYRangeOffset $yRangeOffset)" + 
				"	(geospace:hasYRangeMax $yRangeMax)" + 
				"	(geospace:hasCoordinateReferenceSystem $crsCode)" + 
				"	(geospace:hasLatLowerBound $latLowerBound)" + 
				"	(geospace:hasLonLowerBound $lonLowerBound)" + 
				"	(geospace:hasLatUpperBound $latUpperBound)" + 
				"	(geospace:hasLonUpperBound $lonUpperBound))";
		
		Hashtable<String, Object> sym = new Hashtable<String, Object>();

		ReferencedEnvelope env = shape.getEnvelope();

		/*
		 * fv 11/2010: force the cell to square, so that primitive software
		 * (Arc-Info) does not stop working with the data we produce.
		 */
		double csize = (env.getMaxX() - env.getMinX())/xcells;
		
		sym.put("xRangeOffset", 0);
		sym.put("xRangeMax", xcells);
		sym.put("yRangeOffset", 0);
		sym.put("yRangeMax", ycells);
		sym.put("crsCode", Geospace.getCRSIdentifier(shape.getCRS(), true));
		sym.put("latLowerBound", env.getMinY());
		sym.put("lonLowerBound", env.getMinX());
		// adjusted to fit the cell size; this will at most add to the bounding box 
		sym.put("latUpperBound", env.getMinY()+ycells*csize); 
		sym.put("lonUpperBound", env.getMaxX());
		
		try {
			ret = Polylist.parseWithTemplate(grid, sym);
		} catch (MalformedListException e) {
			throw new ThinklabInternalErrorException(e);
		}
		
		return ObservationFactory.addReflectedField(ret, "shape", shape);
	}

	public double getTop() {
		return latUB;
	}

	public double getRight() {
		return lonUB;
	}

	public double getLeft() {
		return lonLB;
	}

	public double getBottom() {
		return latLB;
	}

	public String getCRSId() {
		return crsId;
	}

	@Override
	public IExtent getExtent() throws ThinklabException {
		return extent;
	}

	/**
	 * Get the x,y coordinates corresponding to linear index 
	 * @param index
	 * @return
	 */
	public int[] getXYCoordinates(int index) {
		int xx = index % getColumns();
		int yy = getRows() - (index / getColumns()) - 1;
		return new int[]{xx, yy};
	}

	/**
	 * Get the x,y coordinates corresponding to linear index in a grid of
	 * given size.
	 * 
	 * @param index
	 * @return
	 */
	public static int[] getXYCoordinates(int index, int width, int height) {
		int xx = index % width;
		int yy = height - (index / width) - 1;
		return new int[]{xx, yy};
	}
	
	/**
	 * Get the linear index corresponding to given coordinates.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getIndex(int x, int y) {
		return (x * getColumns()) + y;
	}

	@Override
	public ShapeValue getBoundingBox() {
		try {
			 ReferencedEnvelope e = Geospace.normalizeEnvelope(
					extent.getDefaultEnvelope().transform(
							Geospace.get().getDefaultCRS(), true, 10), 
							Geospace.get().getDefaultCRS());

			return new ShapeValue(e);
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public ShapeValue getCentroid() {
		return getShape().getCentroid();
	}

	@Override
	public ShapeValue getShape() {
		return shape == null ? getBoundingBox() : shape;
	}

	public void mask(ShapeValue roi) throws ThinklabException {
		this.mask = ThinklabRasterizer.createMask(roi, extent);
		this.shape = roi;
	}
	
	public IGridMask getMask() {
		return this.mask;
	}

	@Override
	public void checkUnitConformance(
			IConcept concept, 
			org.integratedmodelling.corescience.units.Unit unit)
			throws ThinklabValidationException {
		
		if (CoreScience.isExtensive(concept) && !unit.isArealDensity()) {

// TODO reintegrate when we do things like 	"mm of precipitation" properly
//			throw new ThinklabValidationException(
//					"concept " + 
//					concept + 
//					" is observed in 2d-space but unit " + 
//					unit + 
//					" does not specify an areal density");
		}
	}

	
}
