/**
 * Shell.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
package org.integratedmodelling.thinklab.tclj;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.integratedmodelling.clojure.REPL;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.java.plugin.boot.Application;
import org.java.plugin.boot.ApplicationPlugin;
import org.java.plugin.util.ExtendedProperties;

/**
 * A simple command-line driven knowledge manager. Just run and type 'help'.
 * @author Ferdinando Villa
 */
public class TcljApplication extends ApplicationPlugin implements Application {

    public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.tclj";
	
	ISession session;
	String[] args = null;
	Properties properties = new Properties();
	
	@Override
	protected Application initApplication(ExtendedProperties arg0, String[] arg1)
			throws Exception {

		getManager().activatePlugin("org.integratedmodelling.thinklab.core");
		getManager().activatePlugin("org.integratedmodelling.thinklab.clojure");
		
		/*
		 * load additional plugins from configuration
		 */
		File props = 
			new File(LocalConfiguration.getDataDirectory(PLUGIN_ID) + "/config/tclj.properties");
		
		if (props.exists()) {
			
			this.properties.load(new FileInputStream(props));
			
			String addplug = properties.getProperty("tclj.plugins");
			
			if (addplug != null) {
				String[] plugs = addplug.split(",");
				
				for (String plug : plugs) {
					getManager().activatePlugin(Thinklab.resolvePluginName(plug, true));
				}
			}
		}
		
		this.args = arg1;
		
		return this;
	}

	@Override
	protected void doStart() throws Exception {
	}

	@Override
	protected void doStop() throws Exception {
	}

	@Override
	public void startApplication() throws Exception {
		
		REPL repl = new REPL();
		
		Session session = new Session();
		repl.setSession(session);
		repl.setInput(session.getInputStream());
		repl.setOutput(session.getOutputStream());
	
		repl.run(args);
	}
}
