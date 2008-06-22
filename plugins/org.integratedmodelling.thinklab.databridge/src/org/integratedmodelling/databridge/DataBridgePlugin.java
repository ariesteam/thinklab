/**
 * DataBridgePlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Apr 7, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDataBridgePlugin.
 * 
 * ThinklabDataBridgePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDataBridgePlugin is distributed in the hope that it will be useful,
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
 * @date      Apr 7, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.databridge;


import java.io.File;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.extensions.KnowledgeProvider;
import org.integratedmodelling.thinklab.plugin.Plugin;
import org.w3c.dom.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class AgentPlugin.
 */
public class DataBridgePlugin extends Plugin {

	/** The Constant ID. */
	static final String ID = "DataBridge";
   
	
	/**
	 * Gets the plugin.
	 * 
	 * @return the agent plugin
	 */
	public static DataBridgePlugin get() {
		return (DataBridgePlugin) getPlugin(ID );
	}
	

    /* (non-Javadoc)
     * @see org.integratedmodelling.thinklab.plugin.Plugin#load(org.integratedmodelling.thinklab.KnowledgeManager, java.io.File, java.io.File)
     */
    public void load(KnowledgeManager km, File baseReadPath, File baseWritePath)
            throws ThinklabPluginException {
    }

    /* (non-Javadoc)
     * @see org.integratedmodelling.thinklab.plugin.Plugin#unload(org.integratedmodelling.thinklab.KnowledgeManager)
     */
    public void unload(KnowledgeManager km) throws ThinklabPluginException {
        // TODO Auto-generated method stub

    }

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.Plugin#initialize()
	 */
	public void initialize() throws ThinklabException {

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.Plugin#notifyResource(java.lang.String, long, long)
	 */
	@Override
	public void notifyResource(String name, long time, long size) throws ThinklabException {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IPlugin#notifyConfigurationNode(org.w3c.dom.Node)
	 */
	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}

}
