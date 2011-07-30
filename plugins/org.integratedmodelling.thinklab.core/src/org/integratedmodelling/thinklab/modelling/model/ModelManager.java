package org.integratedmodelling.thinklab.modelling.model;

import java.util.Collection;
import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.interpreter.ModelGenerator;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.modelling.IAgentModel;
import org.integratedmodelling.thinklab.api.modelling.IAnnotation;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelManager;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.proxy.ModellingModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ModelManager implements IModelManager, IModelFactory {

	private static ModelManager _this = null;
	private static Namespace _defaultNS = null;
	
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
	}
	
	
	
	public IAnnotation retrieveAnnotation(String s) {
		// TODO Auto-generated method stub
		return getAnnotation(s);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel getModel(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAgentModel getAgentModel(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IScenario getScenario(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext getContext(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INamespace getNamespace(String ns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModelObject getModelObject(String object) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public INamespace createNamespace(String namespace, String ontologyId, Polylist ontology) {

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
		
	}

	@Override
	public IModel createModel(INamespace ns, SemanticType modelType, Map<String, Object> def) {

		IModel ret = null;
		
		if (modelType.equals(IModelFactory.C_MODEL)) {
			
		} else if (modelType.equals(IModelFactory.C_MEASUREMENT)) {
			
		}
		
		return ret;
	}

}
