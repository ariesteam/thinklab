package org.integratedmodelling.thinklab.proxy;

import org.integratedmodelling.thinklab.api.knowledge.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;

import com.google.inject.AbstractModule;

/**
 * Used for dependency injection to support any external code written
 * using only thinklab-api.
 * 
 * Will bind all Thinklab managers and factories referenced in other
 * external implementations to their implementations here. All factories
 * that are singletons are proxied by deferring objects.
 * 
 * @author Ferd
 *
 */
public class ModellingModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IModelFactory.class).to(ProxyModelFactory.class);
		bind(IKnowledgeManager.class).to(ProxyKnowledgeManager.class);
	}
	
}
