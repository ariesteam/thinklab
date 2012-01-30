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
package org.integratedmodelling.thinklab.project;

import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.api.project.IProjectFactory;
import org.java.plugin.Plugin;

public class ProjectFactory implements IProjectFactory {

	HashMap<String, IProject> _projects = new HashMap<String, IProject>();
	private static ProjectFactory _this = null;
	
	protected ProjectFactory() {
		
	}
	
	public static ProjectFactory get() {

		if (_this == null) {
			_this = new ProjectFactory();
		}
		return _this;
	}

	ThinklabProject registerProject(Plugin plugin) throws ThinklabException {
		ThinklabProject ret = new ThinklabProject(plugin);
		_projects.put(plugin.getDescriptor().getId(), ret);
		return ret;
	}
		
	public IProject getProject(String id, boolean attemptLoading) {
		if (_projects.containsKey(id))
			return _projects.get(id);
		
		if (attemptLoading) {
			
		}
		
		return null;
		
	}
	
	public void removeProject(String id) {
		_projects.remove(id);
	}

	public Collection<IProject> getProjects() {
		return _projects.values();
	}
	
	@Override
	public IProject createProject(String id) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteProject(String id) throws ThinklabException {
		// TODO Auto-generated method stub

	}


}
