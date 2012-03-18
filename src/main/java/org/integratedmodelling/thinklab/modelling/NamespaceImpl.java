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

	ArrayList<IModelObject> _modelObjects = new ArrayList<IModelObject>();
	IOntology _ontology;
	
	public NamespaceImpl(IReferenceList list, Object object) {
		super(list, object);
	}
	
	NamespaceImpl(Namespace obj) {
		super(null, obj);
	}
	
	Namespace _bean() {
		return (Namespace)getObject();
	}

	public void initialize() {
		
		System.out.println("namespace::initialize called");

		/*
		 * TODO scan model objects and fill list
		 */

		/*
		 * TODO create ontology from axioms
		 */
	}

	@Override
	public LanguageElement getLanguageElement() {
		return _bean();
	}

	@Override
	public String getNamespace() {
		return _bean().getId();
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
		return _bean().getTimeStamp();
	}

	public void setOntology(IOntology ontology) {
		this._ontology = ontology;
	}
}
