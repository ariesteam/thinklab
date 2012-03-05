package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.SemanticAnnotation;
import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.lang.model.Model;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;
import org.integratedmodelling.thinklab.modelling.internal.MN;

public class ModelImpl implements IModel, IConceptualizable {

	Model _bean;
	SemanticAnnotation _observable;
	IMetadata _metadata;
	IObserver _observer;

	public ModelImpl(Model o) {
		_bean = o;
	}

	@Override
	public IInstance getObservable() {
		return null; //_observable;
	}

	@Override
	public SemanticAnnotation conceptualize() throws ThinklabException {

		ArrayList<Object> ret = new ArrayList<Object>();
		
		ret.add(MN.MODEL);
		ret.add(PolyList.list(MN.HAS_OBSERVABLE, _observable.asList()));
		
		return new SemanticAnnotation(PolyList.fromCollection(ret), Thinklab.get());
	}

	@Override
	public void define(SemanticAnnotation conceptualization) throws ThinklabException {
		// TODO Auto-generated method stub
		
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
	public Set<IInstance> getObservables() {
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
