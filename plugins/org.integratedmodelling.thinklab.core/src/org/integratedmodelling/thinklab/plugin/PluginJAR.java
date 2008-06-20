/**
 * PluginJAR.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.plugin;

/*
 * PluginJAR.java - Controls JAR loading and unloading
 *
 * Copyright (C) 1999, 2004 Slava Pestov
 * Modified 2006 Ferdinando Villa for use within JIMT package
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IPlugin;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.XMLDocument;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Loads and unloads plugins.<p>
 *
 * <h3>JAR file contents</h3>
 *
 * When loading a plugin, Thinklab looks for the following resources:
 *
 * <ul>
 * <li>A file named <code>plugin.xml</code> defining what the plugin provides.
 * Only one such file per plugin is allowed.</li>
 * 
 * A plugin can supply any number of property files. The property file called
 * [PLUGIN_ID].properties is copied to the user .thinklab directory, and 
 * properties are loaded from it so they can override plugin defaults. Any
 * other property file contained in the jar will not be exposed to the user, so
 * it is good for properties that must remain in control of the plugin 
 * developer.
 * 
 * For a plugin to actually do something once it is resident in memory,
 * it must contain a class whose name ends with <code>Plugin</code>.
 * This class, known as the <i>plugin core class</i> must extend
 * {@link Plugin} and define a few required properties, otherwise it is
 * ignored.
 *
 * @author Ferdinando Villa
 * @version $Id$
 */
public class PluginJAR
{
	
	private String path;
	private File file;

	private ZipFile zipFile;

	public ArrayList<String> ontologies = new ArrayList<String>();
	public ArrayList<String> dependencies = new ArrayList<String>();
	public ArrayList<String> propertyFiles = new ArrayList<String>();

	private String id;
	private String version;
	private String description;
	private String shortDescription;
	private String jimtVersion;
	private long modTime;
	private ArrayList<String> authors = new ArrayList<String>();
	private String date;
	private ArrayList<ZipEntry> resources = new ArrayList<ZipEntry>();
	private ArrayList<File> jarfiles = new ArrayList<File>();
	private File loadDir;
	private File libDir;
	private String mainClassName;
	private ArrayList<Node> configNodes = new ArrayList<Node>();
	
	/**
	 * Returns the full path name of this plugin's JAR file.
	 */
	public String getPath()
	{
		return path;
	} 

	/**
	 * Returns a file pointing to the plugin JAR.
	 */
	public File getFile()
	{
		return file;
	} 

	public void notifyConfigurationNodes(IPlugin plugin) {
		for (Node n : configNodes) 
			plugin.notifyConfigurationNode(n);
	}

	public void notifyResources(Plugin p) throws ThinklabException {
		for (ZipEntry z : resources)
			p.addResource(z.getName(), z.getTime(), z.getSize());
	}

	public void loadOntologies(IPlugin plug, IKnowledgeRepository kr, File cacheDir) throws ThinklabException {
		
		// first extract them all 
		List<URL> onto = new ArrayList<URL>();
		
		String excluded = plug.getProperties().getProperty(KnowledgeManager.EXCLUDE_ONTOLOGY_PROPERTY, "");
		
		for (String z : ontologies) {

			String obase = MiscUtilities.getFileName(z);
			
			if (excluded.contains(obase))
				continue;
			/**
			 * FIXME 
			 * Ontologies are saved in a flat folder. It should not be necessary but Protege has problems
			 * finding dependencies otherwise. It should not be a problem anywhere, so this is the fastest
			 * solution.
			 */
			URL uu = saveResourceCachedWithoutPath(z, cacheDir);
			
			onto.add(uu);
		}
		
		for (URL uu : onto) {
			kr.refreshOntology(uu, MiscUtilities.getNameFromURL(uu.toString()));
		}
	}
	
	/**
	 * Returns the plugin's JAR file, opening it if necessary.
	 */
	public synchronized ZipFile getZipFile() throws IOException
	{
		if(zipFile == null)
		{
			zipFile = new ZipFile(path);
		}
		return zipFile;
	} 


	PluginJAR(File file, File loadDir, File libDir) throws ThinklabException
	{		
		this.path = file.getPath();
		this.file = file;
		this.loadDir = loadDir;
		this.libDir = libDir;

		init();
	} 

	/**
	 * Extract all resources for notification, recognizing default ones.
	 * Extract .class files in plugin load area, under plugin ID
	 * Extract all .jar files in a lib/ subdirectory into common lib/ area in plugin load area
	 * Remember class name for main plugin class
	 * 
	 * @throws IOException
	 * @throws ThinklabIOException 
	 */
	public void init() throws ThinklabException {
		
		ArrayList<ZipEntry> classes  = new ArrayList<ZipEntry>();
		ArrayList<String> classNames = new ArrayList<String>();
		
		ZipFile zipFile = null;
		try {
			zipFile = getZipFile();
		} catch (IOException e1) {
			throw new ThinklabIOException(e1);
		}

		modTime = file.lastModified();

		Enumeration<?> entries = zipFile.entries();
		while(entries.hasMoreElements())
		{
			ZipEntry entry = (ZipEntry)entries.nextElement();
			String name = entry.getName();
			String lname = name.toLowerCase();
			
			if(lname.equals("plugin.xml"))
			{
				// read the plugin.xml file, establish dependencies and info for PluginRegistry
				try {
					readManifest(zipFile.getInputStream(entry));
				} catch (Exception e) {
					throw new ThinklabIOException(id + ": " + e.getMessage());
				}
				
			} else if(name.endsWith(".owl")) {

				// add ontology
				resources.add(entry);
				ontologies.add(name);
				
			} else if (name.endsWith(".properties")) {
				
				resources.add(entry);
				propertyFiles.add(name);
				
			} else if(name.endsWith(".class"))	{

				String className = MiscUtilities.fileToClass(name);
				if(className.endsWith("Plugin"))
					mainClassName = className;

				classes.add(entry);
				classNames.add(name);

			} else if (name.endsWith(".jar") && name.contains("lib/")) {
				// we want to save this one in the main libdir, without path,
				// and add it to the classloader.
				PluginRegistry.get().getClassLoader().addJAR(
						saveResourceWithoutPath(entry, name, libDir));
			} else if (name.endsWith("/") || 
					   name.equals("MANIFEST")||
					   name.endsWith("MANIFEST.MF") ||
					   name.endsWith("package.html")) {
				// ignore dirs and manifest
				
			} else {
				resources.add(entry);
			}
		}
		
		/* save all class files in their dir under the global classpath */
		File classdir = loadDir;
		classdir.mkdir();
		
		for (int i = 0; i < classes.size(); i++) {
			jarfiles.add(saveResource(classes.get(i), classNames.get(i), classdir));
		}
	} 

	private void readManifest(InputStream resource) throws SAXException, IOException {
		
		XMLDocument doc = new XMLDocument(resource);
		
		for (Node n = doc.root().getFirstChild(); n != null; n = n.getNextSibling()) {
			
			if (n.getNodeName().equals("id")) 
				id = n.getTextContent();
			else if (n.getNodeName().equals("version"))
				version = n.getTextContent();
			else if (n.getNodeName().equals("short-description"))
				shortDescription = n.getTextContent();
			else if (n.getNodeName().equals("description"))
				description = n.getTextContent();
			else if (n.getNodeName().equals("jimt-version") || n.getNodeName().equals("thinklab-version"))
				jimtVersion = n.getTextContent();
			else if (n.getNodeName().equals("author"))
				authors.add(n.getTextContent());
			else if (n.getNodeName().equals("date"))
				date = n.getTextContent();
			else if (n.getNodeName().equals("depends-on")) {
				for (Node nn = n.getFirstChild(); nn != null; nn = nn.getNextSibling()) {
					if (nn.getNodeName().equals("plugin")) {
						dependencies.add(nn.getTextContent());
					}
				}
			} else {
				notifyConfigurationNode(n);
			}
		}
	}

	private void notifyConfigurationNode(Node n) {
		configNodes.add(n);
		
	}

	public String getID() {
		return id;
	}

	public String getDate() {
		return date;
	}

	public ArrayList<String> getDependencies() {
		return dependencies;
	}

	public String getDescription() {
		return description;
	}

	public String getJimtVersion() {
		return jimtVersion;
	}

	public long getModTime() {
		return modTime;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public String getVersion() {
		return version;
	}

	public InputStream retrieveResource(String name) throws ThinklabIOException {

		for (ZipEntry z : resources) {
			if (z.getName().equals(name)) {
				try {
					return zipFile.getInputStream(z);
				} catch (IOException e) {
					throw new ThinklabIOException(e);
				}
			}
		}
		return null;
	}
	
	public long getResourceTime(String name) {
		for (ZipEntry z : resources) {
			if (z.getName().equals(name)) {
				return z.getTime();
			}
		}
		return -1;
	}
	
//	
	/**
	 * Save named resource from zip entry in named dir.
	 */
	public File saveResource(ZipEntry zz, String name, File dir) throws ThinklabIOException {
		
		File f = new File(dir.toString() + "/" + name);
		
		// check cache
		if (!f.exists() || f.lastModified() < zz.getTime())
			try {
				MiscUtilities.writeToFile(f.toString(), zipFile.getInputStream(zz), true);
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
				
		return f;
	}
	
	/**
	 * Save named resource from zip entry in named dir, ignoring any paths in zip entry.
	 */
	public URL saveResourceWithoutPath(ZipEntry zz, String name, File dir) throws ThinklabIOException {
		
		name = MiscUtilities.getFileName(name);
		
		File f = new File(dir.toString() + "/" + name);
		
		// check cache
		if (!f.exists() || f.lastModified() < zz.getTime())
			try {
				MiscUtilities.writeToFile(f.toString(), zipFile.getInputStream(zz), true);
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
				
		return MiscUtilities.getURLForResource(f.toString());
	}
	
	public URL saveResourceCached(String name, File cacheDir) throws ThinklabException {
		
		File f = new File(cacheDir.toString() + "/" + id + "/" + name);
		ZipEntry zz = null;
		
		for (ZipEntry z : resources) {
			if (z.getName().equals(name)) {
				zz = z;
				break;
			}
		}

		if (zz == null)
			throw new ThinklabIOException("resource " + name + " can't be found in plugin " + id);

		// check cache
		if (!f.exists() || f.lastModified() < zz.getTime())
			try {
				MiscUtilities.writeToFile(f.toString(), zipFile.getInputStream(zz), true);
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		
		URL ret = null;

		try {
			ret = f.toURL();
		} catch (MalformedURLException e) {
			// shouldn't happen, shut up
			throw new ThinklabException(e);
		}
		
		return ret;
		
		
	}

	public URL saveResourceCachedWithoutPath(String name, File cacheDir) throws ThinklabException {
		
		String fname = MiscUtilities.getFileName(name);
		
		File f = new File(cacheDir.toString() + "/" + id + "/" + fname);
		ZipEntry zz = null;
		
		for (ZipEntry z : resources) {
			if (z.getName().equals(name)) {
				zz = z;
				break;
			}
		}

		if (zz == null)
			throw new ThinklabIOException("resource " + name + " can't be found in plugin " + id);

		// check cache
		if (!f.exists() || f.lastModified() < zz.getTime())
			try {
				MiscUtilities.writeToFile(f.toString(), zipFile.getInputStream(zz), true);
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		
		URL ret = null;

		try {
			ret = f.toURL();
		} catch (MalformedURLException e) {
			// shouldn't happen, shut up
			throw new ThinklabException(e);
		}
		
		return ret;
		
		
	}

	
	public Collection<String> getAuthors() {
		return authors;
	}

	public void loadProperties(IPlugin plug) throws ThinklabException {
		
		for (String z : propertyFiles) {
			try {				
				if (z.contains(id + ".properties")) {
					
					/**
					 * Plugin properties in user dir override property file in jar; template file is produced
					 * if properties are in jar but not in user dir.
					 */
					File pf = plug.getPropertiesFilePath();
					
					if (!pf.exists()) {
						MiscUtilities.writeToFile(pf.toString(), retrieveResource(z), false);
					}
					
					plug.getProperties().load(new FileInputStream(pf));
					
				} else {
					
					/**
					 * All property files in jar that are not named <PLUGIN>.properties are loaded from jar 
					 * as they are; good for stable properties that should not be modified except by the plugin
					 * developer.
					 */
					plug.getProperties().load(retrieveResource(z));
				}
			} catch (Exception e) {
				throw new ThinklabException(e);
			}
		}
		
		String blk = plug.getProperties().getProperty(KnowledgeManager.IGNORE_PROPERTY_PROPERTY);

		if (blk != null) {
			String[] bk = blk.trim().split(",");
			for (String s : bk) {
				KnowledgeManager.get().blacklistProperty(s);
			}
		}

		blk = plug.getProperties().getProperty(KnowledgeManager.IGNORE_CONCEPT_PROPERTY);

		if (blk != null) {
			String[] bk = blk.trim().split(",");
			for (String s : bk) {
				KnowledgeManager.get().blacklistConcept(s);
			}
		}

	}

	public String getMainClassName() {
		return mainClassName;
	}
	
	public String toString(){
		return this.mainClassName;
	}

	public Collection<File> getEmbeddedJarFiles() {
		return jarfiles;
	}
}
