/**
 * IExtent.java
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
package org.integratedmodelling.corescience.interfaces;

import java.util.Collection;

import org.integratedmodelling.corescience.implementations.observations.Measurement.PhysicalNature;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Pair;

/**
 * An Extent describes the topology of the observable
 * it's linked to. Conceptual models are capable of producing unions or intersections
 * of extents.
 * 
 * Extents must be conceptualizable and the result of conceptualizing
 * them must be an observation describing the extent.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract interface IExtent extends IState, ITopologicallyComparable {

	public static interface Aggregator {
		public abstract double getAggregationFactor(int granule, double value);
	}
	
	/**
	 * Returned by getAggregationParameters
	 * @author ferdinando.villa
	 *
	 */
	public static class AggregationParameters {
		Aggregator     aggregator;
		Unit           aggregatedUnit;
		PhysicalNature aggregatedNature;
		String         aggregationOperator;
		String         uncertaintyOperator;
	}
	
	/**
	 * Return the value that is the union of all granules, aggregated in the way that makes
	 * sense for the particular conceptual domain.
	 * @deprecated use getAggregatedExtent().getState(0)
	 * @return
	 */
	public IValue getFullExtentValue();
	
	/**
	 * Return the 1-dimensional extent that corresponds to the full extent of our topology.
	 * @return
	 */
	public IExtent getAggregatedExtent();
	

	/**
	 * Return the n-th member of the ordered topology.
	 * @param granule
	 * @return
	 */
	public IExtent getExtent(int granule);
	
	/**
	 * merge with the passed extent of the same observable into a new extent and return it.
	 * Merging is always an intersection operation - return the largest common extent with 
	 * the finest common grain.
	 * @deprecated use intersection
	 */
	public IExtent and(IExtent extent) throws ThinklabException;

	/**
	 * Add the passed extent so that the end result represents both. 
	 * 
	 * @param myExtent
	 * @return
	 * @deprecated use union
	 */
	public IExtent or(IExtent myExtent);
	
	
	/**
	 * Return a copy of our extent constrained by the passed one. Constraining is also an 
	 * intersection but the grain in the final extent should become the same as the 
	 * constraining extent's.
	 * 
	 * @param extent
	 * @return
	 * @throws ThinklabException 
	 * @deprecated do not use - intersection, union, and force should do the job
	 */
	public IExtent constrain(IExtent extent) throws ThinklabException;


	/*
	 * FIXME still rough - return a restriction that will match observations with
	 * similar topology, using the parameter to define the relationship (which should be
	 * a formal enum or topological operator)
	 */
	public abstract Restriction getConstraint(String operator) throws ThinklabException;

	
	/**
	 * Return the transformation, if any, that will be necessary to operate on a 
	 * datasource that conforms to us so that it matches the passed extent.
	 * 
	 * @param mainObservable the observable for the main observation that owns the extent
	 * 		  (what the states mean)
	 * @param extent the extent we must adapt the datasource to
	 * @return a transformation to be passed to the datasource
	 * @throws ThinklabException 
	 */
	public IDatasourceTransformation getDatasourceTransformation(
			IConcept mainObservable, IExtent extent) throws ThinklabException;


	/**
	 * Create a string signature that has no spaces, represents the extent accurately,
	 * and is the same for extents that are equal. Used to cache data across runs and
	 * within runs of the same model.
	 * 
	 * @return
	 */
	public abstract String getSignature();

	/**
	 * Return a list of location references to use in expressions and
	 * their relative offsets in the dimension described by this extent.
	 * For example, a time grid extent will return "previous, -1" so that
	 * the previous state can be referenced in expressions (with syntax
	 * dependent on the language: e.g. in the Clojure accessor, for state
	 * "altitude", the var will be :altitude/previous). Space grids should
	 * return things like n,s,e,w, etc. with offsets determined by the
	 * specific configuration.
	 * 
	 * The list of locators depends on the specific location, so the overall
	 * index in the dimension is passed.
	 * 
	 * If no locators can be defined, a null should be returned.
	 * @return
	 */
	public abstract Collection<Pair<String,Integer>> getStateLocators(int index);

	/**
	 * Should return whether the coverage of the domain is discontinuous to the
	 * point of breaking the internal rules of the represented topologies. E.g.,
	 * if we represent continuous uninterrupted space, discontinuities will break
	 * neighborhood relationships that models may need to count on. This may come
	 * as a result of intersecting partial extents.
	 * 
	 * @return
	 */
	public boolean checkDomainDiscontinuity() throws ThinklabException;


	/**
	 * Return an extent which represents the intersection of this with the passed
	 * one. NOTE: it is expected that the intersected extent can be index-remapped to
	 * a sub-extent of this. Meaning that subdivisions etc. need to be in sync.
	 * 
	 * @param myExtent
	 * @return
	 */
	public IExtent intersection(IExtent extent) throws ThinklabException;

	/**
	 * Return an extent which represents the union of this with the passed
	 * one. Same note as intersect(): the union extent must be usable together with
	 * this through an index remap operation.
	 * 
	 * @param myExtent
	 * @return
	 */
	public IExtent union(IExtent extent) throws ThinklabException;

	/**
	 * Return an extent that is capable of representing the passed one 
	 * exactly. If the passed one is of the same class, it can just return
	 * the passed one, but it's provided to give the extent a chance of 
	 * adjustments or of raising errors. 
	 * 
	 * @param extent
	 * @return
	 * @throws ThinklabException
	 */
	public IExtent force(IExtent extent) throws ThinklabException;

	/**
	 * Return a descriptor of how aggregation should be performed in this
	 * extent for a value of the passed type. If a unit is associated, it
	 * must be capable of creating the unit of the aggregated concept, which
	 * will eliminate the dimension we represent if the concept is an
	 * extensive one.
	 * 
	 * @param concept
	 * @param unit
	 * @return
	 */
	public abstract AggregationParameters getAggregationParameters(IConcept concept, Unit unit);
	
}