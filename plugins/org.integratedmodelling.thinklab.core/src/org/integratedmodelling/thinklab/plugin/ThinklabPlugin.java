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
package org.integratedmodelling.thinklab.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.Escape;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.interfaces.IResourceLoader;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.integratedmodelling.utils.ClassUtils;
import org.integratedmodelling.utils.ClassUtils.Visitor;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.MiscUtilities;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Version;
import org.restlet.resource.ServerResource;


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
	
	private static ArrayList<IResourceLoader> resourceLoaders = 
		new ArrayList<IResourceLoader>();
	
	private File dataFolder;
	private File confFolder;
	private File plugFolder;
	private File loadFolder;
	
	/**
	 * ALWAYS call this one to ensure that all the necessary plugins are loaded, even if
	 * dependencies are properly declared.
	 * 
	 * @param pluginId
	 * @throws ThinklabPluginException
	 */
	protected void requirePlugin(String pluginId) throws ThinklabException {

		try {
			getManager().activatePlugin(pluginId);
		} catch (PluginLifecycleException e) {
			throw new ThinklabInternalErrorException(e);
		}
	}
	
	/*
	 * intercepts the beginning of doStart()
	 */
	protected void preStart() throws Exception {
		
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#getClassLoader()
	 */
	public ClassLoader getClassLoader() {
		return getManager().getPluginClassLoader(getDescriptor());
	}
	
	public static ClassLoader getClassLoaderFor(Object o) {
		return ((ThinklabPlugin)(Thinklab.get().getManager().getPluginFor(o))).getClassLoader();
	}
	
	public static ThinklabPlugin getPluginFor(Object o) {
		return (ThinklabPlugin)(Thinklab.get().getManager().getPluginFor(o));
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#logger()
	 */
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
		}
		return ret;
	}
	
	public void installResourceLoader(IResourceLoader l) {
		resourceLoaders.add(l);
	}
	
	protected String getPluginBaseName() {
		String[] sp = getDescriptor().getId().split("\\.");
		return sp[sp.length - 1];
	}
	
	@Override
	protected final void doStart() throws Exception {
		
		/*
		 * ensure we have a knowledge manager first of all.
		 */
		KnowledgeManager km = KnowledgeManager.get();
		km.setPluginManager(getManager());

		
		loadConfiguration();

		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.prePluginLoaded(this);
		}

		
		loadKnowledge();

		preStart();

		visitAnnotations();
		
		load(km);
		
		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.onPluginLoaded(this);
		}
		
		for (IResourceLoader loader : resourceLoaders) {
			loader.load(getThinklabPluginProperties(), getLoadDirectory());
		}
	}
	
	private void loadKnowledge() throws ThinklabException {

		File pth = new File(getLoadDirectory() + File.separator + "knowledge");
		if (pth.exists()) {
			ModelManager.get().loadSourceDirectory(pth);
		}
	}

	private void visitAnnotations() throws ThinklabException {
		
		ClassUtils.visitPackage(this.getClass().getPackage().getName(), 
				new Visitor() {
					
					@Override
					public void visit(Class<?> clls) throws ThinklabException {

						for (Annotation a : clls.getAnnotations()) {
							if (a instanceof Literal) {
								registerLiteral(clls, (Literal)a);
							} else if (a instanceof Concept) {
								registerAnnotation(clls, (Concept)a);								
							} else if (a instanceof ThinklabCommand) {
								registerCommand(clls, (ThinklabCommand)a);
							} else if (a instanceof RESTResourceHandler) {
								registerRESTResource(clls, (RESTResourceHandler)a);	
							} else if (a instanceof ListingProvider) {
								registerListingProvider(clls, (ListingProvider)a);
							} else if (a instanceof Function) {
								registerFunction(clls, (Function)a);
							} 
						}
						
					}
				}, 
				this.getClassLoader());
	}
	
	private void registerFunction(Class<?> cls, Function annotation) throws ThinklabException {

		String   id = annotation.id();
		String[] parameterNames = annotation.parameterNames();
		try {
			ModelManager.get().registerFunction(id, parameterNames, cls);
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
	}

	private void registerListingProvider(Class<?> cls, ListingProvider annotation) throws ThinklabException {
		
		String name = annotation.label();
		String sname = annotation.itemlabel();
		try {
			CommandManager.get().registerListingProvider(name, sname, cls);
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void registerRESTResource(Class<?> cls, RESTResourceHandler annotation) throws ThinklabException {
		
		String path = annotation.id();
		String description = annotation.description();
		String argument = annotation.arguments();
		String options = annotation.options();
		RESTManager.get().registerService(path, (Class<? extends ServerResource>) cls,
				description, argument, options);		
	}

	private void registerCommand(Class<?> cls, ThinklabCommand annotation) throws ThinklabException {

		String name = annotation.name();
		String description = annotation.description();
		
		CommandDeclaration declaration = new CommandDeclaration(name, description);
		
		String retType = annotation.returnType();
		
		if (!retType.equals(""))
			declaration.setReturnType(KnowledgeManager.get().requireConcept(retType));
		
		String[] aNames = annotation.argumentNames().split(",");
		String[] aTypes = annotation.argumentTypes().split(",");
		String[] aDesc =  annotation.argumentDescriptions().split(",");

		for (int i = 0; i < aNames.length; i++) {
			if (!aNames[i].isEmpty())
				declaration.addMandatoryArgument(aNames[i], aDesc[i], aTypes[i]);
		}
		
		String[] oaNames = annotation.optionalArgumentNames().split(",");
		String[] oaTypes = annotation.optionalArgumentTypes().split(",");
		String[] oaDesc =  annotation.optionalArgumentDescriptions().split(",");
		String[] oaDefs =  annotation.optionalArgumentDefaultValues().split(",");

		for (int i = 0; i < oaNames.length; i++) {
			if (!oaNames[i].isEmpty())
				declaration.addOptionalArgument(oaNames[i], oaDesc[i], oaTypes[i], oaDefs[i]);				
		}

		String[] oNames = annotation.optionNames().split(",");
		String[] olNames = annotation.optionLongNames().split(",");
		String[] oaLabel = annotation.optionArgumentLabels().split(",");
		String[] oTypes = annotation.optionTypes().split(",");
		String[] oDesc = annotation.optionDescriptions().split(",");

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
		
	}

	private void registerAnnotation(Class<?> clls, Concept a) throws ThinklabException {
		KnowledgeManager.get().registerAnnotation(clls, a.value());
	}

	private void registerLiteral(Class<?> clls, Literal a) throws ThinklabException {
		KnowledgeManager.get().registerLiteralAnnotation(clls, a.concept(), a.datatype(), a.javaClass());
	}
	
	private Properties getThinklabPluginProperties() throws ThinklabIOException {

		Properties ret = new Properties();
		File pfile = 
			new File(
				getLoadDirectory() + 
				File.separator + 
				"THINKLAB-INF" +
				File.separator + 
				"thinklab.properties");
		
		if (pfile.exists()) {
			try {
				ret.load(new FileInputStream(pfile));
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		}
		
		return ret;
	}
	
	public ClassLoader swapClassloader() {
		ClassLoader clsl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClassLoader());
		return clsl;
	}
	
	public void resetClassLoader(ClassLoader clsl) {
		Thread.currentThread().setContextClassLoader(clsl);
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
       
       /*
        * load all non-customized properties files directly from plugin load dir
        */
       File cdir = new File(getLoadDirectory() + File.separator + "config");
       if (cdir.exists() && cdir.isDirectory())
		for (File f : cdir.listFiles()) {
			if (f.toString().endsWith(".properties") &&
			    !(f.toString().endsWith(getPluginBaseName() + ".properties"))) {
				try {
					logger().info("reading additional properties from " + f);
					FileInputStream inp = new FileInputStream(f);
					properties.load(inp);
					inp.close();
				} catch (Exception e) {
					throw new ThinklabIOException(e);
				}
			}
		}
       
       // load custom properties, overriding any in system folder.
       if (pfile.exists()) {
    	   try {
    		propertySource = pfile;
    		FileInputStream inp = new FileInputStream(pfile);
			properties.load(inp);
			inp.close();
			logger().info("plugin customized properties loaded from " + pfile);
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
       }
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#writeConfiguration()
	 */
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
	
	public URL getResourceURL(String resource) throws ThinklabIOException 	{
		return getResourceURL(resource, null);
	}
	
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
	
//	public Object createInstance(String clazz) throws ThinklabException {
//		
//		Object ret = null;
//		
//		ClassLoader classLoader = getManager().getPluginClassLoader(getDescriptor());
//		Class<?> cls = null;
//		try {
//
//			cls = classLoader.loadClass(clazz);
//			ret = cls.newInstance();
//			
//		} catch (Exception e) {
//			throw new ThinklabInternalErrorException(e);
//		}
//		return ret;
//	}
	
	@Override
	protected final void doStop() throws Exception {
		
		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.prePluginUnloaded(this);
		}
		
		unloadOntologies();
		unload();
		
		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.onPluginUnloaded(this);
		}
	}
	
	private void unloadOntologies() {

		for (String cspace : ontologies)
			KnowledgeManager.get().getKnowledgeRepository().releaseOntology(cspace);
		ontologies.clear();
	}

	public boolean hasResource(String name) {
		return resources.get(name) != null;
	} 
	
	public Properties getProperties() {
		return properties;
	}
	
	public File getScratchPath() throws ThinklabException  {
		return dataFolder;
	}
	
	public Version getVersion() {
		return getDescriptor().getVersion();
	}

	public File getConfigPath() {
		return confFolder;
	}

	public void persistProperty(String var, String val) throws ThinklabIOException {
		
		String configFile = getPluginBaseName() + ".properties";
		File pfile = new File(confFolder + File.separator + configFile);

		// load custom properties, overriding any in system folder.
		Properties props = new Properties();
		if (pfile.exists()) {
			try {
				FileInputStream inp = new FileInputStream(pfile);
				props.load(inp);
				inp.close();
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		}
		props.setProperty(var, val);
		try {
			FileOutputStream out = new FileOutputStream(pfile);
			props.store(out, null);
			out.close();
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}

	}
}
