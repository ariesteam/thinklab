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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;

/**
 * Start and stop the REST service.
 * 
 * @author ferdinando.villa
 *
 */
@ThinklabCommand(
		name="kbox",
		argumentNames="cmd,kbox",
		argumentTypes="thinklab:Text,thinklab:Text",
		argumentDescriptions="command (list|clear|export),kbox",
		optionNames="f,o",
		optionLongNames="format,file",
		optionDescriptions="use specified format (default xml),output to file",
		optionTypes="thinklab:Text,thinklab:Text",
		optionArgumentLabels="format,file"
		)
public class Kbox implements ICommandHandler {
	
	@Override
	public ISemanticObject execute(Command command, ISession session)
			throws ThinklabException {
		
		IKbox kbox = Thinklab.get().requireKbox(command.getArgumentAsString("kbox"));
		File file = null;

		String cmd = command.getArgumentAsString("cmd");
		
		if (command.hasOption("file"))
			file = new File(command.getOptionAsString("file"));
		
		if (cmd.equals("list")) {
			
			int i = 0;
			for (ISemanticObject obj : kbox.retrieveAll()) {
				session.print(i++ + ": " + obj);
			}
			
		} else if (cmd.equals("clear")) {

			kbox.clear();
			
		}  else if (cmd.equals("export")) {
		
			FileOutputStream out;
			try {
				
				out = new FileOutputStream(file);

				/*
				 * TODO
				 * use appropriate adapter and output everything to file.
				 */
				
				out.close();
			} catch (FileNotFoundException e) {
				throw new ThinklabIOException(e);
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
			
			
		}
		
		return null;
	}

}
