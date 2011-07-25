package org.integratedmodelling.thinklab.proxy;

import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;
import org.integratedmodelling.thinklab.modelling.model.ModelManager;

public class ProxyModelFactory implements IModelFactory {

	@Override
	public INamespace createNamespace(String namespace, Polylist ontology) {
		return ModelManager.get().createNamespace(namespace, ontology);
	}

	@Override
	public IModel createModel(String namespace, Polylist ontology) {
		return ModelManager.get().createModel(namespace, ontology);
	}

	@Override
	public INamespace getDefaultNamespace() {	
		return ModelManager.get().getDefaultNamespace();
	}

}
