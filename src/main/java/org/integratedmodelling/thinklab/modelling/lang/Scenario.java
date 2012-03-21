package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.lang.parsing.IScenarioDefinition;
import org.integratedmodelling.thinklab.api.modelling.IScenario;

@Concept(NS.SCENARIO)
public class Scenario extends ModelObject<Scenario> implements IScenarioDefinition {

	@Override
	public void merge(IScenario scenario) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Scenario demote() {
		return this;
	}


}
