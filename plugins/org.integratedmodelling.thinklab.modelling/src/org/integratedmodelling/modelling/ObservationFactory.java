/**
 * ObservationFactory.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Apr 7, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDataBridgePlugin.
 * 
 * ThinklabDataBridgePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDataBridgePlugin is distributed in the hope that it will be useful,
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
 * @date      Apr 7, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.modelling;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.Polylist;

public class ObservationFactory extends org.integratedmodelling.corescience.ObservationFactory {

	/**
	 * Return a constraint to select an observation of the passed observable
	 * concept. 
	 * 
	 * @param what
	 * @return
	 * @throws ThinklabException 
	 */
	public static Constraint queryObservation(String what) throws ThinklabException {
		
		return 
			new Constraint(CoreScience.OBSERVATION)
				.restrict(new Restriction(
						CoreScience.HAS_OBSERVABLE, new Constraint(what)));
	}

	public static IObservation findObservation(IConcept observable, ISession session, Topology ... extents)
		throws ThinklabException {
	
		IObservation ret = null;
		Constraint c = queryObservation(observable, extents);
		IQueryResult qret = KBoxManager.get().query(c);	
		if (qret.getTotalResultCount() > 0) {
			ret = getObservation(qret.getResult(0, session).asObjectReference().getObject());
		}
		return ret;
	}
	
	public static Constraint queryObservation(IConcept observable, Topology ... extents) throws ThinklabException {

		Constraint c = new Constraint(CoreScience.Observation());
		
		c = c.restrict(
				new Restriction(CoreScience.HAS_OBSERVABLE, new Constraint(observable)));

		if (extents.length > 0) {
			
			ArrayList<Restriction> er = new ArrayList<Restriction>();
			for (Topology o : extents) {
				Restriction r = o.getConstraint("contains");
				if (r != null)
					er.add(r);
			}
			
			if (er.size() > 0) {
				c = c.restrict(
						er.size() == 1 ? 
							er.get(0) : 
							Restriction.AND(er.toArray(new Restriction[er.size()])));
			}
		}
		
		return c;
	}
	
	/**
	 * Return a constraint to select an observation of the passed observable
	 * concept in a given space. 
	 * 
	 * @param what
	 * @param where a ShapeValue or other value that can be used to refer to space
	 * @return
	 * @throws ThinklabException 
	 */
	public static Constraint queryObservation(String what, IValue where) throws ThinklabException {

		Constraint c = new Constraint(CoreScience.OBSERVATION);
		
		c.restrict(new Restriction(CoreScience.HAS_OBSERVABLE, new Constraint(what)));

		return c;
	}
	
	/**
	 * Return a constraint to select an observation of the passed observable
	 * concept in a given space and time. 
	 * 
	 * @param what
	 * @param where
	 * @param when
	 * @return
	 * @throws ThinklabException 
	 */
	public static Constraint queryObservation(String what, IValue where, IValue when) throws ThinklabException {

		Constraint c = new Constraint(CoreScience.OBSERVATION);
		
		c.restrict(new Restriction(CoreScience.HAS_OBSERVABLE, new Constraint(what)));

		return c;	
	}
	
	/**
	 * Return a constraint to select an observation of the passed observable
	 * concept in a given space. Use the passed kbox's metadata instead of
	 * constraining the context if the metadata contain any spatial knowledge.
	 * 
	 * @param kbox
	 * @param what
	 * @param where
	 * @return
	 * @throws ThinklabException 
	 */
	public static Constraint queryObservation(IKBox kbox, String what, IValue where) throws ThinklabException {

		Constraint c = new Constraint(CoreScience.OBSERVATION);
		
		c.restrict(new Restriction(CoreScience.HAS_OBSERVABLE, new Constraint(what)));

		return c;
	}
	
	/**
	 * Return a constraint to select an observation of the passed observable
	 * concept in a given space and time. Use the passed kbox's metadata instead of
	 * constraining the context if the metadata contain any spatial and/or 
	 * temporal knowledge.
	 * 
	 * @param kbox
	 * @param what
	 * @param where
	 * @param when
	 * @return
	 * @throws ThinklabException 
	 */
	public static Constraint queryObservation(IKBox kbox, String what, IValue where, IValue when) throws ThinklabException {
	
		Constraint c = new Constraint(CoreScience.OBSERVATION);
		
		c.restrict(new Restriction(CoreScience.HAS_OBSERVABLE, new Constraint(what)));

		return c;
	}
	
	
	/**
	 * Add the given spatial context to this observation and return its list
	 * representation.
	 * 
	 * @param observation
	 * @param where
	 * @return
	 */
	public static Polylist setSpatialContext(Polylist observation, IValue where) {
		return observation;
	}
	
	/**
	 * Add the given spatial context as a raster grid to this observation and return
	 * its list representation.
	 * 
	 * @param observation
	 * @param where
	 * @param maxLinearResolution the finest linear resolution we want. The longest
	 * 	bounding box dimension will have that many subdivisions; the shortest will
	 *  have as many as necessary to keep the cells as close to square as possible.
	 *  Maximum total cell number is guaranteed to be <= maxLinearResolution^2.
	 * @return
	 * @throws ThinklabException 
	 */
	public static Polylist setSpatialContext(Polylist observation, 
			ShapeValue where, int maxLinearResolution) throws ThinklabException {

		return observation.appendElement(
				Polylist.list(
						CoreScience.HAS_EXTENT,
						RasterGrid.createRasterGrid(where, maxLinearResolution)));
	}

	/**
	 * Add a temporal context to reflect the passed value.
	 * 
	 * @param observation
	 * @param when
	 * @return
	 */
	public static Polylist setTemporalContext(Polylist observation, IValue when) {
		return observation;
	}
	
	/**
	 * 
	 * @param obs
	 * @return
	 */
	public static boolean isSpatiallyDistributed(IObservation obs) {
		return obs.getExtent(Geospace.get().SpaceObservable()) != null;
	}

	/**
	 * 
	 * @param obs
	 * @return
	 */
	public static IObservation getSpatialExtent(IObservation obs) {
		return obs.getExtent(Geospace.get().SpaceObservable());
	}

	/**
	 * 
	 * @param obs
	 * @return
	 */
	public static boolean isRaster(IObservation obs) {
		return 
			obs.getExtent(Geospace.get().SpaceObservable()) != null &&
			obs.getExtent(Geospace.get().SpaceObservable()) instanceof RasterGrid;
	}

	/**
	 * 
	 * @param obs
	 * @return
	 */
	public static RasterGrid getRasterGrid(IObservation obs) {
		return (RasterGrid)obs.getExtent(Geospace.get().SpaceObservable());
	}

	/**
	 * 
	 * @param observation
	 * @param ds
	 * @return
	 */
	public static Polylist addDatasource(Polylist observation, Polylist ds) {
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_DATASOURCE, ds));
	}
	
	/**
	 * 
	 * @param extents
	 * @return
	 */
	public static IObservationContext buildContext(ArrayList<Topology> extents) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param observation
	 * @param id
	 * @return
	 */
	public static Polylist addFormalName(Polylist observation, String id) {
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_FORMAL_NAME, id));
	}
	

	/**
	 * Return a contingency handler for the given observable
	 * @param observableClass
	 * @return
	 */
	public static Polylist createStatefulContingencyMerger(Polylist observable) {
		
		return Polylist.list(ModellingPlugin.STATEFUL_MERGER_OBSERVATION, 
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						observable));
	}

	public static Polylist createStatelessContingencyMerger(Polylist observable) {
		
		return Polylist.list(CoreScience.STATELESS_MERGER_OBSERVATION, 
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						observable));
	}
	
}
