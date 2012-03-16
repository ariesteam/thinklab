package org.integratedmodelling.thinklab.project;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabProjectException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.api.factories.IProjectManager;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.StringUtils;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class ProjectManager implements IProjectManager {

	public class ProjectDescriptor {
		
		File file;
		String id;
		IProject project = null;
		String[] prerequisites;
		boolean loaded = false;
		
		@Override
		public boolean equals(Object arg0) {
			return 
					arg0 instanceof ProjectDescriptor && 
					((ProjectDescriptor)arg0).id.equals(id);
		}
		@Override
		public int hashCode() {
			return id.hashCode();
		}		
	}
	
	DefaultDirectedGraph<ProjectDescriptor, DefaultEdge> _dependencies =
			new DefaultDirectedGraph<ProjectDescriptor, DefaultEdge>(DefaultEdge.class);
	
	ArrayList<File> _projectDirectories = new ArrayList<File>();
	ArrayList<ProjectDescriptor> _projects = new ArrayList<ProjectDescriptor>();
	HashMap<String, ProjectDescriptor> _projectIndex = new HashMap<String, ProjectDescriptor>();
	
	public void boot() throws ThinklabException {
		
		/*
		 * reentrant - clear everything except the project directories.
		 */
		_dependencies = new DefaultDirectedGraph<ProjectManager.ProjectDescriptor, DefaultEdge>(DefaultEdge.class);
		_projectIndex.clear();
		_projects.clear();
		
		ArrayList<String> pnames = new ArrayList<String>();
		
		for (File pdir : _projectDirectories) {
			for (File dir : pdir.listFiles(
					new FileFilter() {
						
						@Override
						public boolean accept(File pathname) {
							return pathname.isDirectory() &&
									new File(pathname + File.separator + IProject.THINKLAB_META_INF).isDirectory();
						}
					})) {
			
				/*
				 * read project IDs and dependencies; create dependency graph.
				 */
				File pfile = new File(dir + File.separator + IProject.THINKLAB_META_INF + File.separator + IProject.THINKLAB_PROPERTIES_FILE);
				if (pfile.exists()) {
					
					ProjectDescriptor pd = new ProjectDescriptor();
					
					Properties p = new Properties();
					InputStream inp;
					try {
						inp = new FileInputStream(pfile);
						p.load(inp);
						inp.close();
					} catch (Exception e) {
						throw new ThinklabRuntimeException(e);
					}

					pd.id = MiscUtilities.getFileBaseName(dir.toString());
					pd.prerequisites = StringUtils.split(p.getProperty(IProject.PREREQUISITES_PROPERTY, ""), ',');
					pd.project = new ThinklabProject(dir);
					
					if (_projectIndex.containsKey(pd.id)) 
						throw new ThinklabProjectException("duplicate projects named " + pd.id + " on the project path");
					
					_projectIndex.put(pd.id, pd);
					pnames.add(pd.id);
					
				}
				
			}
		}
		
		/*
		 * create dependency graph and ensure we have all required projects
		 */
		for (String pid : pnames) {
			
			ProjectDescriptor pd = _projectIndex.get(pid);
			_dependencies.addVertex(pd);
			for (String req : pd.prerequisites) {
				ProjectDescriptor rpd = _projectIndex.get(req);
				if (rpd == null) {
					throw new ThinklabProjectException("project " + req + " required by project " + pid + " cannot be found in project path");
				}
				_dependencies.addVertex(rpd);
				_dependencies.addEdge(rpd, pd);
			}
		}
		
		CycleDetector<ProjectDescriptor, DefaultEdge> cd = 
				new CycleDetector<ProjectManager.ProjectDescriptor, DefaultEdge>(_dependencies);
		
		if (cd.detectCycles()) {
			
			String s = "";
			for (ProjectDescriptor pd : cd.findCycles()) {
				s += (s.isEmpty() ? "" : ", ") + pd.id;
			}
			throw new ThinklabProjectException("circular dependencies detected between projects " + s);
		}

		TopologicalOrderIterator<ProjectDescriptor, DefaultEdge> tord = 
				new TopologicalOrderIterator<ProjectManager.ProjectDescriptor, DefaultEdge>(_dependencies);
		
		while (tord.hasNext()) {
			_projects.add(tord.next());
		}
	}
	
	@Override
	public IProject getProject(String projectId) {
		ProjectDescriptor pd = _projectIndex.get(projectId);
		if (pd != null)
			return pd.project;
		return null;
	}

	@Override
	public Collection<IProject> getProjects() {

		ArrayList<IProject> ret = new ArrayList<IProject>();
		for (ProjectDescriptor pd : _projects) {
			ret.add(pd.project);
		}
		return ret;
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

	/**
	 * boot() MUST be called after calling this. 
	 */
	@Override
	public void addProjectDirectory(File projectDirectory) {
		_projectDirectories.add(projectDirectory);
	}

}
