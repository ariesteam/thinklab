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
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservingObject;
import org.integratedmodelling.thinklab.api.modelling.parsing.IModelDefinition;
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
	ArrayList<Triple<IModel, String, Boolean>> _dependencies = 
			new ArrayList<Triple<IModel,String, Boolean>>();

	@Property(NS.HAS_OBSERVABLE)
	ArrayList<ISemanticObject<?>> _observables = 
			new ArrayList<ISemanticObject<?>>();

	protected boolean _initialized = false;
	
	/*
	 * non-persistent fields
	 */
	ArrayList<IList> _observableDefs = new ArrayList<IList>();
	
	@Override
	public void addObservable(IList instance) {
		_observableDefs.add(instance);
	}

	@Override
	public void addDependency(IModelDefinition cmodel, String formalName, boolean required) {
		_dependencies.add(new Triple<IModel, String, Boolean>((IModel)cmodel, formalName, required));
	}

	@Override
	public List<Triple<IModel, String, Boolean>> getDependencies() {
		return _dependencies;
	}
	
	public void initialize() throws ThinklabException {
		
		if (_initialized)
			return;
		
		for (IList list : _observableDefs) {
			_observables.add(Thinklab.get().entify(list));
		}
		
		_initialized = true;
	}
}
