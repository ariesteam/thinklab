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
package org.integratedmodelling.thinklab;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.integratedmodelling.collections.NumericInterval;
import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.configuration.IConfiguration;
import org.integratedmodelling.thinklab.api.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.factories.IPluginManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemantics;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.plugin.IPluginLifecycleListener;
import org.integratedmodelling.thinklab.api.plugin.IThinklabPlugin;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.configuration.Configuration;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.owlapi.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.plugin.PluginManager;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.integratedmodelling.utils.ClassUtils;
import org.integratedmodelling.utils.ClassUtils.Visitor;
import org.integratedmodelling.utils.template.MVELTemplate;
import org.restlet.resource.ServerResource;
import org.restlet.service.MetadataService;

/**
 * Activating this plugin means loading the knowledge manager, effectively booting the
 * Thinklab system. KnowledgeManager is the actual KM, but it remains hidden in this
 * package, using Thinklab as a proxy for everthing except those activities that need
 * a knowledge manager before Thinklab exists.
 * 
 * @author Ferdinando Villa
 *
 */
public class Thinklab implements IKnowledgeManager, IConfiguration, IPluginManager {

	public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.core";
	
	public static IConcept DOUBLE;
	public static IConcept BOOLEAN;
	public static IConcept TEXT;
	public static IConcept LONG;
	public static IConcept INTEGER;
	public static IConcept FLOAT;
	public static IConcept NUMBER;
	public static IConcept THING;
	public static IConcept NOTHING;
	public static IConcept ORDERED_RANGE_MAPPING;
	public static IConcept ORDINAL_RANKING;
	public static IConcept BOOLEAN_RANKING;
	
	public static IProperty CLASSIFICATION_PROPERTY;
	public static IProperty ABSTRACT_PROPERTY;
	HashMap<String, URL> resources = new HashMap<String, URL>();
	
	private MetadataService _metadataService;

	static Thinklab _this = null;
	
	protected KnowledgeManager _km;
	protected Configuration _configuration;
	protected PluginManager _pluginManager;
	protected IKnowledgeRepository _knowledgeRepository;
		
	
	Log logger = LogFactory.getLog(this.getClass());

	public Thinklab() throws ThinklabException {
		
		_configuration = new Configuration();
		_pluginManager = new PluginManager();
		_km            = new KnowledgeManager();
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#getClassLoader()
	 */
	public ClassLoader getClassLoader() {
		return this.getClass().getClassLoader();
	}
		
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.plugin.IThinklabPlugin#logger()
	 */
	public Log logger() {
		return logger;
	}
	
	protected final void startup() throws ThinklabException {

		_knowledgeRepository = new FileKnowledgeRepository();
		_knowledgeRepository.initialize();
		
		loadKnowledge();

		INTEGER  = getConcept(NS.INTEGER);
		FLOAT    = getConcept(NS.FLOAT);
		TEXT     = getConcept(NS.TEXT);
		LONG     = getConcept(NS.LONG);
		DOUBLE   = getConcept(NS.DOUBLE);
		NUMBER   = getConcept(NS.NUMBER);
		BOOLEAN  = getConcept(NS.BOOLEAN);
		BOOLEAN_RANKING  = getConcept(NS.BOOLEAN_RANKING);
		ORDINAL_RANKING  = getConcept(NS.ORDINAL_RANKING);
		ORDERED_RANGE_MAPPING  = getConcept(NS.ORDINAL_RANGE_MAPPING);
		
		CLASSIFICATION_PROPERTY = getProperty(NS.CLASSIFICATION_PROPERTY);
		ABSTRACT_PROPERTY       = getProperty(NS.ABSTRACT_PROPERTY);
							
		/*
		 * install known, useful API classes into annotation factory.
		 */
		registerAnnotatedClass(Pair.class, getConcept(NS.PAIR));
		registerAnnotatedClass(Triple.class, getConcept(NS.TRIPLE));
		registerAnnotatedClass(NumericInterval.class, getConcept(NS.NUMERIC_INTERVAL));
		
		/*
		 * TODO modeling beans
		 */
		
		/*
		 * TODO use plugin manager for this
		 */
		visitAnnotations();
		
		/*
		 * register all plugins
		 */
		_pluginManager.registerPluginPath(getLoadPath(SUBSPACE_PLUGINS));
		_pluginManager.boot();


	}

    public IConcept getRootConcept() {
        return _knowledgeRepository.getRootConcept();
    }
    
	private void loadKnowledge() throws ThinklabException {

		File pth = _configuration.getWorkspace(SUBSPACE_KNOWLEDGE);
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
			declaration.setReturnType(Thinklab.c(retType));
		
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
		_km.registerAnnotation(clls, a.value());
	}

	private void registerLiteral(Class<?> clls, Literal a) throws ThinklabException {
		_km.registerLiteralAnnotation(clls, a.concept(), a.datatype(), a.javaClass());
	}
	
	public ClassLoader swapClassloader() {
		ClassLoader clsl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClassLoader());
		return clsl;
	}
	
	public void resetClassLoader(ClassLoader clsl) {
		Thread.currentThread().setContextClassLoader(clsl);
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
					getClassLoader().getResource(resource);
			}
		} catch (MalformedURLException e) {
			throw new ThinklabIOException(e);
		}
		
		return ret;
	}	

	public boolean hasResource(String name) {
		return resources.get(name) != null;
	} 
	
	public Version getVersion() {
		return new Version();
	}

	public File getConfigPath() {
		return getWorkspace(SUBSPACE_CONFIG);
	}

	/**
	 * Return the only instance of Thinklab, your favourite knowledge manager.
	 * 
	 * @return
	 */
	public static Thinklab get() {
		return _this;
	}
	
	public static void boot() throws ThinklabException {
		_this = new Thinklab();		
		_this.startup();
	}
	
	/**
	 * Quickest way to get a IConcept from a string. Throws an unchecked exception if not present.
	 * 
	 * @param conceptId
	 * @return
	 */
	public static IConcept c(String conceptId) {
		IConcept ret = get().getConcept(conceptId);
		if (ret == null) {
			throw new ThinklabRuntimeException("concept " + conceptId + " is unknown");
		}
		return ret;
	}
	
	/**
	 * Quickest way to get a IProeprty from a string. Throws an unchecked exception if not present.
	 * 
	 * @param propertyId
	 * @return
	 */
	public static IProperty p(String propertyId) {
		IProperty ret = get().getProperty(propertyId);
		if (ret == null) {
			throw new ThinklabRuntimeException("property " + propertyId + " is unknown");
		}
		return ret;
	}

	public IKnowledgeRepository getKnowledgeRepository() {
		return _knowledgeRepository;
	}

	public MetadataService getMetadataService() throws ThinklabException {
		
		if (this._metadataService == null) {
			this._metadataService = new MetadataService();
			try {
				this._metadataService.start();
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}
		}
		return _metadataService;
	}

	public void shutdown(String hook, final int seconds, Map<String, String> params) throws ThinklabException {

		
		// TODO update to new config
		
		String inst = System.getenv("THINKLAB_INST");
		String home = System.getenv("THINKLAB_HOME");
		
		if (hook != null) {
						
			if (inst == null || home == null) {
				throw new ThinklabRuntimeException(
						"can't use the hook system: thinklab home and/or installation directories not defined");
			}
			
			File hdest = 
				new File(inst + File.separator + "tmp" + File.separator + "hooks");
			File hsour = new File(home + File.separator + "hooks" + File.separator + hook + ".hook");
			
			if (!hsour.exists()) {
				throw new ThinklabRuntimeException(
					"shutdown hook " + hook + " not installed");				
			}
			
			hdest.mkdirs();
			hdest = new File(hdest + File.separator + hook);
			
			MVELTemplate tmpl = new MVELTemplate(hsour);
			tmpl.write(hdest, params);
		}
		
		/*
		 * schedule shutdown
		 */
		new Thread() {
			
			@Override
			public void run() {
			
				int status = 0;
				try {
					sleep(seconds * 1000);
				} catch (InterruptedException e) {
					status = 255;
				}
				System.exit(status);
				
			}
		}.start();
	}

	@Override
	public IProperty getProperty(String prop) {
		return _km.getProperty(prop);
	}

	@Override
	public IConcept getConcept(String prop) {
		return _km.getConcept(prop);
	}

	@Override
	public IConcept getLeastGeneralCommonConcept(IConcept... cc) {
		return _km.getLeastGeneralCommonConcept(Arrays.asList(cc));
	}

	@Override
	public IKbox createKbox(String uri) throws ThinklabException {
		return _km.createKbox(uri);
	}

	@Override
	public void dropKbox(String uri) throws ThinklabException {
		_km.dropKbox(uri);
	}

	@Override
	public IKbox requireKbox(String uri) throws ThinklabException {
		return _km.requireKbox(uri);
	}

	/**
	 * Return the designated kbox to store data for this namespace.
	 * In this implementation, the projects that contain the namespace
	 * can designate a kbox; if not, this defaults to a public
	 * overall kbox if one exists.
	 *  
	 * @param ns
	 * @return
	 */
	public IKbox getStorageKboxForNamespace(INamespace ns) {
		// TODO Auto-generated method stub
		return null;
	}

	public IKbox getLookupKboxForNamespace(INamespace ns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISemanticObject parse(String literal, IConcept concept)
			throws ThinklabException {
		return _km.parse(literal, concept);
	}

	@Override
	public ISemanticObject annotate(Object object) throws ThinklabException {
		return _km.annotate(object);
	}

	@Override
	public Object instantiate(ISemantics semantics) throws ThinklabException {
		return _km.instantiate(semantics);
	}

	@Override
	public ISemantics conceptualize(Object object) throws ThinklabException {
		return _km.conceptualize(object);
	}

	@Override
	public void registerAnnotatedClass(Class<?> cls, IConcept concept) {
		_km.registerAnnotatedClass(cls, concept);
	}
	
	public CommandManager getCommandManager() {
		return _km.getCommandManager();
	}

	@Override
	public void registerPluginPath(File path) {
		_pluginManager.registerPluginPath(path);
	}

	@Override
	public void addPluginLifecycleListener(IPluginLifecycleListener listener) {
		_pluginManager.addPluginLifecycleListener(listener);
	}

	@Override
	public List<IThinklabPlugin> getPlugins() {
		return _pluginManager.getPlugins();
	}

	@Override
	public File getWorkspace() {
		return _configuration.getWorkspace();
	}

	@Override
	public File getWorkspace(String subspace) {
		return _configuration.getWorkspace(subspace);
	}

	@Override
	public File getScratchArea() {
		return _configuration.getScratchArea();
	}

	@Override
	public File getScratchArea(String subArea) {
		return _configuration.getScratchArea(subArea);
	}

	@Override
	public File getTempArea(String subArea) {
		return _configuration.getTempArea(subArea);
	}

	@Override
	public File getLoadPath(String subArea) {
		return _configuration.getLoadPath(subArea);
	}

	@Override
	public Properties getProperties() {
		return _configuration.getProperties();
	}

}