package org.integratedmodelling.thinklab.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.integratedmodelling.exceptions.ThinklabConfigurationException;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.api.configuration.IConfiguration;

/**
 * Global Thinklab configuration. Thinklab proxies to one instance
 * of this.
 * 
 * @author Ferd
 *
 */
public class Configuration implements IConfiguration {

	static OS os = null;

	/*
	 * all configurable paths; others are derived from them.
	 */
	File _installPath;
	File _scratchPath;
	File _dataPath;
	File _workspacePath;
	
	Properties _properties;
	
	public Configuration() throws ThinklabException {
		
		String installPath = System.getenv(Env.ENV_THINKLAB_INSTALL_DIR);
		if (installPath == null)
			throw new ThinklabConfigurationException(
				"configuration error: the environmental variable " +
				Env.ENV_THINKLAB_INSTALL_DIR + 
				" must be defined");
	
		String home = System.getProperty("user.home");
		
		this._installPath = new File(installPath);
		this._dataPath = 
				getPath(Env.ENV_THINKLAB_DATA_DIR, 
						home + File.separator + ".thinklab");
		this._scratchPath = 
				getPath(Env.ENV_THINKLAB_SCRATCH_DIR, 
						this._dataPath + File.separator + ".scratch");
		this._workspacePath = 
				getPath(Env.ENV_THINKLAB_WORKSPACE, 
						home + File.separator + "thinklab");

		/*
		 * install configuration and knowledge directories from installation
		 * directories
		 */
		File config = new File(_workspacePath + File.separator + SUBSPACE_CONFIG);
		if (!config.exists()) {
			File confPath = getLoadPath(SUBSPACE_CONFIG);
			if (confPath.exists()) {
				try {
					config.mkdirs();
					FileUtils.copyDirectory(confPath, config);
				} catch (IOException e) {
					throw new ThinklabIOException(e);
				}
			}
		}
		
		File knowledge = new File(_workspacePath + File.separator + SUBSPACE_KNOWLEDGE);
		if (!knowledge.exists()) {
			File confPath = getLoadPath(SUBSPACE_KNOWLEDGE);
			if (confPath.exists()) {
				try {
					knowledge.mkdirs();
					FileUtils.copyDirectory(confPath, knowledge);
				} catch (IOException e) {
					throw new ThinklabIOException(e);
				}
			}
		}
	}
	
	/*
	 * create default unless var for first is there, make directories,
	 * complain if not there, return file.
	 */
	private File getPath(String env, String string) throws ThinklabException {

		String path = System.getenv(env);
		if (path == null)
			path = string;
		
		File f = new File(path);

		try {
			f.mkdirs();
		} catch (Exception e) {
		}
		
		if (!f.exists() || !f.isDirectory()) {
			throw new ThinklabIOException("cannot create or access system path " + path);
		}
		
		return f;
	}

	@Override
	public Properties getProperties() {
		
		if (_properties == null) {
			/*
			 * load or create thinklab system properties
			 */
			_properties = new Properties();
			File properties = 
					new File(getWorkspace(SUBSPACE_CONFIG) + File.separator + "thinklab.properties");
			try {
				if (properties.exists()) {
					FileInputStream input;

					input = new FileInputStream(properties);
					_properties.load(input);
					input.close();
				} else {
					FileUtils.touch(properties);
				}
			} catch (Exception e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		return _properties;
	}

	@Override
	public File getWorkspace() {
		return _workspacePath;
	}

	@Override
	public File getWorkspace(String subspace) {
		File ret = new File(_workspacePath + File.separator + subspace);
		ret.mkdirs();
		return ret;
	}

	@Override
	public File getScratchArea() {
		return _scratchPath;
	}

	@Override
	public File getScratchArea(String subArea) {
		File ret = new File(_scratchPath + File.separator + subArea);
		ret.mkdirs();
		return ret;
	}

	@Override
	public File getTempArea(String subArea) {

		File ret = new File(_scratchPath + File.separator + "tmp");
		if (subArea != null) {
			ret = new File(ret + File.separator + subArea);
		}
		ret.mkdirs();
		return ret;
	}

	@Override
	public File getLoadPath(String subArea) {

		return subArea == null ?
			_installPath :
			new File(_installPath + File.separator + subArea);
	}
	
	/**
	 * Quickly and unreliably retrieve the class of OS we're running on.
	 * @return
	 */
	static public OS getOS() {

		if (os == null) {

			String osd = System.getProperty("os.name").toLowerCase();

			// TODO ALL these checks need careful checking
			if (osd.contains("windows")) {
				os = OS.WIN;
			} else if (osd.contains("mac")) {
				os = OS.MACOS;
			} else if (osd.contains("linux") || osd.contains("unix")) {
				os = OS.UNIX;
			}

		}

		return os;
	}

}
