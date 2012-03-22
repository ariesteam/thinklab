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
package org.integratedmodelling.thinklab.commandline.commands;

import java.net.URL;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.modelling.lang.ModelManager;
import org.integratedmodelling.thinklab.modelling.lang.Namespace;

@ThinklabCommand(name="test",argumentNames="arg",argumentTypes="thinklab:Text", argumentDescriptions="test argument")
public class Test implements ICommandHandler {

	
	@Override
	public ISemanticObject<?> execute(Command command, ISession session)
			throws ThinklabException {
				
		URL test1 = ClassLoader.getSystemResource("org/integratedmodelling/thinklab/tests/tql/test1.tql");
		INamespace ns = ModelManager.get().loadFile(test1.toString(), null, null);
		System.out.println(((Namespace)ns).getSemantics().prettyPrint());
		
		IKbox kbox = Thinklab.get().requireKbox("models");
		long id = kbox.store(ns);
		
		ISemanticObject<?> zio = kbox.retrieve(id);
		
		System.out.println(zio);
		
		return null;
	}

	
	public void testKbox() throws ThinklabException {
		
	}
}
