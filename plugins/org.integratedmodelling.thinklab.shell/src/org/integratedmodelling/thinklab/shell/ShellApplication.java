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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.shell;

import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.commandline.GraphicalShell;
import org.integratedmodelling.thinklab.commandline.Shell;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.literals.BooleanValue;
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
	
	public ISession session;
	
	@Override
	protected Application initApplication(ExtendedProperties arg0, String[] arg1)
			throws Exception {

		getManager().activatePlugin("org.integratedmodelling.thinklab.core");
		getManager().activatePlugin("org.integratedmodelling.thinklab.commandline");
	
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
		if (isGraphical) {			
			GraphicalShell shell = new GraphicalShell();
			shell.startConsole();
		} else {
			Shell shell = new Shell();
			shell.startConsole();
		}
	}
}
