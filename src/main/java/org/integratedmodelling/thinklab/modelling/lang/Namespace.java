package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IAxiom;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.parsing.IModelObjectDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.INamespaceDefinition;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.modelling.ModelManager;

@Concept(NS.NAMESPACE)
public class Namespace extends SemanticObject<INamespace> implements INamespaceDefinition {

	@Property(NS.HAS_TIMESTAMP)
	long _timeStamp;

	@Property(NS.HAS_ID)
	String _id;
	
	String _trainingKbox = null;
	String _storageKbox = null;
	String _lookupKbox = null;
	String _expressionLanguage = ModelManager.DEFAULT_EXPRESSION_LANGUAGE;
	
	ArrayList<IModelObject> _modelObjects = new ArrayList<IModelObject>();
	ArrayList<IAxiom> _axioms = new ArrayList<IAxiom>();
	ArrayList<INamespace> _importedNamespaces = new ArrayList<INamespace>();
	HashSet<IAxiom> _axiomHash = new HashSet<IAxiom>();
	
	HashMap<String, IModelObject> _namedObjects = new HashMap<String, IModelObject>();
	
	IOntology _ontology;
	String _resourceUrl;

	IProject _project;

	IContext _coverage;
	
	// these shouldn't be here, but ok
	int        _lastLineNumber = 0;
	int        _firstLineNumber = 0;
	
	private int _nextAxiom;

	public Namespace(IReferenceList list) {
		super(list);
	}
	
	public Namespace() {
	}

	/**
	 * Exec all axioms accumulated so far to actualize gathered knowledge.
	 * @throws ThinklabException 
	 */
	public void flushKnowledge() throws ThinklabException {

		if (_ontology == null) {
			
			if (Thinklab.get().hasOntology(_id)) 
				Thinklab.get().dropOntology(_id);
			
			/*
			 * create ontology from the axioms collected in namespace. First create the bare ontology and 
			 * assign it to the NS
			 */
			_ontology = Thinklab.get().createOntology(
					_id,
					(_project == null ? NS.DEFAULT_THINKLAB_ONTOLOGY_PREFIX : _project.getOntologyNamespacePrefix()),
					null);
			
			/*
			 * add current axioms when we already assigned _ontology, so that namespace lookup will work from
			 * inside define()
			 */
			_ontology.define(_axioms);
			
		} else {
			ArrayList<IAxiom> axioms = new ArrayList<IAxiom>();
			for (int i = _nextAxiom; i < _axioms.size(); i++) {
				axioms.add(_axioms.get(i));
			}
			_ontology.define(axioms);
		}
		
		_nextAxiom = _axioms.size();
		
	}
	
	public void initialize() throws ThinklabException {

	}

	private boolean isAnonymous(IModelObject o) {
		return o.getId() == null || ModelManager.isGeneratedId(o.getId());
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
		
		if (_axiomHash.contains(axiom)) 
			return;
		_axioms.add(axiom);
		_axiomHash.add(axiom);
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
	public void addImportedNamespace(INamespace namespace) {
		_importedNamespaces.add(namespace);
	}

	@Override
	public void addModelObject(IModelObjectDefinition modelObject) {
		_modelObjects.add((IModelObject)modelObject);
		if (!isAnonymous(modelObject)) {
			_namedObjects.put(modelObject.getId(), modelObject);
		}
	}

	@Override
	public void setProject(IProject project) {
		_project = project;
	}

	@Override
	public IConcept getConcept(String s) {
		return _ontology == null ? null : _ontology.getConcept(s);
	}

	@Override
	public IProperty getProperty(String s) {
		return _ontology == null ? null : _ontology.getProperty(s);
	}

	@Override
	public IProject getProject() {
		return _project;
	}
	
	@Override
	public IModelObject getModelObject(String mod) {
		return _namedObjects.get(mod);
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
	
	@Override
	public void setStorageKbox(String kboxUri) {
		_storageKbox = kboxUri;
	}

	@Override
	public void setTrainingKbox(String kboxUri) {
		_trainingKbox = kboxUri;
	}

	@Override
	public String getStorageKbox() {
		return _storageKbox;
	}

	@Override
	public String getTrainingKbox() {
		return _trainingKbox;
	}
	
	@Override
	public void setLookupKbox(String kboxUri) {
		_lookupKbox = kboxUri;
	}
	
	@Override
	public String getLookupKbox() {
		return _lookupKbox;
	}

	@Override
	public void setExpressionLanguage(String language) {
		_expressionLanguage = language;
	}

	@Override
	public String getExpressionLanguage() {
		return _expressionLanguage;
	}

	public void releaseKnowledge() {
		if (_ontology != null)
			Thinklab.get().getKnowledgeRepository().releaseOntology(_ontology.getConceptSpace());
	}

	@Override
	public IContext getCoverage() {
		return _coverage;
	}

	@Override
	public void addCoveredExtent(IExtent extent) {
		if (_coverage == null) {
			_coverage = new Context();
		}
		try {
			_coverage.merge(extent);
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

}
