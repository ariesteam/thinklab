package org.integratedmodelling.thinklab.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.plugin.IThinklabPlugin;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.utils.MiscUtilities;

import agg.xt_basis.Version;

public class ThinklabProject implements IProject {

	
	File   _dir;
	String _id;
	Properties _properties;
	ArrayList<INamespace> _namespaces = new ArrayList<INamespace>();
	ArrayList<String> _requisites = new ArrayList<String>();
	
	public ThinklabProject(File resource) {

		if (!resource.isDirectory()) {
			
			/*
			 * Must be .zip or .jar.
			 * 
			 * TODO unpack archive in kosher location, updating as 
			 * necessary.
			 * 
			 * Set directory to location.
			 * 
			 */
		} else {
			_dir = resource;
		}
		
		_id  = MiscUtilities.getFileBaseName(_dir.toString());
		
		loadProperties();
		
	}
	


	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return _id;
	}

	@Override
	public List<IThinklabPlugin> getPrerequisites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load() throws ThinklabException {

		/*
		 * unload everything
		 */
		for (INamespace n : _namespaces) {
			ModelManager.get().releaseNamespace(n.getNamespace());
		}
		_namespaces.clear();
		
		/*
		 * refresh any prerequisites are loaded
		 */

		
		/*
		 * load everything
		 */
		ModelManager.get().loadSourceDirectory(getSourceDirectory());
	}

	@Override
	public void unload() throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public File findResource(String resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getProperties() {
		return _properties;
	}

	@Override
	public File getWorkspace() {
		return Thinklab.get().getWorkspace(_id);
	}

	@Override
	public File getWorkspace(String subspace) {
		return Thinklab.get().getWorkspace(_id + File.separator + subspace);
	}

	@Override
	public File getScratchArea() {
		return Thinklab.get().getScratchArea(_id);
	}

	@Override
	public File getScratchArea(String subArea) {
		return Thinklab.get().getScratchArea(_id + File.separator + subArea);
	}

	@Override
	public File getTempArea(String subArea) {
		return Thinklab.get().getTempArea(_id + File.separator + subArea);
	}

	@Override
	public File getLoadPath(String subArea) {
		File ret = new File(_dir + File.separator + subArea);
		ret.mkdirs();
		return ret;
	}

	@Override
	public Collection<INamespace> getNamespaces() {
		return _namespaces;
	}

	@Override
	public File getSourceDirectory() {
		return getLoadPath(_properties.getProperty(SOURCE_FOLDER_PROPERTY, "src"));
	}

	@Override
	public String getOntologyNamespacePrefix() {
		// TODO Auto-generated method stub
		return _properties.getProperty(ONTOLOGY_NAMESPACE_PREFIX_PROPERTY, 
				NS.DEFAULT_THINKLAB_ONTOLOGY_PREFIX);
	}

	@Override
	public void addDependency(String plugin, boolean reload)
			throws ThinklabException {

		if (!_requisites.contains(plugin)) {

			_requisites.add(plugin);
			_properties.setProperty(PREREQUISITES_PROPERTY, StringUtils.join(_requisites,","));
			persistProperties();
			
			if (reload) {
				load();
			}
		}
	}

	@Override
	public File findResourceForNamespace(String namespace, String extension) {
		
		String fp = namespace.replace('.', File.separatorChar);
		File ff = new File(getSourceDirectory() + File.separator + fp + "." + extension);
		if (ff.exists()) {
			return ff;
		}
			
		return null;
	}

	/*
	 * ---------------------------------------------------------------------------------------------
	 * non-API
	 * ---------------------------------------------------------------------------------------------
	 */
	
	private void loadProperties() {

		File f = new File(getLoadPath(THINKLAB_META_INF) + File.separator + THINKLAB_PROPERTIES_FILE);
		if (!f.exists()) {
			try {
				FileUtils.touch(f);
			} catch (IOException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		
		try {
			InputStream inStream = new FileInputStream(f);
			_properties.load(inStream);
			inStream.close();
		} catch (IOException e) {
			throw new ThinklabRuntimeException(e);
		}
		
		_requisites.clear();		
		for (String s : StringUtils.split(_properties.getProperty(PREREQUISITES_PROPERTY, ""), ','))
			_requisites.add(s);
	}
	
	private void persistProperties() {
		try {
			File f = new File(getLoadPath(THINKLAB_META_INF) + File.separator + THINKLAB_PROPERTIES_FILE);
			OutputStream out = new FileOutputStream(f);
			_properties.store(out, "Written by Thinklab " + new Version());
			out.close();
		} catch (IOException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
}
