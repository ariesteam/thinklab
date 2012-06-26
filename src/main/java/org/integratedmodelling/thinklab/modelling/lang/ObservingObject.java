package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.IObservingObject;
import org.integratedmodelling.thinklab.api.modelling.parsing.IObservingObjectDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IPropertyDefinition;

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
	ArrayList<IDependency> _dependencies = new ArrayList<IDependency>();

	@Property(NS.HAS_OBSERVABLE)
	ArrayList<ISemanticObject<?>> _observables = 
			new ArrayList<ISemanticObject<?>>();

	protected boolean _initialized = false;
	String _observableCName;

	ArrayList<IPropertyDefinition> _propdefs = new ArrayList<IPropertyDefinition>();
	
	@Concept(NS.DEPENDENCY)
	public static class Dependency implements IDependency {

		private Object _observable;
		private String _formalName;
		private IProperty _property;
		private boolean _optional;

		public Dependency() {}
		
		Dependency(Object cmodel, String formalName, IProperty property, boolean required) {
			this._observable = cmodel;
			this._formalName = formalName;
			this._property = property;
			this._optional = required;
		}
		
		@Override
		public Object getObservable() {
			return _observable;
		}

		@Override
		public String getFormalName() {
			return _formalName;
		}

		@Override
		public boolean isOptional() {
			return _optional;
		}

		@Override
		public IProperty getProperty() {
			return _property;
		}
		
	}
	
	
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
	public void addDependency(Object cmodel, String formalName, IPropertyDefinition property, boolean optional) {
		_propdefs.add(property);
		_dependencies.add(new Dependency(cmodel, formalName, null, optional));
	}

	@Override
	public List<IDependency> getDependencies() {
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
		ArrayList<IDependency> deps = new ArrayList<IDependency>();
		
		int i = 0;
		for (IDependency  dp : _dependencies) {
			
			IProperty property = null;
			if (_propdefs.get(i) != null) {
				property = Thinklab.p(_propdefs.get(i).getName());
			}
			
			if (dp.getObservable() instanceof IList) {
				deps.add(new Dependency(
							Thinklab.get().entify((IList)(dp.getObservable())), 
							dp.getFormalName(), null, dp.isOptional()));
			} else {
				((Model)(dp.getObservable())).initialize();
				deps.add(dp);
			}
			((Dependency)dp)._property = property;

			i++;
		}
		
		_dependencies = deps;
		
	}
}
