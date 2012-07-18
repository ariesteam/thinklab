package org.integratedmodelling.thinklab.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabProjectException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.factories.IProjectManager;
import org.integratedmodelling.thinklab.api.lang.IResolver;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.utils.FolderZiper;
import org.integratedmodelling.utils.MiscUtilities;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class ProjectManager implements IProjectManager {
		
	HashMap<String, IProject> _projects = new HashMap<String, IProject>();
	
	@Override
	public IProject getProject(String projectId) {
		return _projects.get(projectId);
	}

	@Override
	public Collection<IProject> getProjects() {
		return _projects.values();
	}

	@Override
	public void loadAllProjects() throws ThinklabException {

		for (IProject p : getProjects()) {
			if (!((Project)p).isLoaded()) {
				IResolver resolver = getResolver();
				((ModelManager.Resolver)resolver).setProject(p);
				((Project)p).load(resolver);
			}
		}
	}
	
	@Override
	public IProject loadProject(String projectId) throws ThinklabException {
		
		IProject p = _projects.get(projectId);
		
		if (p == null)
			throw new ThinklabResourceNotFoundException("project " + projectId + " does not exist");
		
		ModelManager.Resolver resolver = (ModelManager.Resolver) getResolver();
		resolver.setProject(p);

		((Project)p).load(resolver);
		
		return p;
	}

	@Override
	public IProject deployProject(String pluginId, String resource)
			throws ThinklabException {
		
		File archive = MiscUtilities.resolveUrlToFile(resource);
		File deployDir = getPluginDeployDir();

		IProject pd = _projects.get(pluginId);
		if (pd != null) {

			Thinklab.get().logger().info("undeploying " + pd.getId() + " from " + pd.getLoadPath());
			((Project)(pd)).unload();
			
			try {
				FileUtils.deleteDirectory(pd.getLoadPath());
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}
		
		Thinklab.get().logger().info("deploying " + pluginId + " in " + deployDir);
		FolderZiper.unzip(archive, deployDir);
		
		registerProject(new File(deployDir + File.separator + pluginId));
		
		return getProject(pluginId);
	}

	private File getPluginDeployDir() {
		return Thinklab.get().getScratchArea("deploy");
	}

	@Override
	public void undeployProject(String projectId) throws ThinklabException {
		
		IProject project = _projects.get(projectId);

		if (project == null)
			throw new ThinklabResourceNotFoundException("cannot unload project " + projectId + ": project not found");
		
		if (((Project)project).isLoaded()) {
			((Project)project).unload();
		}
		
		unregisterProject(projectId);
	}

	@Override
	public String[] registerProject(File... projectDir)  {
		
		String[] ret = new String[projectDir.length];
		
		for (int i = 0; i < projectDir.length; i++) {
			IProject p = new Project(projectDir[i], this);
			ret[i] = p.getId();
			_projects.put(p.getId(), p);
		}
		
		return ret;
	}

	@Override
	public void unregisterProject(String projectId) {
		_projects.remove(projectId);
	}

	@Override
	public void refreshProject(String projectId) throws ThinklabException {
		
		IProject project = _projects.get(projectId);

		if (project == null)
			throw new ThinklabResourceNotFoundException("cannot unload project " + projectId + ": project not found");
		
		if (((Project)project).isLoaded()) {
			((Project)project).unload();
		}

		ModelManager.Resolver resolver = (ModelManager.Resolver) getResolver();
		resolver.setProject(project);

		((Project)project).load(resolver);
	}

	@Override
	public void registerProjectDirectory(File projectDirectory)  {

		/*
		 * register all projects in configured directory
		 * 
		 */
		ArrayList<File> pdirs = new ArrayList<File>();
		
		for (File f : projectDirectory.listFiles()) {
			if (isThinklabProject(f)) {
				pdirs.add(f);
			}
		}
		
		if (pdirs.size() > 0) {
			registerProject(pdirs.toArray(new File[pdirs.size()]));
		}

	}

	@Override
	public IResolver getResolver() {
		return ((ModelManager)(Thinklab.get().getModelManager())).getResolver(null, null);
	}

	public static boolean isThinklabProject(File dir) {
		File f = 
			new File(dir + File.separator + "META-INF" + File.separator + "thinklab.properties");
		return f.exists();
	}

	/*
	 * non-API
	 */
	
	/** 
	 * Computes the dependency graph for all projects registered; return an iterator of them in 
	 * order of dependency. Throws an exception if circular dependencies are detected.
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public List<IProject> computeDependencies() throws ThinklabException {
		
		DefaultDirectedGraph<IProject, DefaultEdge> ret = 
				new DefaultDirectedGraph<IProject, DefaultEdge>(DefaultEdge.class);
		
		for (IProject pd : _projects.values()) {
			
			ret.addVertex(pd);
			for (String req : ((Project)pd).getPrerequisiteIds()) {
				
				IProject rpd = getProject(req);
				
				if (rpd == null) {
					throw new ThinklabProjectException("project " + req + " required by project " + pd.getId() + " cannot be found in project path");
				}
				
				ret.addVertex(rpd);
				ret.addEdge(rpd, pd);
			}
		}
		
		CycleDetector<IProject, DefaultEdge> cd = 
				new CycleDetector<IProject, DefaultEdge>(ret);
		
		if (cd.detectCycles()) {
			
			String s = "";
			for (IProject pd : cd.findCycles()) {
				s += (s.isEmpty() ? "" : ", ") + pd.getId();
			}
			throw new ThinklabProjectException("circular dependencies detected between projects " + s);
		}

		TopologicalOrderIterator<IProject, DefaultEdge> tord = 
				new TopologicalOrderIterator<IProject, DefaultEdge>(ret);
		
		
		ArrayList<IProject> r = new ArrayList<IProject>();
		
		while (tord.hasNext())
			r.add(tord.next());
		
		return r;
	}

	public List<IProject> computeDependencies(IProject project) throws ThinklabException {
		
		DefaultDirectedGraph<IProject, DefaultEdge> ret = 
				new DefaultDirectedGraph<IProject, DefaultEdge>(DefaultEdge.class);
		
		for (IProject pd : collectDependencies(project, null)) {
			
			ret.addVertex(pd);
			for (String req : ((Project)pd).getPrerequisiteIds()) {
				
				IProject rpd = getProject(req);
				
				if (rpd == null) {
					throw new ThinklabProjectException("project " + req + " required by project " + pd.getId() + " cannot be found in project path");
				}
				
				ret.addVertex(rpd);
				ret.addEdge(rpd, pd);
			}
		}
		
		CycleDetector<IProject, DefaultEdge> cd = 
				new CycleDetector<IProject, DefaultEdge>(ret);
		
		if (cd.detectCycles()) {
			
			String s = "";
			for (IProject pd : cd.findCycles()) {
				s += (s.isEmpty() ? "" : ", ") + pd.getId();
			}
			throw new ThinklabProjectException("circular dependencies detected between projects " + s);
		}

		TopologicalOrderIterator<IProject, DefaultEdge> tord = 
				new TopologicalOrderIterator<IProject, DefaultEdge>(ret);
		
		
		ArrayList<IProject> r = new ArrayList<IProject>();
		
		while (tord.hasNext())
			r.add(tord.next());
		
		return r;
	}

	private Collection<IProject> collectDependencies(IProject project, Collection<IProject> ret) throws ThinklabException {
		
		if (ret == null)
			ret = new HashSet<IProject>();

		if (ret.contains(project))
			/*
			 * circular dependency: we let the sorter deal with that
			 */
			return ret;
		
		ret.add(project);
		
		for (String id : ((Project)project).getPrerequisiteIds()) {
			IProject dep = getProject(id);
			if (dep == null)
				throw new ThinklabResourceNotFoundException(
						"project " + id + " required by " + project.getId() + " has not been registered");

			collectDependencies(dep, ret);
		}
		
		return ret;
	}

	
}
