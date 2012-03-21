package org.integratedmodelling.thinklab.modelling.lang;

import java.util.Set;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.lang.parsing.ICategorizingObserverDefinition;

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

}
