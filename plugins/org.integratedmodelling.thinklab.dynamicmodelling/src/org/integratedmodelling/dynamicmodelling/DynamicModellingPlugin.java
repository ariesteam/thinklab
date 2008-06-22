/**
 * DynamicModellingPlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDynamicModellingPlugin.
 * 
 * ThinklabDynamicModellingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDynamicModellingPlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.dynamicmodelling;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.integratedmodelling.dynamicmodelling.commands.MDoc;
import org.integratedmodelling.dynamicmodelling.interfaces.IModelLoader;
import org.integratedmodelling.dynamicmodelling.interfaces.IModelLoaderConstructor;
import org.integratedmodelling.dynamicmodelling.loaders.DocumentationLoaderConstructor;
import org.integratedmodelling.dynamicmodelling.loaders.ModelDocumentationGenerator;
import org.integratedmodelling.dynamicmodelling.loaders.ModelOWLLoader;
import org.integratedmodelling.dynamicmodelling.loaders.OWLLoaderConstructor;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeLoaderPlugin;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.plugin.Plugin;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;
import org.w3c.dom.Node;

public class DynamicModellingPlugin extends Plugin implements IKnowledgeLoaderPlugin {

	static final public String FLOW_TYPE = "measurement:Ranking";
	static final public String STOCK_TYPE = "measurement:Ranking";
	static final public String VARIABLE_TYPE = "measurement:Ranking";
	static final public String FLOW_DATASOURCE = "dynmod:FlowVariable";
	static final public String STOCK_DATASOURCE = "dynmod:StockVariable";
	static final public String VARIABLE_DATASOURCE = "dynmod:ComputedVariable";
	static final public String HAS_INFLOW = "dynmod:hasInflow";
	static final public String HAS_OUTFLOW = "dynmod:hasOutflow";
	
	/* log4j logger used for this class. Can be used by other classes through logger()  */
	private static  Logger log = Logger.getLogger(DynamicModellingPlugin.class);
	
	private HashMap<String, IModelLoaderConstructor> modelLoaders = new HashMap<String, IModelLoaderConstructor>();
	private ArrayList<String> htmlResources = new ArrayList<String>();

	static final public String ID = "DynamicModelling";
	static final public String DEFAULT_LOADER = "observation";
	public static final String STOCK_INITVALUE_LITERAL = "dynmod:hasInitialValue";
	
	public void initialize() throws ThinklabException {

		ModelDocumentationGenerator.initialize(this);
	}
	
	public static DynamicModellingPlugin get() {
		return (DynamicModellingPlugin) getPlugin(ID);
	}

	public static Logger logger() {
		return log;
	}
	
	public void load(KnowledgeManager km, File baseReadPath, File baseWritePath)
			throws ThinklabPluginException {
		
		try {
			
			// install temporary command to parse a file. 
			new MDoc().install(km);
			
		} catch (ThinklabException e) {
			throw new ThinklabPluginException(e);
		}

		/*
		 * register default and other known loaders
		 */
		registerModelLoader(DEFAULT_LOADER, new OWLLoaderConstructor());
		registerModelLoader("doc", new DocumentationLoaderConstructor());
	}

	/**
	 * Construct a new model loader for passed type and return it. If no such loader type is registered, return
	 * null without complaining.
	 *  
	 * @param id the type of model loader desired. Must match a constructor registered with registerModelLoader.
	 * @return a new model loader of a type matching the passed id.
	 * @throws ThinklabException 
	 * @see registerModelLoader
	 */
	public IModelLoader retrieveModelLoader(String id) throws ThinklabException {
		
		IModelLoader ret = null;
		IModelLoaderConstructor mc = modelLoaders.get(id);
		if (mc != null) {
			ret = mc.createModelLoader();
		}
		return ret;
	}
	
	/**
	 * Register a constructor for a new model loader.
	 * 
	 * @param id
	 * @param constructor
	 */
	public void registerModelLoader(String id, IModelLoaderConstructor constructor) {
		modelLoaders.put(id, constructor);
	}
	
	
	public void notifyResource(String name, long time, long size)
			throws ThinklabException {

		if (name.startsWith("resources/")) {
			htmlResources.add(name);
		}

	}

	public void unload(KnowledgeManager km) throws ThinklabPluginException {
		// TODO Auto-generated method stub

	}

	public boolean handlesFormat(String format) {
		return format.equals("simile") || format.equals("sml");
	}

	public Collection<IInstance> loadKnowledge(URL url, ISession session, IKBox kbox) throws ThinklabException {

		/* look for installed observation loader */
		IModelLoader l = DynamicModellingPlugin.get().retrieveModelLoader("observation");
			
		// default to OWL loader
		if (l == null) {
			l = new ModelOWLLoader();
		}
		
		Collection<Polylist> instances = l.loadModel(url.toString());
		ArrayList<IInstance> ret = new ArrayList<IInstance>();
		
		if (instances != null)
			for (Polylist list : instances) {
				if (list != null) {
					IInstance i = session.createObject(list);
					ret.add(i);
					if (kbox != null) {
						kbox.storeObject(i, session);
					}
				}
			}

		return ret;
	}

	public void copyHTMLResources(File docPath) throws ThinklabException {
		
		// copy all resources coming from /resources path in jar into passed dir, 
		// omitting the resources/ thing.
		for (String res : htmlResources) {
			
			InputStream inp = this.retrieveResource(res);
			String fileName = docPath + "/" + res.substring(10);
			try {
				MiscUtilities.writeToFile(fileName, inp, true);
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
			
		}
			
		
	}

	/* wouldn't it be nice, translate SIMILE to STELLA and back. */
	public void writeKnowledge(File outfile, String format, IInstance... instances)
			throws ThinklabException {
		throw new ThinklabInappropriateOperationException(
				"output to proprietary model formats is unsupported. " +
				"Please serialize models to XML.");
		
	}

	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}
	
	

}
