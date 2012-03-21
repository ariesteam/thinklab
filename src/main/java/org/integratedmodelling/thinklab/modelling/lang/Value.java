package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.lang.parsing.IValuingObserverDefinition;

@Concept(NS.VALUING_OBSERVER)
public class Value extends Observer<Value> implements IValuingObserverDefinition {

	@Override
	public Value demote() {
		return this;
	}

}
