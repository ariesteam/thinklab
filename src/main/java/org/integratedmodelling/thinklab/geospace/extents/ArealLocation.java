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

import java.util.Collection;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.modelling.lang.Observation;

/**
 * This one is technically unnecessary, as ArealFeatureSet subsumes it, but we keep it 
 * for now as only ArealLocation admits literals, and this makes things easier.
 * @author UVM Affiliate
 *
 */
@Concept("geospace:ArealLocation")
public class ArealLocation extends Observation implements IExtent {

	ShapeValue boundingBox = null;
	ShapeValue shape = null;
	ShapeValue centroid = null;
	
//	@Override
	public ShapeValue getBoundingBox() {
		return boundingBox;
	}

//	@Override
	public ShapeValue getCentroid() {
		return centroid;
	}

//	@Override
	public ShapeValue getShape() {
		return shape;
	}

	@Override
	public String toString() {
		return ("areal-location(" + shape.getBoundingBox()+")");
	}

//	@Override
	public IExtent getExtent() throws ThinklabException {
		return new ShapeExtent(shape.getGeometry(), 
				shape.getGeometry().getEnvelopeInternal(), shape.getCRS());
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

//	@Override
//	public void checkUnitConformance(IConcept concept, Unit unit)
//			throws ThinklabValidationException {
//		
//		if (!unit.isArealDensity())
//			throw new ThinklabValidationException(
//					"concept " + 
//					concept + 
//					" is observed in 2d-space but unit " + 
//					unit + 
//					" does not specify an areal density");
//	}
	
}
