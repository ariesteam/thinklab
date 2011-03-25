/**
 * ThinkcapApplication.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinkcap.
 * 
 * Thinkcap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinkcap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.http;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.http.utils.FileOps;
import org.integratedmodelling.thinklab.http.utils.JPFUtils;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;

/**
 * Represents metadata and properties connected to a specific Thinkcap application.
 * Applications are declared by plug-ins using the plugin.xml file, except the
 * main thinkcap.xml application which is declared in WEB-INF/thinkcap.xml.
 * 
 * @author Ferdinando Villa
 * @see WEB-INF/thinkcap.xml for syntax example.
 */
public class ThinklabWebApplication {

	private Plugin registeringPlugin = null;
	
	volatile int currentUserCount = 0;
	
	/*
	 * these are properties in user config that users can override with respect
	 * to defaults set in the plugin configuration. The corresponding property
	 * in the plugin must have the name <appid>.<propertyname>.
	 */
	private final static String BANNER_PROPERTY = "banner";
	private final static String PLUGINS_PROPERTY = "plugins";
	private final static String STYLE_PROPERTY = "style";
	private final static String SKIN_PROPERTY = "skin";
	
	/*
	 * and these are the corresponding values. Their setters will write the
	 * properties file, making the change permanent.
	 */
	private String banner = null;
	private String plugins = null;
	private String style = null;
	private String skin = null;
	
	protected String id = null;
	protected String version = null;
	private String modelClass = null;
	private String logoSmall;
	private String logoLarge;
	private String shortDescription;
	private String longDescription;
	private String runningHead;
	private String entryPoint;
	private String copyright;
	private String location;
	private URL appURL;
	
	private boolean poweredByThinklab = false;
	private boolean published = false;
	
	// TEMPORARY files or directories to be skipped when copying the web contents. Should become
	// a configured property.
	private String[] skipped ={  "site" };
	
	/**
	 * Application descriptor; properties are read from the plugin manifest through the
	 * thinklab-application extension point.
	 */
	 Properties properties;
	
	private ArrayList<ThinkcapAuthor> authors =
		new ArrayList<ThinkcapAuthor>();

	private File webPath;
	
	public class ThinkcapAuthor {
		
		String name;
		String url;
		String logo;

		public String getName() { return name; }
		public String getUrl() { return url; }
		public String getLogo() { return logo; }
	}
		
	public ThinklabWebApplication(Plugin plugin, Extension ext) {

		registeringPlugin = plugin;	
	
		this.id = ext.getParameter("name").valueAsString();
		this.location = ext.getParameter("location").valueAsString();
		this.entryPoint = ext.getParameter("entry-point").valueAsString();
		this.shortDescription = ext.getParameter("short-description").valueAsString();
		this.longDescription = ext.getParameter("long-description").valueAsString();
		this.runningHead = JPFUtils.getParameter(ext, "running-head", "");
		this.copyright = JPFUtils.getParameter(ext, "copyright", "");
		this.logoSmall = JPFUtils.getParameter(ext, "logo-small");
		this.logoLarge = JPFUtils.getParameter(ext, "logo-large");
		this.banner = JPFUtils.getParameter(ext, "banner");
		this.skin = JPFUtils.getParameter(ext, "skin");
		this.style = JPFUtils.getParameter(ext, "style");
		this.modelClass = JPFUtils.getParameter(ext, "model-class");
		
		for (Extension.Parameter aext : ext.getParameters("author")) {
			
			ThinkcapAuthor author = new ThinkcapAuthor();
			
			author.name = aext.getSubParameter("name").valueAsString();
			author.url = aext.getSubParameter("url").valueAsString();
			author.logo = aext.getSubParameter("logo").valueAsString();
			
			authors.add(author);
		}
				
		/*
		 * TODO authentication
		 */
	}
	
	public void setAdditionalPlugins(String pl) throws ThinklabIOException {
		
		System.out.println(pl);
		
		if (this.plugins != null) {

			/*
			 * unload those that we don't want anymore
			 */
			for (String p : pl.split(",")) {
				if (!pl.contains(p)) {
					Thinklab.get().getManager().deactivatePlugin(p);
				}
			}
		}
		
		String actp = "";
		
		for (String p : pl.split(",")) {
			try {
				Thinklab.get().getManager().activatePlugin(p);
				
				if (!actp.equals(""))
					actp += ",";
				actp += p;
				
			} catch (PluginLifecycleException e) {
				Thinklab.get().logger().warn(
						"application " + id + 
						" failed to activate requested plugin " +
						p);
			}
		}
		
		this.plugins = actp;
		
		writeProperties();
	}
	
	public synchronized void notifyUserConnected(ThinklabWebSession session) {
		this.currentUserCount ++;
	}
	
	
	public synchronized void notifyUserDisconnected(ThinklabWebSession session) {
		this.currentUserCount --;
	}
	
	/*
	 * write only the modified application-specific properties to user
	 * config file.
	 */
	private void writeProperties() throws ThinklabIOException {
	
		Properties p = ((ThinklabPlugin)registeringPlugin).getProperties();
		
		if (this.plugins != null)
			p.setProperty(id + "." + PLUGINS_PROPERTY, this.plugins);
		if (this.banner != null)
			p.setProperty(id + "." + BANNER_PROPERTY, this.plugins);
		if (this.style != null)
			p.setProperty(id + "." + STYLE_PROPERTY, this.plugins);
		if (this.skin != null)
			p.setProperty(id + "." + SKIN_PROPERTY, this.plugins);
		
		((ThinklabPlugin)registeringPlugin).writeConfiguration();
		
	}

	public void setBanner(String banner) throws ThinklabIOException {
		this.banner = banner;
		((ThinklabPlugin)registeringPlugin).writeConfiguration();
	}
	
	public void setStyle(String style) throws ThinklabIOException {
		this.style = style;
		((ThinklabPlugin)registeringPlugin).writeConfiguration();
	}
	
	public void setSkin(String skin) throws ThinklabIOException {
		this.skin = skin;
		((ThinklabPlugin)registeringPlugin).writeConfiguration();
	}

	public String getId() {
		return id;
	}

	public String getCopyright() {
		return copyright;
	}
	
	public String getEntryPoint() {
		return entryPoint;
	}
	
	public String getSmallLogoURL() {
		return logoSmall;
	}
	
	public String getLogoURL() {
		return "/" + id + "/" + logoLarge;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getShortDescription() {
		return shortDescription;
	}
	
	public String getLongDescription() {
		return longDescription;
	}
	
	public String getRunningHead() {
		return runningHead;
	}
	
	public Collection<ThinkcapAuthor> getAuthors() {
		return authors;
	}
	
	public boolean getPoweredByThinklab() {
		return poweredByThinklab;
	}
	
	public String getAppUrl() {
		return "/" + id + ".app";
	}
	
	public String getWebspaceUrl(ISession session) {
		return "/" + id + "/" + session.getSessionWorkspace();
	}
	
	public URL getUrl() {
		
		if (appURL == null)
			throw new ThinklabRuntimeException("internal: application URL requested before application is published");
		return appURL;
	}
	
	public String getLocalUrl() {
		return id + ".app?page=main";
	}

	/*
	 * Called when the servlet is requested to initiate an application from
	 * an HTTP request that ends in .app
	 * copy files to web space; define redirection URL
	 */
	public void publish(File serverWebSpace) throws ThinklabException {

		if (published)
			return;
		
		/*
		 * first of all, make sure the registering plugin is activated
		 */
		try {
			ThinkWeb.get().getPluginManager().activatePlugin(
					registeringPlugin.getDescriptor().getId());
		} catch (PluginLifecycleException e) {
			throw new ThinklabException(e);
		}
		
		/*
		 * override any application-specific configurations from plugin 
		 * user config.
		 */
		if (registeringPlugin instanceof ThinklabPlugin)
			for (Entry<Object, Object> eset : 
				((ThinklabPlugin)registeringPlugin).getProperties().entrySet()) {
			
				String pname = eset.getKey().toString();
				String pvalu = eset.getValue().toString();
			
				if (pname.startsWith(this.id + ".")) {
				
					if (pname.equals(this.id + "." + PLUGINS_PROPERTY))	{
						setAdditionalPlugins(pvalu);
					} else 	if (pname.equals(this.id + "." + BANNER_PROPERTY))	{
						setBanner(pvalu);
					} else 	if (pname.equals(this.id + "." + STYLE_PROPERTY))	{
						setStyle(pvalu);
					} else 	if (pname.equals(this.id + "." + SKIN_PROPERTY))	{
						setSkin(pvalu);
					}
				}
		}
		
		/*
		 * look for location dir in registering plugin
		 */
		URL uloc = registeringPlugin.getManager().getPluginClassLoader(
					registeringPlugin.getDescriptor()).
						getResource(location);
		
		File fsource = null;
		if (uloc != null)
			 fsource = FileOps.getFileFromUrl(uloc);
		
		if (fsource == null || !fsource.exists() || !fsource.isDirectory()) {
			throw new ThinklabException(
					"location " + location + " (" + fsource + ") for application " +
					id + " absent or invisible in plugin " +
					registeringPlugin.getDescriptor().getId() +
					", or is not a directory");
		}
		
		File fdest = 
			new File(
					ThinkWeb.get().getWebSpace() + 
					"/" + id);
		
		this.webPath = fdest;
							
		try {
			
			FileOps.copyFilesCached(fsource, fdest, skipped);
			
			/*
			 * FIXME
			 * TODO 
			 * symlink fdest to fdest + id + tc. That requires nio and the Path class, 
			 * standard in Java 7. For now just copy all resources we believe useful.
			 */
			FileOps.copyFilesCached(
					new File(ThinkWeb.get().getWebSpace()  + "/images"),
					new File(fdest  + "/tc/images"));
			
		
		} catch (IOException e) {
			throw new ThinklabException(e);
		}
		
		try {
			appURL = new URL(
						ThinkWeb.get().getBaseUrl() + 
						"/" + id + 
						"/" + entryPoint);
		} catch (MalformedURLException e) {
			throw new ThinklabException(e);
		}
		
		published = true;
			
	}

	public ThinklabWebModel createModelInstance() throws ThinklabException {

		ThinklabWebModel ret = null;
		
		if (modelClass != null && registeringPlugin != null && registeringPlugin instanceof ThinklabWebPlugin) {
			ret = (ThinklabWebModel) ((ThinklabWebPlugin)registeringPlugin).createInstance(modelClass);
		}
		return ret;
	}

	public File getWebPath(ISession session) {
	
		File ret = webPath;
		
		if (session != null) {
			ret = new File(ret + File.separator + session.getSessionWorkspace());
			ret.mkdir();
		}
		
		return ret;
	}
	
	/*
	 * return a pair <file, url> for a new resource file in the web space of the current application
	 * and session
	 */
	public synchronized Pair<String, String> getNewResourceUrl(String extension, ISession session) throws ThinklabIOException {
		File folder = getWebPath(session);
		try {
			folder = File.createTempFile("aimg", extension, folder);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		String url  = getWebspaceUrl(session) + 
					"/" + 
					MiscUtilities.getFileName(folder.toString());
		return new Pair<String, String>(folder.toString(), url);
	}
	
	public synchronized Pair<String, String> getNewDirectoryUrl(ISession session) throws ThinklabIOException {
		
		String dir = "aimg" + MiscUtilities.getDateSuffix();
		File folder = new File(getWebPath(session) + File.separator + dir);
		folder.mkdirs();
		String url = getWebspaceUrl(session) + "/" + dir;
		return new Pair<String, String>(folder.toString(), url);
	}

	public String toString() {
		return "[" + getId() + "]";
	}
	
	/**
	 * Number of users currently connected.
	 * @return
	 */
	public synchronized int getCurrentUserCount() {
		return currentUserCount;
	}
	
}
