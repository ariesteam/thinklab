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

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class ObservationFactory {

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
	 * Add a new instance of an observable. If null is passed, see if we can
	 * define a meaningful observable automatically. 
	 * 
	 * @param observation
	 * @param observableClass
	 * @return
	 */
	public static Polylist setObservable(Polylist observation, String observableClass) {
		
		return observation.appendElement(
				Polylist.list(
						CoreScience.HAS_OBSERVABLE, 
						Polylist.list(observableClass)));
	}
	
	/**
	 * Return an identification of the given observable
	 * @param observableClass
	 * @return
	 */
	public static Polylist createIdentification(String idType, String observableClass) {
		
		return Polylist.list(idType, 
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						Polylist.list(observableClass)));
	}
	
	/**
	 * Return an identification of the given type of the given observable
	 * @param observableClass
	 * @return
	 */
	public static Polylist createIdentification(String idType, Polylist observable) {

		return Polylist.list(idType, 
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						observable));
	}

	/**
	 * Return an identification of the given observable
	 * @param observableClass
	 * @return
	 */
	public static Polylist createIdentification(String observableClass) {
		
		return Polylist.list(CoreScience.IDENTIFICATION, 
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						Polylist.list(observableClass)));
	}
	
	/**
	 * Return an identification of the given observable
	 * @param observableClass
	 * @return
	 */
	public static Polylist createIdentification(Polylist observable) {
		
		return Polylist.list(CoreScience.IDENTIFICATION, 
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						observable));
	}

	/**
	 * 
	 * @param observation
	 * @param dependent
	 * @return
	 */
	public static Polylist addDependency(Polylist observation, Polylist dependent) {
		
		return observation.appendElement(
				Polylist.list(CoreScience.DEPENDS_ON, dependent));	
	}
	
	/**
	 * 
	 * @param observation
	 * @param dependent
	 * @return
	 */
	public static Polylist addDependency(Polylist observation, IInstance dependent) {
		
		return observation.appendElement(
				Polylist.list(CoreScience.DEPENDS_ON, dependent));
	}
	
	/**
	 * 
	 * @param observation
	 * @param dependent
	 * @return
	 */
	public static Polylist addContingency(Polylist observation, Polylist dependent) {
		
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_CONTINGENCY, dependent));
	}
	
	/**
	 * 
	 * @param observation
	 * @param dependent
	 * @return
	 */
	public static Polylist addContingency(Polylist observation, IInstance dependent) {
		
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_CONTINGENCY, dependent));
	}
	
	/**
	 * 
	 * @return
	 */
	public static Polylist createIdentification() {
		
		return Polylist.list(CoreScience.IDENTIFICATION);		
	}

	/**
	 * Return the concept that this observation is observing.
	 * @param data
	 * @return
	 * @throws ThinklabException 
	 */
	public static IConcept getObservableClass(IInstance data) throws ThinklabException {
		return ((IObservation)(data.getImplementation())).getObservableClass();
	}

	/**
	 * Add the given observable definition to the given observation spec.
	 * 
	 * @param observation
	 * @param observableSpecs
	 * @return
	 */
	public static Polylist setObservable(Polylist observation, Polylist observableSpecs) {
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_OBSERVABLE, observableSpecs));
	}

	/**
	 * Add the given mediated definition
	 * 
	 * @param observation
	 * @param mediated
	 * @return
	 */
	public static Polylist addMediatedObservation(Polylist observation, Polylist mediated) {
		return observation.appendElement(
				Polylist.list(CoreScience.MEDIATES_OBSERVATION, mediated));
	}

	/**
	 * Return the associated IObservation from an instance, making sure it's actually an observation.
	 * 
	 * @param o
	 * @return
	 * @throws ThinklabException 
	 */
	public static IObservation getObservation(IInstance o) throws ThinklabException {
		
		Object iret = o.getImplementation();
		
		if (iret == null || !(iret instanceof IObservation))
			throw new ThinklabValidationException("object " + o.getLocalName() + " is not an observation");
		
		return (IObservation)iret;
	}

	/**
	 * Add an extent to the passed obs
	 * 
	 * @param observation
	 * @param extent
	 * @return
	 */
	public static Polylist addExtent(Polylist observation, Polylist extent) {
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_EXTENT, extent));
	}
	

	/**
	 * Add an aux observation in same context for provenance recording. 
	 * 
	 * @param observation
	 * @param extent
	 * @return
	 */
	public static Polylist addSameContextObservation(Polylist observation, Polylist obs) {
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_SAME_CONTEXT_ANTECEDENT, obs));
	}
	
	/**
	 * Add an aux observation in same context for provenance recording. 
	 * 
	 * @param observation
	 * @param extent
	 * @return
	 */
	public static Polylist addSameContextObservation(Polylist observation, IInstance obs) {
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_SAME_CONTEXT_ANTECEDENT, obs));
	}

	/**
	 * 
	 * @param obs
	 * @return
	 */
	public static boolean isSpatiallyDistributed(IObservation obs) {
		return ((Observation)obs).getExtent(Geospace.get().SpaceObservable()) != null;
	}

	/**
	 * 
	 * @param obs
	 * @return
	 */
	public static IObservation getSpatialExtent(IObservation obs) {
		return ((Observation)obs).getExtent(Geospace.get().SpaceObservable());
	}

	/**
	 * 
	 * @param obs
	 * @return
	 */
	public static boolean isRaster(IObservation obs) {
		return 
			((Observation)obs).getExtent(Geospace.get().SpaceObservable()) != null &&
			((Observation)obs).getExtent(Geospace.get().SpaceObservable()) instanceof RasterGrid;
	}

	/**
	 * 
	 * @param obs
	 * @return
	 */
	public static RasterGrid getRasterGrid(IObservation obs) {
		return (RasterGrid)((Observation)obs).getExtent(Geospace.get().SpaceObservable());
	}

	
	
	
}
