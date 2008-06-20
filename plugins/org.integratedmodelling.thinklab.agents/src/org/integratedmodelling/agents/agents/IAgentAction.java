package org.integratedmodelling.agents.agents;

public interface IAgentAction {

	public abstract String getActionId();
	public abstract Object executeAction(ThinklabBehavior behavior, Object ... parameters);
}
