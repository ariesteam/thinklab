/**
 * SearchEnginePlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabSearchEnginePlugin.
 * 
 * ThinklabSearchEnginePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabSearchEnginePlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.application;

import java.util.ArrayList;
import java.util.Properties;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.MalformedListException;
import org.integratedmodelling.utils.Polylist;
import org.java.plugin.registry.Extension;

public class ApplicationPlugin extends ThinklabPlugin {

	static final String PLUGIN_ID = "org.integratedmodelling.thinklab.searchengine";

	ArrayList<Application> applications = new ArrayList<Application>();
	
	@Override
	protected void loadExtensions() throws Exception {
		// TODO Auto-generated method stub
		super.loadExtensions();
		for (Extension ext : getAllExtensions(PLUGIN_ID, "application")) {
			applications.add(createApplication(ext, getProperties()));			
		}
	}


	public static ApplicationPlugin get() {
		return (ApplicationPlugin) getPlugin(PLUGIN_ID );
	}
	/**
	 * Get your engine here, passing the necessary configuration properties. 
	 * @param properties 
	 * 
	 * @param id
	 * @param properties
	 * @return
	 * @throws ThinklabException
	 */
	public Application createApplication(Extension ext, Properties properties) throws ThinklabException {
		
		String id = getParameter(ext, "id");
		
		log.info("registering application " + id);

		String wflow = getParameter(ext, "workflow", "()");
		String mclass = getParameter(ext, "model-class");
		String[] packages = getParameters(ext, "step-package");
		
		log.info("application " + id + " registered");
		
		try {
			return new Application(id, Polylist.parse(wflow), mclass, packages);
		} catch (MalformedListException e) {
			throw new ThinklabValidationException(
					"error creating application " + id + "; workflow is not a well-formed list");
		}
	}
	
	public Application getApplication(String id) {
		
		Application ret = null;
		
		for (Application app : applications) {
			if (app.getId().equals(id)) {
				ret = app;
				break;
			}
		}
		
		return ret;
	}

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
	}

	@Override
	protected void unload() throws ThinklabException {
		// drop all search engines, close them		
	}

}
