package org.integratedmodelling.thinklab.modelling.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.runtime.ISession;

public abstract class DefaultAbstractObserver implements IObserver {

	/**
	 * Mediation chain can be conditional.
	 */
	ArrayList<Pair<DefaultAbstractObserver, IExpression>> _mediated =
			new ArrayList<Pair<DefaultAbstractObserver,IExpression>>();
	
	/*
	 * dependencies can be observations or models and come with a statement of
	 * optional/required and a formal name to use in expressions
	 */
	class Dependency {
		DefaultAbstractObserver observer;
		boolean isRequired = true;
		String formalName = null;
	}
	
	ArrayList<Dependency> _dependencies = new ArrayList<Dependency>();
	
	@Override
	public Set<IInstance> getObservables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LanguageElement getLanguageElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMetadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IObservation> observe(IContext context, IKBox kbox,
			ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObserver train(IContext context, IKBox kbox, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObserver applyScenario(IScenario scenario) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IModel> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * --------------------------------------------------------------------------------------
	 * implementation-specific API
	 * --------------------------------------------------------------------------------------
	 */
	
	/**
	 * Check resolved status non-recursively, i.e. if we use another model to observe, we consider
	 * ourselves resolved even if that model is not.
	 * 
	 * @return true if there's another model, state or observation to take data from. If false, we
	 * need to refer to external information to observe our observable.
	 */
	boolean isResolved() {
		return false;
	}
	
	Collection<IInstance> collectUnresolvedObservables (Collection<IInstance> unresolved) {

		if (unresolved == null)
			unresolved = new ArrayList<IInstance>();
		
//		for (Dependency d : _mediated)
//			((DefaultAbstractModel)d.model).collectUnresolvedModels(unresolved);
		
//		if (this instanceof Model) {
//			((DefaultAbstractModel)((Model)this)._contextModel).collectUnresolvedModels(unresolved);
//		}

//		for (Dependency d : _dependencies) {
//			((DefaultAbstractModel)d.model).collectUnresolvedModels(unresolved);
//		}
//		
//		if (this instanceof AbstractStateModel && !((AbstractStateModel)this).isResolved())
//			unresolved.add((AbstractStateModel) this);

		return unresolved;
	}

}
