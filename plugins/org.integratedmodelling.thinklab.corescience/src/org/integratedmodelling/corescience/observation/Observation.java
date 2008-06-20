/**
 * Observation.java
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
package org.integratedmodelling.corescience.observation;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.CoreSciencePlugin;
import org.integratedmodelling.corescience.contextualization.ObservationContext;
import org.integratedmodelling.corescience.exceptions.ThinklabContextValidationException;
import org.integratedmodelling.corescience.interfaces.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.IContextualizationWorkflow;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationState;
import org.integratedmodelling.corescience.workflow.DefaultWorkflow;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.IRelationship;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.LogicalConnector;

/**
 * Base implementation for an Observation instance. Initializes the observation
 * and its context, defers operations to subclasses. Gives API access to data,
 * contexts, conceptual model and uncertainty model.
 * 
 * @author Ferdinando Villa
 * 
 */
public class Observation implements IObservation, IInstanceImplementation {

	/*
	 * these can be both objects implementations or literals, coming from OWL,
	 * so we store the value and convert on usage. Observation structures built
	 * internally (e.g. from literals) will have these as null, and must provide
	 * their own DS and CM.
	 */
	private IValue dataSourceHolder = null;
	private IValue conceptualModelHolder = null;
	private IValue observationStateHolder = null;
	
	protected IDataSource dataSource = null;
	protected IConceptualModel conceptualModel = null;
	protected IKnowledgeSubject observable = null;
	protected IInstance observation = null;
	protected IInstance dataSourceValue = null;
	
	// the two below MAY be filled in by the contextualization workflow. 
	protected IObservationState currentState = null;
	protected IObservationContext currentContext = null;
	
	protected IObservation[] contingencies = new IObservation[0];
	protected IObservation[] dependencies = new IObservation[0];
	protected IObservation[] extentDependencies = new IObservation[0];
	protected IObservation[] nonExtentDependencies = new IObservation[0];
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.corescience.IObservation#getDataSource()
	 */
	public IDataSource getDataSource() throws ThinklabException {

		if (dataSource == null && dataSourceHolder != null) {

			if (dataSourceHolder.isObjectReference())
				dataSource = (IDataSource) dataSourceHolder.asObjectReference()
						.getObject().getImplementation();
			else
				dataSource = (IDataSource) dataSourceHolder;
		}
		return dataSource;
	}

	public IConceptualModel getConceptualModel() throws ThinklabException {

		if (conceptualModel == null && conceptualModelHolder != null) {

			if (conceptualModelHolder.isObjectReference())
				conceptualModel = (IConceptualModel) conceptualModelHolder
						.asObjectReference().getObject().getImplementation();
			else
				conceptualModel = (IConceptualModel)conceptualModelHolder;
		}

		return conceptualModel;
	}

	public IObservationState getObservationState() throws ThinklabException {

		if (currentState == null && observationStateHolder != null) {

			if (observationStateHolder.isObjectReference())
				currentState = (IObservationState) observationStateHolder
						.asObjectReference().getObject().getImplementation();
			else
				currentState = (IObservationState)observationStateHolder;
		}

		return currentState;
	}

	
	/**
	 * Create an appropriate conceptual model if it is missing. This one is not
	 * relevant to observations whose CM is defined in OWL. Those whose CM can
	 * be implicit, like identifications or any observation created through a
	 * literal, should implement this one.
	 * 
	 * @return a new conceptual model value for the observation.
	 * @throws ThinklabException if anything goes wrong
	 */
	public IConceptualModel createMissingConceptualModel() throws ThinklabException {
		return null;
	}

	public void validate(IInstance i) throws ThinklabException {

		/*
		 * if we had no conceptual model, have the derived obs create an
		 * appropriate one.
		 */
		if (conceptualModel == null)
			conceptualModel = createMissingConceptualModel();
		
		/*
		 * FIXME for now we allow a null CM. We'll see if that's viable.
		 */
		if (conceptualModel != null)
			conceptualModel.validate(this);

	}

	public void initialize(IInstance i) throws ThinklabException {

		/*
		 * this one is easy
		 */
		observation = i;
		
		ArrayList<IObservation> dep = new ArrayList<IObservation>(); 
		ArrayList<IObservation> con = new ArrayList<IObservation>(); 
		ArrayList<IObservation> ext = new ArrayList<IObservation>(); 
		ArrayList<IObservation> nxt = new ArrayList<IObservation>(); 

		/* locate and store various related for efficiency. This method is faster than
		 * getting piece by piece.
		 */
		for (IRelationship r : i.getRelationships()) {
			
			/* again, for speed */
			if (!r.isClassification()) {
				if (observable == null && r.getProperty().is(CoreSciencePlugin.HAS_OBSERVABLE)) {

					observable = 
						r.getValue().isObjectReference() ? 
							r.getValue().asObjectReference().getObject() : 
							r.getValue().getConcept();					
							
				} else if (dataSourceHolder == null && r.getProperty().is(CoreSciencePlugin.HAS_DATASOURCE)) {
					dataSourceHolder = r.getValue();
				} /*
				   * for now, state is merely a product of contextualization
				   else if (observationStateHolder == null && r.getProperty().is(CoreSciencePlugin.HAS_OBSERVATION_STATE)) {
					observationStateHolder = r.getValue();
				} */
				else if (conceptualModelHolder == null && r.getProperty().is(CoreSciencePlugin.HAS_CONCEPTUAL_MODEL)) {
					conceptualModelHolder = r.getValue();
				} else if (r.getProperty().is(CoreSciencePlugin.DEPENDS_ON)) {
					
					dep.add(
							(IObservation)
							r.getValue().asObjectReference().getObject().getImplementation());
					
					if (r.getProperty().is(CoreSciencePlugin.HAS_EXTENT)) {
						ext.add(
								(IObservation)
								r.getValue().asObjectReference().getObject().getImplementation());						
					} else {
						nxt.add(
								(IObservation)
								r.getValue().asObjectReference().getObject().getImplementation());						
					}
					
				} else if (r.getProperty().is(CoreSciencePlugin.HAS_CONTINGENCY)) {
					
					con.add(
							(IObservation)
							r.getValue().asObjectReference().getObject().getImplementation());					
				}

			}
		}

		if (con.size() > 0)
			contingencies = con.toArray(contingencies);

		if (dep.size() > 0)
			dependencies = dep.toArray(dependencies);
		
		if (ext.size() > 0)
			extentDependencies = ext.toArray(extentDependencies);

		if (nxt.size() > 0)
			nonExtentDependencies = nxt.toArray(nonExtentDependencies);

		conceptualModel = getConceptualModel();
		
	}

	public IConcept getObservableClass() {
		return observable.getType();
	}

	public IKnowledgeSubject getObservable() {
		return observable;
	}

	public IObservation[] getContingencies() {
		return contingencies;
	}

	/**
	 * Get all the dependencies, extents and not
	 */
	public IObservation[] getDependencies() {
		return dependencies;
	}
	
	/**
	 * Get the extents only
	 * @return
	 */
	public IObservation[] getExtentDependencies() {
		return extentDependencies;
	}

	/**
	 * Get all the dependencies that are not extents
	 * @return
	 */
	public IObservation[] getNonExtentDependencies() {
		return nonExtentDependencies;
	}

	
	/**
	 * Utility function to retrieve the observation context from a validated
	 * instance of observation.
	 * 
	 * @param value
	 * @return
	 * @throws ThinklabValidationException
	 *             if at any point the code does not find what it expects.
	 */
	public static ObservationContext retrieveContext(IValue value)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Utility method to retrieve whatever implementation we have for a related
	 * object. The IValue returned could be an ObjectReferenceValue if the
	 * "thing" has been represented as an object, in which case we may need the
	 * implementation of THAT. In most cases the value will be the
	 * implementation.
	 * 
	 * Uses the transitive closure of the property.
	 */
	static public IValue getRelatedImplementation(IInstance i, String property)
			throws ThinklabException {

		Collection<IRelationship> rels = i.getRelationshipsTransitive(property);
		return rels.iterator().hasNext() ? rels.iterator().next().getValue()
				: null;
	}
	
	public IObservationContext getObservationContext() throws ThinklabException {

		ObservationContext ret = new ObservationContext(this);
		
		for (IObservation oo : getDependencies()) {

			/* merge extents appropriately */
			if (((Observation)oo).conceptualModel instanceof IExtentConceptualModel)
				ret.mergeExtent(oo, getContextDimension(oo), LogicalConnector.INTERSECTION, true);
			else {
				/* FIXME should notify dependency, too?  */
			}
		}

		
		return ret;
	}

	/**
	 * Create the overall observation context for this observation in order for the observation structure
	 * can be contextualized by the passed workflow. Specifically:
	 * 
	 * 1. insert observation in workflow and notes all dependencies so that a topological
	 * order can be constructed;
	 * 
	 * 2. Creates a new observation context; contextualizes all contingent observations and
	 * sets the context to be the union of the extents of all contingents.
	 * 
	 * 3. Contextualizes all dependencies and sets the context to the intersection of the current
	 * one with that of the dependencies.
	 *  
	 * @param workflow
	 * @return
	 * @throws  
	 * @throws ThinklabException 
	 */
	public IObservationContext getOverallObservationContext(IContextualizationWorkflow workflow) throws ThinklabException {
		
		if (workflow.hasObservation(this))
			return null;
		
		ObservationContext ret = new ObservationContext(this);
		
		/* first thing, make sure that workflows knows it must calculate us, or we won't be
		 * able to set dependencies later.
		 */
		workflow.addObservation(this);

		/* if I am an extent, set context from it. Use instanceof, uglier but SO much faster than
		 * the reasoner. */
		if (conceptualModel instanceof IExtentConceptualModel) {
			ret.mergeExtent(this, getContextDimension(this), LogicalConnector.INTERSECTION, true);
		}

		/* scan all contingencies and build overall maximum context by merging extents for all. */
		for (IObservation contingency : getContingencies()) {

			/* contextualize obs */
			ObservationContext oc = (ObservationContext)
				(((Observation)contingency).getOverallObservationContext(workflow));

			/* merge extents appropriately */
			if (oc != null)
				ret.mergeExtents(oc, LogicalConnector.UNION, false);
			
		}
		
		/* 
		 * AND the merged extent of the contingencies that are not extents 
		 * with the extents of the dependencies 
		 */
		for (IObservation dependency : getNonExtentDependencies()) {
			
			/* contextualize obs */
			ObservationContext oc = (ObservationContext)
				(((Observation)dependency).getOverallObservationContext(workflow));

			/* notify dependency */
			if (oc != null) {
				workflow.addObservationDependency(this, dependency);
			
				/* merge extents appropriately */
				ret.mergeExtents(oc, LogicalConnector.INTERSECTION, false);
			}
		}
		
		/* see if we have a linked observation */
		Observation lobs = getAssociatedActualObservation(this);
		
		if (lobs != null) {

			ObservationContext oc = (ObservationContext)lobs.getOverallObservationContext(workflow);
			
			if (oc != null) {
				/* contextualize it and merge using intersection, because if we are redefined to be
				 * that observation, we are able to see its extents and only those. */
				ret.mergeExtents(oc, LogicalConnector.INTERSECTION, false);

				/* notify mediated dependency to workflow */
				workflow.addMediatedDependency(this, lobs);
			}
		}		

		/* 
		 * Constrain all existing extents with any extents we may have 
		 * with the extents of the dependencies 
		 */
		for (IObservation dependency : getExtentDependencies()) {

			/* contextualize obs */
			ObservationContext oc = (ObservationContext)
				(((Observation)dependency).getOverallObservationContext(workflow));

			/* notify dependency */
			if (oc != null) {
				workflow.addObservationDependency(this, dependency);
			
				/* merge extents appropriately */
				ret.mergeExtents(oc, LogicalConnector.INTERSECTION, true);
			}
		}

		
		// TODO we should perform a final step here to give all CMs the chance of finalizing
		// the structure, reduce data, etc. once all the contexts have been exposed.
		
		return ret;	
	}

	public IConcept getObservationClass() {
		return observation.getDirectType();
	}

	public IInstance getObservationInstance() {
		return observation;
	}

	private static Observation extractObservationFromInstance(IInstance object) throws ThinklabException {
		
		IInstanceImplementation oo = object.getImplementation();

		if (oo == null || !(oo instanceof Observation))
			throw new ThinklabContextValidationException("observation in context of " + 
					object + " is not valid");
		
		return (Observation)oo;
	}

	/**
	 * Determine the context dimension for this observation. Done by determining the
	 * most general common observable.
	 * 
	 * FIXME very much unverified
	 * 
	 * @param obs
	 * @return
	 * @throws ThinklabException
	 */
	public static IConcept getContextDimension(IObservation obs) throws ThinklabException {
		
		IConcept ctg = obs.getObservableClass();
		IConcept obo = KnowledgeManager.Thing();
		
		do {
			ctg = ctg.getLeastGeneralCommonConcept(obo);
		} while (!ctg.equals(obo) && ctg.isAbstract());
		
		if (ctg.equals(obo))
			/* no non-abstract observation classes; it's independent */
			return obs.getObservableClass();
		
		return ctg;
	}
	
	/**
	 * Check that formal observation (without datasource) has an associated actual
	 * observation, and return it.
	 * 
	 * @param cobs
	 * @return
	 * @throws ThinklabException
	 */
	private static Observation getAssociatedActualObservation(IObservation cobs) throws ThinklabException {
		
		Collection<IInstance> same = 
			cobs.getObservationInstance().getEquivalentInstances();
		
		if (same.size() == 0)
			return null;
		
		if (same.size() != 1) 
			throw new ThinklabContextValidationException(
					"formal observation " + cobs + 
					" has no link to an actual observation, or link is ambiguous");
		
		Observation ret = extractObservationFromInstance(same.iterator().next());
		
		if (ret != null && isFormalObservation(ret))
			throw new ThinklabContextValidationException(
					"actual observation linked to " +
					cobs +
					" is formal (has no datasource); dependency cannot be satisfied.");
		
		return ret;
	}
	
	static boolean isFormalObservation(IObservation obs) throws ThinklabException {

		/* FIXME checking for ID as only class without an explicit DS could be 
		 * weak.
		 */
		return 
			obs.getDataSource() == null && 
			!obs.getObservationInstance().is(CoreSciencePlugin.IDENTIFICATION);
	}
	
	/**
	 * Contextualization is the operation of creating the state of all indirect observations in an observation
	 * structure based on their datasources. The operation is guided by the overall context and driven by 
	 * the conceptual model. 
	 * 
	 * Contextualization uses the specified workflow to direct the operations and create the involved structures,
	 * including choosing specific implementations for the state structures. 
	 * 
	 * @param workflow
	 * @throws ThinklabException 
	 */
	public IValue contextualize(IContextualizationWorkflow workflow) throws ThinklabException {
		
		ObservationContext context = (ObservationContext) getOverallObservationContext(workflow);
		context.initialize();
		return workflow.run(this, context);
	}
	
	/**
	 * One-step contextualization using default workflow.

	 * @return an ObjectReferenceValue containing a new set of instances, whose states are calculated
	 * for the overall context. All datasources in the result are static and all mediators and links
	 * have been eliminated.
	 */
	public IValue contextualize()  throws ThinklabException {
	
		DefaultWorkflow workflow = new DefaultWorkflow();
		ObservationContext context = (ObservationContext) getOverallObservationContext(workflow);
		context.initialize();
		return workflow.run(this, context);
	}
	
	public String toString() {
		return 
			"[" +
			this.observation.getDirectType() + 
			": " +
			this.observable.getLocalName() +
			" (" +
			this.observable.getType() +
			")]";
	}

	public void setObservationState(IObservationState state) {
		currentState = state;
	}

	public IObservationContext getCurrentObservationContext() throws ThinklabException {
		return currentContext;
	}

	public void setCurrentObservationContext(IObservationContext context) {
		currentContext = context;
	}
}
