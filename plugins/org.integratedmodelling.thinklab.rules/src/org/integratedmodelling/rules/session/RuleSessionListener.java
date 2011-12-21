/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
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
