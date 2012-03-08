package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.lang.model.Namespace;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;

public class NamespaceImpl implements INamespace {

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
}
