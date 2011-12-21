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
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.Escape;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.thinklab.project.ProjectFactory;
import org.integratedmodelling.thinklab.project.ThinklabProjectInstaller;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.template.MVELTemplate;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.PluginDescriptor;
import org.restlet.service.MetadataService;

import clojure.lang.RT;

/**
 * Activating this plugin means loading the knowledge manager, effectively booting the
 * Thinklab system.
 * 
 * @author Ferdinando Villa
 *
 */
public class Thinklab extends ThinklabPlugin implements IKnowledgeManager {

	public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.core";
	
	public static Thinklab get() {
		return _this;
	}
	
	KnowledgeManager _km = null;
	private HashMap<String, Class<?>> _projectLoaders = 
		new HashMap<String, Class<?>>();

	private MetadataService _metadataService;

	/*
	 * maps XSD URIs to thinklab types for translation of literals.
	 */
	private Hashtable<String, String> xsdMappings = new Hashtable<String, String>();

	private Hashtable<String, Class<?>> instanceImplementationClasses = new Hashtable<String, Class<?>>();
	private Hashtable<String, Class<?>> literalImplementationClasses = new Hashtable<String, Class<?>>();

	// reverse - implementation to (declared) concept. One day I'll start using those delicious
	// bidirectional maps and get it over with.
	private Hashtable<Class<?>, String> instanceImplementationConcepts = new Hashtable<Class<?>, String>();
	
	// only for this plugin, very ugly, but we need to access logging etc. before doStart() has
	// finished and the plugin has been published.
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

		/*
		 * KM is created with the knowledge rep and session manager configured in the properties,
		 * defaulting to protege (for now) and single session.
		 */
		if (_km == null) {
			_km = new KnowledgeManager();
			_km.setPluginManager(getManager());
			_km.initialize();
		}
		
		/*
		 * install listener to handle non-code Thinklab projects
		 */
		getManager().registerListener(new ThinklabProjectInstaller());
		
//		/*
//		 * scan all plugins and initialize thinklab projects from them if they
//		 * have the thinklab admin directories
//		 */
//		for (PluginDescriptor pd : getManager().getRegistry().getPluginDescriptors()) {
//			
//			String lf = 
//				Escape.fromURL(new File(pd.getLocation().getFile()).getAbsolutePath());
//			File ploc = MiscUtilities.getPath(lf);
//			
//			File loc = 
//				new File(
//						ploc +
//						File.separator +
//						"META-INF" +
//						File.separator +
//						"thinklab.properties");
//		
//			if (loc.exists()) {
//			
//				/*
//				 * install project wrapper in global register
//				 */
//				try {
//					ProjectFactory.get().registerProject(ploc);
//				} catch (ThinklabException e) {
//					throw new ThinklabRuntimeException(e);
//				}
//			
//				Thinklab.get().logger().info("thinklab project " + pd.getId() + " registered");
//			}
//		}

	}

	public IKnowledgeRepository getKnowledgeRepository() {
		return _km.getKnowledgeRepository();
	}

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		
		// initialize the Clojure runtime
		try {

			ClassLoader cls = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader ()); 

			logger().info("initializing Clojure runtime");
			RT.loadResourceScript("thinklab.clj");			
			RT.loadResourceScript("utils.clj");			
			RT.loadResourceScript("knowledge.clj");			
			logger().info("Clojure initialized successfully");
			
			Thread.currentThread().setContextClassLoader(cls); 

		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
	}

	@Override
	protected void unload() throws ThinklabException {

		if (_km != null) {
			_km.shutdown();
			_km = null;
		}
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

    /**
     * Return a new (parseable) literal of the proper type to handle the passed concept.
     * The returned literal will need to be initialized by making it parse a 
     * string value. It will implement IParseable, but best to check just in case.
     * 
     * @param type the concept
     * @return a raw literal or null if none is configured to handle the concept
     * @throws ThinklabException if there is ambiguity
     * 
     * TODO make it use the declared classes, abolish validators
     */
    public IValue getRawLiteral(IConcept type) throws ThinklabValidationException {

        class vmatch implements ConceptVisitor.ConceptMatcher {

            private Hashtable<String, Class<?>> coll;

            public Class<?> ret = null;
            
            public vmatch(Hashtable<String, Class<?>> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
                ret = coll.get(c.toString());
                return(ret != null);	
            }    
        }
        
        vmatch matcher = new vmatch(literalImplementationClasses);
        
        IConcept cms = 
            ConceptVisitor.findMatchUpwards(matcher, type);

        IValue ret = null;
        
        if (cms != null) {
        	try {
				ret = (IValue) matcher.ret.newInstance();
			} catch (Exception e) {
				throw new ThinklabValidationException("cannot create literal: " + e.getMessage());
			}
        }
        
        return ret;
    }

    
	/**
	 * If a mapping between the URI of an XSD type and a thinklab semantic type has been
	 * defined, return the correspondent type; otherwise return null.
	 * 
	 * @param XSDUri
	 * @return
	 */
	public String getXSDMapping(String XSDUri) {
		return xsdMappings.get(XSDUri);
	}

	public void registerInstanceImplementationClass(String concept, Class<?> cls) {
		instanceImplementationClasses.put(concept, cls);
	}
	
	public void registerLiteralImplementationClass(String concept, Class<?> cls) {
		literalImplementationClasses.put(concept, cls);	
	}
	
	public void registerXSDTypeMapping(String xsd, String type) {
		xsdMappings.put(xsd, type);	
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
		return _km.retrieveProperty(prop);
	}

	@Override
	public IConcept getConcept(String prop) {
		return _km.retrieveConcept(prop);
	}

	@Override
	public IConcept getConceptForClass(Class<?> cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getClassForConcept(IConcept type) {
		
        class vmatch implements ConceptVisitor.ConceptMatcher {

            private Hashtable<String, Class<?>> coll;
            
            public vmatch(Hashtable<String, Class<?>> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
                Class<?> cc = coll.get(c.toString());
                return (cc != null);
            }    
        }
        
        /*
         * I may be wrong, but there's no problem finding more than one constructor - just return the
         * least general one... 
         * There IS a problem if the ambiguity comes from a logical union - this should be checked, but
         * not now.
         */
        return
    	  new ConceptVisitor<Class<?>>().findMatchingInMapUpwards(
    			  instanceImplementationClasses, 
    			  new vmatch(instanceImplementationClasses), type);
	}

	@Override
	public IConcept getLeastGeneralCommonConcept(IConcept... cc) {
		return _km.getLeastGeneralCommonConcept(Arrays.asList(cc));
	}

	@Override
	public IValue validateLiteral(IConcept c, String literal)
			throws ThinklabException {
		return _km.validateLiteral(c, literal);
	}

	@Override
	public IKBox getDefaultKbox() {
		return KBoxManager.get();
	}

	@Override
	public void loadKnowledge(IList list) {
		// TODO Auto-generated method stub
		
	}
}
