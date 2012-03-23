package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.modelling.parsing.IAgentModelDefinition;

@Concept(NS.AGENT_MODEL)
public class AgentModel extends ModelObject<AgentModel> implements IAgentModelDefinition {

	@Override
	public AgentModel demote() {
		return this;
	}

}
