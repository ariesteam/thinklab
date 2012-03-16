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
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;

@ListingProvider(label="projects", itemlabel="project")
public class ProjectLister implements IListingProvider {

	@Override
	public Collection<Object> getListing() throws ThinklabException {
		
		ArrayList<Object> ret = new ArrayList<Object>();
		for (IProject proj : Thinklab.get().getProjects()) {
			ret.add(proj.getId());
		}
		return ret;
	}

	@Override
	public Collection<Object> getSpecificListing(String id) throws ThinklabException {

		ArrayList<Object> ret = new ArrayList<Object>();
		IProject proj = Thinklab.get().getProject(id);
		if (proj != null) {
			Properties props = proj.getProperties();
			ret.add("properties for project " + id + ":");
			for (Object s : props.keySet()) {
				ret.add("  " + s + " = " + props.getProperty(s.toString()));				
			}
		} else {
			ret.add("project " + id + " does not exist");			
		}
		return ret;
	}

	@Override
	public void notifyParameter(String parameter, String value) {
		// TODO Auto-generated method stub
		
	}

}
