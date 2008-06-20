/**
 * GroovyPlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGroovyPlugin.
 * 
 * ThinklabGroovyPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGroovyPlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.groovy;

import java.io.File;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.IAlgorithmInterpreter;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeProvider;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IAlgorithmInterpreter.IContext;
import org.integratedmodelling.thinklab.interpreter.AlgorithmInterpreterFactory;
import org.integratedmodelling.thinklab.interpreter.InterpreterPlugin;
import org.w3c.dom.Node;

public class GroovyPlugin extends InterpreterPlugin {

	public GroovyPlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() throws ThinklabException {

	}

	@Override
	public void load(KnowledgeManager km, File baseReadPath, File baseWritePath)
			throws ThinklabPluginException {
		
		km.registerLiteralValidator("groovy:GroovyCode", new GroovyAlgorithmValidator());
		
		/* bind interpreter to literals of groovy:GroovyCode */
		AlgorithmInterpreterFactory.get().registerInterpreter("groovy:GroovyCode", "Groovy");

	}

	@Override
	public void notifyResource(String name, long time, long size)
			throws ThinklabException {
		
		if (name.endsWith(".gv")) {
			/* initialization code we want to run */
		}

	}

	@Override
	public void unload(KnowledgeManager km) throws ThinklabPluginException {
		// TODO Auto-generated method stub

	}

	@Override
	public IAlgorithmInterpreter getInterpreter() {
		return new GroovyInterpreter();
	}

	@Override
	public IContext getNewContext(ISession session) {
		GroovyContext ctx = new GroovyContext();
		ctx.getBinding().setVariable("session", session);
		return ctx;
	}

	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}

}
