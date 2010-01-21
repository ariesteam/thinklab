/**
 * Plugin.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.PersistenceManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.application.ApplicationDescriptor;
import org.integratedmodelling.thinklab.application.ApplicationManager;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.extensions.Interpreter;
import org.integratedmodelling.thinklab.extensions.KBoxHandler;
import org.integratedmodelling.thinklab.extensions.KnowledgeLoader;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;
import org.integratedmodelling.thinklab.interfaces.annotations.PersistentObject;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ITask;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.interfaces.storage.IPersistentObject;
import org.integratedmodelling.thinklab.interpreter.InterpreterManager;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.Version;
import org.java.plugin.registry.Extension.Parameter;


/**
 * A specialized JPF plugin to support extension of the knowledge manager.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class ThinklabPlugin extends Plugin
{
	HashMap<String, URL> resources = new HashMap<String, URL>();
	Properties properties = new Properties();
	File propertySource = null;
	
	private ArrayList<String> ontologies = new ArrayList<String>();
	
	private File dataFolder;
	private File confFolder;
	private File plugFolder;
	private File loadFolder;
	private HashSet<String> _bindingsLoaded = new HashSet<String>();
	
	/**
	 * ALWAYS call this one to ensure that all the necessary plugins are loaded, even if
	 * dependencies are properly declared.
	 * 
	 * @param pluginId
	 * @throws ThinklabPluginException
	 */
	protected void requirePlugin(String pluginId) throws ThinklabPluginException {

		try {
			getManager().activatePlugin(pluginId);
		} catch (PluginLifecycleException e) {
			throw new ThinklabPluginException(e);
		}
	}
	
	/*
	 * intercepts the beginning of doStart()
	 */
	protected void preStart() throws Exception {
		
	}
	
	public ClassLoader getClassLoader() {
		return getManager().getPluginClassLoader(getDescriptor());
	}
	
	public static ClassLoader getClassLoaderFor(Object o) {
		return ((ThinklabPlugin)(Thinklab.get().getManager().getPluginFor(o))).getClassLoader();
	}
	
	public static ThinklabPlugin getPluginFor(Object o) {
		return (ThinklabPlugin)(Thinklab.get().getManager().getPluginFor(o));
	}
	
	public Log logger() {
		return log;
	}
	
	/**
	 * Demand plugin-specific initialization to this callback; 
	 * we intercept doStart
	 * @param km TODO
	 * @throws ThinklabException 
	 */
	abstract protected void load(KnowledgeManager km) throws ThinklabException;
	
	abstract protected void unload() throws ThinklabException;
	
	protected static Plugin getPlugin(String id) {
		
		Plugin ret = null;
		try {
			ret = KnowledgeManager.get().getPluginManager().getPlugin(id);
		} catch (Exception e) {
			// TODO shouldn't really happen, but...
			
		}
		return ret;
	}
	
	/**
	 * Any extensions other than the ones handled by default should be handled here.
	 * @throws ThinklabException 
	 */
	protected void loadExtensions() throws Exception {
		
	}
	
	protected void unloadExtensions() throws Exception {
		
	}
	protected String getPluginBaseName() {
		String[] sp = getDescriptor().getId().split("\\.");
		return sp[sp.length - 1];
	}
	
	@Override
	protected final void doStart() throws Exception {
		
		loadConfiguration();

		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.prePluginLoaded(this);
		}
		
		preStart();
		
		loadOntologies();
		loadLiteralValidators();
		loadKBoxHandlers();
		loadKnowledgeImporters();
		loadKnowledgeLoaders();
		loadLanguageInterpreters();
		loadCommandHandlers();
		loadCommands();
		loadInstanceImplementationConstructors();
		loadPersistentClasses();
		loadSessionListeners();
		loadKboxes();
		loadApplications();

		loadLanguageBindings();
		
		load(KnowledgeManager.get());

		loadExtensions();
		
		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.onPluginLoaded(this);
		}
	}

	/**
	 * Load bindings for all languages. Because this must be called by hand (see reason in
	 * loadLanguageBindings(String), typically you want to use the language-specific version,
	 * but here goes.
	 * 
	 * @throws ThinklabException
	 */
	public synchronized void loadLanguageBindings() throws ThinklabException {
		
		for (Extension ext : getOwnThinklabExtensions("language-binding")) {

			String language = getParameter(ext, "language");
			loadLanguageBindings(language);
		}
	}
	
	/**
	 * Load declared bindings for given language. Must be called by hand and must make sure
	 * that if the bindings refer to plugin classes, the plugin that calls the interpreter has 
	 * declared its dependencies with reverse-lookup=true, otherwise the bindings won't see the 
	 * Java classes in their own plugin. 
	 * 
	 * @param language
	 * @throws ThinklabException
	 */
	public void loadLanguageBindings(String language) throws ThinklabException {
		
		// may happen more than once if bound to external language, so synchronize
		if (this._bindingsLoaded.contains(language) ) {
			return;
		}

		_bindingsLoaded.add(language);
		
		for (Extension ext : getOwnThinklabExtensions("language-binding")) {

			String lang = getParameter(ext, "language");
			String[] tpacks = getParameters(ext, "task-package");
			String[] resource = getParameters(ext, "resource");
			
			if (!language.equals(lang))
				continue;
			
			Interpreter intp = InterpreterManager.get().newInterpreter(language);
			
			/*
			 * automatically declare tasks included in package if supplied. These can't possibly
			 * use the language bindings, so do it first.
			 */
			for (String pk : tpacks)
				declareTasks(pk, intp);
			
			for (String r : resource) {
				
				logger().info("loading " + language + " binding file: " + r);
				intp.loadBindings(getResourceURL(r), getClassLoader());
			}
		}
	}

	private void declareTasks(String taskPackage, Interpreter intp) throws ThinklabException {
		
		for (Class<?> cls : MiscUtilities.findSubclasses(ITask.class, taskPackage, getClassLoader())) {
			intp.defineTask(cls, getClassLoader());
		}
	}
	
	public ClassLoader swapClassloader() {
		ClassLoader clsl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClassLoader());
		return clsl;
	}
	
	public void resetClassLoader(ClassLoader clsl) {
		Thread.currentThread().setContextClassLoader(clsl);
	}
	
	private void loadKboxes() throws ThinklabException {

		for (Extension ext : getOwnThinklabExtensions("kbox")) {
			
			String protocol = getParameter(ext, "protocol");
			String url = getParameter(ext, "url");
			String id = getParameter(ext, "id");
			String schema  = getParameter(ext, "schema");
//			// TODO modernize handling of metadata
//			String mschema = getParameter(ext, "metadata-schema");
			
			File pfile = new File(
					getScratchPath() + File.separator + id + ".kbox");
			
			boolean isnew = !pfile.exists();
			
			Properties kpro = new Properties();
			kpro.setProperty(IKBox.KBOX_PROTOCOL_PROPERTY, protocol);
			kpro.setProperty(IKBox.KBOX_URI_PROPERTY, url);
			
			if (schema != null) {
				kpro.setProperty(IKBox.KBOX_SCHEMA_PROPERTY, schema);
			}

			// TODO should list pairs of field, concept to store as kbox.metadata.fieldname=concept
//			if (mschema != null ) {
//				kpro.setProperty(IKBox.KBOX_METADATA_SCHEMA_PROPERTY, mschema);
//			}
			
			try {
				kpro.store(new FileOutputStream(pfile), 
						"Generated by thinklab based on plugin manifest - do not modify");
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}

			IKBox kbox = null;
			
			try {
				kbox = 
					KBoxManager.get().requireGlobalKBox(pfile.toURI().toURL().toString());
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}

			String content = getParameter(ext, "initial-content");

			if (content != null && isnew) {
				
				URL curl = getResourceURL(content);
				
				if (curl == null) {
					throw new ThinklabIOException(
							"resource " + content + 
							" named for initial contents of kbox " + id + 
							"could not be resolved");
				}
				
				/*
				 * load content into kbox
				 */
				Session session = new Session();
				Collection<IInstance> objs = session.loadObjects(curl);
				
				HashMap<String, String> references = new HashMap<String, String>();

				for (IInstance obj : objs) {
					kbox.storeObject(obj, null, null, session, references);
				}
			}
		}
	}

	public File getLoadDirectory() {
		
		if (loadFolder == null) {
			String lf = new File(getDescriptor().getLocation().getFile()).getAbsolutePath();
			loadFolder = new File(Escape.fromURL(MiscUtilities.getPath(lf).toString()));
		}

		return loadFolder;
	}
	
	protected void loadConfiguration() throws ThinklabIOException {
		
		loadFolder = getLoadDirectory();

        plugFolder = LocalConfiguration.getDataDirectory(getDescriptor().getId());
        confFolder = new File(plugFolder + File.separator + "config");
        dataFolder = new File(plugFolder + File.separator + "data");
	
       /*
        * make sure we have all paths
        */
       if (
    		   (!plugFolder.isDirectory() && !plugFolder.mkdirs()) || 
    		   (!confFolder.isDirectory() && !confFolder.mkdirs()) || 
    		   (!dataFolder.isDirectory() && !dataFolder.mkdirs()))
    	   throw new ThinklabIOException("problem writing to plugin directory: " + plugFolder);
       
		/*
		 * check if plugin contains a <pluginid.properties> file
		 */
       String configFile = getPluginBaseName() + ".properties";
       File pfile = new File(confFolder + File.separator + configFile);
       
       if (!pfile.exists()) {
    	   
    	   /*
    	    * copy stock properties if existing
    	    */
    	   URL sprop = getResourceURL(configFile);
    	   if (sprop != null)
    		   CopyURL.copy(sprop, pfile);
    	   
       } 
       
       if (pfile.exists()) {
    	   try {
    		propertySource = pfile;
			properties.load(new FileInputStream(pfile));
			logger().info("plugin properties loaded from " + pfile);
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
       }
		
	}

	public void writeConfiguration() throws ThinklabIOException {
	
		if (propertySource != null) {
			FileOutputStream fout;
			try {
				fout = new FileOutputStream(propertySource);
				properties.store(fout, "written by thinklab " + new Date());
				fout.close();
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		}
	}
	
	/**
	 * Return all the extensions in this plugin that extend the given Thinklab extension 
	 * point (declared in the core plugin).
	 * 
	 * @param extensionPoint
	 * @return
	 */
	protected Collection<Extension> getOwnThinklabExtensions(String extensionPoint) {
		
		return getOwnExtensions(Thinklab.PLUGIN_ID, extensionPoint);
	}

	/**
	 * Return all the extension in this plugin that extend an extension point declared
	 * in the passed plugin with the passed name.
	 * 
	 * @param extendedPlugin
	 * @param extensionPoint
	 * @return
	 */
	protected Collection<Extension> getOwnExtensions(String extendedPlugin, String extensionPoint) {
		
		ArrayList<Extension> ret = new ArrayList<Extension>();
		
		ExtensionPoint toolExtPoint = 
			getManager().getRegistry().getExtensionPoint(extendedPlugin, extensionPoint);

		for (Iterator<Extension> it =  toolExtPoint.getConnectedExtensions().iterator(); it.hasNext(); ) {
			Extension ext = it.next();
			if (ext.getDeclaringPluginDescriptor().getId().equals(getDescriptor().getId())) {
				ret.add(ext);
			}
		}
		
		return ret;
	}
	
	/**
	 * Return all the extension in any plugins that extend an extension point declared
	 * in the passed plugin with the passed name.
	 * 
	 * @param extendedPlugin
	 * @param extensionPoint
	 * @return
	 */
	protected Collection<Extension> getAllExtensions(String extendedPlugin, String extensionPoint) {
		
		ArrayList<Extension> ret = new ArrayList<Extension>();
		
		ExtensionPoint toolExtPoint = 
			getManager().getRegistry().getExtensionPoint(extendedPlugin, extensionPoint);

		for (Iterator<Extension> it =  toolExtPoint.getConnectedExtensions().iterator(); it.hasNext(); ) {
			Extension ext = it.next();
				ret.add(ext);
		}
		
		return ret;
	}

	/**
	 * Return all the extension in a specified plugin that extend an extension point declared
	 * in the passed plugin with the passed name.
	 * 
	 * @param pluginId the plugin that contains the extensions
	 * @param extendedPlugin the plugin that contains the extension point
	 * @param extensionPoint the extension point id
	 * @return
	 */
	protected Collection<Extension> getPluginExtensions(String pluginId, String extendedPlugin, String extensionPoint) {
		
		ArrayList<Extension> ret = new ArrayList<Extension>();
		
		ExtensionPoint toolExtPoint = 
			getManager().getRegistry().getExtensionPoint(extendedPlugin, extensionPoint);

		for (Iterator<Extension> it =  toolExtPoint.getConnectedExtensions().iterator(); it.hasNext(); ) {
			Extension ext = it.next();
			if (ext.getDeclaringPluginDescriptor().getId().equals(pluginId)) {
				ret.add(ext);
			}
		}
		
		return ret;
	}

	protected void loadInstanceImplementationConstructors() throws ThinklabPluginException {
		
		String ipack = this.getClass().getPackage().getName() + ".implementations";
		
		for (Class<?> cls : MiscUtilities.findSubclasses(IInstanceImplementation.class, ipack, getClassLoader())) {	
			
			String concept = null;

			/*
			 * lookup annotation, ensure we can use the class
			 */
			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
				continue;
			
			/*
			 * lookup implemented concept
			 */
			for (Annotation annotation : cls.getAnnotations()) {
				if (annotation instanceof InstanceImplementation) {
					concept = ((InstanceImplementation)annotation).concept();
				}
			}
			
			if (concept != null) {

				logger().info("registering class " + cls + " as implementation for instances of type " + concept);				
				KnowledgeManager.get().registerInstanceImplementationClass(concept, cls);
			}
		}
	}
	
	protected void loadPersistentClasses() throws ThinklabPluginException {
		
		String ipack = this.getClass().getPackage().getName() + ".implementations";
		
		for (Class<?> cls : MiscUtilities.findSubclasses(IPersistentObject.class, ipack, getClassLoader())) {	
			
			String ext = null;

			/*
			 * lookup annotation, ensure we can use the class
			 */
			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
				continue;
			
			/*
			 * lookup implemented concept
			 */
			for (Annotation annotation : cls.getAnnotations()) {
				if (annotation instanceof PersistentObject) {
					ext = ((PersistentObject)annotation).extension();
					if (ext.equals("__NOEXT__"))
						ext = null;
					break;
				}
			}

			PersistenceManager.get().registerSerializableClass(cls, ext);
		}
	}

	public URL getResourceURL(String resource) throws ThinklabIOException 	{
		return getResourceURL(resource, null);
	}
	
	/**
	 * Retrieve an URL for the named resource: if the resource string represents a URL, return the
	 * url constructed from it; otherwise, check if the resource string represents an existing
	 * file path. If so, create a file url from it and return it. Otherwise, construct a URL from
	 * the plugin path and the resource name and return that.
	 * 
	 * @param resource
	 * @return
	 * @throws ThinklabIOException 
	 */
	public URL getResourceURL(String resource, ThinklabPlugin plugin) throws ThinklabIOException 	{

		URL ret = null;
		
		try {
			
			File f = new File(resource);
			
			if (f.exists()) {
				ret = f.toURI().toURL();
			} else if (resource.contains("://")) {
				ret = new URL(resource);
			} else {			
				ret = 
					getManager().
						getPluginClassLoader(plugin == null ? getDescriptor() : plugin.getDescriptor()).
							getResource(resource);
			}
		} catch (MalformedURLException e) {
			throw new ThinklabIOException(e);
		}
		
		return ret;
	}
	
	protected void loadOntologies() throws ThinklabException {
	
		for (Extension ext : getOwnThinklabExtensions("ontology")) {

			String url = ext.getParameter("url").valueAsString();
			String csp = ext.getParameter("concept-space").valueAsString();

			KnowledgeManager.get().getKnowledgeRepository().refreshOntology(getResourceURL(url), csp, false);
			
			ontologies.add(csp);
		}
		
	}

	protected void loadLiteralValidators() throws ThinklabException {
		
		String ipack = this.getClass().getPackage().getName() + ".literals";
		
		for (Class<?> cls : MiscUtilities.findSubclasses(ParsedLiteralValue.class, ipack, getClassLoader())) {	
			
			String concept = null;
			String xsd = null;

			/*
			 * lookup annotation, ensure we can use the class
			 */
			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
				continue;
			
			/*
			 * lookup implemented concept
			 */
			for (Annotation annotation : cls.getAnnotations()) {
				if (annotation instanceof LiteralImplementation) {
					concept = ((LiteralImplementation)annotation).concept();
					xsd = ((LiteralImplementation)annotation).xsd();
				}
			}
			
			if (concept != null) {
				
				logger().info("registering class " + cls + " as implementation for literals of type " + concept);
				
				KnowledgeManager.get().registerLiteralImplementationClass(concept, cls);
				if (!xsd.equals(""))
					
					logger().info("registering XSD type mapping: " + xsd + " -> " + concept);
					KnowledgeManager.get().registerXSDTypeMapping(xsd, concept);
			}
			
		}

	}

	protected void loadCommandHandlers() throws ThinklabException {
		
		String ipack = this.getClass().getPackage().getName() + ".commands";
		
		for (Class<?> cls : MiscUtilities.findSubclasses(ICommandHandler.class, ipack, getClassLoader())) {	
			
			/*
			 * lookup annotation, ensure we can use the class
			 */
			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
				continue;
			
			/*
			 * lookup implemented concept
			 */
			for (Annotation annotation : cls.getAnnotations()) {
				if (annotation instanceof ThinklabCommand) {
					
					String name = ((ThinklabCommand)annotation).name();
					String description = ((ThinklabCommand)annotation).description();
					
					CommandDeclaration declaration = new CommandDeclaration(name, description);
					
					String retType = ((ThinklabCommand)annotation).returnType();
					
					if (!retType.equals(""))
						declaration.setReturnType(KnowledgeManager.get().requireConcept(retType));
					
					String[] aNames = ((ThinklabCommand)annotation).argumentNames().split(",");
					String[] aTypes = ((ThinklabCommand)annotation).argumentTypes().split(",");
					String[] aDesc =  ((ThinklabCommand)annotation).argumentDescriptions().split(",");

					for (int i = 0; i < aNames.length; i++) {
						if (!aNames[i].isEmpty())
							declaration.addMandatoryArgument(aNames[i], aDesc[i], aTypes[i]);
					}
					
					String[] oaNames = ((ThinklabCommand)annotation).optionalArgumentNames().split(",");
					String[] oaTypes = ((ThinklabCommand)annotation).optionalArgumentTypes().split(",");
					String[] oaDesc =  ((ThinklabCommand)annotation).optionalArgumentDescriptions().split(",");
					String[] oaDefs =  ((ThinklabCommand)annotation).optionalArgumentDefaultValues().split(",");

					for (int i = 0; i < oaNames.length; i++) {
						if (!oaNames[i].isEmpty())
							declaration.addOptionalArgument(oaNames[i], oaDesc[i], oaTypes[i], oaDefs[i]);				
					}

					String[] oNames = ((ThinklabCommand)annotation).optionNames().split(",");
					String[] olNames = ((ThinklabCommand)annotation).optionLongNames().split(",");
					String[] oaLabel = ((ThinklabCommand)annotation).optionArgumentLabels().split(",");
					String[] oTypes = ((ThinklabCommand)annotation).optionTypes().split(",");
					String[] oDesc = ((ThinklabCommand)annotation).optionDescriptions().split(",");

					for (int i = 0; i < oNames.length; i++) {
						if (!oNames[i].isEmpty())
								declaration.addOption(
										oNames[i],
										olNames[i], 
										(oaLabel[i].equals("") ? null : oaLabel[i]), 
										oDesc[i], 
										oTypes[i]);
					}
					
					try {
						CommandManager.get().registerCommand(declaration, (ICommandHandler) cls.newInstance());
					} catch (Exception e) {
						throw new ThinklabValidationException(e);
					}
					
					break;
				}
			}
		}

	}

	
	protected void loadSessionListeners() throws ThinklabException {
		
		for (Extension ext : getOwnThinklabExtensions("session-listener")) {

			Class<?> lv = getHandlerClass(ext, "class");
			KnowledgeManager.get().registerSessionListenerClass(lv);
		}

	}
	
	private void loadApplications() throws ThinklabIOException {

		/*
		 * publish all applications from loaded plugins.
		 */
		for (Extension ext : getOwnThinklabExtensions("application")) {
			ApplicationManager.get().registerApplication(new ApplicationDescriptor(this, ext));
		}
	}
	

	
	protected void loadLanguageInterpreters() throws ThinklabException {
		
		for (Extension ext : getOwnThinklabExtensions("language-interpreter")) {

			String icl =  ext.getParameter("class").valueAsString();
			String csp = ext.getParameter("language").valueAsString();
			
			InterpreterManager.get().registerInterpreter(csp, icl);
			
			logger().info("language interpreter registered for " + csp);
		}
		
	}

	protected void loadKnowledgeImporters() {
	
		for (Extension ext : getOwnThinklabExtensions("knowledge-importer")) {

			String url = ext.getParameter("url").valueAsString();
			String csp = ext.getParameter("concept-space").valueAsString();
			
			// TODO
		}
		
	}
	
	protected String getParameter(Extension ext, String field) {
		
		String ret = null;
		Parameter p = ext.getParameter(field);
		if (p != null)
			ret = p.valueAsString();
		return ret;
	}
	

	protected String[] getParameters(Extension ext, String field) {
		
		String[] ret = null;
		Collection<Parameter> p = ext.getParameters(field);
		if (p != null) {
			
			int i = 0;
			ret = new String[p.size()];
			for (Parameter pp : p)
				ret[i++] = pp.valueAsString();
		}
		return ret;
	}

	protected String getParameter(Extension ext, String field, String defValue) {
		
		String ret = null;
		Parameter p = ext.getParameter(field);
		if (p != null)
			ret = p.valueAsString();
		return ret == null ? defValue : ret;
	}
	
	public Object createInstance(String clazz) throws ThinklabPluginException {
		
		Object ret = null;
		
		ClassLoader classLoader = getManager().getPluginClassLoader(getDescriptor());
		Class<?> cls = null;
		try {

			cls = classLoader.loadClass(clazz);
			ret = cls.newInstance();
			
		} catch (Exception e) {
			throw new ThinklabPluginException(e);
		}
		return ret;
	}

	
	protected Object getHandlerInstance(Extension ext, String field) throws ThinklabPluginException {
		
		Object ret = null;
		
		ClassLoader classLoader = getManager().getPluginClassLoader(getDescriptor());
		Class<?> cls = null;
		try {

			cls = classLoader.loadClass(ext.getParameter(field).valueAsString());
			ret = cls.newInstance();
			
		} catch (Exception e) {
			throw new ThinklabPluginException(e);
		}
		return ret;
	}

	protected Class<?> getHandlerClass(Extension ext, String field) throws ThinklabPluginException {
		
		ClassLoader classLoader = getManager().getPluginClassLoader(getDescriptor());
		Class<?> cls = null;
		try {

			cls = classLoader.loadClass(ext.getParameter(field).valueAsString());
			
		} catch (Exception e) {
			throw new ThinklabPluginException(e);
		}
		return cls;
	}

	
	protected void loadKnowledgeLoaders() throws ThinklabException {
		
		for (Extension ext : getOwnThinklabExtensions("knowledge-loader")) {

			String[] format = getParameter(ext, "format").split(",");	
			
			for (String f : format) {
				KnowledgeManager.get().registerKnowledgeLoader(
					f, 
					(KnowledgeLoader) getHandlerInstance(ext, "class"));
			}
		}
		
	}
	
	protected void loadKBoxHandlers() throws ThinklabNoKMException, ThinklabPluginException {
		
		for (Extension ext : getOwnThinklabExtensions("kbox-handler")) {

			String[] format = getParameter(ext, "protocol").split(",");	
			
			for (String f : format) {
				KBoxManager.get().registerKBoxProtocol(
					f, 
					(KBoxHandler) getHandlerInstance(ext, "class"));
			}
		}	
		
	}

	@Override
	protected final void doStop() throws Exception {
		
		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.prePluginUnloaded(this);
		}
		
		unloadExtensions();
		
		unloadKboxes();
		unloadInstanceImplementationConstructors();
		unloadCommands();
		unloadLanguageInterpreters();
		unloadKnowledgeLoaders();
		unloadKnowledgeImporters();
		unloadKBoxHandlers();
		unloadLiteralValidators();
		unloadSessionListeners();
		unloadOntologies();
		
		loadExtensions();
		unload();
		
		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.onPluginUnloaded(this);
		}
	}
	
	private void unloadKboxes() {
		// TODO Auto-generated method stub
		
	}

	private void unloadInstanceImplementationConstructors() {
		// TODO Auto-generated method stub
		
	}

	private void unloadCommands() {
		// TODO Auto-generated method stub
		
	}

	private void unloadLanguageInterpreters() {
		// TODO Auto-generated method stub
		
	}

	private void unloadKnowledgeLoaders() {
		// TODO Auto-generated method stub
		
	}

	private void unloadKnowledgeImporters() {
		// TODO Auto-generated method stub
		
	}

	private void unloadKBoxHandlers() {
		// TODO Auto-generated method stub
		
	}

	private void unloadLiteralValidators() {
		// TODO Auto-generated method stub
		
	}
	
	private void unloadSessionListeners() {
		// TODO Auto-generated method stub
		
	}

	private void unloadOntologies() {

		for (String cspace : ontologies)
			KnowledgeManager.get().getKnowledgeRepository().releaseOntology(cspace);
		ontologies.clear();
	}

	protected void loadCommands() throws ThinklabException {

		for (Extension ext : getOwnThinklabExtensions("command-handler")) {

			ICommandHandler chandler = (ICommandHandler) getHandlerInstance(ext, "class");

			if (chandler == null)
				continue;
			
			String name = getParameter(ext, "command-name");
			String description = getParameter(ext, "command-description");
			
			CommandDeclaration declaration = new CommandDeclaration(name, description);
			
			String retType = getParameter(ext, "return-type");
			
			if (retType != null)
				declaration.setReturnType(KnowledgeManager.get().requireConcept(retType));
			
			String[] aNames = getParameter(ext, "argument-names","").split(",");
			String[] aTypes = getParameter(ext, "argument-types","").split(",");
			String[] aDesc =  getParameter(ext, "argument-descriptions","").split(",");

			for (int i = 0; i < aNames.length; i++) {
				if (!aNames[i].isEmpty())
					declaration.addMandatoryArgument(aNames[i], aDesc[i], aTypes[i]);
			}
			
			String[] oaNames = getParameter(ext, "optional-argument-names","").split(",");
			String[] oaTypes = getParameter(ext, "optional-argument-types","").split(",");
			String[] oaDesc =  getParameter(ext, "optional-argument-descriptions","").split(",");
			String[] oaDefs =  getParameter(ext, "optional-argument-default-values","").split(",");

			for (int i = 0; i < oaNames.length; i++) {
				if (!oaNames[i].isEmpty())
					declaration.addOptionalArgument(oaNames[i], oaDesc[i], oaTypes[i], oaDefs[i]);				
			}

			String[] oNames = getParameter(ext, "option-names","").split(",");
			String[] olNames = getParameter(ext, "option-long-names","").split(",");
			String[] oaLabel = getParameter(ext, "option-argument-labels","").split(",");
			String[] oTypes = getParameter(ext, "option-types","").split(",");
			String[] oDesc =  getParameter(ext, "option-descriptions","").split(",");

			for (int i = 0; i < oNames.length; i++) {
				if (!oNames[i].isEmpty())
						declaration.addOption(
								oNames[i],
								olNames[i], 
								(oaLabel[i].equals("") ? null : oaLabel[i]), 
								oDesc[i], 
								oTypes[i]);
			}
			
			CommandManager.get().registerCommand(declaration, chandler);
			
		}
	}
	
	/**
	 * Use to check if a specific resource has been found in the JAR
	 * @param name
	 * @return
	 */
	public boolean hasResource(String name) {
		return resources.get(name) != null;
	} 
	
	/**
	 * Return the plugin properties, read from any .properties file in distribution.
	 * @return the plugin Properties. It's never null.
	 */
	public Properties getProperties() {
		return properties;
	}
	
	public File getScratchPath() throws ThinklabException  {
		return dataFolder;
	}
	
	public File getLoadPath() throws ThinklabException  {
		return plugFolder;	
	}
	
	public Version getVersion() {
		return getDescriptor().getVersion();
	}

	public File getConfigPath() {
		return confFolder;
	}
}
