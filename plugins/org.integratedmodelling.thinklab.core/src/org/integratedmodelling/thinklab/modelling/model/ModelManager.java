package org.integratedmodelling.thinklab.modelling.model;

import java.util.Collection;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.interpreter.ModelGenerator;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinkQL.impl.ThinkQLFactoryImpl;
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

	@SuppressWarnings("unchecked")
	@Override
	public INamespace loadFile(String resourceId) throws ThinklabException {

//	    <package 
//	       uri = "http://www.integratedmodelling.org/ThinkQL" 
//	       class = "org.integratedmodelling.thinkQL.ThinkQLPackage"
//	       genModel = "org/integratedmodelling/ThinkQL.genmodel" /> 
//		EPackage.Registry.INSTANCE.put(
//				"http://www.integratedmodelling.org/ThinkQL", 
//				new EPackageDescriptor.Dynamic(element, ATT_LOCATION));

		
		Injector injector = Guice.createInjector(new ModellingModule());
		ModelGenerator mg = injector.getInstance(ModelGenerator.class);

		
		INamespace ns = mg.load(resourceId);
		
		return ns;
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
	public INamespace createNamespace(String namespace, Polylist ontology) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel createModel(String namespace, Polylist ontology) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INamespace getDefaultNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

}
