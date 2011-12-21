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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.authentication.AuthenticationManager;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;

@ListingProvider(label="users", itemlabel="user")
public class UserLister implements IListingProvider {

	@Override
	public Collection<?> getListing() throws ThinklabException {
		return AuthenticationManager.get().listUsers();
	}

	@Override
	public Collection<?> getSpecificListing(String username) throws ThinklabException {

		ArrayList<Object> ret = new ArrayList<Object>();

		if (AuthenticationManager.get().haveUser(username)) {
			Properties props = AuthenticationManager.get().getUserProperties(username);
			ret.add("properties for user " + username + ":");
			for (Object s : props.keySet()) {
				ret.add("  " + s + " = " + props.getProperty(s.toString()));				
			}
		} else {
			ret.add("user " + username + " does not exist");			
		}
		return ret;
	}

	@Override
	public void notifyParameter(String parameter, String value) {
	}

}
