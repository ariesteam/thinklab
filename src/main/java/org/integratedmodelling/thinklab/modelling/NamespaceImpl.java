package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.lang.model.Namespace;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IAxiom;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.api.lang.parsing.IModelObjectDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.INamespaceDefinition;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;

@Concept(NS.NAMESPACE)
public class NamespaceImpl extends SemanticObject<INamespace> implements INamespace, INamespaceDefinition {

	ArrayList<IModelObject> _modelObjects = new ArrayList<IModelObject>();
	IOntology _ontology;
	String _id;
	ArrayList<IAxiom> _axioms;
	String _resourceUrl;
	long _timeStamp;
	ArrayList<INamespace> _importedNamespaces = new ArrayList<INamespace>();
	
	public NamespaceImpl(IReferenceList list) {
		super(list);
	}
	
	public NamespaceImpl() {
	}

	Namespace _bean() {
		return (Namespace)demote();
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
		return _id;
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

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public INamespace demote() {
		return this;
	}


	@Override
	public void setId(String id) {
		_id = id;
	}


	@Override
	public void addAxiom(IAxiom axiom) {
		// TODO Auto-generated method stub
		_axioms.add(axiom);
	}


	@Override
	public void setResourceUrl(String resourceUrl) {
		_resourceUrl = resourceUrl;
	}

	@Override
	public void setTimeStamp(long timestamp) {
		_timeStamp = timestamp;
	}

	@Override
	public void addImportedNamespace(INamespaceDefinition namespace) {
		_importedNamespaces.add((INamespace)namespace);
	}

	@Override
	public void addModelObject(IModelObjectDefinition modelObject) {
		_modelObjects.add((IModelObject)modelObject);
	}
}
