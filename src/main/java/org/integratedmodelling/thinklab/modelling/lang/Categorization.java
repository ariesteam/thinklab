package org.integratedmodelling.thinklab.modelling.lang;

import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.parsing.ICategorizingObserverDefinition;

@Concept(NS.CATEGORIZING_OBSERVER)
public class Categorization extends Observer<Categorization> implements ICategorizingObserverDefinition {

	Set<String> _dictionary;
	
	@Override
	public void setDictionary(Set<String> dictionary) {
		_dictionary = dictionary;
	}

	@Override
	public Set<String> getDictionary() {
		return _dictionary;
	}

	@Override
	public Categorization demote() {
		return this;
	}

	@Override
	public IState createState(ISemanticObject<?> observable, IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAccessor getNaturalAccessor() {
		// TODO Auto-generated method stub
		return null;
	}

}
