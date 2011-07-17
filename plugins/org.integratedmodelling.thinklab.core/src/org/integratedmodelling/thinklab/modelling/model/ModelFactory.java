package org.integratedmodelling.thinklab.modelling.model;

import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.listeners.IContextualizationListener;
import org.integratedmodelling.thinklab.api.modelling.IAgentModel;
import org.integratedmodelling.thinklab.api.modelling.IAnnotation;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.runtime.ISession;

public class ModelFactory implements IModelFactory {

	@Override
	public IAnnotation retrieveAnnotation(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAnnotation requireAnnotation(String s) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel retrieveModel(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel requireModel(String s) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAgentModel retrieveAgentModel(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAgentModel requireAgentModel(String s) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IScenario retrieveScenario(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IScenario requireScenario(String s) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext retrieveContext(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext requireContext(String s) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseNamespace(String namespace) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSource(String object) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IModelObject> getDependencies(String object)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNamespaceLastModification(String ns) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IOntology getNamespaceOntology(String ns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getNamespaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IModelObject> listNamespace(String ns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModelObject getModelObject(String name) throws ThinklabException {
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
	public IContext run(IModel model, IKBox kbox, ISession session,
			Collection<IContextualizationListener> listeners, IContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String loadFile(String resourceId) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
