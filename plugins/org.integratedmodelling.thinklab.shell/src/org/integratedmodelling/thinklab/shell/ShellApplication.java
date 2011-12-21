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
package org.integratedmodelling.thinklab.shell;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;

import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.commandline.GraphicalShell;
import org.integratedmodelling.thinklab.commandline.Shell;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.utils.MiscUtilities;
import org.java.plugin.boot.Application;
import org.java.plugin.boot.ApplicationPlugin;
import org.java.plugin.util.ExtendedProperties;

/**
 * A simple command-line driven knowledge manager. Just run and type 'help'.
 * @author Ferdinando Villa
 */
public class ShellApplication extends ApplicationPlugin implements Application {

    public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.shell";
	private static final String GRAPHICAL_SHELL_PROPERTY = "commandline.graphical.shell";
	
	private ArrayList<String> scriptFiles = 
		new ArrayList<String>();
	
	public ISession session;
	
	@Override
	protected Application initApplication(ExtendedProperties arg0, String[] arg1)
			throws Exception {

		getManager().activatePlugin("org.integratedmodelling.thinklab.core");
		getManager().activatePlugin("org.integratedmodelling.thinklab.commandline");
	
		scriptFiles.addAll(LocalConfiguration.getProfileScripts());
		
		for (String s : arg1) {
			
			// FIXME?
			// temporary fix - the first arg in cl is the main class name, but
			// that doesn't apply when running through the JVM API so just 
			// starting from 1 would require some inelegant CL hacks.
			if (s.startsWith("org.") && !new File(s).exists())
				continue;
			
			scriptFiles.add(s);
		}
		
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
		
		boolean isGraphical = 
			BooleanValue.parseBoolean(
					CommandLine.get().getProperties().getProperty(
							GRAPHICAL_SHELL_PROPERTY, "true"));
		
		if (isGraphical && GraphicsEnvironment.isHeadless())
			isGraphical = false;
		
		boolean hasScript = false;
		ISession session = null;
		for (String s : scriptFiles) {
			
			if (session == null)
				session = new Session();
			
			if (!hasScript && MiscUtilities.getFileName(s).startsWith("."))
				hasScript = true;
			
			Shell.runScript(s, session, log);
		}
		
		if (hasScript)
			return;
		
		if (isGraphical) {			
			GraphicalShell shell = new GraphicalShell();
			shell.startConsole();
		} else {
			Shell shell = new Shell();
			shell.startConsole();
		}
	}
}
