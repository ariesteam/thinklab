/**
 * ThinklabBehavior.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Apr 25, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabAgentPlugin.
 * 
 * ThinklabAgentPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabAgentPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Apr 25, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.agents.agents;

import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.utils.NameGenerator;

/* 
 * ThinklabBehavior is basically just a collection of methods, initialized with a 
 * session and identified by a unique ID. We provide methods to register and retrieve
 * behaviors in sessions. Thinklab behaviors can be wrapped into JADE ones, so we
 * can leverage the JADE agent platform to provide distributed computation and all
 * the good stuff that comes with it.
 */
public abstract class ThinklabBehavior {

	String id;
	protected ISession session;
	private HashMap<String, IAgentAction> actions = new HashMap<String, IAgentAction>();
	
	protected ThinklabBehavior() {
	}
	
	public ISession getSession() {
		return session;
	}
	
	public String getId() {
		return id;
	}
	
	static public ThinklabBehavior getBehavior(String id, ISession session) {
		return (ThinklabBehavior) session.retrieveUserData(id);
	}
	
	static public ThinklabBehavior createBehavior(String bClass, ISession session) throws ThinklabException {

// TODO move to extensions
//		Class<?> cl = null;
//		try {
//			cl = Class.forName(bClass, true, PluginRegistry.get().getClassLoader());
//		} catch (ClassNotFoundException e) {
//			throw new ThinklabInternalErrorException("behavior class " + bClass + " unknown");			
//		}
		
		ThinklabBehavior beh = null;
//		try {
//			beh = (ThinklabBehavior) cl.newInstance();
//		} catch (Exception e) {
//			throw new ThinklabInternalErrorException(e);			
//		}
//		
//		beh.register(session);
		
		return beh;
	}
	
	protected void register(ISession session) throws ThinklabException {
	
		this.session = session;
		
		/* create ID and set it */
		id = NameGenerator.newName("tk_behavior");
		
		/* register into session */
		session.registerUserData(id, this);
	
	}
	
	/**
	 * This is a portable way to execute named actions without having to 
	 * subclass the behavior, which would entail problems with real
	 * agents as the system needs to track behaviors in complex ways.
	 * 
	 * Enqueue an action which will be invoked by name at executeAction(),
	 * passing the behavior.
	 * 
	 * @param ad
	 */
	public void registerAction(IAgentAction ad) {
		actions.put(ad.getActionId(), ad);
	}
	
	public Object executeAction(String actionId) {
		return executeAction(actionId, (Object[]) null);
	}
	
	public Object executeAction(String actionId, Object ... parameters) {
		IAgentAction action = actions.get(actionId);
		return action.executeAction(this, parameters);
	}
	
}
