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
package org.integratedmodelling.thinklab.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.http.utils.FileOps;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * 
 * @author Ferdinando Villa
 * @deprecated should be an interface
 */
public abstract class ThinklabWebPlugin extends ThinklabPlugin {
	
	/**
	 * Create a jar containing the passed lang-addon.xml and the contents of any components/
	 * directory, put it in the web server's WEB-INF/lib directory.
	 * @throws ThinkcapException 
	 */
	public static void createComponentJar(ThinklabWebPlugin plugin) throws ThinklabException {

		File zklang = new File(plugin.getLoadDirectory() + File.separator + "lang-addon.xml");
		
		if (!zklang.exists())
			return;
		
		plugin.logger().info(
				"publishing ZK resources");
		
		File outfile = new File(
				ThinkWeb.get().getWebSpace() + 
				File.separator + 
				"WEB-INF" +
				File.separator +
				"lib" + 
				File.separator +
				ThinklabWebPlugin.getPluginName(plugin.getDescriptor().getId()) +
				"z.jar");
		
		FileOutputStream fos = null;
		JarOutputStream jos = null;
		
		try {
			fos = new FileOutputStream(outfile);
		
			// stupid manifest		
			Manifest manifest = new Manifest();
			Attributes manifestAttr = manifest.getMainAttributes();
			manifestAttr.putValue("Manifest-Version", "1.0");
		
			jos = new JarOutputStream(fos, manifest);
			
			jos.putNextEntry(new JarEntry("metainfo/"));
			jos.putNextEntry(new JarEntry("metainfo/zk/"));

			FileInputStream in = new FileInputStream(zklang);
			byte[] buf = new byte[4096];
			
			jos.putNextEntry(new JarEntry("metainfo/zk/lang-addon.xml"));      

			int len;
			while ((len = in.read(buf)) > 0) {
			   jos.write(buf, 0, len);
			}

			jos.closeEntry();
			in.close();
			
			File zkcomp = 
				new File(plugin.getLoadDirectory() + File.separator + "components");

			if (zkcomp.exists() && zkcomp.isDirectory()) {
				
				jos.putNextEntry(new JarEntry("components/"));
				
				for (File cmp :  zkcomp.listFiles()) {

					/*
					 * TODO check if we want to include something other than 
					 * .zul files.
					 */
					if (!cmp.isDirectory() && !cmp.isHidden() && cmp.toString().endsWith(".zul")) {
						
						String bname = MiscUtilities.getFileName(cmp.toString());
						jos.putNextEntry(new JarEntry("component/" + bname));

						in = new FileInputStream(cmp);
						while ((len = in.read(buf)) > 0) {
						   jos.write(buf, 0, len);
						}

						jos.closeEntry();
						in.close();
					}
				}
			}
			
			jos.close();
			
		} catch (Exception e) {
			throw new ThinklabException(e);
		} finally {

		}
	}
	
	
	/**
	 * 
	 * @param subdir
	 * @return
	 */
	public File getPluginWebSpace() {
		
		File outfile =
			new File(
					ThinkWeb.get().getWebSpace()+"/" + 
					getDescriptor().getId());

		return outfile;
	}
	
	/**
	 * 
	 * @param subdir
	 * @return
	 */
	public File getPluginWebSpace(String subdir) {
		
		File outfile =
			new File(
					ThinkWeb.get().getWebSpace()+"/" + 
					getDescriptor().getId() + "/" + subdir);
		outfile.mkdirs();
		return outfile;
	}

	/**
	 * 
	 * @param session
	 * @return
	 */
	public File getPluginWebSpace(ISession session) {
		
		File outfile =
			new File(
					ThinkWeb.get().getWebSpace()+"/" + 
					getDescriptor().getId() + "/" + session.getSessionWorkspace());
		outfile.mkdirs();
		return outfile;
	}

	/**
	 * 
	 * @param subdir
	 * @return
	 */
	public String getPluginWebSpaceUrl(String subdir) {
		return ThinkWeb.get().getBaseUrl() + "/" + 
			getDescriptor().getId() + "/" +
			subdir;
	}
	
	/**
	 * 
	 * @param subdir
	 * @return
	 */
	public String getPluginWebSpaceUrl() {
		return ThinkWeb.get().getBaseUrl() + "/" + 
			getDescriptor().getId();
	}
	
	
	/**
	 * 
	 * @param session
	 * @return
	 */
	public String getPluginWebSpaceUrl(ISession session) {
		return ThinkWeb.get().getBaseUrl() + "/" + 
			getDescriptor().getId() + "/" +
			session.getSessionWorkspace();
	}
	
	/**
	 * Get the last part of the plugin id if it's in dot notation
	 * 
	 * @param pluginId
	 * @return
	 */
	public static String getPluginName(String pluginId) {
		String[] pids = pluginId.split("\\.");
		return pids[pids.length - 1];
	}
	
	@Override
	protected void loadExtensions() throws Exception {
		
		/*
		 * brute force is good for ZK, and saves so much effort trying to make
		 * it work otherwise. Oh I LOVE embedded servers.
		 */
		publishZKComponents();

		/*
		 * independent from declared applications, if there is a web/ directory that is visible within
		 * the plugin space, copy its contents under the plugin ID in the main web space so all web
		 * resources are made available.
		 */
		publishWebResources();
		
		registerQueryForms();
		registerTypeDecorations();
//		registerApplications();
//		registerPortlets();
//		registerLayouts();
		
	}
	
	private void publishWebResources() throws IOException {
		
		File webDir = new File(getLoadDirectory() + "/web");
		
		if (webDir.exists()) {
			
			File dest = new File(getPluginWebSpace() + "/" + getDescriptor().getId());
			FileOps.copyFilesCached(webDir, dest);
			
		}
		
	}

	/**
	 * Copy a resource to a file in the web space and return a URL to it.
	 * @param prefix
	 * @param resource
	 * @return
	 * @throws ThinklabException
	 */
	public String publishResource(String resource) throws ThinklabException {
		
		URL res = getResourceURL(resource);
		
		/* copy in web area under plugin directory */		
		File newf = new File(getPluginWebSpace() + "/" + MiscUtilities.getFileName(resource.toString()));
		
		CopyURL.copy(res, newf);
		
		return 
			getPluginWebSpaceUrl() +
			"/" 
			+ MiscUtilities.getFileName(resource.toString());
	}
	
	/**
	 * Copy a resource to a file in the session-specific web space and return a URL to it.
	 * @param prefix
	 * @param resource
	 * @return
	 * @throws ThinklabException
	 */
	public String publishResource(String resource, ISession session) throws ThinklabException {
		
		URL res = getResourceURL(resource);
		
		/* copy in web area under plugin directory */		
		File newf = new File(getPluginWebSpace(session) + "/" + MiscUtilities.getFileName(resource.toString()));
		
		CopyURL.copy(res, newf);
		
		return 
			getPluginWebSpaceUrl(session) +
			"/" 
			+ MiscUtilities.getFileName(resource.toString());
	}
	
	/**
	 * Copy a resource to a file in a given folder of the web space and return a URL to it.
	 * @param prefix
	 * @param resource
	 * @return
	 * @throws ThinklabException
	 */
	public String publishResource(String resource, String folder) throws ThinklabException {
		
		URL res = getResourceURL(resource);
		
		/* copy in web area under plugin directory */		
		File newf = new File(getPluginWebSpace(folder) + "/" + MiscUtilities.getFileName(resource.toString()));
		
		CopyURL.copy(res, newf);
		
		return 
			getPluginWebSpaceUrl(folder) +
			"/" +
			MiscUtilities.getFileName(resource.toString());
	}

	private void publishZKComponents() throws ThinklabException {
		
		/*
		 * check if we have a lang-addon.xml anywhere visible;
		 * if so, jar it along with any macro component definitions
		 * and put it in the lib/ webapp dir before the server
		 * starts.
		 */
		createComponentJar(this);
		
	}

	@Override
	protected void unloadExtensions() throws Exception {
		
		unregisterApplications();
		unregisterTypeDecorations();
		unregisterQueryForms();
	}
//
//	private void registerApplications() {
//
//		/*
//		 * publish all applications from loaded plugins.
//		 */
//		for (Extension ext : getOwnExtensions("org.integratedmodelling.thinkcap.core", "thinkcap-application")) {
//			ThinkWeb.get().registerThinkcapApplication(new ThinklabWebApplication(this, ext));
//		}
//	}
	
//	private void registerPortlets() {
//
//		for (Extension ext : getOwnExtensions("org.integratedmodelling.thinkcap.core", "portlet")) {
//			Thinkcap.get().registerPortlet(new PortletDescriptor(this, ext));
//		}
//	}
//	private void registerLayouts() {
//
//		for (Extension ext : getOwnExtensions("org.integratedmodelling.thinkcap.core", "layout")) {
//			Thinkcap.get().registerLayout(new LayoutDescriptor(this, ext));
//		}
//	}
	
	private void unregisterApplications() {
		// TODO Auto-generated method stub
		
	}

	private void registerTypeDecorations() {
		
	}

	private void unregisterTypeDecorations() {
		
	}

	private void registerQueryForms() {
		
	}

	private void unregisterQueryForms() {
		
	}
	
	
}
