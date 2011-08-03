package org.integratedmodelling.thinklab.modelling.model.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.list.InstanceList;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
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
import org.integratedmodelling.thinklab.modelling.context.Context;
import org.integratedmodelling.thinklab.modelling.context.ObservationList;
import org.integratedmodelling.thinklab.modelling.internal.NamespaceQualified;
import org.integratedmodelling.thinklab.modelling.model.ModelManager;


public abstract class DefaultAbstractModel extends NamespaceQualified implements IModel {

	public enum StateType {
		UPDATE,
		RATE,
		MOVE,
		DIFFUSE
	}
	
	protected IMetadata _metadata = new Metadata();
	IInstance  _observable = null;
	INamespace _namespace = null;
	
	/*
	 * I am optional if declared an optional dependency or part of a chain of
	 * dependencies declared optional above me.
	 */
	protected boolean _optional = false;
	
	/*
	 * all state expressions (update, move, rate, etc) are stored here indexed
	 * by their StateType
	 */
	HashMap<StateType, IExpression> _stateExpressions = 
			new HashMap<DefaultAbstractModel.StateType, IExpression>();

	/*
	 * dependent models are indexed by this structure to
	 * keep track of their interpretation within this model.
	 */
	class Dependency {
		IModel      model;
		String      localName; // used only for dependencies
		IExpression when; // used only in definition 
		boolean     required; // used only for dependencies
	}
	
	/*
	 * stores dependencies in all models. It's always empty in Model.
	 */
	ArrayList<Dependency> _dependencies = new ArrayList<Dependency>();

	/*
	 * Stores mediated models in all models except Model, which uses
	 * uses this for its definition
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
	 * @return the sets of 1. resolved and 2. unresolved observables in this order.
	 */
	public Pair<Set<IInstance>, Set<IInstance>> analyzeObservables() {
		
		HashSet<IInstance> res = new HashSet<IInstance>();
		HashSet<IInstance> unr = new HashSet<IInstance>();
		this.collectObservables(res, unr);
		return new Pair<Set<IInstance>, Set<IInstance>>(res, unr);
	}
	


	private void collectObservables(HashSet<IInstance> resolved, HashSet<IInstance> unresolved) {

		for (Dependency d : _mediated)
			((DefaultAbstractModel)d.model).collectObservables(resolved, unresolved);
		
		if (this instanceof Model) {
			((DefaultAbstractModel)((Model)this)._contextModel).collectObservables(resolved, unresolved);
		}

		for (Dependency d : _dependencies) {
			((DefaultAbstractModel)d.model).collectObservables(resolved, unresolved);
		}
		
		if (this instanceof AbstractStateModel && ((AbstractStateModel)this).isResolved())
			resolved.add(_observable);
		else 
			unresolved.add(_observable);
	}

	private Collection<AbstractStateModel> collectUnresolvedModels(Collection<AbstractStateModel> unresolved) {

		if (unresolved == null)
			unresolved = new ArrayList<AbstractStateModel>();
		
		for (Dependency d : _mediated)
			((DefaultAbstractModel)d.model).collectUnresolvedModels(unresolved);
		
		if (this instanceof Model) {
			((DefaultAbstractModel)((Model)this)._contextModel).collectUnresolvedModels(unresolved);
		}

		for (Dependency d : _dependencies) {
			((DefaultAbstractModel)d.model).collectUnresolvedModels(unresolved);
		}
		
		if (this instanceof AbstractStateModel && !((AbstractStateModel)this).isResolved())
			unresolved.add((AbstractStateModel) this);

		return unresolved;
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
	public IInstance getObservable() {
		return _observable;
	}
	
	@Override
	public IObservationList observe(IContext ctx, IKBox kbox, ISession session) 
			throws ThinklabException {

		ArrayList<Pair<IModel, IQueryResult>> deps = new ArrayList<Pair<IModel, IQueryResult>>();
		ArrayList<IModel> notfound   = new ArrayList<IModel>();
		Context context = (Context)ctx;
		/*
		 * Extract all unresolved dependencies and query them all. Complain only
		 * for mandatory dependencies that remain unresolved.
		 */
		for (AbstractStateModel model : collectUnresolvedModels(null)) {
			
			/*
			 * just skip it if it's already in context
			 */
			if (context.containsState(model.getObservable())) {
				context.getListenerSet().notifyAlreadyObservedState(model);
				continue;
			}
			
			boolean found = false;
			IQuery q = model.generateObservableQuery(null, session, context);
			IQueryResult r = null;
			if (!q.isEmpty()) {
				r = kbox.query(q);
				if (r.getResultCount() > 0) {
					found = true;
					deps.add(new Pair<IModel, IQueryResult>(model, r));
					((Context)context).getListenerSet().notifyDependencyFound(model);
				}
			}
			
			if (!found) {
 				if (!model._optional) {
 					((Context)context).getListenerSet().notifyOptionalDependencyNotFound(model);
 				} else {
 					((Context)context).getListenerSet().notifyOptionalDependencyNotFound(model);
 					notfound.add(model);
 				}
			} 
		}

		if (notfound.size() > 0) {
			String s = "";
			for (IModel m: notfound) {
				if (!s.isEmpty())
					s += ", ";
				s += m.getObservable().getDirectType();
			}
			
			throw new ThinklabResourceNotFoundException("required observations of " + s + " not found in context"); 
		}
		
		return new ObservationList(deps);
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
	public IModel define(Object ... objects) throws ThinklabException {
		
		HashMap<String, Object> def= new HashMap<String, Object>();
		for (int i = 0; i < objects.length; i += 2) {
			def.put(objects[i].toString(), objects[i+1]);
		}		
		return define(def);
	}
	
	
	@SuppressWarnings("unchecked")
	public IModel define(Map<String, Object> def) throws ThinklabException {

		Collection<IModel> deps = (Collection<IModel>) def.get(IModelFactory.K_DEPENDENCIES);
		if (deps != null) {			
			List<String> names = (List<String>) def.get(IModelFactory.K_DEPENDENCYNAMES);
			List<Boolean> mands = (List<Boolean>) def.get(IModelFactory.K_MANDATORYDEPENDENCY);
			
		}
		
		/*
		 * may be 
		 */
		InstanceList inst = (InstanceList) def.get(IModelFactory.K_OBSERVABLE);
		if (inst != null) {
			this._observable = ModelManager.get().createObservable(inst);
		}

		List<IModel> meds = (List<IModel>) def.get(IModelFactory.K_MEDIATED);
		if (deps != null) {			
			List<IExpression> whens = (List<IExpression>) def.get(IModelFactory.K_MCONDITIONALS);
		}

		
		return this;
	}


	


}
