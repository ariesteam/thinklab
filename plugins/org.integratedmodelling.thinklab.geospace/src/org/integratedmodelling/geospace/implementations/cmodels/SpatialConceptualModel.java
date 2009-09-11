package org.integratedmodelling.geospace.implementations.cmodels;

import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.corescience.exceptions.ThinklabConceptualModelValidationException;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.utils.LogicalConnector;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public abstract class SpatialConceptualModel implements IConceptualModel, ExtentConceptualModel, IInstanceImplementation  {

	private CoordinateReferenceSystem crs;

	protected double latUpperBound = 0.0;
	protected double latLowerBound = 0.0;
	protected double lonUpperBound = 0.0;
	protected double lonLowerBound = 0.0;
	
	/**
	 * Choose the CRS we prefer - either one of the two or yet another one, depending on plugin configuration.
	 * Will be called at each mergeExtent, possibly with identical CRSs. 
	 * @param crs1
	 * @param crs2
	 * @return
	 */
	public static CoordinateReferenceSystem chooseCRS(CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2) {

		CoordinateReferenceSystem ret = Geospace.get().getPreferredCRS();
		
		if (ret == null) {
			ret = crs1;
			Geospace.get().setPreferredCRS(ret);
		}
		
		return ret;
	}
	
	protected void setCRS(CoordinateReferenceSystem crs) {
		this.crs = crs;
	}
	
	public CoordinateReferenceSystem getCRS() {
		return crs;
	}
	
	public IExtent mergeExtents(IExtent original, IExtent other,
			LogicalConnector connector, boolean constrainExtent)
			throws ThinklabException {
		
		if ( !(other instanceof ArealExtent) || !(original instanceof ArealExtent)) {
			throw new ThinklabUnimplementedFeatureException("areal extent can only merge other areal extents");
		}
		
		ArealExtent orextent = (ArealExtent)original;
		ArealExtent otextent = (ArealExtent)other;
		
		CoordinateReferenceSystem crs1 = orextent.getCRS();
		CoordinateReferenceSystem crs2 = otextent.getCRS();
		
		CoordinateReferenceSystem ccr = chooseCRS(crs1, crs2);;
		
		ReferencedEnvelope env1 = orextent.getEnvelope();
		ReferencedEnvelope env2 = otextent.getEnvelope();
		
		if (!(crs1.equals(crs2) && ccr.equals(crs1))) {
		
			/*
			 * make sure we're talking the same numbers
			 */
			
			try {
				env1 = env1.transform(ccr, true, 10);
				env2 = env2.transform(ccr, true, 10);
				
			} catch (Exception e) {
				throw new ThinklabConceptualModelValidationException(e);
			}
		}
		
		/* 
		 * set a new envelope to the intersection or union of the original ones after reprojecting them to
		 * the common crs. 
		 */
		Envelope common = env2;
		
		if (connector.equals(LogicalConnector.INTERSECTION))
			common = env1.intersection(env2); 
		else {
			/* no union in frickin' JTS envelope */
			Envelope2D e1 = new Envelope2D(env1);
			Envelope2D e2 = new Envelope2D(env2);
			Envelope2D ee = new Envelope2D(env1);
			Envelope2D.union(e1, e2, ee);
			common = new Envelope(ee.getMinX(), ee.getMaxX(), ee.getMinY(), ee.getMaxY());
		}
		

		/**
		 * Here we send out to a virtual to create the appropriate areal extent with this envelope and CRS, 
		 * adding whatever else we need to use it.
		 */
		return createMergedExtent(orextent, otextent, ccr, common, constrainExtent);
	}

	
	protected void setBoundary(double latUpperBound, double latLowerBound,
			double lonUpperBound, double lonLowerBound) {

		this.latUpperBound = latUpperBound;
		this.latLowerBound = latLowerBound;
		this.lonUpperBound = lonUpperBound;
		this.lonLowerBound = lonLowerBound;

	}
	
	ReferencedEnvelope getBoundary() {
		return new ReferencedEnvelope(lonLowerBound, lonUpperBound, latLowerBound, latUpperBound, crs);
	}

	/**
	 * Called to create a new areal extent which merges two others, using the appropriate type and reflecting the given
	 * needs. We pass pre-computed envelope, reference system, and any transformations needed to mediate the original
	 * extents into the new one.
	 *  
	 * @param orextent The original extent that was asked to mediate the other.
	 * @param otextent The other extent.
	 * @param crs The NEW coordinate reference system, chosen according to configuration. May or may not be equal
	 * 			  to any of the CRS embedded in the original extents, and must be the one that the returned extent
	 * 			  adopts.
	 * @param envelope The NEW envelope that the new extent must reflect. May be changed if necessary to adjust for
	 * 				   precision or such.
	 * @param transf1 A transformation that mediates the CRS of the original extent into the passed crs. Passed here because it
	 * 				  was computed already and may be expensive to compute. Will not be null, may be identity.
	 * @param transf2 A transformation that mediates the CRS of the other extent into the passed crs. 
	 * @return a new extent that adopts the best of the passed recommendations.
	 * @throws ThinklabException just in case.
	 */
	protected abstract IExtent createMergedExtent(ArealExtent orextent,
			ArealExtent otextent, CoordinateReferenceSystem crs2,
			Envelope common, boolean otherExtentConstrainsOurs) throws ThinklabException;


}
