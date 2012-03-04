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
package org.integratedmodelling.geospace.extents;

import java.util.Collection;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IRelationship;
import org.integratedmodelling.thinklab.api.knowledge.query.IRestriction;
import org.integratedmodelling.thinklab.api.lang.IOperator;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.IDataSource.Transformation;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.units.IUnit;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.modelling.ObservationImpl;
import org.integratedmodelling.thinklab.modelling.units.Unit;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * An observation class that represents a view of space subdivided into irregular,
 * discontinuous areal extents. 
 * @author Ferdinando Villa
 */
@InstanceImplementation(concept="geospace:ArealFeatureSet")
public class SpatialCoverage extends ObservationImpl implements IExtent {

	double latLB, lonLB, latUB, lonUB;
	CoordinateReferenceSystem crs;
	private ShapeExtent extent;
	
//	public void initialize(IInstance i) throws ThinklabException {
//
//		/*
//		 * link the obvious observable - do it now, so that super.initialize() finds it.
//		 */
//		i.addObjectRelationship(
//					CoreScience.HAS_OBSERVABLE, 
//					Geospace.get().absoluteSpatialCoverageInstance(i.getOntology()));
//		
//		String crsId = null;
//				
//		// read requested parameters from properties
//		for (IRelationship r : i.getRelationships()) {
//			
//			/* for speed */
//			if (r.isLiteral()) {
//				
//				if (r.getProperty().equals(Geospace.LAT_LOWER_BOUND)) {
//					latLB = r.getValue().asNumber().asDouble();
//				} else if (r.getProperty().equals(Geospace.LON_LOWER_BOUND)) {
//					lonLB = r.getValue().asNumber().asDouble();
//				} else if (r.getProperty().equals(Geospace.LAT_UPPER_BOUND)) {
//					latUB = r.getValue().asNumber().asDouble();
//				} else if (r.getProperty().equals(Geospace.LON_UPPER_BOUND)) {
//					lonUB = r.getValue().asNumber().asDouble();
//				} else if (r.getProperty().equals(Geospace.CRS_CODE)) {
//					crsId = r.getValue().toString();
//				} 			
//			}
//		}
//
//		if (crsId != null)
//			crs = Geospace.getCRSFromID(crsId);
//		
//		super.initialize(i);
//
//		this.extent = new ShapeExtent(this.getBoundary(), crs);
//	}
	
	ReferencedEnvelope getBoundary() {
		return new ReferencedEnvelope(lonLB, lonUB, latLB, latUB, crs);
	}

//	@Override
	public IExtent getExtent() throws ThinklabException {
		return extent;
	}

//	@Override
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
	
//	@Override
	public ShapeValue getCentroid() {
		return getBoundingBox().getCentroid();
	}

//	@Override
	public ShapeValue getShape() {
		return getBoundingBox();
	}

//	@Override
	public void checkUnitConformance(IConcept concept, Unit unit)
			throws ThinklabValidationException {
		
		if (!unit.isArealDensity()) {
			// TODO reintegrate when we do things like 	"mm of precipitation" properly
//			throw new ThinklabValidationException(
//					"concept " + 
//					concept + 
//					" is observed in 2d-space but unit " + 
//					unit + 
//					" does not specify an areal density");
		}
	}

	@Override
	public IDataSource getDatasource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDoubleValue(int index) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getValueCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IConcept getObservableClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IState aggregate(IConcept concept) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSpatiallyDistributed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTemporallyDistributed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMultiplicity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IExtent intersection(IExtent other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent union(IExtent other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(IExtent o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean overlaps(IExtent o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(IExtent o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IExtent collapse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent getExtent(int stateIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCovered(int stateIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IRestriction getConstraint(IOperator operator)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transformation getDatasourceTransformation(IConcept mainObservable,
			IExtent extent) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Pair<String, Integer>> getStateLocators(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDiscontinuous() throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IExtent force(IExtent extent) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AggregationParameters getAggregationParameters(IConcept concept,
			IUnit unit) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
}
