package org.integratedmodelling.thinklab.transformations;

import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class TransformationFactory {

	static TransformationFactory _this = new TransformationFactory();
	
	TransformationChooser _chooser = new TransformationChooser();
	
	HashMap<String, ITransformation> _transformations  = 
		new HashMap<String, ITransformation>();
	
	public ITransformation retrieveTransformation(String id, Object[] parameters) {
		return _transformations.get(id);
	}
	
	public ITransformation requireTransformation(String id, Object[] parameters) throws ThinklabResourceNotFoundException {
		ITransformation ret = _transformations.get(id);
		if (ret == null)
			throw new ThinklabResourceNotFoundException(
					"transformation factory: no transformation found for id " + id);
		return ret;
	}

	public static TransformationFactory get() {
		return _this;
	}

	public void registerTransformation(String name, ITransformation transformation) {
		_transformations.put(name, transformation);
	}

	public void loadTransformationMappings(Properties properties) throws ThinklabException {
		_chooser.load(properties);
	}

	public ITransformation getTransformation(IConcept type) throws ThinklabException {
		return _chooser.get(type);
	}
}
