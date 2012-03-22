package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.parsing.IModelDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IObservingObjectDefinition;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservingObject;

/**
 * Models and Observers. They both have observables, which are complicated enough to handle
 * to deserve being handled once.
 * 
 * @author Ferd
 *
 */
@Concept(NS.OBSERVING_OBJECT)
public abstract class ObservingObject<T> extends ModelObject<T> implements IObservingObject, IObservingObjectDefinition {
	
	ArrayList<Triple<IModel, String, Boolean>> _dependencies = 
			new ArrayList<Triple<IModel,String, Boolean>>();
	
	ArrayList<ISemanticObject<?>> _observables = new ArrayList<ISemanticObject<?>>();

	/*
	 * non-persistent fields
	 */
	ArrayList<IList> _observableDefs = new ArrayList<IList>();


	/*
	 *  called by the namespace after all concepts are in
	 */
	void createObservables() throws ThinklabException {
		for (IList list : _observableDefs) {
			_observables.add(Thinklab.get().entify(list));
		}
	}
	
	@Override
	public void addObservable(IList instance) {
		_observableDefs.add(instance);
	}

	@Override
	public List<ISemanticObject<?>> getObservables() {
		return _observables;
	}

	@Override
	public void addDependency(IModelDefinition cmodel, String formalName, boolean required) {
		_dependencies.add(new Triple<IModel, String, Boolean>((IModel)cmodel, formalName, required));
	}

	@Override
	public List<Triple<IModel, String, Boolean>> getDependencies() {
		return _dependencies;
	}
	

}