package org.integratedmodelling.thinklab.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.project.interfaces.IProjectLoader;
import org.java.plugin.Plugin;

public class ThinklabProject {
	
	public ThinklabProject(String pluginId, String[] dependencies) {
		
		/*
		 * check if there, load if so; if dependencies != null, ensure 
		 * they're all there or recreate plugin manifest.
		 */
	}
	
	/**
	 * Get the content of THINKLAB-INF/thinklab.properties if the plugin contains that
	 * directory, or null if it doesn't. Can be used to check if a plugin is a 
	 * thinklab plugin based on the null return value.
	 * 
	 * TODO move to client library and load the library in the server package
	 * 
	 * @param plugin
	 * @return
	 * @throws ThinklabIOException
	 */
	public static Properties getThinklabPluginProperties(Plugin plugin) throws ThinklabIOException {
		
			Properties ret = null;
			File pfile = 
				new File(
					Thinklab.getPluginLoadDirectory(plugin) + 
					File.separator + 
					"THINKLAB-INF" +
					File.separator + 
					"thinklab.properties");
			
			if (pfile.exists()) {
				try {
					ret = new Properties();
					ret.load(new FileInputStream(pfile));
				} catch (Exception e) {
					throw new ThinklabIOException(e);
				}
			}
			
			return ret;
		}

	public static ThinklabProject create(String id) {
		
		ThinklabProject ret = new ThinklabProject(id, null);

		return ret;
	}

	public static ThinklabProject load(String id, boolean createIfAbsent)  throws ThinklabException {
		
		ThinklabProject ret = new ThinklabProject(id, null);

		return ret;
	}

	public boolean exists(String id) {
		return false;
	}
	
	public void addLoader(Class<? extends IProjectLoader> cls)  throws ThinklabException { 
		
	}
	
	public static String deploy(File archive, String pluginId, boolean activate) throws ThinklabException {
		
		String instDir = System.getProperty("thinklab.inst");
		
		System.out.println(instDir);
		
		return null;
	}

	public static void undeploy(String id)  throws ThinklabException  {
		
		
	}
	
	public void createManifest(File pluginDir) throws ThinklabIOException {

		try {
			FileOutputStream fout = 
				new FileOutputStream(new File(pluginDir + File.separator + "plugin.xml"));
			PrintStream out = new PrintStream(fout);
			String header = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
				"<!DOCTYPE plugin PUBLIC \"-//JPF//Java Plug-in Manifest 1.0\" \"http://jpf.sourceforge.net/plugin_1_0.dtd\">\r\n" + 
				"<plugin id=\"org.integratedmodelling.thinklab.ecology\" version=\"0.8.1.20110428103733\">\r\n" + 
				"   <requires>\r\n";

//		"	<import exported=\"false\" match=\"compatible\" optional=\"false\" plugin-id=\"org.integratedmodelling.thinklab.core\" reverse-lookup=\"false\"/>\r\n" + 
			out.print(
				"   </requires>\r\n" + 
				"</plugin>");
		
			fout.close();
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

}
