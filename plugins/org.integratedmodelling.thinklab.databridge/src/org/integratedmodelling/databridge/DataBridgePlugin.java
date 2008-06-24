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
import org.integratedmodelling.thinklab.interfaces.IKnowledgeProvider;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.w3c.dom.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class AgentPlugin.
 */
public class DataBridgePlugin extends ThinklabPlugin {

	/** The Constant ID. */
	static final String ID = "org.integratedmodelling.thinklab.databridge";
   
	
	/**
	 * Gets the plugin.
	 * 
	 * @return the agent plugin
	 */
	public static DataBridgePlugin get() {
		return (DataBridgePlugin) getPlugin(ID );
	}
	

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
