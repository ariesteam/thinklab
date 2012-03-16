package org.integratedmodelling.thinklab.project;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.factories.IProjectManager;
import org.integratedmodelling.thinklab.api.project.IProject;

public class ProjectManager implements IProjectManager {

	ArrayList<IProject> _projects = new ArrayList<IProject>();
	
	public void boot() {
		
		for (File dir : Thinklab.get().getProjectPath().listFiles(
				new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() &&
					   new File(pathname + File.separator + IProject.THINKLAB_META_INF).isDirectory();
			}
		})) {
			
			
		}
	}
	
	@Override
	public IProject getProject(String projectId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IProject> getProjects() {
		return _projects;
	}

	@Override
	public IProject deployProject(String resourceId) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void undeployProject(String projectId) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
