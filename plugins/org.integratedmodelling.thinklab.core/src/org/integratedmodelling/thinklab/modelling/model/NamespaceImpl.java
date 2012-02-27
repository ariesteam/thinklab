package org.integratedmodelling.thinklab.modelling.model;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.lang.model.Namespace;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;

public class NamespaceImpl implements INamespace, IConceptualizable {

	Namespace _bean;
	ArrayList<IModelObject> _modelObjects;
	IOntology _ontology;
	
	public NamespaceImpl() {}
	
	public NamespaceImpl(Namespace bean) {
		initialize(bean);
	}
	
	private void initialize(Namespace bean) {
		_bean = bean;
	}

	@Override
	public LanguageElement getLanguageElement() {
		return _bean;
	}

	@Override
	public String getNamespace() {
		return _bean.getId();
	}

	@Override
	public IOntology getOntology() {
		return _ontology;
	}

	@Override
	public Collection<IModelObject> getModelObjects() {
		return _modelObjects;
	}

	@Override
	public long getLastModification() {
		return _bean.getTimeStamp();
	}

	@Override
	public IList conceptualize() throws ThinklabException {
		return _bean.conceptualize();
	}

	@Override
	public void define(IList conceptualization) throws ThinklabException {
		// TODO Auto-generated method stub
		Namespace bean = new Namespace();
		bean.define(conceptualization);
		initialize(bean);
	}

}
