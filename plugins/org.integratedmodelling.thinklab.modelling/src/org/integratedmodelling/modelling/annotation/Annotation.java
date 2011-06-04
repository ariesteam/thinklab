package org.integratedmodelling.modelling.annotation;

import java.util.Set;

import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.NameGenerator;

public class Annotation implements IModelForm {

	String _namespace;
	String _id;
	String _description;
	
	public Annotation(String namespace) {
		this._namespace = namespace;
		this._id = NameGenerator.newName("ann");
	}
	
	
	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String getNamespace() {
		return _namespace;
	}

	@Override
	public String getName() {
		return _namespace + "/" + _id;
	}

	@Override
	public Set<IConcept> getObservables() {
		// TODO Auto-generated method stub
		return null;
	}

}
