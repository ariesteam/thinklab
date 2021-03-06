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

import java.util.Properties;

import org.integratedmodelling.thinklab.authentication.AuthenticationManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.BooleanValue;

@ThinklabCommand(
		name="su",
		description="authenticate a user into the current session",
		argumentNames="user,password",
		argumentDescriptions="name of user,password for user",
		argumentTypes="thinklab-core:Text,thinklab-core:Text")
public class Su implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String username = command.getArgumentAsString("user");
		String password = command.getArgumentAsString("password");

		Properties p = session.getProperties();

		boolean ret = AuthenticationManager.get().authenticateUser(username,
				password, p);

		if (ret) {

			/*
			 * tell session who the user is
			 */
			p.put(AuthenticationManager.USERID_PROPERTY, username);

			/*
			 * merge session properties with user properties
			 */
			p.putAll(AuthenticationManager.get().getUserProperties(username));

			/*
			 * let the interactive bastard know
			 */
			session.getOutputStream().println("user " + username + " logged in");
		} else {
			session.getOutputStream().println("login failed");
		}

		return new BooleanValue(ret);
	}

}
