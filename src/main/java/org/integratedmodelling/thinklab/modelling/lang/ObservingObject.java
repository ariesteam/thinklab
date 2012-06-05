package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.IObservingObject;
import org.integratedmodelling.thinklab.api.modelling.parsing.IObservingObjectDefinition;

/**
 * Models and Observers. They both have observables, which are complicated enough to handle
 * to deserve being handled once.
 * 
 * @author Ferd
 *
 */
@Concept(NS.OBSERVING_OBJECT)
public abstract class ObservingObject<T> extends ModelObject<T> implements IObservingObject, IObservingObjectDefinition {
	
	@Property(NS.HAS_DEPENDENCY)
	ArrayList<Triple<Object, String, Boolean>> _dependencies = 
			new ArrayList<Triple<Object,String, Boolean>>();

	@Property(NS.HAS_OBSERVABLE)
	ArrayList<ISemanticObject<?>> _observables = 
			new ArrayList<ISemanticObject<?>>();

	protected boolean _initialized = false;
	String _observableCName;

	/*
	 * non-persistent fields
	 */
	ArrayList<IList> _observableDefs = new ArrayList<IList>();
	
	@Override
	public void addObservable(IList instance) {
		_observableCName = instance.first().toString();
		_observableDefs.add(instance);
	}
	
	
	@Override
	public String getObservableConceptName() {
		return _observableCName;
	}

	@Override
	public void addDependency(Object cmodel, String formalName, boolean required) {
		
		_dependencies.add(new Triple<Object, String, Boolean>(cmodel, formalName, required));
	}

	@Override
	public List<Triple<Object, String, Boolean>> getDependencies() {
		return _dependencies;
	}
	
	public void initialize() throws ThinklabException {
		
		if (_initialized)
			return;

		_initialized = true;
		
		for (IList list : _observableDefs) {
			_observables.add(Thinklab.get().entify(list));
		}
		
		/*
		 * create or initialize any object we depend on
		 */
		ArrayList<Triple<Object, String, Boolean>> deps = 
				new ArrayList<Triple<Object,String, Boolean>>();
		
		for (Triple<Object, String, Boolean>  dp : _dependencies) {
			if (dp.getFirst() instanceof IList) {
				deps.add(new Triple<Object, String, Boolean>(
							Thinklab.get().entify((IList)(dp.getFirst())), 
							dp.getSecond(), dp.getThird()));
			} else {
				((Model)(dp.getFirst())).initialize();
				deps.add(dp);
			}
		}
		
		_dependencies = deps;
		
	}
}
