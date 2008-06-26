package org.integratedmodelling.thinklab.boot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.java.plugin.JpfException;
import org.java.plugin.ObjectFactory;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.standard.StandardPluginLocation;

/**
 * This singleton handles the bootstrap of the Thinklab system. It will discover and notify all plugins in 
 * the passed plugin path, then initialize the core plugin and optionally transfer control to another class in
 * another application plugin.
 * 
 * It's basically a replica of the JPF boot library with thinklab-specific enhancements.
 * 
 * @author Ferdinando
 *
 */
public class ThinklabBootManager {

	private static final String THINKLAB_PLUGIN_PATH = "thinklab.plugin.path";
	private static final String THINKLAB_PLUGIN_DOWNLOAD_LOCATIONS = "thinklab.plugin.download.locations";
	private static final String THINKLAB_SESSIONMANAGER_PLUGIN = "thinklab.sessionmanager.plugin";
	private static final String THINKLAB_SESSIONMANAGER_CLASS = "thinklab.sessionmanager.class";
	private static final String THINKLAB_APPLICATION_PLUGIN = "thinklab.application.plugin";

	private static Properties properties = new Properties();

	private static ThinklabBootManager _this;
	
	private static ArrayList<PluginLocation> pluginLocations =
		 new ArrayList<PluginLocation>();
	
	private static PluginManager pluginManager = null;
	
	private static void setup() {
		
		String pluginPath = properties.getProperty(THINKLAB_PLUGIN_PATH);
		
		/*
		 * break the plugin path into individual directories
		 */
		String[] dirs = pluginPath.split(";");
		
		for (String dir : dirs)
			collectPlugins(dir);
		
		if (pluginLocations.size() > 0) {
			
			pluginManager = ObjectFactory.newInstance().createManager();
			
			try {
				pluginManager.publishPlugins(pluginLocations.toArray(new PluginLocation[pluginLocations.size()]));
			} catch (JpfException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	
	private static void collectPlugins(String d) {
		
		File dir = new File(d);
		
		if (dir.exists() && dir.isDirectory() && dir.canRead()) {
			
			File[] plugins = dir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return dir.isDirectory() && new File(dir + File.separator + "plugin.xml").exists();
				}
			});
			
			try {
				
				for (File f : plugins) {
					pluginLocations.add(StandardPluginLocation.create(f));
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static void boot() throws PluginLifecycleException {
		
		
		if (properties.containsKey(THINKLAB_SESSIONMANAGER_PLUGIN)) {


		}
		
		if (properties.containsKey(THINKLAB_APPLICATION_PLUGIN)) {
			
			String appPlugin = properties.getProperty(THINKLAB_APPLICATION_PLUGIN);
			
			pluginManager.activatePlugin(appPlugin);
			
			/*
			 * boot the application plugin
			 */
			Plugin app = pluginManager.getPlugin(appPlugin);
			
		}
		
		
	}


	/**
	 * Properties must contain:
	 * 
	 * THINKLAB_PLUGIN_PATH (thinklab.plugin.path)
	 * THINKLAB_PLUGIN_DOWNLOAD_LOCATIONS we'll see
	 * THINKLAB_SESSION_MANAGER_CLASS 
	 * THINKLAB_SESSION_MANAGER_PLUGIN
	 * THINKLAB_APPLICATION_PLUGIN to boot if any
	 * THINKLAB_APPLICATION_CLASS if any
	 * @param p
	 */
	public static void setProperties(Properties p) {
		properties.putAll(p);
		setup();
	}
	
	
	public static void upgradePlugin(String plugin) {
		
	}

	public static void installPlugin(String plugin) {
		
	}
	
	public static void removePlugin(String plugin) {
		
	}

	/**
	 * Boot the application if any was specified. If a property file is passed as an argument, load
	 * configuration from it.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length > 0) {
			File f = new File(args[0]);
			if (f.exists()) {
				try {
					properties.load(new FileInputStream(f));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			setup();
			
			try {
				boot();
			} catch (PluginLifecycleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
