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
package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.contextualization.ObservationContext;
import org.integratedmodelling.corescience.contextualization.Compiler;
import org.integratedmodelling.corescience.exceptions.ThinklabContextValidationException;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.TransformingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IContextualizationCompiler;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.LogicalConnector;

/**
 * Base implementation for an Observation instance. Initializes the observation
 * and its context, defers operations to subclasses. Gives API access to data,
 * contexts, conceptual model and uncertainty model.
 * 
 * Most importantly, it provides the contextualization functionalities that
 * allow context compilation to work. So unless you don't plan to contextualize,
 * don't even dream of not deriving your IObservation from this one.
 * 
 * @author Ferdinando Villa
 */
@InstanceImplementation(concept = "observation:Observation")
public class Observation implements IObservation, IInstanceImplementation {

	/*
	 * these can be both objects implementations or literals, coming from OWL,
	 * so we store the value and convert on usage. Observation structures built
	 * internally (e.g. from literals) will have these as null, and must provide
	 * their own DS and CM.
	 */
	private IValue dataSourceHolder = null;
	private IValue conceptualModelHolder = null;

	protected IDataSource<?> dataSource = null;
	protected IConceptualModel conceptualModel = null;
	protected IInstance observable = null;
	protected IInstance observation = null;
	protected IInstance dataSourceValue = null;
	protected IObservation[] contingencies = new IObservation[0];
	protected IObservation[] dependencies = new IObservation[0];
	protected IObservation[] extentDependencies = new IObservation[0];
	protected IObservation[] nonExtentDependencies = new IObservation[0];
	protected IObservation mediatedObservation = null;
	protected IObservation mediatorObservation = null;
	private boolean beingTransformed = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.corescience.IObservation#getDataSource()
	 */
	public IDataSource<?> getDataSource() throws ThinklabException {

		if (dataSource == null && dataSourceHolder != null) {

			if (dataSourceHolder.isObjectReference())
				dataSource = (IDataSource<?>) dataSourceHolder
						.asObjectReference().getObject().getImplementation();
			else
				dataSource = (IDataSource<?>) dataSourceHolder;
		}
		return dataSource;
	}

	@Override
	public IObservation getMediatedObservation() {
		return mediatedObservation;
	}

	public IConceptualModel getConceptualModel() throws ThinklabException {

		if (conceptualModel == null && conceptualModelHolder != null) {

			if (conceptualModelHolder.isObjectReference())
				conceptualModel = (IConceptualModel) conceptualModelHolder
						.asObjectReference().getObject().getImplementation();
			else
				conceptualModel = (IConceptualModel) conceptualModelHolder;
		}

		return conceptualModel;
	}

	/**
	 * Create an appropriate conceptual model if it is missing. This one is not
	 * relevant to observations whose CM is defined in OWL. Those whose CM can
	 * be implicit, like any observation created through a literal or shortened
	 * form, should implement this one.
	 * 
	 * @return a new conceptual model value for the observation.
	 * @throws ThinklabException
	 *             if anything goes wrong
	 */
	protected IConceptualModel createMissingConceptualModel()
			throws ThinklabException {
		return null;
	}

	/**
	 * Create an appropriate datasource if it is missing. Can be used to
	 * simplify definition of observation where the datasource is obvious.
	 * 
	 * @return a new conceptual model value for the observation.
	 * @throws ThinklabException
	 *             if anything goes wrong
	 * @Override
	 */
	protected IDataSource<?> createMissingDatasource() throws ThinklabException {
		return null;
	}

	public void validate(IInstance i) throws ThinklabException {

		/*
		 * if we had no conceptual model, have the derived observation create an
		 * appropriate one.
		 */
		if (conceptualModel == null)
			conceptualModel = createMissingConceptualModel();

		/*
		 * we allow a null CM, which should be limited to identifications.
		 */
		if (conceptualModel != null)
			conceptualModel.validate(this);

		/*
		 * if we had no datasource, have the derived obs create one if
		 * appropriate.
		 */
		if (dataSource == null)
			dataSource = createMissingDatasource();

	}

	/**
	 * collect observable, datasource, conceptual model, and all contingencies
	 * and dependencies; classify dependencies into extent and non-extent. After
	 * this is done, validate() will be called and the virtuals
	 * createMissingConceptualModel() and createMissingDatasource() will be
	 * called in sequence if no CM or DS are provided.
	 */
	public void initialize(IInstance i, Properties properties) throws ThinklabException {

		/*
		 * this one is easy
		 */
		observation = i;

		ArrayList<IObservation> dep = new ArrayList<IObservation>();
		ArrayList<IObservation> con = new ArrayList<IObservation>();
		ArrayList<IObservation> ext = new ArrayList<IObservation>();
		ArrayList<IObservation> nxt = new ArrayList<IObservation>();

		/*
		 * locate and store various related for efficiency. This method is
		 * faster than getting piece by piece.
		 */
		for (IRelationship r : i.getRelationships()) {

			/* again, for speed */
			if (!r.isClassification()) {
				if (observable == null
						&& r.getProperty().is(CoreScience.HAS_OBSERVABLE)) {
					observable = r.getValue().asObjectReference().getObject();
				} else if (dataSourceHolder == null
						&& r.getProperty().is(CoreScience.HAS_DATASOURCE)) {
					dataSourceHolder = r.getValue();
				} else if (conceptualModelHolder == null
						&& r.getProperty().is(CoreScience.HAS_CONCEPTUAL_MODEL)) {
					conceptualModelHolder = r.getValue();
				} else if (r.getProperty().is(CoreScience.DEPENDS_ON)) {

					dep.add((IObservation) r.getValue().asObjectReference()
							.getObject().getImplementation());

					if (r.getProperty().is(CoreScience.HAS_EXTENT)) {
						ext.add((IObservation) r.getValue().asObjectReference()
								.getObject().getImplementation());
					} else {
						nxt.add((IObservation) r.getValue().asObjectReference()
								.getObject().getImplementation());
					}

					if (r.getProperty().is(CoreScience.MEDIATES_OBSERVATION)) {
						mediatedObservation = (IObservation) r.getValue()
								.asObjectReference().getObject()
								.getImplementation();
					}

				} else if (r.getProperty().is(CoreScience.HAS_CONTINGENCY)) {

					con.add((IObservation) r.getValue().asObjectReference()
							.getObject().getImplementation());
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

		/*
		 * if we are mediating something and we have our own observable, we must
		 * be punished. This may be questionable in general, but that's our
		 * definition of mediation, and it works great in contextualization.
		 */
		if (mediatedObservation != null && observable != null)
			throw new ThinklabValidationException(
					"mediator observations should not declare an observable");

		/*
		 * ensure we know the observable if we're mediating another obs and we
		 * don't have our own observable.
		 */
		IObservation mobs = mediatedObservation;
		while (observable == null && mobs != null) {
			observable = mediatedObservation.getObservable();
			mobs = mobs.getMediatedObservation();
		}

		/*
		 * if we STILL have no observable, we're in trouble. Observables cannot
		 * be null.
		 */
		if (observable == null)
			throw new ThinklabValidationException("observation "
					+ i.getLocalName() + " has no observable");

		if (mediatedObservation != null) {
			((Observation) mediatedObservation).mediatorObservation = this;
		}
	}

	public IConcept getObservableClass() {
		return observable.getType();
	}

	public IInstance getObservable() {
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
	 * 
	 * @return
	 */
	public IObservation[] getExtentDependencies() {
		return extentDependencies;
	}

	/**
	 * Get all the dependencies that are not extents
	 * 
	 * @return
	 */
	public IObservation[] getNonExtentDependencies() {
		return nonExtentDependencies;
	}

	/**
	 * Get the extent observation that observes the passed observable
	 * 
	 * @param extentObservable
	 * @return
	 */
	public IObservation getExtent(IConcept extentObservable) {

		IObservation ret = null;
		for (IObservation ext : getExtentDependencies()) {

			if (ext.getObservableClass().is(extentObservable)) {
				ret = ext;
				break;
			}
		}

		return ret;
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

	/**
	 * TODO we should have contingencies merged in here, but then it becomes
	 * much more complex to merge in extent CMs and it may be impossible if
	 * they're not representation-compatible along contingencies, which may 
	 * well happen. If we throw an exception in that case, we cut out a lot
	 * of useful possibilities, so we should have a "lenient" way of merging
	 * that isn't used in contextualization but returns the full extents for
	 * all dimensions.
	 */
	public IObservationContext getObservationContext() throws ThinklabException {

		ObservationContext ret = new ObservationContext(this);

		for (IObservation oo : getDependencies()) {

			/* merge extents appropriately */
			if (((Observation) oo).conceptualModel instanceof ExtentConceptualModel)
				ret.mergeExtent(oo, getContextDimension(oo),
						LogicalConnector.INTERSECTION, true);
			else {
				/* FIXME should notify dependency, too? */
			}
		}

		ret.initialize();

		return ret;
	}

	/**
	 * Build the COMMON observation context, which is the merged
	 * intersection of all extents along the dependency tree, so that 
	 * states of all dependencies can be calculated by a contextualizer 
	 * according to it. Called by Compiler.compile(). This will not
	 * merge contingencies, which may be completely different structures
	 * not intended for being harmonized; rather, they will be contextualized
	 * separately and merged into the resulting instance. 
	 * 
	 */
	public IObservationContext getCommonObservationContext(
			IContextualizationCompiler compiler, ISession session)
			throws ThinklabException {

		ObservationContext ret = getCommonObservationContext_(compiler,
				session, new HashSet<Observation>());

		return ret;
	}

	/**
	 * Create the overall observation context for this observation in order for
	 * the observation structure can be contextualized by the passed workflow.
	 * Specifically:
	 * 
	 * 1. insert observation in workflow and notes all dependencies so that a
	 * topological order can be constructed;
	 * 
	 * 2. Creates a new observation context; contextualizes all contingent
	 * observations and sets the context to be the union of the extents of all
	 * contingents.
	 * 
	 * 3. Contextualizes all dependencies and sets the context to the
	 * intersection of the current one with that of the dependencies.
	 * 
	 * @param compiler
	 * @param session
	 * @return
	 * @throws
	 * @throws ThinklabException
	 */
	private ObservationContext getCommonObservationContext_(
			IContextualizationCompiler compiler, ISession session,
			HashSet<Observation> inserted) throws ThinklabException {

		if (inserted.contains(this))
			return null;

		/*
		 * if this is a transformer, we want to contextualize it with its own
		 * compiler and filter the resulting observation through the
		 * transforming model, which may produce a transformed observation. Whatever
		 * is returned is the observation we want to use: we notify the result
		 * to the compiler and return its context instead of the original one.
		 */
		if (getConceptualModel() instanceof TransformingConceptualModel && !this.beingTransformed) {

			this.beingTransformed = true;
			
			IInstance inst = Compiler.contextualize(this, session);
			IInstance trs = ((TransformingConceptualModel) getConceptualModel())
					.transformObservation(inst);
			Observation obs = extractObservationFromInstance(trs);
			compiler.addObservation(obs);

			this.beingTransformed = false;
			
			return (ObservationContext) obs.getObservationContext();
		}

		ObservationContext ret = new ObservationContext(this);

		/*
		 * first thing, make sure that the compiler knows it must calculate us,
		 * or we won't be able to set dependencies later.
		 */
		compiler.addObservation(this);

		/* if I am an extent, set context from it. */
		if (conceptualModel instanceof ExtentConceptualModel) {
			ret.mergeExtent(this, getContextDimension(this),
					LogicalConnector.INTERSECTION, true);
		}

// TODO contingencies must be brought in after full contextualization
//		/*
//		 * scan all contingencies and build overall maximum context by merging
//		 * extents for all.
//		 */
//		for (IObservation contingency : getContingencies()) {
//
//			/* contextualize obs */
//			ObservationContext oc = (ObservationContext) (((Observation) contingency)
//					.getOverallObservationContext(compiler, session));
//
//			/* merge extents appropriately */
//			if (oc != null)
//				ret.mergeExtents(oc, LogicalConnector.UNION, false);
//
//		}

		/*
		 * AND the merged extent of the contingencies that are not extents with
		 * the extents of the dependencies
		 */
		for (IObservation dependency : getNonExtentDependencies()) {

			/* contextualize obs */
			ObservationContext oc = (ObservationContext) (((Observation) dependency)
					.getCommonObservationContext(compiler, session));

			/* notify dependency */
			if (oc != null) {
				compiler.addObservationDependency(this, dependency);

				/* merge extents appropriately */
				ret.mergeExtents(oc, LogicalConnector.INTERSECTION, false);
			}
		}

// TODO I don't think this is a good model. What we want to do is to install
// a mediator observation, not arbitrarily "linking" external observations, but
// this MAY still make sense.
// 
//		/* see if we have a linked observation */
//		Observation lobs = getAssociatedActualObservation(this);
//
//		if (lobs != null) {
//
//			ObservationContext oc = (ObservationContext) lobs
//					.getOverallObservationContext(compiler, session);
//
//			if (oc != null) {
//				/*
//				 * contextualize it and merge using intersection, because if we
//				 * are redefined to be that observation, we are able to see its
//				 * extents and only those.
//				 */
//				ret.mergeExtents(oc, LogicalConnector.INTERSECTION, false);
//
//				/* notify mediated dependency to workflow */
//				compiler.addMediatedDependency(this, lobs);
//			}
//		}

		/*
		 * Constrain all existing extents with any extents we may have with the
		 * extents of the dependencies
		 */
		for (IObservation dependency : getExtentDependencies()) {

			/* contextualize obs */
			ObservationContext oc = (ObservationContext) (((Observation) dependency)
					.getCommonObservationContext(compiler, session));

			/* notify dependency */
			if (oc != null) {
				compiler.addObservationDependency(this, dependency);

				/* merge extents appropriately */
				ret.mergeExtents(oc, LogicalConnector.INTERSECTION, true);
			}
		}

		// initialize this context
		ret.initialize();

		return ret;
	}

	public IConcept getObservationClass() {
		return observation.getDirectType();
	}

	public IInstance getObservationInstance() {
		return observation;
	}

	private static Observation extractObservationFromInstance(IInstance object)
			throws ThinklabException {

		IInstanceImplementation oo = object.getImplementation();

		if (oo == null || !(oo instanceof Observation))
			throw new ThinklabContextValidationException(
					"observation in context of " + object + " is not valid");

		return (Observation) oo;
	}

	/**
	 * Determine the context dimension for this observation. Done by determining
	 * the most general common observable.
	 * 
	 * FIXME very much unverified
	 * 
	 * @param obs
	 * @return
	 * @throws ThinklabException
	 */
	public static IConcept getContextDimension(IObservation obs)
			throws ThinklabException {

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

//	/**
//	 * Check that formal observation (without datasource) has an associated
//	 * actual observation, and return it.
//	 * 
//	 * @param cobs
//	 * @return
//	 * @throws ThinklabException
//	 */
//	private static Observation getAssociatedActualObservation(IObservation cobs)
//			throws ThinklabException {
//
//		Collection<IInstance> same = cobs.getObservationInstance()
//				.getEquivalentInstances();
//
//		if (same.size() == 0)
//			return null;
//
//		if (same.size() != 1)
//			throw new ThinklabContextValidationException(
//					"formal observation "
//							+ cobs
//							+ " has no link to an actual observation, or link is ambiguous");
//
//		Observation ret = extractObservationFromInstance(same.iterator().next());
//
//		if (ret != null && isFormalObservation(ret))
//			throw new ThinklabContextValidationException(
//					"actual observation linked to "
//							+ cobs
//							+ " is formal (has no datasource); dependency cannot be satisfied.");
//
//		return ret;
//	}

	static boolean isFormalObservation(IObservation obs)
			throws ThinklabException {

		/*
		 * FIXME checking for ID as only class without an explicit DS could be
		 * weak.
		 */
		return obs.getDataSource() == null
				&& !obs.getObservationInstance().is(CoreScience.IDENTIFICATION);
	}

	public String toString() {
		return "[" + this.observation.getDirectType() + ": "
				+ this.observable.getLocalName() + " ("
				+ this.observable.getType() + ")]";
	}

	@Override
	public IObservation getMediatorObservation() {
		return mediatorObservation;
	}

	@Override
	public boolean isMediated() {
		return mediatorObservation != null;
	}

	@Override
	public boolean isMediator() {
		return mediatedObservation != null;
	}

	@Override
	public boolean equals(Object obj) {

		return (obj instanceof Observation) ? observation
				.equals(((Observation) obj).observation) : false;
	}

	@Override
	public int hashCode() {
		return observation.hashCode();
	}

	@Override
	public IObservation getObservation(IConcept observable) {
		return Obs.findObservation(this, observable);
	}

	@Override
	public IContextualizedState getState(IConcept observable)
			throws ThinklabException {

		IObservation o = getObservation(observable);
		if (o != null && o.getDataSource() != null
				&& o.getDataSource() instanceof IContextualizedState)
			return (IContextualizedState) o.getDataSource();
		return null;
	}
}
