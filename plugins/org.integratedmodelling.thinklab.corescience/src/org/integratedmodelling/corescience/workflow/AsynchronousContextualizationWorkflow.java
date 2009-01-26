/**
 * AsynchronousContextualizationWorkflow.java
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
package org.integratedmodelling.corescience.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.corescience.contextualization.ObservationContext;
import org.integratedmodelling.corescience.contextualization.ObservationContextState;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentCoverage;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueMediator;
import org.integratedmodelling.corescience.interfaces.cmodel.MediatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.ScalingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.ValidatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IContextualizationWorkflow;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.interfaces.observation.IObservationState;
import org.integratedmodelling.corescience.observation.Observation;
import org.integratedmodelling.thinklab.exception.ThinklabCircularDependencyException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.TopologicalSorter;
import org.jscience.mathematics.number.Rational;


/**
 * Contains a topological sorter, support for actions and their execution in an asynchronous workflow. Used
 * to implement default and debug workflows.
 * 
 * FIXME we should use a graph to hold the dependency structure and do the topological
 * sorting, to speed up the retrieval of dependents and dependencies.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class AsynchronousContextualizationWorkflow implements IContextualizationWorkflow {

	protected TopologicalSorter topologicalSorter = new TopologicalSorter();
	protected ArrayList<IObservation> observationOrder    = null;
	protected ArrayList<ActivationRecord> activationOrder = null;
	protected HashMap<String, String> links = new HashMap<String, String>();
	private boolean useIndex;
	private boolean haveMediation;
	protected IObservationContext overallContext = null;

	/**
	 * used in contextualization: one for each observation, in order of dependency, 
	 * holding state for all dependencies in current context and managing the VERY messy details of 
	 * handling mediation of values and extents, with full account of uncertainties.
	 * 
	 * In order to fully generalize the workflow, we'll need a factory for these, and a more carefully
	 * thought out class structure. For now it's way more than enough, at least until it works 
	 * reliably.
	 * 
	 * @author Ferdinando Villa
	 */
	public class ActivationRecord {
		
		public ActivationStatus active = ActivationStatus.ACTIVE;
		public IObservation observation = null;
		public ActivationRecord[] dependents = null;
		
		// we keep parallel arrays for dependencies and mediators. Mediators are
		// chained to each other, so there is one main mediator per dependency.
		public ActivationRecord[] dependencies = null;
		private IExtentMediator[] mediators = null;

		public IObservationState state = null;
		public IDataSource dataSource = null;
		public IConceptualModel conceptualModel = null;
		public String key;
		
		// when the object has mediators, the last state may differ from the current value, which
		// reflects partial extents and is for the use of the dependents.
		public IValue value = null;
		public IValue lastState = null;
		public IConcept stateType = null;
		
		// the next two are instantiated if the value for this variable comes
		// from another one by means of an equivalence relationship (link).
		public ActivationRecord linkedDependency = null;
		public IValueMediator linkedMediator = null;
		
		private ObservationContext ownContext;
		public int multiplicity;
		boolean useGranuleIdx = false;
		boolean haveExtentMediators = false;
		private IUncertainty uncertainty;
		private String id;
		private IValueAggregator[] dependenciesAccumulator = null;
		
		public void activate() {
			active = ActivationStatus.ACTIVE;
		}
		
		public void deactivate() {
			active = ActivationStatus.INACTIVE;
		}
		
		private ActivationRecord(IObservation obs) {
			
			observation = obs;
			key = obs.getObservationInstance().getURI();
		}

		private void addDependentRecords(Collection<ActivationRecord> ars) {
			
			// extract dependents and set into array
			Collection<String> deps = 
				topologicalSorter.getDependentKeys(observation.getObservationInstance().getURI());

			if (deps.size() > 0)
				dependents = new ActivationRecord[deps.size()];
			
			// FIXME we should probably use an OrderedMap to speed this one up, or produce
			// a dependency graph during contextualization and use that for everything. 
			int i = 0;
			for (String o : deps) {
				for (ActivationRecord a : ars)
					if (a.key.equals(o)) {
						dependents[i++] = a;
					}						
			}
		}
		
		private void addDependencyRecords(Collection<ActivationRecord> ars,
					IObservationContext ctx) throws ThinklabException {

			
			// if I take my values from another linked obs, I need no other deps
			String linkedOb = links.get(observation.getObservationInstance().getURI());
			if (linkedOb != null) {

				for (ActivationRecord a : ars)
					if (a.key.equals(linkedOb)) {
						linkedDependency = a;
						break;
					}						

				/* create mediators in linkedMediator */
				if (conceptualModel instanceof MediatingConceptualModel)
					linkedMediator = ((MediatingConceptualModel)conceptualModel).getMediator(linkedDependency.conceptualModel, ctx);
					
			} else {	
				
				Collection<String> deps = 
				topologicalSorter.getDependencyKeys(observation.getObservationInstance().getURI());

				if (deps.size() > 0)
					dependencies = new ActivationRecord[deps.size()];
			
				int i = 0;
				for (String o : deps) {
							
					for (ActivationRecord a : ars) {
						if (a.key.equals(o)) {
							dependencies[i++] = a;
						}						
					}
				}
			}	
			
		}
		
		/**
		 * Extent mediators are created to match our own context with the overall one.
		 * We create an array of as many mediators as we have extents in the overall context, in the
		 * same order, leaving nulls where no mediation is necessary.
		 * 
		 * @param ctx
		 * @throws ThinklabException
		 */
		private void createExtentMediators(IObservationContext ctx) throws ThinklabException {
			
			haveExtentMediators = false;

			if (ownContext == null || ownContext.size() == 0)
				return;

			mediators = new IExtentMediator[ctx.size()];
			
			int i = 0;
			for (IConcept c : ctx.getContextDimensions()) {
				
				IExtentMediator mediator = null;
				IExtent extown = ownContext.getExtent(c);
				
				if (extown != null) {
					IExtent extent = ctx.getExtent(c);
					mediator = extown.getConceptualModel().getExtentMediator(extent);
					if (mediator != null) {
						haveExtentMediators = true;
					}
				}
				
				mediators[i++] = mediator;
			}
		}

		
		/**
		 * Deactivate records representing all the observations that depend on ours. Called when a context state
		 * submitted is not visible 
		 */
		private void deactivateDependents() {
						
			for (ActivationRecord ar : dependents) {
				ar.deactivate();
			}
		}

		/**
		 * Process dependencies and dependents for later use;
		 * Create necessary mediators;
		 * Perform datasource handshaking;
		 * Create states;
		 * 
		 * @throws ThinklabException 
		 */
		private void initialize(
				Collection<ActivationRecord> activationOrder,
				IObservationContext ctx) 
			throws ThinklabException {
			
			// add dependents
			addDependentRecords(activationOrder);
			
			// add dependencies, create mediators
			addDependencyRecords(activationOrder, ctx);

			dataSource = observation.getDataSource();
			conceptualModel = observation.getConceptualModel();
			if (conceptualModel != null)
				stateType = conceptualModel.getStateType();

			// define our id: if we represent an extent, we use the ID of the dimension, otherwise
			// the ID of the observable.
			// FIXME we may need a smarter strategy, particularly when we have compatible extents
			// of different classes we should call them with the ID of the common most specific 
			// class.
			IKnowledgeSubject observable = observation.getObservable();
			
			if (conceptualModel != null && conceptualModel instanceof ExtentConceptualModel) {
				id = ctx.getDimension(observable.getType()).getLocalName();
			} else {
				id = observable.getLocalName();
			}
			
			// let CM know what we decided to name the observation. We could give it to the 
			// observation, but all validations are done in the CM or the datasource, so I'd
			// expect this to save on parameter passing.
			if (conceptualModel != null)
				conceptualModel.setObjectName(id);
			
			// extract observation's defined context to prepare for subsequent operations
			if (ownContext != null)
				ownContext.initialize();

			// perform initial datasource handshaking
			if (dataSource != null) {
				
				useGranuleIdx = 
					dataSource.handshake(conceptualModel, ownContext, ctx);
			
				// set initial value and uncertainty if any
				Pair<IValue,IUncertainty> init = 
					dataSource.getInitialValue();
				
				value = init == null ? null : init.getFirst();
				uncertainty = init == null ? null : init.getSecond();
			}
			
			// create state if necessary. Will use its own context if it
			// has it; otherwise will inherit context from the overall context
			// as long as it has a conceptual model.
			multiplicity = 0;
			
			/**
			 *  TODO for now we don't generate state for the extents. This could be done later (although
			 * they normally don't have a datasource, so it should be left to the CM which is new
			 * logic). Maybe conditioned to a global option.
			 */
			if (ownContext == null && conceptualModel != null && 
					!(conceptualModel instanceof ExtentConceptualModel)) {
				multiplicity = ctx.getMultiplicity();
			} else if (ownContext != null) {
				multiplicity = ownContext.getMultiplicity();
			}
			
			if (multiplicity > 0 && conceptualModel != null) {
				
				state = createObservationState(observation, stateType, multiplicity);

				if (state != null)
					state.initialize(observation, ctx, conceptualModel);
			}
			
			// create all necessary extent mediators; no need if we don't have extents
			createExtentMediators(ctx);
		}

		/**
		 * Nice and debuggey
		 * @throws ThinklabException of course
		 */
		public void dump() throws ThinklabException {
			
			System.out.println("\n*** Activation record for variable " + observation);
			System.out.println("\tKnown to its dependents as: " + id);
			System.out.println("\tMultiplicity = " + multiplicity);
			System.out.println("\tConceptual model = " + conceptualModel);
			System.out.println("\tDatasource = " + dataSource);
			System.out.println("\tState = " + state);
			
			System.out.println("\tObservation's own context:");
			if (ownContext == null) {
				System.out.println("\t\tNULL");
			} else {
				ownContext.dump("\t\t");
			}
			System.out.println(useGranuleIdx ? "\tUsing extent indexes" : "\tUsing extent values");

			System.out.println("\tDependencies:");
			if (dependencies != null)
				for (ActivationRecord rr : dependencies) {
					System.out.println("\t\t" + rr);
				}
			else System.out.println("\t\tnone");

			
			System.out.println("\tDependents:");
			if (dependents != null)
				for (ActivationRecord rr : dependents) {
					System.out.println("\t\t" + rr);
				}
			else System.out.println("\t\tnone");

			
			System.out.println("\tContext:");
			if (ownContext != null)
				ownContext.dump("\t\t");
			else System.out.println("\t\t\none");
		}
		
		public String toString() {
			return "Activation record for " + observation;
		}

		public void extractStateFromDatasource(ObservationContextState contextState) 
			throws ThinklabException {

			Pair<IValue, IUncertainty> mret = null;

			if (active == ActivationStatus.INACTIVE)
				return;
			
			if (linkedDependency != null) {

				/*
				 * our value is the mediated value of our linked dependency;
				 * just take it and go.
				 */
				if (linkedMediator != null) {
					mret = 
						mediate(linkedDependency.value, linkedMediator,
								linkedDependency.uncertainty, contextState);

					value = mret.getFirst();
					uncertainty = mret.getSecond();
				}

			} else if (dataSource != null) {

				/* if we have any extent mediators, our granularity differs from the overall
				 * one in at least one dimension, and we must accumulate until covered and only
				 * activate the datasource when the extent is fully covered.
				 */
				if (haveExtentMediators) {
					
					active = ActivationStatus.ACTIVE; 
					int i = 0;
					
					for (IExtentMediator mediator : mediators) {
						
						if (mediator != null) {
							
							/* we only need to add the one for the dimension that has
								CHANGED in this cycle. */
							if (contextState.hasChanged(i))
								mediator.addForeignExtent(contextState.getValue(i++));		
							
							ExtentCoverage coverage = mediator.checkCoverage();
							if (coverage  == ExtentCoverage.PARTIAL) {
								active = ActivationStatus.INCOMPLETE;
							} else if (coverage == ExtentCoverage.NONE) {
								active = ActivationStatus.INACTIVE;
								deactivateDependents();
							}
						}
					}
				}
				
				/* only perform the final steps if the context state is relevant to all our context
				 * dimensions.
				 * 
				 * We accumulate the finer-grained dependencies into suitable aggregators every time that we
				 * don't calculate the datasource because its extents do not match a state of the observation's
				 * own extent. Accumulation requires that we pass the current context state, because extents may be irregular,
				 * and must be another function performed by the conceptual model.
				 */
				if (active == ActivationStatus.ACTIVE) {
					
					// use dependencies from accumulators
					// TODO/FIXME: this one should not have to go through the extents
					for (int i = 0; i < dependencies.length; i++) {
						
						Pair<IValue, IUncertainty> vdep = null;
						
						if (dependenciesAccumulator != null && dependenciesAccumulator[i] != null) 
							vdep = dependenciesAccumulator[i].aggregateAndReset();
						
						IValue val = vdep == null ? dependencies[i].value : vdep.getFirst();
						IUncertainty unc = vdep == null ? dependencies[i].uncertainty : vdep.getSecond();
						
						/* set mediated value and uncertainty into context state */
						contextState.set(dependencies[i].id, val, unc);
					}
					
					/* finally, extract and have CM validate appropriately */
					if (dataSource.getValueType() == IDataSource.ValueType.IVALUE) {
						mret = dataSource.getValue(contextState, stateType, useGranuleIdx);
						if (conceptualModel instanceof ValidatingConceptualModel)
							value = lastState = ((ValidatingConceptualModel)conceptualModel).validateValue(mret.getFirst(), contextState);
						else
							value = lastState = mret.getFirst();
						
						uncertainty = mret.getSecond();
					} else {
						Pair<String, IUncertainty> lret = 
							dataSource.getValueLiteral(contextState, stateType, useGranuleIdx);
						value = lastState = conceptualModel.validateLiteral(lret.getFirst(), contextState);						
						uncertainty = lret.getSecond();
					}

					/* reset extents in mediators, leaving any remaining extent not part of the 
					 * original one covered.
					 */
					if (mediators != null)
						for (IExtentMediator mediator : mediators)
							if (mediator != null)
								mediator.resetForeignExtents();

					
				} else if (active == ActivationStatus.INCOMPLETE){
					
					if (dependencies.length > 0) {
						
						if (dependenciesAccumulator  == null) {
							dependenciesAccumulator = new IValueAggregator[dependencies.length];

							/* create the necessary aggregators */
							int i = 0;
							for (ActivationRecord ar : dependencies) {
								
								if (ar.conceptualModel instanceof ScalingConceptualModel)
									dependenciesAccumulator[i++] = 
										((ScalingConceptualModel)(ar.conceptualModel)).getAggregator(ownContext, overallContext);
							}
						}
					
						/*
						 * accumulate the dependencies (which have been partitioned to suit the
						 * context state) into suitably defined
						 * aggregators here, relative to current context state.
						 */
						for (int i = 0; i < dependencies.length; i++) {
							
							if (dependenciesAccumulator[i] != null)
								dependenciesAccumulator[i].addValue(
										dependencies[i].value,
										dependencies[i].uncertainty,
										contextState);
						
						}
					}
					
					if (dependents.length > 0) {

						// reinitialize value to last officially assigned
						Rational ratio = Rational.ONE;

						/*
						 * If we are not active, at least one of our extents has
						 * been covered only partially. We must mediate our
						 * current value to reflect the addition of the new
						 * context states, so our dependents can use it. If we
						 * have no dependents, there is no reason to do so.
						 */
						for (IExtentMediator mediator : mediators) {
							
							// determine proportion of extent covered across all extents
							if (mediator != null) {

								Pair<Rational, IUncertainty> eret = mediator
										.getCurrentCoverage(uncertainty);

								ratio = ratio.times(eret.getFirst());
								uncertainty = eret.getSecond();
							}
						}
						value = ((ScalingConceptualModel)conceptualModel).partition(lastState, ratio);
					}

				}
			}

			// put value in state if we have it after all
			if (state != null) {
				state.setValue(value, uncertainty, contextState);
			}
		}

		public void dumpState(String prefix) {
			
			System.out.println(prefix + "*** activation of " + observation + " (" + active.toString() + ")");
			System.out.println(prefix + "state = " + state);
			System.out.println(prefix + "uncertainty = " + uncertainty);
			if (haveExtentMediators) {
				System.out.println(prefix + "Extent mediators: ");
			}
		}
	} // end ActivationRecord
	
	public Pair<IValue, IUncertainty> mediate(IValue value, IValueMediator mediator, 
			IUncertainty uncertainty, IObservationContextState context)
		throws ThinklabException {

		IValue vret = value;
		IUncertainty uret = uncertainty;

		if (mediator.isExact()) {				
			vret = mediator.getMediatedValue(vret, context);
		} else {
			Pair<IValue,IUncertainty> vr = 
				mediator.getMediatedValue(vret, uret, context);
       		vret = vr.getFirst();
       		uret = vr.getSecond();
		}
		
		return new Pair<IValue,IUncertainty>(vret, uret);
	}
	
	/**
	* Returns true if the overall context can be calculated based only on the extent
	* index, as opposed to the extent IValue for each context state. Can be used to save
	* computation time.
	* 
	 * To be used only after createActivationRecords() has been called. No check is made.
	 * @return
	 */
	public boolean canUseExtentIndex() {
		return useIndex;
	}
	
	/**
	 * Returns true if there is any mediation going on in the workflow. Can be used to 
	 * choose a simpler contextualization strategy.
	 * 
	 * To be used only after createActivationRecords() has been called. No check is made.
	 * 
	 * @return
	 */
	public boolean usesMediation() {
		return haveMediation;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.interfaces.IContextualizationWorkflow#addObservation(org.integratedmodelling.corescience.interfaces.IObservation)
	 */
	public void addObservation(IObservation observation) {
		topologicalSorter.addObject(observation, observation.getObservationInstance().getURI());
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.interfaces.IContextualizationWorkflow#hasObservation(org.integratedmodelling.corescience.observation.Observation)
	 */
	public boolean hasObservation(Observation observation) {
		return topologicalSorter.haveObject(observation.getObservationInstance().getURI());
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.interfaces.IContextualizationWorkflow#addObservationDependency(org.integratedmodelling.corescience.interfaces.IObservation, org.integratedmodelling.corescience.interfaces.IObservation)
	 */
	public void addObservationDependency(IObservation destination, IObservation source) {
		
		int idx = topologicalSorter.getObjectIndexForKey(destination.getObservationInstance().getURI());
		int odx = topologicalSorter.addObject(source, source.getObservationInstance().getURI());

		topologicalSorter.addDependency(odx, idx);
	}

	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.interfaces.IContextualizationWorkflow#addMediatedDependency(org.integratedmodelling.corescience.interfaces.IObservation, org.integratedmodelling.corescience.interfaces.IObservation)
	 */
	public void addMediatedDependency(IObservation destination, IObservation source) {

		addObservationDependency(destination, source);
		
		/* we must notify the activation record that any datasource must be 
		 * ignored, but the value should be taken from the dependency directly,
		 * after mediating as necessary. */
		links.put(destination.getObservationInstance().getURI(),
				  source.getObservationInstance().getURI());
	}

	/**
	 * This should make it reentrant, although it's just about the same as using a new
	 * workflow every time, which also protects much more effectively from my sloppiness.
	 */
	public void reset() {
		observationOrder = null;
		activationOrder = null;
		overallContext = null;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.interfaces.IContextualizationWorkflow#getObservationOrder()
	 */
	public Collection<IObservation> getObservationOrdering() throws ThinklabCircularDependencyException {
		
		if (observationOrder == null) {
			observationOrder = new ArrayList<IObservation>();			
			Object[] obs = topologicalSorter.sort();	
			for (Object o : obs) {
				observationOrder.add((IObservation)o);	
			}
		}
		
		return observationOrder;
	}
	
	/**
	 * Create the activation records for the stated observation structure. Must be called
	 * after all dependencies and observations are notified.
	 * 
	 * As another side effect, this defines the two parameters that tell us whether the
	 * workflow can be run by passing only the extext indexes instead of the whole
	 * variable states, and whether there is any mediation going on within the workflow.
	 * These can be accessed by using canUseExtentIndex() and usesMediation().
	 * 
	 * @param observation
	 * @param ctx
	 * @throws ThinklabException
	 */
	protected void createActivationRecords(IObservation observation, IObservationContext ctx) throws ThinklabException {

		overallContext = ctx;
		
		// create all activation records
		if (activationOrder  == null) {
			
			activationOrder = new ArrayList<ActivationRecord>();
			
			for (IObservation obs : getObservationOrdering()) {
				activationOrder.add(createActivationRecord(obs));
			}
		}
		
		// perform initial datasource handshaking, create states, etc
		useIndex = true;
		haveMediation = false;
		
		for (ActivationRecord ar : activationOrder) {
			ar.initialize(activationOrder, ctx);
			
			/* in this workflow, we use indexes for context states only if all observation
			 * can use them. This will be the case for most single source structures.
			 */
			if (useIndex && !ar.useGranuleIdx)
				useIndex = false;
			
			/* check if any of the activation records have extent mediators after
			 * initialization. If there are no mediators, we can use a simpler
			 * contextualization strategy.
			 */
			if (!haveMediation && ar.haveExtentMediators)
				haveMediation = true;
		}
	}

	protected Collection<ActivationRecord> getActivationRecords() {
		return activationOrder;
	}
	
	public IValue run(IObservation observation, IObservationContext ctx) throws ThinklabException {
				
		/* create a list of structures holding the strategy of contextualization */
		createActivationRecords(observation, ctx);
		
		/* iterate over all fine-grained context states */
		for (IObservationContextState contextState : ctx.getContextStates(this)) {

			/* 
			 * Activate all records before we start. Uncovered extents will deactivate
			 * all dependent records for the whole cycle and we need to start fresh at
			 * each extent cycle.
			 */
			for (ActivationRecord ar : activationOrder) {
				ar.activate();
			}

			/*
			 * Extract state, mediating and deactivating as necessary
			 */
			for (ActivationRecord ar : activationOrder) {
				ar.extractStateFromDatasource((ObservationContextState) contextState);
			}
		}
		
		// do whatever we need to do with the states left in activation records
		return generateResult(observation, ctx);
	}
	
	/**
	 * This one is called after contextualization is finished, and has access to the filled in
	 * activation records so it can create what it wants with the states and observation
	 * structure.
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IValue generateResult(IObservation observation, IObservationContext context) throws ThinklabException;
	
	/**
	 * Create the initial activation record for an observation and perform one-time handshaking with
	 * datasource.
	 * 
	 * @param observation
	 * @return
	 */
	public ActivationRecord createActivationRecord(IObservation observation) {
		return new ActivationRecord(observation);
	}

}
