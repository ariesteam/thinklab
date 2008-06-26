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

import java.util.HashMap;

import org.integratedmodelling.districting.algorithms.ISODATAAlgorithmConstructor;
import org.integratedmodelling.districting.algorithms.KMeansAlgorithmConstructor;
import org.integratedmodelling.districting.interfaces.IDistrictingAlgorithm;
import org.integratedmodelling.districting.interfaces.IDistrictingAlgorithmConstructor;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class DistrictingPlugin extends ThinklabPlugin {

	private HashMap<String, IDistrictingAlgorithmConstructor> districtingAlgorithms = 
		new HashMap<String, IDistrictingAlgorithmConstructor>();

	static final public String PLUGIN_ID = "org.integratedmodelling.thinklab.geoclustering";
	static final public String DEFAULT_ALGORITHM = "k-means";

	public static DistrictingPlugin get() throws ThinklabPluginException {
	    return (DistrictingPlugin) getPlugin(PLUGIN_ID);
	}
	
	public void load(KnowledgeManager km) throws ThinklabPluginException {
	    
	    /*
	     * register default and other known districting algorithms
	     * TODO these should go as new extensions
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
	
	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub		
	}

}