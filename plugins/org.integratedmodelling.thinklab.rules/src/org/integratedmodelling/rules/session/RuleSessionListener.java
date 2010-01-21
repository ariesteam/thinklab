package org.integratedmodelling.rules.session;

import org.integratedmodelling.rules.RulePlugin;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.IThinklabSessionListener;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

public class RuleSessionListener implements IThinklabSessionListener {

	public void objectCreated(IInstance object)  throws ThinklabException {
		// TODO Auto-generated method stub

	}

	public void objectDeleted(IInstance object)  throws ThinklabException {
		// TODO Auto-generated method stub

	}

	public void sessionCreated(ISession session) throws ThinklabException {
		
		if (RulePlugin.get().isUsingJess() || RulePlugin.get().isUsingDrools()) {
//			session.registerUserData(
//					RulePlugin.ENGINE_USERDATA_ID, 
//					RulePlugin.get().createRuleEngine());
		}

	}

	public void sessionDeleted(ISession session)  throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
