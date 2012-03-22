package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
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
	ArrayList<IAxiom> _axioms = new ArrayList<IAxiom>();
	ArrayList<INamespace> _importedNamespaces = new ArrayList<INamespace>();

	IOntology _ontology;
	String _id;
	String _resourceUrl;
	long _timeStamp;
	IProject _project;

	// these shouldn't be here, but ok
	int        _lastLineNumber = 0;
	int        _firstLineNumber = 0;

	public Namespace(IReferenceList list) {
		super(list);
	}
	
	public Namespace() {
	}


	public void initialize() throws ThinklabException {
		
		/*
		 * create ontology from the axioms collected in namespace
		 */
		_ontology = Thinklab.get().createOntology(
				_id,
				(_project == null ? NS.DEFAULT_THINKLAB_ONTOLOGY_PREFIX : _project.getOntologyNamespacePrefix()),
				_axioms);
		/*
		 * initialize observables for all observing objects now that we have the
		 * namespace concepts.
		 */
		for (IModelObject o : _modelObjects) {
			if (o instanceof ObservingObject) {
				((ObservingObject<?>)o).createObservables();
			}
		}
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

	@Override
	public String getResourceUrl() {
		return _resourceUrl;
	}
	
	@Override
	public int getFirstLineNumber() {
		return _firstLineNumber;
	}
	
	@Override
	public int getLastLineNumber() {
		return _lastLineNumber;
	}
	
	@Override
	public void setLineNumbers(int startLine, int endLine) {
		_firstLineNumber = startLine;
		_lastLineNumber  = endLine;
	}
}
