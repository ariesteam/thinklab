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
package org.integratedmodelling.thinklab.geospace.extents;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.integratedmodelling.thinklab.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.modelling.Observation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public abstract class ArealExtent extends Observation implements IExtent {

	public abstract ShapeValue getShape();
	
//	@Override
//	public boolean contains(ITopologicallyComparable o)
//			throws ThinklabException {
//		ArealExtent e = (ArealExtent) o;
//		return getBoundingBox().getGeometry().contains(e.getBoundingBox().getGeometry());
//
//	}
//
//	public abstract double getTotalAreaSquareMeters();
//	
//	@Override
//	public boolean intersects(ITopologicallyComparable o)
//			throws ThinklabException {
//		ArealExtent e = (ArealExtent) o;
//		return getBoundingBox().getGeometry().intersects(e.getBoundingBox().getGeometry());
//	}
//
//	@Override
//	public boolean overlaps(ITopologicallyComparable o)
//			throws ThinklabException {
//		ArealExtent e = (ArealExtent) o;
//		return getBoundingBox().getGeometry().overlaps(e.getBoundingBox().getGeometry());
//	}

	// the envelope in here is always east-west on the X axis. getDefaultEnvelope() can be used to retrieve
	// the envelope that will work with the CRS. 
	ReferencedEnvelope envelope = null;
	CoordinateReferenceSystem crs;
	
	public ArealExtent(CoordinateReferenceSystem crs, double minx, double miny, double maxx, double maxy) {
		this.crs = crs;
		this.envelope = new ReferencedEnvelope(minx, maxx, miny, maxy, crs);
	}
	
	public ArealExtent(ShapeValue shape) {
		this.envelope = shape.getEnvelope();
		this.crs = shape.getCRS();
	}
	
	public CoordinateReferenceSystem getCRS() {
		return crs;
	}

	/**
	 * Get an envelope where the X axis is always east-west.
	 * @return
	 */
	public ReferencedEnvelope getEnvelope() {
		return envelope;
	}
	
	public ShapeValue getBoundingBox() {
		try {
			 ReferencedEnvelope e =
					envelope.transform(Geospace.get().getDefaultCRS(), true, 10);
			return new ShapeValue(e);
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	public ShapeValue getCentroid() {
		return getBoundingBox().getCentroid();
	}
	
	public boolean contains(Coordinate c) {
		return envelope.contains(c);
	}

	/**
	 * Get the envelope with the same axis order of the passed CRS.
	 * @return
	 */
	public ReferencedEnvelope getDefaultEnvelope(CoordinateReferenceSystem ocr) {
		
		ReferencedEnvelope ret = envelope;
		
		AxisDirection myOrder = crs.getCoordinateSystem().getAxis(0).getDirection();
		AxisDirection otOrder = ocr.getCoordinateSystem().getAxis(0).getDirection();
		
		if (!otOrder.equals(myOrder)) {
			/*
			 * swap x/y to obtain the right envelope according to the CRS
			 */
			ret = new ReferencedEnvelope(
					ret.getMinY(), ret.getMaxY(),
					ret.getMinX(), ret.getMaxX(), crs);
		} 
		
		return ret;
	}

	public double getNorth() {
		return envelope.getMaxY();
	}

	public double getWest() {
		return envelope.getMinX();
	}

	public double getSouth() {
		return envelope.getMinY();
	}

	public double getEast() {
		return envelope.getMaxX();
	}
	
	public double getEWExtent() {
		return envelope.getWidth();
	}

	public double getNSExtent() {
		return envelope.getHeight();
	}

	@Override
	public String toString() {
		return "areal-extent(" + envelope + "," + crs.getName() + ")";
	}

	/**
	 * @deprecated
	 */
	Object[] computeCommonExtent(IExtent extent) throws ThinklabException {

		if (! (extent instanceof ArealExtent))
			throw new ThinklabValidationException(
					"areal extent " + this + " cannot be merged with non-areal extent " + extent);
		
		ArealExtent orextent = this;
		ArealExtent otextent = (ArealExtent)extent;
		
		CoordinateReferenceSystem crs1 = orextent.getCRS();
		CoordinateReferenceSystem crs2 = otextent.getCRS();
		
		CoordinateReferenceSystem ccr = Geospace.chooseCRS(crs1, crs2);
		ReferencedEnvelope env1 = orextent.getEnvelope();
		ReferencedEnvelope env2 = otextent.getEnvelope();
		
		if (!(crs1.equals(crs2) && ccr.equals(crs1))) {
		
			/*
			 * transformations will swap axes as required so we want the
			 * envelopes with the CRS's axis order
			 */
			env1 = orextent.getEnvelope();
			env2 = otextent.getEnvelope();
			
			try {
				/*
				 * transformations will return axes swapped to what CRS defines, and we 
				 * want them normalized back to east-west on x before we use them
				 */
				env1 = env1.transform(ccr, true, 10);
				env2 = env2.transform(ccr, true, 10);
				
			} catch (Exception e) {
				throw new ThinklabValidationException(e);
			}
		}
		
		/* 
		 * At this point the two envelopes are in the same CRS and with east-west on the X axis.
		 * Set a new envelope to the intersection of the original ones after reprojecting them to
		 * the common crs. 
		 */
		Envelope common = env1.intersection(env2); 
		return new Object[] {orextent, otextent, ccr, common, env1, env2};
		
	}
	
//	@Override
	public IExtent and(IExtent extent) throws ThinklabException {

		Object[] cc = computeCommonExtent(extent);
		
		ArealExtent orextent = (ArealExtent) cc[0];
		ArealExtent otextent = (ArealExtent) cc[1];
		CoordinateReferenceSystem ccr = (CoordinateReferenceSystem) cc[2];
		Envelope common = (Envelope) cc[3];
		Envelope orenvnorm = (Envelope) cc[4];
		Envelope otenvnorm = (Envelope) cc[5];
		
		/*
		 * TODO intersection may be empty - this should be checked in createMergedExtent instead
		 * of cursing here.
		 */
		if (common.isNull()) {
			return null;
		}
		
		/**
		 * send out to a virtual to create the appropriate areal extent with this envelope and CRS, 
		 * adding whatever else we need to use it.
		 */
		return createMergedExtent(orextent, otextent, ccr, common, orenvnorm, otenvnorm);
	}
	
//	@Override
	public IExtent constrain(IExtent extent) throws ThinklabException {

		Object[] cc = computeCommonExtent(extent);
		
		ArealExtent orextent = (ArealExtent) cc[0];
		ArealExtent otextent = (ArealExtent) cc[1];
		CoordinateReferenceSystem ccr = (CoordinateReferenceSystem) cc[2];
		Envelope common = (Envelope) cc[3];
		Envelope orenvnorm = (Envelope) cc[4];
		Envelope otenvnorm = (Envelope) cc[5];
		
		/*
		 * TODO intersection may be empty - this should be checked in createMergedExtent instead
		 * of cursing here.
		 */
		if (common.isNull()) {
			return null;
		}
		
		/**
		 * send out to a virtual to create the appropriate areal extent with this envelope and CRS, 
		 * adding whatever else we need to use it.
		 */
		return createConstrainedExtent(orextent, otextent, ccr, common, orenvnorm, otenvnorm);
	}

	/**
	 * Does the actual work of merging with the extent, after merge() has ensured that the extents
	 * are spatial and have a common intersection and crs.
	 * 
	 * @param orextent
	 * @param otextent
	 * @param ccr
	 * @param common
	 * @param otenvnorm 
	 * @param orenvnorm 
	 * @return
	 * @throws ThinklabException 
	 */
	protected abstract IExtent createMergedExtent(
			ArealExtent orextent, ArealExtent otextent,
			CoordinateReferenceSystem ccr, Envelope common, Envelope orenvnorm, Envelope otenvnorm) throws ThinklabException;

	/**
	 * Does the actual work of merging with the extent, ensuring that the grain of the
	 * extent is the same as that of the constraining extent. Called after merge() has ensured 
	 * that the extents are spatial and have a common intersection and crs.
	 * 
	 * @param orextent
	 * @param otextent
	 * @param ccr
	 * @param common
	 * @param otenvnorm 
	 * @param orenvnorm 
	 * @return
	 * @throws ThinklabException 
	 */
	protected abstract IExtent createConstrainedExtent(
			ArealExtent orextent, ArealExtent otextent,
			CoordinateReferenceSystem ccr, Envelope common, Envelope orenvnorm, Envelope otenvnorm) throws ThinklabException;

//	/* (non-Javadoc)
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof IExtent) {
//			return getSignature().equals(((IExtent)obj).getSignature());
//		}
//		return false;
//	}
//
//	/* (non-Javadoc)
//	 * @see java.lang.Object#hashCode()
//	 */
//	@Override
//	public int hashCode() {
//		return getSignature().hashCode();
//	}

	@Override
	public IConcept getObservableClass() {
		return Geospace.get().SubdividedSpaceObservable();
	}

	
	@Override
	public boolean isSpatiallyDistributed() {
		return getValueCount() > 1;
	}

	@Override
	public boolean isTemporallyDistributed() {
		return false;
	}
	

}
