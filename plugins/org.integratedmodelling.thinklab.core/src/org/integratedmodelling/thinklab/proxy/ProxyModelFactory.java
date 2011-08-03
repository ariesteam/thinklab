package org.integratedmodelling.thinklab.proxy;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;
import org.integratedmodelling.thinklab.api.modelling.observation.IUnit;
import org.integratedmodelling.thinklab.modelling.model.ModelManager;

public class ProxyModelFactory implements IModelFactory {

	@Override
	public INamespace createNamespace(String namespace, String ontoid, IList ontology) {
		return ModelManager.get().createNamespace(namespace, ontoid, ontology);
	}

	@Override
	public INamespace getDefaultNamespace() {	
		return ModelManager.get().getDefaultNamespace();
	}

	@Override
	public IModel createModel(INamespace arg0, SemanticType arg1,
			Map<String, Object> arg2) throws ThinklabException {
		return ModelManager.get().createModel(arg0, arg1, arg2);
	}

	@Override
	public void register(IModelObject arg0, String arg1, INamespace arg2) {	
		ModelManager.get().register(arg0, arg1, arg2);
	}

	@Override
	public IUnit parseUnit(String unit) throws ThinklabValidationException {
		return ModelManager.get().parseUnit(unit);
	}

}
