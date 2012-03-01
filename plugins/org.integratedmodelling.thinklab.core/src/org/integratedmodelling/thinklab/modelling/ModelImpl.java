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


	IInstance _observable;
	

	public ModelImpl(Model o) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IInstance getObservable() {
		return _observable;
	}

	@Override
	public SemanticAnnotation conceptualize() throws ThinklabException {

		ArrayList<Object> ret = new ArrayList<Object>();
		
		ret.add(MN.MODEL);
		ret.add(PolyList.list(MN.HAS_OBSERVABLE, _observable.conceptualize().asList()));
		
		return new SemanticAnnotation(PolyList.fromCollection(ret), Thinklab.get());
	}

	@Override
	public void define(SemanticAnnotation conceptualization) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IObserver getObserver() {
		// TODO Auto-generated method stub
		return null;
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
	public Collection<IObservation> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

}
