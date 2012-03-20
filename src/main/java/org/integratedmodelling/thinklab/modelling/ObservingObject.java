package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.collections.Triple;
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
public abstract class ObservingObject extends ModelObject implements IObservingObject, IObservingObjectDefinition {
	
	ArrayList<Triple<IModel, String, Boolean>> _dependencies = 
			new ArrayList<Triple<IModel,String, Boolean>>();
	
	ArrayList<IList> _observables = new ArrayList<IList>();

	@Override
	public void addObservable(IList instance) {
		_observables.add(instance);
	}

	@Override
	public List<IList> getObservables() {
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
