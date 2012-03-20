package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IAxiom;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.api.lang.parsing.IModelObjectDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.INamespaceDefinition;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.project.IProject;

@Concept(NS.NAMESPACE)
public class Namespace extends SemanticObject<INamespace> implements INamespaceDefinition {

	ArrayList<IModelObject> _modelObjects = new ArrayList<IModelObject>();
	IOntology _ontology;
	String _id;
	ArrayList<IAxiom> _axioms;
	String _resourceUrl;
	long _timeStamp;
	ArrayList<INamespace> _importedNamespaces = new ArrayList<INamespace>();
	IProject _project;
	
	public Namespace(IReferenceList list) {
		super(list);
	}
	
	public Namespace() {
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
	public List<IModelObject> getModelObjects() {
		return _modelObjects;
	}

	@Override
	public long getTimeStamp() {
		return _timeStamp;
	}

	public void setOntology(IOntology ontology) {
		this._ontology = ontology;
	}

	public IOntology getOntology() {
		return _ontology;
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

	@Override
	public void setProject(IProject project) {
		_project = project;
	}

	@Override
	public IConcept getConcept(String s) {
		return _ontology.getConcept(s);
	}

	@Override
	public IProperty getProperty(String s) {
		return _ontology.getProperty(s);
	}

	@Override
	public IProject getProject() {
		return _project;
	}
	
	@Override
	public IModelObject getModelObject(String mod) {
		// TODO Auto-generated method stub
		return null;
	}
}
