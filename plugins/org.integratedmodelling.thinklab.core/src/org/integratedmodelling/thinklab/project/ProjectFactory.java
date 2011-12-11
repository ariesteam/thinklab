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
		
	public IProject getProject(String id) {
		return _projects.get(id);
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
