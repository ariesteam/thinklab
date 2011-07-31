package org.integratedmodelling.thinklab.modelling.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.InstanceList;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;
import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservationList;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.metadata.Metadata;
import org.integratedmodelling.thinklab.modelling.internal.NamespaceQualified;

import clojure.lang.IFn;

public abstract class DefaultAbstractModel extends NamespaceQualified implements IModel {

	protected IMetadata _metadata = new Metadata();
	IInstance  _observable = null;
	INamespace _namespace = null;

	/*
	 * dependent models are indexed by this structure to
	 * keep track of their interpretation within this model; these
	 * correspond to the keywords after the model in :context
	 */
	class Dependency {
		IModel  model;
		String  localName; // used only for dependencies
		IFn     when; // used only in definition 
		boolean required; // used only for dependencies
	}
	
	ArrayList<Dependency> _dependencies = new ArrayList<Dependency>();

	/*
	 * Model uses the mediated array for its definition
	 */
	ArrayList<Dependency> _mediated     = new ArrayList<Dependency>();
	
	// --- internal API below ------------------------------------------------
	
	public DefaultAbstractModel(INamespace ns) {
		this._namespace = ns;
	}

	/**
	 * Analyze the observables along the chain of dependencies and separate them in
	 * those that are resolved (have a state or a way to compute it) and unresolved
	 * (they need to be resolved in a kbox).
	 * 
	 * @return the sets of resolved and resolved observables in this order.
	 */
	public Pair<Set<IInstance>, Set<IInstance>> analyzeObservables() {
		return null;
	}
	
	// --- public API below --------------------------------------------------
 	
	@Override
	public Set<IInstance> getObservables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMetadata getMetadata() {
		return _metadata;
	}

	@Override
	public IConcept getObservableClass() {
		/*
		 * TODO mediated issue
		 */
		return _observable.getDirectType();
	}

	@Override
	public IObservationList observe(IContext context, IKBox kbox,
			ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel train(IContext context, IKBox kbox, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel applyScenario(IScenario scenario) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IModel> getDependencies() {
		
		ArrayList<IModel> ret = new ArrayList<IModel>();
		for (Dependency d : _dependencies) {
			ret.add(d.model);
		}
		return ret;
	}
	
	// --- private methods ---------------------------------------------------

	@SuppressWarnings("unchecked")
	public IModel define(Map<String, Object> def) {

		Collection<IModel> deps = (Collection<IModel>) def.get(IModelFactory.K_DEPENDENCIES);
		if (deps != null) {			
			
		}
		
		/*
		 * may be 
		 */
		InstanceList inst = (InstanceList) def.get(IModelFactory.K_OBSERVABLE);
		if (inst != null) {
			
		}

		Collection<IModel> meds = (Collection<IModel>) def.get(IModelFactory.K_MEDIATED);
		if (deps != null) {			
			
		}

		
		return this;
	}

	
}
