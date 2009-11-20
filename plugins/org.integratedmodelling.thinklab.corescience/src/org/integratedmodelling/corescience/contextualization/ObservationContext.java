/**
 * ObservationContext.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience.contextualization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.integratedmodelling.corescience.exceptions.ThinklabContextValidationException;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.utils.Ticker;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.utils.LogicalConnector;
import org.integratedmodelling.utils.Polylist;

/**
 * An observation context represents the cartesian product of all topologies
 * that are in the context of an observation. Each "dimension" of the context
 * is the topology of one observable, and it has its own internal dimensionality.
 * 
 * @author Ferdinando Villa
 */
public class ObservationContext implements IObservationContext {

	IObservation observation;
	Hashtable<String, IExtent> extents = new Hashtable<String, IExtent>();
	ArrayList<IConcept> order = new ArrayList<IConcept>();
	int totalSize = -1;
	int[] dimensionalities = null;
	boolean _initialized = false;
	
	@Override
	public String toString() {
		return 
			"observation-context(" + extents + "): " + 
			observation.getObservationClass().getLocalName() + 
			"(" + observation.getObservableClass() + ")";
	}
	
	public class TopologyIterator implements Iterator<IObservationContext> {

		Ticker ticker = new Ticker();

		public TopologyIterator() {
			
			for (int i : dimensionalities)
				ticker.addDimension(i);
		}
		
		@Override
		public boolean hasNext() {
			return ticker.expired();
		}

		@Override
		public IObservationContext next() {
			ObservationContext ret = new ObservationContext(observation);
			int i = 0;
			for (IConcept c : order) {
				IExtent e = extents.get(c);
				if (e != null)
					ret.extents.put(
							c.toString(), 
							e.getExtent(ticker.current(i++)));
			}
			ticker.increment();
			
			ret.initialize();
			return ret;
			
		}

		@Override
		public void remove() {
		}
		
	}
	
	public ObservationContext(IObservation mainObservation) {
		observation = mainObservation;
	}
	
	private void sortContext() {
			
		order.clear();
		
		for (String ss : extents.keySet())
			try {
				order.add(KnowledgeManager.get().retrieveConcept(ss));
			} catch (ThinklabException e) {
			}
		
		/* TODO sort. Is it fair to think that if two extent concepts have an ordering 
		 * relationship, they should know about each other? So that we can implement the
		 * ordering as a relationship between extent observation classes? */
		
	}
	
	/*
	 * must be called after all extents have been merged in. Defines dimensionalities.
	 */
	public void initialize()  {
		
		if (_initialized)
			return;
		
		sortContext();
		
		dimensionalities = new int[extents.size()];
		totalSize = 1;
		
		int i = 0;
		for (IConcept s : order) {
									
			IExtent extent = extents.get(s.toString());
			int gr = extent.getTotalGranularity();
			dimensionalities[i++] = gr;
			totalSize *= gr;
		}		
		_initialized = true;
	}

//	public void mergeExtent(IObservation observation, IConcept dimension, LogicalConnector connector, boolean isConstraint) 
//		throws ThinklabException {
//
//		// retrieve conceptual model of obs and ensure it is an extent model
//		IConceptualModel cmv = observation.getConceptualModel();
//		
//		if (!(cmv instanceof ExtentConceptualModel))
//			throw new ThinklabContextValidationException("extent relationship on " +
//					observation + 
//					" does not link to an extent observation: " +
//					dimension);
//
//		ExtentConceptualModel cm = (ExtentConceptualModel)cmv;
//		
//		// see if we already have an extent for this dimension
//		IExtent extent = extents.get(dimension.toString());
//		
//		if (extent == null) {
//			
//			/* just add the extent */
//			IExtent newExt = cm.getExtent();
//			extents.put(dimension.toString(), newExt);
//		
//		} else {
//
//			/* ask CM to modify the current extent record in order to represent the
//			   new one as well. */
//			IExtent merged = cm.mergeExtents(extent, cm.getExtent(), connector, isConstraint);
//			extents.put(dimension.toString(), merged);
//		}		
//		
//	}


	public Collection<IConcept> getDimensions() {
		return order;
	}

	public IConcept getDimension(IConcept concept) throws ThinklabException {
		
		IConcept ret = null;
		
		// FIXME use class tree
		for (IConcept c : order) {
			if (c.is(concept)) {
				if (ret != null)
					throw new ThinklabContextValidationException(
							"ambiguous request: context contains more than one dimension of type " +
							concept);
				ret = c;
			}
		}
		
		return ret;
	}

 
	public int getMultiplicity() {
		return totalSize;
	}


	public int getMultiplicity(IConcept dimension) throws ThinklabException {
		return 
			extents.get(getDimension(dimension).toString()).getTotalGranularity();
	}

	public IExtent getExtent(IConcept c) {
		
		IConcept dim = null;
		try {
			dim = getDimension(c);
		} catch (ThinklabException e) {
		}
		
		return dim == null ? null : extents.get(dim.toString());
	}

	
	public void mergeExtents(ObservationContext coo, LogicalConnector connector, boolean isConstraint) 
		throws ThinklabException {

		/* take all extents in foreign context and merge with appropriate
		 * extent.
		 */
		for (String entry : coo.extents.keySet()) {
			
			// see if we already have an extent for this dimension
			IExtent extent = extents.get(entry);
			IExtent foreign = coo.extents.get(entry);
			
			if (extent == null) {
				/* just add the extent */
				extents.put(entry, foreign);
			} else {
				// ask CM to modify the current extent record in order to represent the
				// new one as well.
				IExtent ext = extent.getConceptualModel().mergeExtents(extent, foreign, connector, isConstraint);
				if (ext != null)
					extents.put(entry,ext);
			}					
		}	
	}

	/**
	 * For debugging
	 * @param prefix 
	 * @throws ThinklabException 
	 */
	public void dump(String prefix) throws ThinklabException {

		if (totalSize == -1) {
			System.out.println(prefix + "Dumping observation context: CONTEXT NOT INITIALIZED. Exiting.");
			return;
		}
		
		System.out.println(
				prefix + 
				"Dumping observation context: " + 
				extents.size() +
 				" total dimensions, " +
 				getMultiplicity() + 
 				" total states");
		
		System.out.println("\n" + prefix + "Extents in order of contextualization:");
		
		for (IConcept c : order) {
			System.out.println(
					prefix + 
					"\tExtent dimension: " +
					c +
					": " +
					getMultiplicity(c) +
					" states [" +
					getExtent(c) + 
					"]");
		}
		
	}

	public int size() {
		return extents.size();
	}

	public int[] getDimensionSizes() {
		return dimensionalities;
	}

	@Override
	public Polylist conceptualizeExtent(IConcept c) throws ThinklabException {
		
		Polylist ret = null;
		IExtent extent = getExtent(c);
		
		if (extent != null) {
			if (extent instanceof IConceptualizable) {
				ret = ((IConceptualizable)extent).conceptualize();
			} else {
				throw new ThinklabInappropriateOperationException(
						"extent of type " + c + " cannot be turned into an observation");
			}
		}
		return ret;
	}


	@Override
	public IObservationContext remapExtents(IObservationContext ctx) {
	
		ObservationContext ret = 
			new ObservationContext(((ObservationContext)ctx).observation);
		
		for (IConcept c : getDimensions()) {
			IExtent extent = ctx.getExtent(c);
			if (extent != null)
				ret.extents.put(c.toString(), extent);
		}
		
		/* fv nov 09: add those I don't have - TODO check */
		for (IConcept c : ctx.getDimensions()) {
			IExtent extent = getExtent(c);
			if (extent == null)
				ret.extents.put(c.toString(), ctx.getExtent(c));
		}
		
		ret.initialize();

		return ret;
	}

	@Override
	public Iterator<IObservationContext> iterator() {
		return new TopologyIterator();
	}

	public void mergeExtent(IObservation extent, LogicalConnector connector) throws ThinklabException {
		
		// retrieve conceptual model of obs and ensure it is an extent model
		IConceptualModel cmv = extent.getConceptualModel();
		IConcept dimension = Observation.getContextDimension(extent);
		
		if (!(cmv instanceof ExtentConceptualModel))
			throw new ThinklabContextValidationException("extent relationship on " +
					observation + 
					" does not link to an extent observation: " +
					dimension);

		ExtentConceptualModel cm = (ExtentConceptualModel)cmv;
		
		// see if we already have an extent for this dimension
		IExtent ext = extents.get(dimension.toString());
		
		if (ext == null) {
			
			/* just add the extent */
			IExtent newExt = cm.getExtent();
			extents.put(dimension.toString(), newExt);
		
		} else {

			/* ask CM to modify the current extent record in order to represent the
			   new one as well. 
			   FIXME make sure the isConstraint parameter is necessary and if so, correct.
			   */
			IExtent merged = cm.mergeExtents(ext, cm.getExtent(), connector, false);
			if (merged != null)
				extents.put(dimension.toString(), merged);
		}		

	}

	
}
