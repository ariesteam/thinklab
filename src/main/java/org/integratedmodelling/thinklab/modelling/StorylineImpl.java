package org.integratedmodelling.thinklab.modelling;

import java.util.Set;

import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.lang.model.Storyline;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.modelling.internal.NamespaceQualified;

public class StorylineImpl extends NamespaceQualified implements IModelObject  {

	public  StorylineImpl(IReferenceList semantics, Object object) {
		super(semantics, object);
	}

	public StorylineImpl(Storyline o) {
		super(null, o);
	}

	@Override
	public LanguageElement getLanguageElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMetadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ISemanticObject> getObservables() {
		// TODO Auto-generated method stub
		return null;
	}

}
