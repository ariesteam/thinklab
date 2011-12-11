package org.integratedmodelling.thinklab.proxy;

import org.integratedmodelling.thinklab.api.knowledge.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;
import org.integratedmodelling.thinklab.api.project.IProject;

import com.google.inject.AbstractModule;

/**
 * Used for dependency injection to support any external code written
 * using only thinklab-api.
 * 
 * Will bind all Thinklab managers and factories referenced in other
 * external implementations to their implementations here. All factories
 * that are singletons are proxied by delegated objects.
 * 
 * @author Ferd
 *
 */
public class ModellingModule extends AbstractModule {

	// may be null; if not, the code will belong to this project.
	IProject _project;
	
	@Override
	protected void configure() {
		bind(IModelFactory.class).to(ProxyModelFactory.class);
		bind(IKnowledgeManager.class).to(ModelKnowledgeManager.class);
	}
	
}
