/**
 * AgentPlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

import java.io.File;
import java.security.Permission;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeProvider;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.w3c.dom.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class AgentPlugin.
 */
public class AgentPlugin extends ThinklabPlugin {

	/** The Constant ID. */
	static final String PLUGIN_ID = "org.integratedmodelling.thinklab.agent";
    
	/** The container. */
	AgentContainer theContainer = null;
	
	/**
	 * The Class MySecurityManager.
	 */
	public class MySecurityManager extends SecurityManager {
		
		/**
		 * Instantiates a new my security manager.
		 */
		public MySecurityManager() {
		}
		
		/**
		 * Check permission.
		 */
		public void checkPermission() {
		}
		
		/* (non-Javadoc)
		 * @see java.lang.SecurityManager#checkPermission(java.security.Permission)
		 */
		public void checkPermission(Permission perm) {
		}
		
		/* (non-Javadoc)
		 * @see java.lang.SecurityManager#checkPermission(java.security.Permission, java.lang.Object)
		 */
		public void checkPermission(Permission perm, Object context) {
		}
	} 
	
	/**
	 * Gets the plugin.
	 * 
	 * @return the agent plugin
	 */
	public static AgentPlugin get() {
		return (AgentPlugin) getPlugin(PLUGIN_ID);
	}
	
	
	/**
	 * Synchronized static method that initializes the Jade system. Should not
	 * be called directly; calls to getAgentContainer() ensure that it's called
	 * when necessary. It ensures that
	 * a non-main container is created once and only once, with a proxy agent
	 * associated to this vm.
	 * 
	 * @throws ThinklabException
	 *             the thinklab exception
	 */
	synchronized private void initJade() throws ThinklabException {

		// TODO this is only to make it work within Eclipse. 
		MySecurityManager mySm = new MySecurityManager();
		SecurityManager secMan = System.getSecurityManager();
		System.setSecurityManager(mySm); 
		
		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();

		// FIXME not sure
		rt.setCloseVM(true);
		
		// Create a default profile
		Profile p = new ProfileImpl();
		
		// TODO link to plugin properties
		p.setParameter(Profile.MAIN_HOST, "localhost");
		p.setParameter(Profile.MAIN_PORT, "1099");
		
		// Create a new non-main container, connecting to the default main
		// container (i.e. on this host, port 1099)
		theContainer = rt.createMainContainer(p);
		
		try {
			theContainer.start();
		} catch (ControllerException e) {
			throw new ThinklabException(e);
		}
	} 
	
    /* (non-Javadoc)
     * @see org.integratedmodelling.thinklab.plugin.Plugin#load(org.integratedmodelling.thinklab.KnowledgeManager, java.io.File, java.io.File)
     */
    public void load(KnowledgeManager km)
            throws ThinklabPluginException {
    }

    /* (non-Javadoc)
     * @see org.integratedmodelling.thinklab.plugin.Plugin#unload(org.integratedmodelling.thinklab.KnowledgeManager)
     */
    public void unload() throws ThinklabPluginException {
        // TODO Auto-generated method stub

    }
    
    public AgentContainer getAgentContainer() throws ThinklabException {
		if (theContainer == null)
			initJade();
		return theContainer;
    }


}
