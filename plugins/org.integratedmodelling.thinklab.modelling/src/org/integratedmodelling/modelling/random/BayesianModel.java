package org.integratedmodelling.modelling.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.modelling.DefaultStatefulAbstractModel;
import org.integratedmodelling.modelling.corescience.ClassificationModel;
import org.integratedmodelling.modelling.implementations.observations.BayesianTransformer;
import org.integratedmodelling.modelling.interfaces.IContextOptional;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class BayesianModel extends DefaultStatefulAbstractModel implements IContextOptional {

	String source = null;
	String algorithm = null;
	ArrayList<IConcept> keepers = new ArrayList<IConcept>();
	
	/*
	 * models representing each node. They may be there or not, if they
	 * are the result observation will contextualize the nodes as specified.
	 */
	HashMap<IConcept,IModel> nodeModels = new HashMap<IConcept, IModel>();
	
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
				keepers.add(KnowledgeManager.get().requireConcept(c.toString()));

		} else super.applyClause(keyword, argument);
			
	}

	/**
	 * define the model for a specific node.
	 * @param model
	 * @throws ThinklabException
	 */
	public void addNodeModel(IModel model) throws ThinklabException {
		
		if (! (model instanceof ClassificationModel)) {
			throw new ThinklabValidationException(
					"bayesian node " + model.getObservable() + 
					" should be a classification");
		}
		nodeModels.put(model.getObservable(), model);
		// anything that is specifically modeled becomes a keeper automatically
		keepers.add(model.getObservable());
	}
	
	@Override
	protected void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
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
		ret.algorithm = this.algorithm;
		ret.keepers = this.keepers;
		ret.nodeModels = this.nodeModels;
		ret.source = this.source;
		return ret;
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session) throws ThinklabException {

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
		for (IConcept c : nodeModels.keySet()) {
			arr.add(Polylist.list(
					BayesianTransformer.HAS_PROTOTYPE_MODEL,
					nodeModels.get(c).buildDefinition(kbox, session)));
		}
		
		return Polylist.PolylistFromArrayList(arr);
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
