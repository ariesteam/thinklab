/**
 * SearchEnginePlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabSearchEnginePlugin.
 * 
 * ThinklabSearchEnginePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabSearchEnginePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.searchengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.Extension.Parameter;

public class SearchEnginePlugin extends ThinklabPlugin {

	static final String PLUGIN_ID = "org.integratedmodelling.thinklab.searchengine";
	
	ArrayList<SearchEngine> engines = new ArrayList<SearchEngine>();
	
	/**
	 * Set to a true value to enable indexing of individuals contained in
	 * ontologies. Kbox indexing is not affected by this property.
	 */
	public static final String SEARCHENGINE_INDEX_INDIVIDUALS_PROPERTY = 
		"searchengine.%.index.individuals";
	
	/**
	 * Set to the path where you want the Lucene index to be created. Default
	 * is scratch path + /index.
	 */
	public static final String SEARCHENGINE_INDEX_PATH_PROPERTY = 
		"searchengine.%.index.path";
	
	/**
	 * If set to a true value, concepts without comments or labels are indexed using
	 * their id. Otherwise they're ignored. Default is false.
	 */
	public static final String SEARCHENGINE_INDEX_UNCOMMENTED_PROPERTY = 
		"searchengine.%.index.uncommented";
	
	/**
	 * Class to use for the analyzer; if not supplied, the standard
	 * Lucene analyzer (English) is used.
	 */
	public static final String SEARCHENGINE_ANALYZER_CLASS_PROPERTY = 
		"searchengine.%.analyzer.class";
	
	
	public static final String SEARCHENGINE_INDEX_TYPES_PROPERTY =
		"searchengine.%.index.types";
	
	/**
	 * Comma-separated list of kbox URLs that should be indexed
	 */
	public static final String SEARCHENGINE_KBOX_LIST_PROPERTY = 
		"searchengine.%.kbox";
	
	/**
	 * Ontologies listed here will be included unless "all" is one of the ontologies, then
	 * all will be included unless listed here with a ! in front of them.
	 */
	public static final String SEARCHENGINE_INDEX_ONTOLOGIES_PROPERTY = 
		"searchengine.%.index.ontologies";
	
	public static SearchEnginePlugin get() {
		return (SearchEnginePlugin) getPlugin(PLUGIN_ID );
	}
	
	/**
	 * Get your engine here, passing the necessary configuration properties. 
	 * 
	 * @param id
	 * @param properties
	 * @return
	 * @throws ThinklabException
	 */
	public SearchEngine createSearchEngine(String id, Properties properties) throws ThinklabException {
		
		SearchEngine engine = new SearchEngine(id, properties);
		engines.add(engine);
		return engine;
	}

	/**
	 * Get your engine here, passing the necessary configuration properties. 
	 * 
	 * @param id
	 * @param properties
	 * @return
	 * @throws ThinklabException
	 */
	public SearchEngine createSearchEngine(Extension ext, Properties properties) throws ThinklabException {
		
		String id = getParameter(ext, "id");
		
		/*
		 * find the declaring plugin so we can find data and files in its classpath
		 */
		ThinklabPlugin resourceFinder = null;
		try {
			resourceFinder =
				(ThinklabPlugin)getManager().getPlugin(ext.getDeclaringPluginDescriptor().getId());
		} catch (PluginLifecycleException e) {
			throw new ThinklabValidationException("can't determine the plugin that created the engine "+ id);
		}
		
		log.info("creating search engine " + id);

		String kboxes = getParameter(ext, "kbox");
		String ontolo = getParameter(ext, "ontology");
		String select = getParameter(ext, "select-classes");
		String uncomm = getParameter(ext, "index-uncommented", ontolo == null ? "false" : "true");
		String indivi = getParameter(ext, "index-individuals", ontolo == null ? "true" : "false");

		Properties p = new Properties();
		p.putAll(properties);
		
		p.setProperty(SEARCHENGINE_INDEX_UNCOMMENTED_PROPERTY, uncomm);
		p.setProperty(SEARCHENGINE_INDEX_INDIVIDUALS_PROPERTY, indivi);
		
		if (kboxes != null) {
			p.setProperty(SEARCHENGINE_KBOX_LIST_PROPERTY, kboxes);
		}
		if (ontolo != null) {
			p.setProperty(SEARCHENGINE_INDEX_ONTOLOGIES_PROPERTY, ontolo);
		}
		if (select != null) {
			p.setProperty(SEARCHENGINE_INDEX_TYPES_PROPERTY, select);
		}
		
		SearchEngine engine = new SearchEngine(id, p);
		
		engine.setResourceFinder(resourceFinder);
		
		for (Extension.Parameter aext : ext.getParameters("index")) {
						
			String itype = aext.getSubParameter("type").valueAsString();
			
			Parameter iweight = aext.getSubParameter("weight");
			double weigh = 1.0;
			if (iweight != null)
				weigh = Double.parseDouble(iweight.valueAsString());
			
			IProperty property = 
				KnowledgeManager.get().requireProperty(
						aext.getSubParameter("property").valueAsString());

			engine.addIndexField(property, itype, weigh);
			
		}
		
		log.info("search engine " + id + " created");
		
		engines.add(engine);
		return engine;
	}
	
	/**
	 * Load the search engines specified in the passed plugin and set them in the
	 * engine repository. Must be called explicitly by plugins declaring search engines;
	 * otherwise it usually ends up being called too early, like when the kbox we want to
	 * index hasn't been created yet.
	 * 
	 * @param pluginId
	 * @throws ThinklabException
	 */
	public List<SearchEngine> loadSearchEngines(String pluginId) throws ThinklabException {

		ArrayList<SearchEngine> ret = new ArrayList<SearchEngine>();
		
		/*
		 * find all search engines declared by plugins. At this point we should have
		 * all kboxes and ontologies.
		 */
		for (Extension ext : getPluginExtensions(pluginId, PLUGIN_ID, "search-engine")) {
			ret.add(createSearchEngine(ext, getProperties()));
		}
		
		return ret;
	}
	

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
	}

	@Override
	protected void unload() throws ThinklabException {
		// drop all search engines, close them		
		engines.clear();
	}

	public SearchEngine getSearchEngine(String string) {
		
		for (SearchEngine s : engines) {
			if (s.getID().equals(string))
				return s;
		}
		
		return null;
	}	

}
