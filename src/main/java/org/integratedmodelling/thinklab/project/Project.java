package org.integratedmodelling.thinklab.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.integratedmodelling.common.HashableObject;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.factories.IProjectManager;
import org.integratedmodelling.thinklab.api.lang.IResolver;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.FolderZiper;
import org.integratedmodelling.utils.MiscUtilities;


public class Project extends HashableObject implements IProject {
	
	String _id = null;
	File _path;

	/**
	 * Number of loaded projects that reference this one, including ourselves. Unloading a 
	 * project that references this will unload this only if the decremented count drops to 0. 
	 */
//	int _refcount = 0;

	Properties _properties = null;
	private ArrayList<INamespace> _namespaces = new ArrayList<INamespace>();
	private String[] _dependencies;
	private boolean _loaded = false;
	
	ProjectManager _manager;
	
	/*
	 * these 2 are in sync
	 */
	private ArrayList<File> _resourcesInError = new ArrayList<File>();
	private ArrayList<String> _errors = new ArrayList<String>();

	/*
	 * if true, we need refresh
	 */
	private boolean _isDirty = false;
	
	
	public Project(File path, IProjectManager manager) {
		
		_path = path;
		_manager = (ProjectManager) manager;
		_id = org.integratedmodelling.utils.MiscUtilities.getFileName(path.toString());
		_properties = getProperties();
		String pp = getProperties().getProperty(IProject.PREREQUISITES_PROPERTY, "");
		_dependencies = 
			pp.isEmpty() ? new String[0] :
			getProperties().getProperty(IProject.PREREQUISITES_PROPERTY, "").split(",");
	}
	
	@Override
	public String getId() {
		return _id;
	}

	/**
	 * Use this to force unload when refresh is called.
	 * 
	 * @param isDirty
	 */
	public void setDirty(boolean isDirty) {
		_isDirty = isDirty;
	}

	public void load(IResolver resolver) throws ThinklabException {

		if (isLoaded()/* && isDirty()*/)
			unload();
		
//		_refcount ++;
		
		/*
		 * if we haven't been unloaded, we didn't need to so we don't need
		 * loading, either.
		 */
//		if (isLoaded())
//			return;
		
		for (IProject p : _manager.computeDependencies(this)) {
			if (p.equals(this))
				continue;
			IResolver r = resolver.getImportResolver(p);
			((Project)p).load(r);
		}
		
		_namespaces = new ArrayList<INamespace>();
		HashSet<File> read = new HashSet<File>();
		
		File sourceDir = new File(_path + File.separator + this.getSourceDirectory());

		for (File f : sourceDir.listFiles()) {
			loadInternal(f, read, _namespaces, "", this, resolver);
		}
		_loaded = true;
		_isDirty = false;
	}

//	private boolean isDirty() {
//		return _isDirty ;
//	}
//

	private void loadInternal(File f, HashSet<File> read, ArrayList<INamespace> ret, String path,
			IProject project, IResolver resolver) throws ThinklabException {

		ModelManager mman = (ModelManager) Thinklab.get().getModelManager();
		
		String pth = 
				path == null ? 
					"" : 
					(path + (path.isEmpty() ? "" : ".") + CamelCase.toLowerCase(MiscUtilities.getFileBaseName(f.toString()), '-'));
						
		if (f. isDirectory()) {
		
			for (File fl : f.listFiles()) {
				loadInternal(fl, read, ret, pth, project, resolver);
			}
			
		} else if (mman.canParseExtension(MiscUtilities.getFileExtension(f.toString()))) {

			/*
			 * already imported by someone else
			 */
			if (Thinklab.get().getNamespace(pth) != null)
				return;
			
			INamespace ns;
			try {
				System.out.println("READING NAMESPACE " + pth + " FROM " + f);
				ns = mman.loadFile(f.toString(), pth, this, resolver);
				if (ns != null) {
					ret.add(ns);	
					System.out.println("READ NAMESPACE " + ns.getId() + " FROM " + f);
				}
			} catch (ThinklabException e) {
				_resourcesInError.add(f);
				_errors.add(e.getMessage());
			}
		}
	}

	public void unload() throws ThinklabException {
		
		if (!isLoaded())
			return;

		/*
		 * unload dependents in reverse order of dependency.
		 */
		List<IProject> deps = _manager.computeDependencies(this);
		for (int i = deps.size() - 1; i >= 0; i--) {
			IProject p = deps.get(i);
			if (p.equals(this))
				continue;
			((Project)p).unload();
		}
		
//		_refcount --;
//		
//		if (_refcount == 0) {
		
			for (INamespace ns : _namespaces) {
				Thinklab.get().releaseNamespace(ns.getId());
			}
		
			_namespaces.clear();
			_resourcesInError.clear();
			_loaded = false;
//		}
	}

	@Override
	public File findResource(String resource) {

		File ff = 
			new File(
				_path + File.separator + getSourceDirectory() + 
				File.separator + resource);
		
		if (ff.exists()) {
			return ff;
		}
		return null;
	}
	
	@Override
	public File findResourceForNamespace(String namespace) {
		
		String fp = namespace.replace('.', File.separatorChar);
		for (String extension : new String[]{"tql", "owl"}) {
			File ff = new File(_path + File.separator + getSourceDirectory() + File.separator + fp + "." + extension);
			if (ff.exists()) {
				return ff;
			}
		}
			
		return null;
	}

	public boolean isLoaded() {
		return _loaded;
	}

	@Override
	public Properties getProperties() {
		
		if (_properties == null) {

			_properties = new Properties();
			try {
				File pfile = 
					new File(
						_path + 
						File.separator + 
						"META-INF" +
						File.separator + 
						"thinklab.properties");
				
				if (pfile.exists()) {
					try {
						_properties.load(new FileInputStream(pfile));
					} catch (Exception e) {
						throw new ThinklabException(e);
					}
				}
					
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		return _properties;
	}
	
	@Override
	public File getWorkspace() {
		return _path;
	}

	@Override
	public File getWorkspace(String subspace) {
		File ret = new File(_path + File.separator + subspace);
		ret.mkdirs();
		return ret;
	}

	@Override
	public File getScratchArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getScratchArea(String subArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getTempArea(String subArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getLoadPath() {
		return _path;
	}

	@Override
	public File getLoadPath(String subArea) {
		File ret = new File(_path + File.separator + subArea);
		return ret.exists() ? ret : null;
	}

	@Override
	public Collection<INamespace> getNamespaces() {
		return _namespaces;
	}

	@Override
	public String getSourceDirectory() {
		return getProperties().getProperty(IProject.SOURCE_FOLDER_PROPERTY, "src");
	}
	
	@Override
	public String getOntologyNamespacePrefix() {
		return getProperties().getProperty(
				IProject.ONTOLOGY_NAMESPACE_PREFIX_PROPERTY, "http://www.integratedmodelling.org/ns");
	}

	@Override
	public List<IProject> getPrerequisites() {
		
		ArrayList<IProject> ret = new ArrayList<IProject>();

		for (String s : _dependencies) {
			IProject p = _manager.getProject(s);
			if (p != null) {
				ret.add(p);
			}
		}
		
		return ret;
	}

	@Override
	public long getLastModificationTime() {

		long lastmod = 0L;
		
		for (File f : FileUtils.listFiles(_path, new String[]{}, true)) {
			if (f.lastModified() > lastmod)
				lastmod = f.lastModified();
		}
		
		return lastmod;		
	}

	// NON-API

	public void addDependency(String plugin) throws ThinklabException {
	
		String pp = getProperties().getProperty(IProject.PREREQUISITES_PROPERTY, "");
		String[] deps = 
			pp.isEmpty() ? new String[0] :
			getProperties().getProperty(IProject.PREREQUISITES_PROPERTY, "").split(",");
		
		String dps = "";
		for (String s : deps) {
			if (s.equals(plugin))
				return;
			dps += (dps.isEmpty()? "" : ",") + s;
		}
		
		dps += (dps.isEmpty()? "" : ",") + plugin;
		getProperties().setProperty(IProject.PREREQUISITES_PROPERTY, dps);
		deps = dps.split(",");

		saveProperties();
		
	}
	
	public void createManifest(String[] dependencies) throws ThinklabException {
			
		File td = new File(_path + File.separator + "META-INF");
		td.mkdirs();
		
		new File(_path + File.separator + getSourceDirectory()).mkdirs();
			
		if (dependencies != null && dependencies.length > 0) {
			for (String d : dependencies)
				addDependency(d);
		} else {
			saveProperties();
		}
	}
	
	private void saveProperties() throws ThinklabException {
		
		File td = 
			new File(_path +
				File.separator + "META-INF" +
				File.separator + "thinklab.properties");
		
		try {
			getProperties().store(new FileOutputStream(td), null);
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		
	}

	public File getZipArchive() throws ThinklabException {
		
		File ret = null;
		try {
			ret = File.createTempFile("tpr", ".zip", Thinklab.get().getTempArea("proj"));
			FolderZiper.zipFolder(
				_path.toString(), 
				ret.toString());
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		return ret;
	}

	public String createNamespace(IProject p, String ns) throws ThinklabException {
		
		File ret = new File(_path + File.separator + getSourceDirectory() + File.separator + 
				ns.replace('.', File.separatorChar) + ".tql");
		File dir = new File(MiscUtilities.getFilePath(ret.toString()));

		try {
			dir.mkdirs();
			PrintWriter out = new PrintWriter(ret);
			out.println("namespace " + ns + ";\n");
			out.close();
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}

		return getSourceDirectory() + File.separator + 
				ns.replace('.', File.separatorChar) + ".tql";
	}
	
	String[] getPrerequisiteIds() {
		return _dependencies;
	}

	@Override
	public boolean providesNamespace(String namespaceId) {
		return 
			findResourceForNamespace(namespaceId) != null;
	}
	
	public String toString() {
		return "[thinklab project: " + _id + "]";
	}
	
	@Override
	public List<String> getUserResourceFolders() {

		ArrayList<String> ret = new ArrayList<String>();
		
		for (File f : _path.listFiles()) {
			if (f.isDirectory() &&
				!f.equals(new File(_path + File.separator + getSourceDirectory())) &&
				!isManagedDirectory(
						MiscUtilities.getFileName(f.toString()))) {
				ret.add(MiscUtilities.getFileBaseName(f.toString()));
			}
		}
		
		return ret;
	}

	private boolean isManagedDirectory(String fileName) {
		// TODO add any other necessary files
		return 
			fileName.startsWith("." ) || 
			fileName.toString().endsWith("META-INF");
	}

	@Override
	public boolean hasErrors() {
		for (INamespace n : _namespaces) {
			if (n.hasErrors())
				return true;
		}
		return false;
	}
	
	@Override
	public boolean hasWarnings() {
		
		for (INamespace n : _namespaces) {
			if (n.hasWarnings())
				return true;
		}
		return false;
	}
}
