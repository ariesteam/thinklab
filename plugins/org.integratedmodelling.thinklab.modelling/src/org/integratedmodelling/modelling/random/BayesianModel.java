package org.integratedmodelling.modelling.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.modelling.DefaultAbstractModel;
import org.integratedmodelling.modelling.Model;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.corescience.ClassificationModel;
import org.integratedmodelling.modelling.implementations.observations.BayesianTransformer;
import org.integratedmodelling.modelling.interfaces.IContextOptional;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;

import smile.Network;

public class BayesianModel extends DefaultAbstractModel implements IContextOptional {

	String source = null;
	String algorithm = null;
	ArrayList<String> keeperIds = new ArrayList<String>();
	ArrayList<IConcept> keepers = new ArrayList<IConcept>();
	
	@Override
	protected void copy(DefaultAbstractModel model) {
		super.copy(model);
		algorithm = ((BayesianModel)model).algorithm;
		keeperIds = ((BayesianModel)model).keeperIds;
		keepers = ((BayesianModel)model).keepers;
		source = ((BayesianModel)model).source;
	}

	@Override
	public void applyClause(String keyword, Object argument)
			throws ThinklabException {
		
		if (keyword.equals(":import")) {
			this.source = argument.toString();
		} else if (keyword.equals(":algorithm")) {
			this.algorithm = argument.toString();
		} else if (keyword.equals(":keep")) {
			
			Collection<?> p = (Collection<?>) argument;
			for (Object c : p)
				keeperIds.add(c.toString());

		} else super.applyClause(keyword, argument);
			
	}

	@Override
	public void addObservedModel(IModel model) {
		
		if (! (((Model)model).getDefinition() instanceof ClassificationModel)) {
			throw new ThinklabRuntimeException(
					"bayesian node " + model.getId() + 
					" should be a classification");
		}

		// anything that is specifically modeled becomes a keeper automatically
		keeperIds.add(((DefaultAbstractModel)model).getObservableId());
		super.addObservedModel(model);
	}
	
	@Override
	public void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		super.validateMediatedModel(model);
	}


	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel getConfigurableClone() {
		
		BayesianModel ret = new BayesianModel();
		ret.copy(this);
		return ret;
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session, Collection<Topology> extents) throws ThinklabException {

		ArrayList<Object> arr = new ArrayList<Object>();
		
		arr.add("modeltypes:BayesianTransformer");
		
		if (!isMediating())
			arr.add(Polylist.list(CoreScience.HAS_OBSERVABLE, this.observableSpecs));
				
		if (source != null)
			arr.add(Polylist.list(BayesianTransformer.HAS_NETWORK_SOURCE, source));

		if (algorithm != null)
			arr.add(Polylist.list(BayesianTransformer.HAS_BAYESIAN_ALGORITHM, algorithm));

		for (int i = 0; i < keepers.size(); i++) {
			arr.add(Polylist.list(
						BayesianTransformer.RETAINS_STATES, 
						keepers.get(i).toString()));
		}
		
		/*
		 * communicate how to model specific nodes that had their
		 * model specified by passing a prototype observation
		 */
		for (IModel c : observed) {
			arr.add(Polylist.list(
					BayesianTransformer.HAS_PROTOTYPE_MODEL,
					((Model)c).getDefinition().buildDefinition(kbox, session, null)));
		}

		return Polylist.PolylistFromArrayList(arr);
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void validateSemantics(ISession session) throws ThinklabException {
		
		for (String s : keeperIds) {
			keepers.add(annotateConcept(s,session, null));
		}
		
		/*
		 * This is fairly expensive so we only do it when annotating.
		 */
		if (session.getVariable(ModellingPlugin.ANNOTATION_UNDERWAY) != null) {

			/**
			 * if in annotation mode and loading a BN from a source,
			 * we should annotate the BN contents in the observable's 
			 * ontology (which also validates the source).
			 */
			Network bn = new Network();
			HashSet<String> stt = new HashSet<String>();
			
			try {
				bn.readFile(MiscUtilities.resolveUrlToFile(source).toString());
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		
			String cspace = observable.getConceptSpace();
			for (String s : bn.getAllNodeIds()) {
				annotateConcept(cspace+":"+s, session, null);
				for (String os : bn.getOutcomeIds(s)) {
					if (stt.contains(os))
						throw new ThinklabValidationException(
								"you did it - state ID " + os + 
								" is duplicated. Please use the node ID in "+ 
								"all outcome: e.g. " + os + s +
								" and avoid generic outcome IDs like High, Low");
					
					annotateConcept(cspace+":"+os, session, cspace+":"+s);	
					stt.add(os);
				}
			}
		}
		
		/**
		 * TODO validate any states defined inline (later)
		 */
	}

}
