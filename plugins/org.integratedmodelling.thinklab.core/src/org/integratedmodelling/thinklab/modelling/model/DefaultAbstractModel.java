package org.integratedmodelling.thinklab.modelling.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservationIterator;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.metadata.Metadata;
import org.integratedmodelling.thinklab.modelling.internal.NamespaceQualified;

import clojure.lang.IFn;

public abstract class DefaultAbstractModel extends NamespaceQualified implements IModel {

	protected IMetadata _metadata = new Metadata();

	/*
	 * dependent models are indexed by this structure to
	 * keep track of their intrerpretation within this model; these
	 * correspond to the keywords after the model in :context
	 */
	class Dependency {
		IModel  _model;
		String  _localName;
		IFn     _when;
		boolean _required;
	}
	
	ArrayList<Dependency> _dependencies = new ArrayList<Dependency>();
	
	// --- internal API below ------------------------------------------------
	
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObservationIterator observe(IContext context, IKBox kbox,
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
		// TODO Auto-generated method stub
		return null;
	}
	
	// --- private methods ---------------------------------------------------

}
