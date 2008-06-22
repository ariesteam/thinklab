/**
 * DistrictingPlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Feb 05, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDistrictingPlugin.
 * 
 * ThinklabDistrictingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDistrictingPlugin is distributed in the hope that it will be useful,
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
 * @author    Gary Johnson (gwjohnso@uvm.edu)
 * @date      Feb 05, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.districting;

import java.io.File;
import java.util.HashMap;
import java.net.URL;

import org.apache.log4j.Logger;
import org.integratedmodelling.districting.algorithms.ISODATAAlgorithmConstructor;
import org.integratedmodelling.districting.algorithms.KMeansAlgorithmConstructor;
import org.integratedmodelling.districting.commands.District;
import org.integratedmodelling.districting.interfaces.IDistrictingAlgorithm;
import org.integratedmodelling.districting.interfaces.IDistrictingAlgorithmConstructor;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.IPlugin;
import org.integratedmodelling.thinklab.plugin.Plugin;
import org.w3c.dom.Node;

public class DistrictingPlugin extends Plugin implements IPlugin {

	/* log4j logger used for this class. Can be used by other classes through logger() */
	private static Logger log = Logger.getLogger(DistrictingPlugin.class);

        private HashMap<String, URL> resources = new HashMap<String, URL>();
	private HashMap<String, IDistrictingAlgorithmConstructor> districtingAlgorithms
	    = new HashMap<String, IDistrictingAlgorithmConstructor>();

	static final public String ID = "Districting";
	static final public String DEFAULT_ALGORITHM = "k-means";

	public static DistrictingPlugin get() {
	    return (DistrictingPlugin) getPlugin(ID);
	}

	public static Logger logger() {
	    return log;
	}
	
	public void load(KnowledgeManager km, File baseReadPath, File baseWritePath)
	              throws ThinklabPluginException {
	    try {
		// install command to run a selected districting
		// algorithm on a spatial dataset

		new District().install(km);

	    } catch (ThinklabException e) {
		throw new ThinklabPluginException(e);
	    }
	    
	    /*
	     * register default and other known districting algorithms
	     */
	    registerDistrictingAlgorithm("k-means", new KMeansAlgorithmConstructor());
	    registerDistrictingAlgorithm("isodata", new ISODATAAlgorithmConstructor());
	}

        public void registerDistrictingAlgorithm(String id, IDistrictingAlgorithmConstructor constructor) {
	    districtingAlgorithms.put(id, constructor);
	}

	public IDistrictingAlgorithm retrieveDistrictingAlgorithm(String id) throws ThinklabException {
	    IDistrictingAlgorithm da = null;
	    IDistrictingAlgorithmConstructor dac = districtingAlgorithms.get(id);
	    if (dac != null) {
		da = dac.createDistrictingAlgorithm();
	    }
	    return da;
	}
	
	public void unload(KnowledgeManager km) throws ThinklabPluginException {
	}

	public void notifyConfigurationNode(Node n) {
	}

	public void notifyResource(String name, long time, long size)
	              throws ThinklabException {

//		
//	    if (name.endsWith(".clj")) {
//		log.info("Districting: found Clojure file: " + name);
//	    } else if (name.endsWith(".lisp")) {
//		log.info("Districting: found Common Lisp file: " + name);
//	    } else if (name.endsWith(".abcl")) {
//		log.info("Districting: found ABCL file (JAR): " + name);
//	    }
//
//	    resources.put(name, exportResourceCached(name));
	}

	public void initialize() throws ThinklabException {
	    // Create the Common Lisp interpreter instance
	    // log.info("Districting: Creating an ABCL Interpreter...");
	    // interpreter = Interpreter.createInstance();
	}

        public String getResourcePath(String resourceID) throws ThinklabException {
            return resources.get(resourceID).getFile();
        }

}