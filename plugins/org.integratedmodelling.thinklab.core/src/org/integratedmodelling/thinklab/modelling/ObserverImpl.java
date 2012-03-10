package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.lang.model.ObservingObject;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IScenario;

public abstract class ObserverImpl implements IObserver {

	protected ObservingObject _bean;
	
	/**
	 * Mediation chain can be conditional.
	 */
	ArrayList<Pair<ObserverImpl, IExpression>> _mediated =
			new ArrayList<Pair<ObserverImpl,IExpression>>();
	
	/*
	 * dependencies can be observations or models and come with a statement of
	 * optional/required and a formal name to use in expressions
	 */
	public class Dependency {
		ObserverImpl observer;
		boolean isRequired = true;
		String formalName = null;
	}
	
	ArrayList<Dependency> _dependencies = new ArrayList<Dependency>();
	
	public ObserverImpl(ObservingObject bean) {
		define(bean);
	}
	
	/**
	 * Derived classes will choose their default accessor; we chain any other
	 * to it and return it in our final getAccessors()
	 * 
	 * @param context
	 * @return
	 */
	protected abstract IAccessor getAccessor(IContext context);
	
	@Override
	public final List<IAccessor> getAccessors(IContext context) {

		ArrayList<IAccessor> ret = new ArrayList<IAccessor>();
		
		IAccessor defacc = getAccessor(context);
		if (defacc != null)
			ret.add(defacc);
		
		/*
		 * if we have a specialized one, create it, initialize it properly and
		 * add it after the default one 
		 */

		return ret;
	}
	
	
	private void define(ObservingObject bean) {
		this._bean = bean;			
	}

	@Override
	public Set<ISemanticObject> getObservables() {
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
	public List<IObservation> observe(IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObserver train(IContext context)
			throws ThinklabException {
		return this;
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
	
	/*
	 * resolve all unresolved observers and return them along with their potential observations in
	 * the passed context. Use the appropriate kbox for the project this observer comes from, or the 
	 * default "thinklab" kbox if undefined. Throw a ThinklabUnresolvedDependencyException if a required 
	 * unresolved dependency cannot be observed. 
	 */
	public Collection<Pair<IObserver, List<IObservation>>> resolve(IContext context) throws ThinklabException {

		ArrayList<Pair<IObserver, List<IObservation>>> tret = new ArrayList<Pair<IObserver, List<IObservation>>>();
		resolveInternal(tret, context);
		
		/*
		 * TODO loop over result; if any of the lists is null throw exception (through listener), otherwise
		 * call warning listeners in context and remove from result.
		 */
		ArrayList<Pair<IObserver, List<IObservation>>> ret = new ArrayList<Pair<IObserver, List<IObservation>>>();
		for (Pair<IObserver, List<IObservation>> rr : tret) {
			
			if (rr.getSecond() == null) {
				
			} else if (rr.getSecond().size() == 0) {
				
			} else {
				ret.add(rr);
			}
		}
		
		return ret;
	}
	
	/*
	 * put a null instead of throwing an exception if a required dependency isn't observable, so we can report
	 * all unresolved ones in a shot. Insert an empty list if an optional dependency is not resolved, so we can
	 * warn about it.
	 */
	private void resolveInternal(ArrayList<Pair<IObserver, List<IObservation>>> ret, IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		
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
		
	}

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
	

}
