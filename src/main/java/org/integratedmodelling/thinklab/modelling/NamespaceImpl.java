package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.lang.model.Namespace;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;

public class NamespaceImpl extends SemanticObject implements INamespace {

	Namespace _bean;
	ArrayList<IModelObject> _modelObjects = new ArrayList<IModelObject>();
	IOntology _ontology;
	
	public NamespaceImpl(IReferenceList list, Object object) {
		super(list, object);
	}
	
//	public NamespaceImpl() {}
//	
	public NamespaceImpl(Namespace bean) {
		super(null, bean);
		initialize(bean);
	}
//	
//	public NamespaceImpl(IOntology ontology) {
//		this._ontology = ontology;
//	}
	
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

	public void setOntology(IOntology ontology) {
		this._ontology = ontology;
	}
}
