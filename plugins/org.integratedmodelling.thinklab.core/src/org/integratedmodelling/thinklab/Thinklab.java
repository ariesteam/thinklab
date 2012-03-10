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
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.collections.NumericInterval;
import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.list.Escape;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemantics;
import org.integratedmodelling.thinklab.api.knowledge.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.thinklab.project.ThinklabProjectInstaller;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.template.MVELTemplate;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.PluginDescriptor;
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
public class Thinklab extends ThinklabPlugin implements IKnowledgeManager {

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
	
	/**
	 * Return the only instance of Thinklab, your favourite knowledge manager.
	 * 
	 * @return
	 */
	public static Thinklab get() {
		return _this;
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

	
	
	private HashMap<String, Class<?>> _projectLoaders = 
		new HashMap<String, Class<?>>();

	private MetadataService _metadataService;

	static Thinklab _this = null;
	
	public Thinklab() {
		_this = this;
	}
	
	
	@Override
	protected void preStart() throws ThinklabException {
		
		/*
		 * initialize global config from plugin properties before setConfiguration() is called
		 */
		URL config = getResourceURL("core.properties");
		
		if (config != null)
			LocalConfiguration.loadProperties(config);

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
		 * install listener to handle non-code Thinklab projects
		 */
		getManager().registerListener(new ThinklabProjectInstaller());

	}

	public IKnowledgeRepository getKnowledgeRepository() {
		return KnowledgeManager.KM.getKnowledgeRepository();
	}

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		
		// initialize the Clojure runtime
		try {

			ClassLoader cls = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader ()); 

//			logger().info("initializing Clojure runtime");
//			RT.loadResourceScript("thinklab.clj");			
//			RT.loadResourceScript("utils.clj");			
//			RT.loadResourceScript("knowledge.clj");			
//			logger().info("Clojure initialized successfully");
			
			Thread.currentThread().setContextClassLoader(cls); 

		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
	}
	

	@Override
	protected void unload() throws ThinklabException {
		KnowledgeManager.KM.shutdown();
	}
	
	
	/**
	 * Return a fully qualified plugin name given a partial or full name. If complain is true, throw an exception
	 * if not found or ambiguous, otherwise return null.
	 * @param name
	 * @return
	 * @throws ThinklabException 
	 */
	public static String resolvePluginName(String name, boolean complain) throws ThinklabException {
		
		String ret = null;
		
		// look for exact match first
		for (PluginDescriptor pd : get().getManager().getRegistry().getPluginDescriptors()) {
			if (pd.getId().equals(name)) {
				ret = pd.getId();
			}
		}
		
		if (ret == null) {
			
			/*
			 * automatically disambiguate partial word matches, which should not be found
			 */
			if (!name.startsWith("."))
				name = "." + name;
			
			for (PluginDescriptor pd : get().getManager().getRegistry().getPluginDescriptors()) {
				if (pd.getId().endsWith(name)) {
					if (ret != null) {
						ret = null;
						break;
					}
					ret = pd.getId();
				}
			}
		}
		
		if (ret == null && complain)
			throw new ThinklabResourceNotFoundException("plugin name " + name + " unresolved or ambiguous");
		
		return ret;
	}
	
	public static ThinklabPlugin resolvePlugin(String name, boolean complain) throws ThinklabException {
	
		ThinklabPlugin ret = null;
		
		String pid = resolvePluginName(name, complain);

		if (pid != null)
			try {
				ret = (ThinklabPlugin)get().getManager().getPlugin(pid);
			} catch (PluginLifecycleException e) {
				throw new ThinklabInternalErrorException(e);
			}
			
		return ret;
	}

	public void requirePlugin(String pluginId, boolean complain) throws ThinklabException {

		String pid = resolvePluginName(pluginId, complain);
		super.requirePlugin(pid);
	}
	
	public static boolean verbose(ISession session) {
		return session.getVariable(ISession.INFO) != null;
	}

	public static boolean debug(ISession session) {
		return session.getVariable(ISession.DEBUG) != null;
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
				getManager().shutdown();
				System.exit(status);
				
			}
		}.start();
	}

	public static File getPluginLoadDirectory(Plugin plugin) {

		String lf = new File(plugin.getDescriptor().getLocation().getFile()).getAbsolutePath();
		return new File(Escape.fromURL(MiscUtilities.getPath(lf).toString()));
	}

	public void registerProjectLoader(String folder, Class<?> cls) {
		_projectLoaders.put(folder, cls);
	}
	
	public Class<?> getProjectLoader(String folder) {
		return _projectLoaders.get(folder);
	}

	@Override
	public IProperty getProperty(String prop) {
		return KnowledgeManager.KM.getProperty(prop);
	}

	@Override
	public IConcept getConcept(String prop) {
		return KnowledgeManager.KM.getConcept(prop);
	}

	@Override
	public IConcept getLeastGeneralCommonConcept(IConcept... cc) {
		return KnowledgeManager.KM.getLeastGeneralCommonConcept(Arrays.asList(cc));
	}

	@Override
	public IKbox createKbox(String uri) throws ThinklabException {
		return KnowledgeManager.KM.createKbox(uri);
	}

	@Override
	public void dropKbox(String uri) throws ThinklabException {
		KnowledgeManager.KM.dropKbox(uri);
	}

	@Override
	public IKbox requireKbox(String uri) throws ThinklabException {
		return KnowledgeManager.KM.requireKbox(uri);
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
		return KnowledgeManager.KM.parse(literal, concept);
	}

	@Override
	public ISemanticObject annotate(Object object) throws ThinklabException {
		return KnowledgeManager.KM.annotate(object);
	}

	@Override
	public Object instantiate(ISemantics semantics) throws ThinklabException {
		return KnowledgeManager.KM.instantiate(semantics);
	}

	@Override
	public ISemantics conceptualize(Object object) throws ThinklabException {
		return KnowledgeManager.KM.conceptualize(object);
	}

	@Override
	public void registerAnnotatedClass(Class<?> cls, IConcept concept) {
		KnowledgeManager.KM.registerAnnotatedClass(cls, concept);
	}

}
