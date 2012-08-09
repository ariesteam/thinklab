package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.collections.Triple;
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
import org.integratedmodelling.thinklab.api.modelling.parsing.IConceptDefinition;
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
	
	ArrayList<String> _trainingNamespaces = new ArrayList<String>();
	ArrayList<String> _lookupNamespaces = new ArrayList<String>();
	
	String _expressionLanguage = ModelManager.DEFAULT_EXPRESSION_LANGUAGE;
	
	ArrayList<IModelObject> _modelObjects = new ArrayList<IModelObject>();
	ArrayList<IAxiom> _axioms = new ArrayList<IAxiom>();
	ArrayList<INamespace> _importedNamespaces = new ArrayList<INamespace>();
	HashSet<IAxiom> _axiomHash = new HashSet<IAxiom>();
	
	HashMap<String, Object> _symbolTable = new HashMap<String, Object>();
	
	/*
	 * use symbol table filled in by the parser instead
	 */
	@Deprecated
	HashMap<String, IModelObject> _namedObjects = new HashMap<String, IModelObject>();
	
	ArrayList<Pair<String, Integer>> _errors = new ArrayList<Pair<String,Integer>>();
	ArrayList<Pair<String, Integer>> _warnings = new ArrayList<Pair<String,Integer>>();
	
	IOntology _ontology;
	String _resourceUrl;

	IProject _project;

	IContext _coverage;
	
	// these shouldn't be here, but ok
	int        _lastLineNumber = 0;
	int        _firstLineNumber = 0;
	
	private int _nextAxiom;

	private IConceptDefinition _agentType;

	public Namespace(IReferenceList list) {
		super(list);
	}
	
	public Namespace() {
	}

	@Override
	public Map<String, Object> getSymbolTable() {
		return _symbolTable;
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

	@Override
	public void setOntology(IOntology ontology) {
		this._ontology = ontology;
	}

	@Override
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

		Object o = _symbolTable.get(mod);
		if (o instanceof IModelObject)
			return (IModelObject)o;
		
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

	@Override
	public void setAgentConcept(IConceptDefinition agentConcept) {
		this._agentType = agentConcept;
	}

	@Override
	public void addWarning(String warning, int lineNumber) {	
		_warnings.add(new Pair<String, Integer>(warning, lineNumber));
	}
	
	@Override
	public void addError(int errorCode, String errorMessage, int lineNumber) {
		this._errors.add(new Pair<String, Integer>(errorMessage, lineNumber));
	}

	@Override
	public boolean hasErrors() {
		return _errors.size() > 0;
	}

	@Override
	public boolean hasWarnings() {
		return _warnings.size() > 0;
	}

	@Override
	public Collection<Triple<Integer, String, Integer>> getErrors() {

		List<Triple<Integer, String, Integer>> ret = new ArrayList<Triple<Integer,String,Integer>>();
		for (Pair<String, Integer> e : _errors) {
			ret.add(new Triple<Integer, String, Integer>(0, e.getFirst(), e.getSecond()));
		}
		return ret;
	}

	@Override
	public Collection<Pair<String, Integer>> getWarnings() {
		return _warnings;
	}
	
	@Override
	public List<String> getTrainingNamespaces() {
		return _trainingNamespaces;
	}
	
	@Override
	public List<String> getLookupNamespaces() {
		return _lookupNamespaces;
	}
	
	@Override
	public void addLookupNamespace(String tns) {
		_lookupNamespaces.add(tns);
	}
	
	@Override
	public void addTrainingNamespace(String tns) {
		_trainingNamespaces.add(tns);
	}

}
