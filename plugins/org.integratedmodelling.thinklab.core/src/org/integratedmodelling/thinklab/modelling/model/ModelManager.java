package org.integratedmodelling.thinklab.modelling.model;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.interpreter.ModelGenerator;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.list.InstanceList;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.IAgentModel;
import org.integratedmodelling.thinklab.api.modelling.IAnnotation;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.ModelTypes;
import org.integratedmodelling.thinklab.api.modelling.classification.IClassification;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelManager;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.units.IUnit;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.modelling.model.implementation.ClassificationModel;
import org.integratedmodelling.thinklab.modelling.model.implementation.MeasurementModel;
import org.integratedmodelling.thinklab.modelling.model.implementation.Model;
import org.integratedmodelling.thinklab.modelling.model.implementation.RankingModel;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.thinklab.proxy.ModellingModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ModelManager implements IModelManager, IModelFactory {

	private static ModelManager _this = null;
	private static Namespace _defaultNS = null;
	
	private Hashtable<String, IModel> modelsById = new Hashtable<String, IModel>();
	private Hashtable<String, IScenario> scenariosById = new Hashtable<String, IScenario>();
	private Hashtable<String, IContext> contextsById = new Hashtable<String, IContext>();
	private Hashtable<String, IAgentModel> agentsById = new Hashtable<String, IAgentModel>();
	private Hashtable<String, IAnnotation> annotationsById = new Hashtable<String, IAnnotation>();
	private Hashtable<String, INamespace> namespacesById = new Hashtable<String, INamespace>();

	/*
	 * we put all model observable instances here.
	 */
	ISession _session = null;
	ModelMap _map = null;
	
	
	/**
	 * Return the singleton model manager. Use injection to modularize the
	 * parser/interpreter.
	 * 
	 * @return
	 */
	public static ModelManager get() {
		
		if (_this == null) {
			_this = new ModelManager();
		}
		return _this;
	}
	
	private ModelManager() {
		_session = new Session();
		_map = new ModelMap();
	}
	
	public IAnnotation requireAnnotation(String s) throws ThinklabException {
		IAnnotation ret = getAnnotation(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("annotation " + s + " not found");
		return ret;
	}

	public IModel retrieveModel(String s) {
		return getModel(s);
	}

	public IModel requireModel(String s) throws ThinklabException {
		IModel ret = getModel(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("model " + s + " not found");
		return ret;
	}

	public IAgentModel retrieveAgentModel(String s) {
		return getAgentModel(s);
	}

	public IAgentModel requireAgentModel(String s) throws ThinklabException {
		IAgentModel ret = getAgentModel(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("agent model " + s + " not found");
		return ret;
	}

	public IScenario retrieveScenario(String s) {
		return getScenario(s);
	}

	public IScenario requireScenario(String s) throws ThinklabException {
		IScenario ret = getScenario(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("scenario " + s + " not found");
		return ret;
	}

	public IContext retrieveContext(String s) {
		return getContext(s);
	}

	public IContext requireContext(String s) throws ThinklabException {
		IContext ret = getContext(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("context " + s + " not found");
		return ret;
	}

	@Override
	public void releaseNamespace(String namespace) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSource(String object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IModelObject> getDependencies(String object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<INamespace> getNamespaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext getCoverage(IModel model, IKBox kbox, ISession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IScenario> getApplicableScenarios(IModel model,
			IContext context, boolean isPublic) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext run(IModel model, IKBox kbox, ISession session, IContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INamespace loadFile(String resourceId) throws ThinklabException {

		INamespace ret = null;
		
		if (resourceId.endsWith(".tql")) {
		
			// TODO separate into expressions with form reader, read one by one,
			// store everything in model map.
			
			Injector injector = Guice.createInjector(new ModellingModule());
			ModelGenerator mg = injector.getInstance(ModelGenerator.class);
			ret = mg.load(resourceId);
			
		} else if (resourceId.endsWith(".clj")) {
			
		}
		
		return ret;
	}

	@Override
	public IAnnotation getAnnotation(String s) {
		return annotationsById.get(s);
	}

	@Override
	public IModel getModel(String s) {
		return modelsById.get(s);
	}

	@Override
	public IAgentModel getAgentModel(String s) {
		return agentsById.get(s);
	}

	@Override
	public IScenario getScenario(String s) {
		return scenariosById.get(s);
	}

	@Override
	public IContext getContext(String s) {
		return contextsById.get(s);
	}

	@Override
	public INamespace getNamespace(String ns) {
		return namespacesById.get(ns);
	}

	@Override
	public IModelObject getModelObject(String object) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public INamespace createNamespace(String namespace, String ontologyId, IList ontology) {

		Namespace ret = new Namespace(namespace, ontologyId);
		
		if (ontology != null) {
			ret.defineOntology(ontology);
		}
		
		try {
			ret.initialize();
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}

		return ret;
	}

	@Override
	public INamespace getDefaultNamespace() {
		
		// TODO/FIXME this should be linked to the current session; at this point no 
		// concurrent runtime should use the default namespace, but there is
		// no way to check.
		if (_defaultNS == null) {
			_defaultNS = new Namespace("user", "user.cspace");
		}
		return _defaultNS;
	}

	@Override
	public void register(IModelObject arg, String arg1, INamespace arg2) {
		// TODO Auto-generated method stub
		if (arg instanceof IModel) {
			modelsById.put(arg2.getNamespace() + "/" + arg1, (IModel) arg);
		} else if (arg instanceof IAgentModel) {
			agentsById.put(arg2.getNamespace() + "/" + arg1, (IAgentModel) arg);
		} else if (arg instanceof IContext) {
			contextsById.put(arg2.getNamespace() + "/" + arg1, (IContext) arg);
		} else if (arg instanceof IScenario) {
			scenariosById.put(arg2.getNamespace() + "/" + arg1, (IScenario) arg);			
		}
	}

	@Override
	public IModel createModel(INamespace ns, SemanticType modelType, Map<String, Object> def) 
			throws ThinklabException {

		IModel ret = null;

//      TODO use class from the concept, instantiate and define
//		Thinklab.get().getClassForConcept(modelType.getConcept(Thinklab.get()));
//		
//		if ()
		
		if (modelType.equals(ModelTypes.C_MODEL)) {
			ret = new Model(ns).define(def);
		} else if (modelType.equals(ModelTypes.C_MEASUREMENT)) {			
			ret = new MeasurementModel(ns, (IUnit) def.get(K_UNIT)).define(def);
		} else if (modelType.equals(ModelTypes.C_RANKING)) {			
			ret = new RankingModel(ns).define(def);
		} else if (modelType.equals(ModelTypes.C_CLASSIFICATION)) {			
			ret = new ClassificationModel(ns, createClassification(def.get(K_CLASSIFICATION))).define(def);
		}
		
		return ret;
	}

	private IClassification createClassification(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IUnit parseUnit(String unit) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance createObservable(InstanceList inst) throws ThinklabException {
		return _session.createObject(inst.asList());
	}

}
