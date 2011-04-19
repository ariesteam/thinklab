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
package org.integratedmodelling.thinklab.http.application;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.http.ThinkWeb;
import org.integratedmodelling.thinklab.http.ThinklabWebModel;
import org.integratedmodelling.thinklab.http.ThinklabWebSession;
import org.integratedmodelling.thinklab.http.extensions.WebApplication;
import org.integratedmodelling.thinklab.http.utils.FileOps;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.java.plugin.Plugin;
import org.mortbay.jetty.Server;

/**
 * Represents metadata and properties connected to a specific Thinkcap application.
 * Applications are declared by plug-ins using the plugin.xml file, except the
 * main thinkcap.xml application which is declared in WEB-INF/thinkcap.xml.
 * 
 * @author Ferdinando Villa
 * @see WEB-INF/thinkcap.xml for syntax example.
 */
public class ThinklabWebApplication {

	private ThinklabPlugin registeringPlugin = null;
	
	volatile int currentUserCount = 0;
	
	
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
	
	/*
	 * the server that runs the app. The new paradigm is one server, one app, no cross-contaminations of apps.
	 */
	Server _server = null;
		
	/**
	 * Application descriptor; properties are read from the plugin manifest through the
	 * thinklab-application extension point.
	 */
	 Properties properties;
	
	private ArrayList<ThinkcapAuthor> authors =
		new ArrayList<ThinkcapAuthor>();

	private File webPath;

	private String banner;
	
	public class ThinkcapAuthor {
		
		String name;
		String url;
		String logo;

		public String getName() { return name; }
		public String getUrl()  { return url; }
		public String getLogo() { return logo; }
	}
		
	public void initialize(ThinklabPlugin plugin, WebApplication wdesc) {

		registeringPlugin = plugin;	
	
		this.id = wdesc.name();
		this.location = wdesc.webLocation();
		this.entryPoint = wdesc.entryPoint();
		this.shortDescription = wdesc.shortDescription();
		this.longDescription = wdesc.longDescription();
		this.runningHead = wdesc.runningHead();
		this.copyright = wdesc.copyright();
		this.logoSmall = wdesc.logoSmall();
		this.logoLarge = wdesc.logoLarge();
		this.banner = wdesc.banner();
		this.modelClass = wdesc.modelClass();
						
		/*
		 * TODO authentication
		 */
	}
	
	public synchronized void notifyUserConnected(ThinklabWebSession session) {
		this.currentUserCount ++;
	}
	
	public synchronized void notifyUserDisconnected(ThinklabWebSession session) {
		this.currentUserCount --;
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
	public void publish(File serverWebSpace, Server server) throws ThinklabException {

		if (published)
			return;
		
		if (location == null || location.isEmpty()) {
			location = id;
		}
		
		this._server = server;
		
		/*
		 * look for location dir in registering plugin
		 */
		File fsource = 
			new File(
				registeringPlugin.getLoadDirectory() + 
				File.separator + 
				"web" + 
				File.separator + location);
		
		if (fsource == null || !fsource.exists() || !fsource.isDirectory()) {
			throw new ThinklabException(
					"location " + location + " (" + fsource + ") for application " +
					id + " absent or invisible in plugin " +
					registeringPlugin.getDescriptor().getId() +
					", or is not a directory");
		}
		
		File fdest = 
			new File(serverWebSpace + File.separator + id);
		
		this.webPath = fdest;
		
		// TODO create symlink, deleting anything already present, as soon as
		// we can switch to JDK 7		
		try {
			FileOps.copyFilesCached(fsource, fdest, null);		
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
		
		if (modelClass != null && registeringPlugin != null) {
			ret = (ThinklabWebModel) ((ThinklabPlugin)registeringPlugin).createInstance(modelClass);
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
