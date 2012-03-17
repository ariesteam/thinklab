package org.integratedmodelling.thinklab.modelling;

import java.util.Collection;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.lang.model.Model;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.modelling.internal.NamespaceQualified;

public class ModelImpl extends NamespaceQualified implements IModel {

	public ModelImpl(IReferenceList semantics, Object object) {
		super(semantics, object);
		// TODO Auto-generated constructor stub
	}

	public ModelImpl(Model o) {
		super(null, o);
		_bean = o;
	}

	Model _bean;
	ISemanticObject _observable;
	IMetadata _metadata;
	IObserver _observer;


	
	@Override
	public ISemanticObject getObservable() {
		return null; //_observable;
	}

	@Override
	public IObserver getObserver() {
		return _observer;
	}

	@Override
	public IObservation contextualize(IContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ISemanticObject> getObservables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LanguageElement getLanguageElement() {
		// TODO Auto-generated method stub
		return _bean;
	}

	@Override
	public String getId() {
		return _bean.getId();
	}

	@Override
	public String getNamespace() {
		return _bean.getNamespace().getId();
	}

	@Override
	public String getName() {
		return getNamespace() + "/" + getId();
	}

	@Override
	public IMetadata getMetadata() {
		return _metadata;
	}

	@Override
	public Collection<IObservation> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

}
