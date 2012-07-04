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
import java.util.Collection;
import java.util.Date;
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
import org.integratedmodelling.lang.Classifier;
import org.integratedmodelling.lang.RankingScale;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.configuration.IConfiguration;
import org.integratedmodelling.thinklab.api.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.factories.IModelManager;
import org.integratedmodelling.thinklab.api.factories.IPluginManager;
import org.integratedmodelling.thinklab.api.factories.IProjectManager;
import org.integratedmodelling.thinklab.api.knowledge.IAxiom;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.api.modelling.IAgentModel;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.plugin.IPluginLifecycleListener;
import org.integratedmodelling.thinklab.api.plugin.IThinklabPlugin;
import org.integratedmodelling.thinklab.api.project.IProject;
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
import org.integratedmodelling.thinklab.project.ProjectManager;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.integratedmodelling.utils.ClassUtils;
import org.integratedmodelling.utils.ClassUtils.AnnotationVisitor;
import org.integratedmodelling.utils.template.MVELTemplate;
import org.restlet.resource.ServerResource;
import org.restlet.service.MetadataService;

/**
 * Thinklab implements all fundamental interfaces in the Thinklab API, serving as a 
 * one-stop access point for the system.
 * 
 * There is only one instance of Thinklab, always accessible using Thinklab.get(). 
 * Use Thinklab.boot() to start Thinklab and Thinklab.shutdown() to stop it.
 * 
 * Thinklab delegates calls to working and properly initialized instances of IKnowledgeManager, 
 * IProjectManager, IConfiguration, IPluginManager and IModelManager. 
 * 
 * @author Ferdinando Villa
 *
 */
public class Thinklab implements 
	IKnowledgeManager, IConfiguration, IPluginManager, IProjectManager, IModelManager {

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
	protected ProjectManager _projectManager;	
	protected ModelManager _modelManager;
	
	Log logger = LogFactory.getLog(this.getClass());
	
	protected long _bootTime;

	public Thinklab() throws ThinklabException {
		
		_configuration  = new Configuration();
		_km             = new KnowledgeManager();
		_pluginManager  = new PluginManager();
		_projectManager = new ProjectManager();
		_modelManager   = new ModelManager();
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

		_bootTime = new Date().getTime();
		
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
		registerAnnotatedClass(Classifier.class, getConcept(NS.CLASSIFIER));
		registerAnnotatedClass(RankingScale.class, getConcept(NS.RANKING_SCALE));
		
		/*
		 * TODO use plugin manager for this
		 */
		visitAnnotations();
		
		/*
		 * register all plugins
		 */
		_pluginManager.registerPluginPath(getLoadPath(SUBSPACE_PLUGINS));
		_pluginManager.boot();
		
		logger.info("SPERMA 3");
		
		/*
		 * and finally the projects
		 */
		_projectManager.addProjectDirectory(getWorkspace(SUBSPACE_PROJECTS));
		_projectManager.setDeployDir(getWorkspace("deploy"));
		_projectManager.boot();

		logger.info("SPERMA 4");
		
	}

    public IConcept getRootConcept() {
        return _knowledgeRepository.getRootConcept();
    }
    
	private void loadKnowledge() throws ThinklabException {

		File kdir = _configuration.getScratchArea(SUBSPACE_KNOWLEDGE);
		_km.extractCoreOntologies(kdir);		
		_modelManager.loadSourceDirectory(kdir, null);
	}

	private void visitAnnotations() throws ThinklabException {
		
		
		ClassUtils.visitAnnotations(this.getClass().getPackage().getName(),
				Literal.class,
				new AnnotationVisitor() {
					@Override
					public void visit(Annotation acls,
							Class<?> target) throws ThinklabException {
						registerLiteral(target, (Literal)acls);
					}
			});
		
		ClassUtils.visitAnnotations(this.getClass().getPackage().getName(),
				Concept.class,
				new AnnotationVisitor() {
					@Override
					public void visit(Annotation acls,
							Class<?> target) throws ThinklabException {
						registerAnnotation(target, (Concept)acls);
					}
			});
		ClassUtils.visitAnnotations(this.getClass().getPackage().getName(),
				ThinklabCommand.class,
				new AnnotationVisitor() {
					@Override
					public void visit(Annotation acls,
							Class<?> target) throws ThinklabException {
						registerCommand(target, (ThinklabCommand)acls);
					}
			});
		ClassUtils.visitAnnotations(this.getClass().getPackage().getName(),
				RESTResourceHandler.class,
				new AnnotationVisitor() {
					@Override
					public void visit(Annotation acls,
							Class<?> target) throws ThinklabException {
						registerRESTResource(target, (RESTResourceHandler)acls);
					}
			});
		ClassUtils.visitAnnotations(this.getClass().getPackage().getName(),
				ListingProvider.class,
				new AnnotationVisitor() {
					@Override
					public void visit(Annotation acls,
							Class<?> target) throws ThinklabException {
						registerListingProvider(target, (ListingProvider)acls);
					}
			});
		ClassUtils.visitAnnotations(this.getClass().getPackage().getName(),
				Function.class,
				new AnnotationVisitor() {
					@Override
					public void visit(Annotation acls,
							Class<?> target) throws ThinklabException {
						registerFunction(target, (Function)acls);
					}
			});
		
//		/*
//		 * FIXME use asm library to find annotations without loading classes
//		 */
//		ClassUtils.visitPackage(this.getClass().getPackage().getName(), 
//				new Visitor() {
//					
//					@Override
//					public void visit(Class<?> clls) throws ThinklabException {
//
//						for (Annotation a : clls.getAnnotations()) {
//							if (a instanceof Literal) {
//								registerLiteral(clls, (Literal)a);
//							} else if (a instanceof Concept) {
//								registerAnnotation(clls, (Concept)a);								
//							} else if (a instanceof ThinklabCommand) {
//								registerCommand(clls, (ThinklabCommand)a);
//							} else if (a instanceof RESTResourceHandler) {
//								registerRESTResource(clls, (RESTResourceHandler)a);	
//							} else if (a instanceof ListingProvider) {
//								registerListingProvider(clls, (ListingProvider)a);
//							} else if (a instanceof Function) {
//								registerFunction(clls, (Function)a);
//							} 
//						}
//						
//					}
//				}, 
//				this.getClassLoader());


	}
	
	private void registerFunction(Class<?> cls, Function annotation) throws ThinklabException {

		String   id = annotation.id();
		String[] parameterNames = annotation.parameterNames();
		try {
			_modelManager.registerFunction(id, parameterNames, cls);
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
	
	/**
	 * Entry point in Thinklab: call boot() before you do anything. Calling more than
	 * once without calling shutdown() has no effect.
	 * 
	 * @throws ThinklabException
	 */
	public static void boot() throws ThinklabException {
		if (_this == null) {
			_this = new Thinklab();		
			_this.startup();
		}
	}
	
	/**
	 * You must call shutdown() when you're done to ensure integrity of 
	 * data and everything. This said, I always abort applications without
	 * getting there and not much happens.
	 */
	public static void shutdown() {
		
		if (_this != null) {
		
			_this.logger.info("Thinklab shutting down");
			
			_this._pluginManager.shutdown();
			_this._km.shutdown();

			_this._configuration = null;
			_this._km = null;
			_this._knowledgeRepository = null;
			_this._pluginManager = null;
			_this = null;
		}
		
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

	public IProjectManager getProjectManager() {
		return _projectManager;
	}

	public IPluginManager getPluginManager() {
		return _pluginManager;
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
	 * The namespace can contain a specification for that, or the project that contains the namespace
	 * can designate a default kbox if not specified; if even that is not specified, this defaults to a public
	 * overall kbox named "thinklab".
	 *  
	 * @param ns
	 * @return
	 * @throws ThinklabException 
	 */
	public IKbox getStorageKboxForNamespace(INamespace ns) throws ThinklabException {

		String rbox = "thinklab";
		if (ns.getStorageKbox() != null) 
			rbox = ns.getStorageKbox();
		else if (ns.getProject() != null)
			rbox = ns.getProject().getProperties().getProperty(IProject.STORAGE_KBOX_PROPERTY, rbox);
		return requireKbox(rbox);
	}

	/**
	 * Return the designated kbox to resolve references for a namespace. Defaults to thinklab
	 * kbox if neither the namespace statement or the project's properties specify one.
	 * 
	 * @param ns
	 * @return
	 * @throws ThinklabException
	 */
	public IKbox getLookupKboxForNamespace(INamespace ns) throws ThinklabException {
		String rbox = "thinklab";
		if (ns.getLookupKbox() != null) 
			rbox = ns.getLookupKbox();
		else if (ns.getProject() != null)
			rbox = ns.getProject().getProperties().getProperty(IProject.LOOKUP_KBOX_PROPERTY, rbox);
		return requireKbox(rbox);
	}

	/**
	 * Return the designated kbox to provide data for training of models in a namespace. Defaults to thinklab
	 * kbox if neither the namespace statement or the project's properties specify one.
	 * 
	 * @param ns
	 * @return
	 * @throws ThinklabException
	 */
	public IKbox getTrainingKboxForNamespace(INamespace ns) throws ThinklabException {
		String rbox = "thinklab";
		if (ns.getTrainingKbox() != null) 
			rbox = ns.getTrainingKbox();
		else if (ns.getProject() != null)
			rbox = ns.getProject().getProperties().getProperty(IProject.TRAINING_KBOX_PROPERTY, rbox);
		return requireKbox(rbox);
	}
	
	@Override
	public ISemanticObject<?> parse(String literal, IConcept concept)
			throws ThinklabException {
		return _km.parse(literal, concept);
	}

	@Override
	public ISemanticObject<?> annotate(Object object) throws ThinklabException {

		if (object instanceof ISemanticObject)
			return (ISemanticObject<?>)object;

		return _km.annotate(object);
	}

	@Override
	public Object instantiate(IList semantics) throws ThinklabException {
		return _km.instantiate(semantics);
	}
	
	@Override
	public ISemanticObject<?> entify(IList semantics) throws ThinklabException {
		return _km.entify(semantics);
	}

	public IReferenceList conceptualize(Object object) throws ThinklabException {
		return ((KnowledgeManager)_km).conceptualize(object);
	}

	@Override
	public void registerAnnotatedClass(Class<?> cls, IConcept concept) {
		_km.registerAnnotatedClass(cls, concept);
	}
	
	public CommandManager getCommandManager() {
		return _km.getCommandManager();
	}

	public IModelManager getModelManager() {
		return _modelManager;
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
	public File getLoadPath() {
		return _configuration.getLoadPath();
	}
	
	@Override
	public File getLoadPath(String subArea) {
		return _configuration.getLoadPath(subArea);
	}

	@Override
	public Properties getProperties() {
		return _configuration.getProperties();
	}
	
	public boolean isJavaLiteralClass(Class<?> cls) {
		return _km.isJavaLiteralClass(cls);
	}
	public boolean isLiteralConcept(IConcept concept) {
		return _km.isLiteralConcept(concept);
	}

	public File getProjectPath() {
		return getWorkspace("projects");
	}

	@Override
	public IProject getProject(String projectId) {
		return _projectManager.getProject(projectId);
	}

	@Override
	public Collection<IProject> getProjects() {
		return _projectManager.getProjects();
	}

	@Override
	public IProject deployProject(String pluginId, String resourceId) throws ThinklabException {
		return _projectManager.deployProject(pluginId, resourceId);
	}

	@Override
	public void undeployProject(String projectId) throws ThinklabException {
		_projectManager.undeployProject(projectId);
	}

	@Override
	public void addProjectDirectory(File projectDirectory) {
		_projectManager.addProjectDirectory(projectDirectory);		
	}

	public ISemanticObject<?> getSemanticObject(IReferenceList list, Object object) {
		return _km.getSemanticObject(list, object);
	}

	public long getBootTime() {
		return _bootTime;
	}

	public IConcept getLiteralConceptForJavaClass(Class<? extends Object> class1) {
		return _km.getLiteralConceptForJavaClass(class1);
	}

	public ISemanticObject<?> getSemanticLiteral(IReferenceList semantics) {
		return _km.getSemanticLiteral(semantics);
	}

	@Override
	public IConcept getXSDMapping(String string) {
		return _km.getXSDMapping(string);
	}

	public IOntology createOntology(String id, String ontologyPrefix, Collection<IAxiom> axioms) throws ThinklabException {
		IOntology ret =  _knowledgeRepository.createOntology(id, ontologyPrefix);
		if (axioms != null)
			ret.define(axioms);
		return ret;
	}

	public boolean hasOntology(String id) {
		return _knowledgeRepository.retrieveOntology(id) != null;
	}

	public void dropOntology(String id) {
		_knowledgeRepository.releaseOntology(id);
	}
	
	/*
	 * ModelManager proxy
	 */

	@Override
	public IModel getModel(String s) {
		return _modelManager.getModel(s);
	}

	@Override
	public IAgentModel getAgentModel(String s) {
		return _modelManager.getAgentModel(s);
	}

	@Override
	public IScenario getScenario(String s) {
		return _modelManager.getScenario(s);

	}

	@Override
	public IContext getContext(String s) {
		return _modelManager.getContext(s);
	}

	@Override
	public INamespace getNamespace(String ns) {
		return _modelManager.getNamespace(ns);
	}

	@Override
	public void releaseNamespace(String namespace) {
		_modelManager.releaseNamespace(namespace);
	}

	@Override
	public IModelObject getModelObject(String object) {
		return _modelManager.getModelObject(object);
	}

	@Override
	public String getSource(String object) {
		return _modelManager.getSource(object);
	}

	@Override
	public Collection<IModelObject> getDependencies(String object) {
		return _modelManager.getDependencies(object);
	}

	@Override
	public Collection<INamespace> getNamespaces() {
		return _modelManager.getNamespaces();
	}

	@Override
	public IContext getCoverage(IModel model) {
		return _modelManager.getCoverage(model);
	}

	@Override
	public Collection<IScenario> getApplicableScenarios(IModel model,
			IContext context, boolean isPublic) throws ThinklabException {
		return _modelManager.getApplicableScenarios(model, context, isPublic);
	}

	@Override
	public INamespace loadFile(String resourceId, String namespaceId,
			IProject project) throws ThinklabException {
		return _modelManager.loadFile(resourceId, namespaceId, project);
	}

	@Override
	public Collection<INamespace> load(IProject project)
			throws ThinklabException {
		return _modelManager.load(project);
	}

	@Override
	public IExpression resolveFunction(String functionId,
			Collection<String> parameterNames) {
		return _modelManager.resolveFunction(functionId, parameterNames);
	}

	@Override
	public Collection<INamespace> loadSourceDirectory(File sourcedir, IProject project)
			throws ThinklabException {
		return _modelManager.loadSourceDirectory(sourcedir, project);
	}

	@Override
	public IObservation observe(Object object, IContext context)
			throws ThinklabException {
		return _modelManager.observe(object, context);
	}

}
