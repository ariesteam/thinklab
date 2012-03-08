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
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.interfaces.IResourceLoader;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.integratedmodelling.thinklab.rest.interfaces.IRESTHandler;
import org.integratedmodelling.utils.ClassUtils;
import org.integratedmodelling.utils.ClassUtils.Visitor;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.MiscUtilities;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.Extension.Parameter;
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
//	/**
//	 * register one of those using registerAnnotatedClass for each class
//	 * that extends the plugin through custom annotations.
//	 * 
//	 * @author ferdinando.villa
//	 */
//	protected interface AnnotatedClassHandler {
//		public void process(Annotation annotation, Class<?> cls, ThinklabPlugin plugin) throws ThinklabException;
//	}
//	
//	class AnnotationExtension {
//		Class<?> objectClass;
//		Class<?> annotationClass;
//		String subpackage;
//		AnnotatedClassHandler handler;
//	}
	
	/*
	 * TODO move all the load() things to this method, using visitPackage.
	 * 
	 * these are global so that all plugins can install and publish extensions
	 */
//	static ArrayList<AnnotationExtension> extensions = 
//		new ArrayList<ThinklabPlugin.AnnotationExtension>();
	
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
	
//	/**
//	 * Any extensions other than the ones handled by default should be handled here.
//	 * @throws ThinklabException 
//	 */
//	protected void loadExtensions() throws Exception {
//		
//	}
//	
//	protected void unloadExtensions() throws Exception {
//		
//	}
//	
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
		
//		loadLiteralValidators();
//		loadKnowledgeImporters();
//		loadCommandHandlers();
//		loadListingProviders();
//		loadRESTHandlers();
//		loadInstanceImplementationConstructors();
//		loadFunctions();

		visitAnnotations();

		load(KnowledgeManager.get());
//
//		loadExtensions();
		
		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.onPluginLoaded(this);
		}
		
		for (IResourceLoader loader : resourceLoaders) {
			loader.load(getThinklabPluginProperties(), getLoadDirectory());
		}
		
//		/*
//		 * load any extension defined through annotations
//		 */
//		for (AnnotationExtension ext : extensions) {
//			loadAnnotatedClass(
//					ext.subpackage, ext.objectClass, 
//					ext.annotationClass, ext.handler);
//		}
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
		// TODO Auto-generated method stub
		
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

	private void registerAnnotation(Class<?> clls, Concept a) {
		// TODO Auto-generated method stub
		
	}

	private void registerLiteral(Class<?> clls, Literal a) {
		// TODO Auto-generated method stub
		
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
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#swapClassloader()
	 */
	public ClassLoader swapClassloader() {
		ClassLoader clsl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClassLoader());
		return clsl;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#resetClassLoader(java.lang.ClassLoader)
	 */
	public void resetClassLoader(ClassLoader clsl) {
		Thread.currentThread().setContextClassLoader(clsl);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#getLoadDirectory()
	 */
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
	
//	/**
//	 * Return all the extensions in this plugin that extend the given Thinklab extension 
//	 * point (declared in the core plugin).
//	 * 
//	 * @param extensionPoint
//	 * @return
//	 */
//
//	protected void loadAnnotatedClass(String subpackage, Class<?> objectClass, Class<?> annotationClass, AnnotatedClassHandler handler) throws ThinklabException {
//		
//		String ipack = this.getClass().getPackage().getName() + "." + subpackage;
//		
//		for (Class<?> cls : MiscUtilities.findSubclasses(objectClass, ipack, getClassLoader())) {	
//			
//			/*
//			 * lookup annotation, ensure we can use the class
//			 */
//			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
//				continue;
//			
//			/*
//			 * find class with annotation and send back to plugin to process it
//			 */
//			for (Annotation annotation : cls.getAnnotations()) {
//				if (annotation.annotationType().equals(annotationClass)) {
//					handler.process(annotation, cls, this);
//				}
//			}	
//		}
//	}
	
//	/**
//	 * Call it in the plugin's load() to register a subpackage where annotated classes
//	 * will be looked up and the handler that will process them at plugin startup.
//	 * 
//	 * @param objectClass
//	 * @param annotationClass
//	 * @param subpackage
//	 * @param handler
//	 */
//	protected void registerAnnotatedClass(Class<?> objectClass, Class<?> annotationClass, String subpackage, AnnotatedClassHandler handler) {
//
//		AnnotationExtension ext = new AnnotationExtension();
//		ext.annotationClass = annotationClass;
//		ext.objectClass = objectClass;
//		ext.handler = handler;
//		ext.subpackage = subpackage;
//		
//		extensions.add(ext);
//	}

//	protected void loadInstanceImplementationConstructors() throws ThinklabException {
//		
//		String ipack = this.getClass().getPackage().getName() + ".implementations";
//		
//		for (Class<?> cls : MiscUtilities.findSubclasses(IInstanceImplementation.class, ipack, getClassLoader())) {	
//			
//			String concept = null;
//
//			/*
//			 * lookup annotation, ensure we can use the class
//			 */
//			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
//				continue;
//			
//			/*
//			 * lookup implemented concept
//			 */
//			for (Annotation annotation : cls.getAnnotations()) {
//				if (annotation instanceof InstanceImplementation) {
//					concept = ((InstanceImplementation)annotation).concept();
//				}
//			}
//			
//			if (concept != null) {
//
//				String[] cc = concept.split(",");
//				
//				for (String ccc : cc) {
//					logger().info("registering class " + cls + " as implementation for instances of type " + ccc);				
//					Thinklab.get().registerInstanceImplementationClass(ccc, cls);
//				}
//			}
//		}
//	}
//	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#getResourceURL(java.lang.String)
	 */
	public URL getResourceURL(String resource) throws ThinklabIOException 	{
		return getResourceURL(resource, null);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#getResourceURL(java.lang.String, org.integratedmodelling.thinklab.plugin.ThinklabPlugin)
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
	
	protected void loadLiteralValidators() throws ThinklabException {
		
		String ipack = this.getClass().getPackage().getName() + ".literals";
		
		for (Class<?> cls : MiscUtilities.findSubclasses(IParseable.class, ipack, getClassLoader())) {	
			
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
				
				Thinklab.get().registerLiteralImplementationClass(concept, cls);
				if (!xsd.equals(""))
					logger().info("registering XSD type mapping: " + xsd + " -> " + concept);
					Thinklab.get().registerXSDTypeMapping(xsd, concept);
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


	protected void loadFunctions() throws ThinklabException {
		
		String ipack = this.getClass().getPackage().getName() + ".functions";
		
		for (Class<?> cls : MiscUtilities.findSubclasses(IExpression.class, ipack, getClassLoader())) {	
			
			/*
			 * lookup annotation, ensure we can use the class
			 */
			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
				continue;
			
			for (Annotation annotation : cls.getAnnotations()) {
				if (annotation instanceof Function) {
					
					String   id = ((Function)annotation).id();
					String[] parameterNames = ((Function)annotation).parameterNames();
					try {
						ModelManager.get().registerFunction(id, parameterNames, cls);
					} catch (Exception e) {
						throw new ThinklabValidationException(e);
					}
					
					break;
				}
			}
		}
	}
	
	protected void loadListingProviders() throws ThinklabException {
		
		String ipack = this.getClass().getPackage().getName() + ".commands";
		
		for (Class<?> cls : MiscUtilities.findSubclasses(IListingProvider.class, ipack, getClassLoader())) {	
			
			/*
			 * lookup annotation, ensure we can use the class
			 */
			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
				continue;
			
			for (Annotation annotation : cls.getAnnotations()) {
				if (annotation instanceof ListingProvider) {
					
					String name = ((ListingProvider)annotation).label();
					String sname = ((ListingProvider)annotation).itemlabel();
					try {
						CommandManager.get().registerListingProvider(name, sname, cls);
					} catch (Exception e) {
						throw new ThinklabValidationException(e);
					}
					
					break;
				}
			}
		}
	}

//	protected void loadProjectLoaders() throws ThinklabException {
//		
//		String ipack = this.getClass().getPackage().getName() + ".ploaders";
//		
//		for (Class<?> cls : MiscUtilities.findSubclasses(IProjectLoader.class, ipack, getClassLoader())) {	
//			
//			/*
//			 * lookup annotation, ensure we can use the class
//			 */
//			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
//				continue;
//			
//			for (Annotation annotation : cls.getAnnotations()) {
//				if (annotation instanceof ProjectLoader) {
//					
//					String folder = ((ProjectLoader)annotation).folder();
//					String description = ((ProjectLoader)annotation).description();
//					Thinklab.get().registerProjectLoader(folder, (Class<?>) cls);	
//					break;
//				}
//			}
//		}
//
//	}

	
	protected void loadRESTHandlers() throws ThinklabException {
		
		String ipack = this.getClass().getPackage().getName() + ".rest";
		
		for (Class<?> cls : MiscUtilities.findSubclasses(IRESTHandler.class, ipack, getClassLoader())) {	
			
			/*
			 * lookup annotation, ensure we can use the class
			 */
			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
				continue;
			
			for (Annotation annotation : cls.getAnnotations()) {
				if (annotation instanceof RESTResourceHandler) {
					
					String path = ((RESTResourceHandler)annotation).id();
					String description = ((RESTResourceHandler)annotation).description();
					String argument = ((RESTResourceHandler)annotation).arguments();
					String options = ((RESTResourceHandler)annotation).options();
					RESTManager.get().registerService(path, (Class<? extends ServerResource>) cls,
							description, argument, options);	
					break;
				}
			}
		}
	}
//
//	protected void loadTransformations() throws ThinklabException {
//		
//		String ipack = this.getClass().getPackage().getName() + ".transformations";
//		
//		for (Class<?> cls : MiscUtilities.findSubclasses(ITransformation.class, ipack, getClassLoader())) {	
//			
//			/*
//			 * lookup annotation, ensure we can use the class
//			 */
//			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
//				continue;
//			
//			for (Annotation annotation : cls.getAnnotations()) {
//				if (annotation instanceof DataTransformation) {
//					
//					String name = ((DataTransformation)annotation).id();
//					try {
//						TransformationFactory.get().registerTransformation(name, (ITransformation)cls.newInstance());
//					} catch (Exception e) {
//						throw new ThinklabValidationException(e);
//					}
//					
//					break;
//				}
//			}
//		}
//
//	}

	protected void loadKnowledgeImporters() {
	
//		String ipack = this.getClass().getPackage().getName() + ".importers";
//		
//		for (Class<?> cls : MiscUtilities.findSubclasses(IKnowledgeImporter.class, ipack, getClassLoader())) {	
//			
//			/*
//			 * lookup annotation, ensure we can use the class
//			 */
//			if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
//				continue;
//			
//			for (Annotation annotation : cls.getAnnotations()) {
//				if (annotation instanceof org.integratedmodelling.thinklab.interfaces.annotations.KnowledgeLoader) {
//					
//					String fmt = 
//						((org.integratedmodelling.thinklab.interfaces.annotations.KnowledgeLoader)annotation).format();
//						KBoxManager.get().registerImporterClass(fmt, cls);
//					
//					break;
//				}
//			}
//		}

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
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#createInstance(java.lang.String)
	 */
	public Object createInstance(String clazz) throws ThinklabException {
		
		Object ret = null;
		
		ClassLoader classLoader = getManager().getPluginClassLoader(getDescriptor());
		Class<?> cls = null;
		try {

			cls = classLoader.loadClass(clazz);
			ret = cls.newInstance();
			
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);
		}
		return ret;
	}

	
	protected Object getHandlerInstance(Extension ext, String field) throws ThinklabException {
		
		Object ret = null;
		
		ClassLoader classLoader = getManager().getPluginClassLoader(getDescriptor());
		Class<?> cls = null;
		try {

			cls = classLoader.loadClass(ext.getParameter(field).valueAsString());
			ret = cls.newInstance();
			
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		return ret;
	}

	protected Class<?> getHandlerClass(Extension ext, String field) throws ThinklabException {
		
		ClassLoader classLoader = getManager().getPluginClassLoader(getDescriptor());
		Class<?> cls = null;
		try {

			cls = classLoader.loadClass(ext.getParameter(field).valueAsString());
			
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		return cls;
	}

	
	@Override
	protected final void doStop() throws Exception {
		
		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.prePluginUnloaded(this);
		}
		
//		unloadKboxes();
//		unloadInstanceImplementationConstructors();
//		unloadCommands();
//		unloadLanguageInterpreters();
//		unloadKnowledgeLoaders();
//		unloadKnowledgeImporters();
//		unloadKBoxHandlers();
//		unloadLiteralValidators();
//		unloadSessionListeners();
		unloadOntologies();
		
//		loadExtensions();
		unload();
		
		for (IPluginLifecycleListener lis : KnowledgeManager.getPluginListeners()) {
			lis.onPluginUnloaded(this);
		}
	}
	
//	private void unloadKboxes() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void unloadInstanceImplementationConstructors() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void unloadCommands() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void unloadLanguageInterpreters() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void unloadKnowledgeLoaders() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void unloadKnowledgeImporters() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void unloadKBoxHandlers() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void unloadLiteralValidators() {
//		// TODO Auto-generated method stub
//		
//	}
//	
//	private void unloadSessionListeners() {
//		// TODO Auto-generated method stub
//		
//	}

	private void unloadOntologies() {

		for (String cspace : ontologies)
			KnowledgeManager.get().getKnowledgeRepository().releaseOntology(cspace);
		ontologies.clear();
	}

	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#hasResource(java.lang.String)
	 */
	public boolean hasResource(String name) {
		return resources.get(name) != null;
	} 
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#getProperties()
	 */
	public Properties getProperties() {
		return properties;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#getScratchPath()
	 */
	public File getScratchPath() throws ThinklabException  {
		return dataFolder;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#getVersion()
	 */
	public Version getVersion() {
		return getDescriptor().getVersion();
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#getConfigPath()
	 */
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
