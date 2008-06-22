/**
 * WorkflowPlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabWorkflowPlugin.
 * 
 * ThinklabWorkflowPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabWorkflowPlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.workflow;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.extensions.KnowledgeProvider;
import org.integratedmodelling.thinklab.plugin.Plugin;
import org.integratedmodelling.thinklab.workflow.commands.TestWorkflow;
import org.w3c.dom.Node;

public class WorkflowPlugin extends Plugin  {

	/* log4j logger used for this class. Can be used by other classes through logger()  */
	private static  Logger log = Logger.getLogger(WorkflowPlugin.class);
	static final public String ID = "Workflow";
	private URL osWorkflowConfiguration;
	/**
	* TODO use plugin properties to define the initial action id. For now
	* keep it simple - initial actions have an id of 0.
	*/
	static final int INITIAL_ACTION_ID = 0;
	public static final String OUTPUT_VARIABLE_PREFIX = "output.";
	public static final String INPUT_VARIABLE_PREFIX = "input.";
	
	// IDs for standard function arguments
	public static final String ARG_COMMAND_NAME = "command.name";
	public static final String ARG_COMMAND_OUTPUT_WRITER = "command.output.writer";
	public static final String ARG_THINKLAB_SESSION = "thinklab.session";
	
	
	public WorkflowPlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() throws ThinklabException {
		
		
	}
	
	public static WorkflowPlugin get() {
		return (WorkflowPlugin) getPlugin(ID);
	}

	public static Logger logger() {
		return log;
	}

	@Override
	public void load(KnowledgeManager km, File baseReadPath, File baseWritePath)
			throws ThinklabPluginException {

		try {
			
			new TestWorkflow().install(km);
			
		} catch (ThinklabException e) {
			throw new ThinklabPluginException(e);
		}
		

	}

	@Override
	public void notifyResource(String name, long time, long size)
			throws ThinklabException {
		
		if (name.endsWith("osworkflow.xml"))
			osWorkflowConfiguration = this.exportResourceCached(name);

	}

	@Override
	public void unload(KnowledgeManager km) throws ThinklabPluginException {
		// TODO Auto-generated method stub

	}
	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}

	public URL getOSWorkflowConfiguration() {
		return osWorkflowConfiguration;
	}

}
