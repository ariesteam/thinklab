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
package org.integratedmodelling.thinklab.workflow;

import java.net.URL;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class WorkflowPlugin extends ThinklabPlugin  {

	static final public String PLUGIN_ID = "org.integratedmodelling.thinklab.workflow";
	
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
	
		
	public static WorkflowPlugin get() {
		return (WorkflowPlugin) getPlugin(PLUGIN_ID);
	}

	@Override
	public void load(KnowledgeManager km) throws ThinklabPluginException {
	}

	@Override
	public void unload() throws ThinklabPluginException {
		// TODO Auto-generated method stub

	}

	public URL getOSWorkflowConfiguration() throws ThinklabIOException {
		return getResourceURL("osworkflow.xml");
	}

}
