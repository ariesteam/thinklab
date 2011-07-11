/**
 * InstanceFeatureImporter.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.feature;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.utils.PropertiesTemplateAdapter;

/**
 * Implements the notifyFeature method to create observations according to specialized properties.
 */
public abstract class InstanceFeatureImporter extends FeatureImporter {

	Hashtable<String, Serializable> variables = new Hashtable<String, Serializable>();
	
	PropertiesTemplateAdapter tengine = null;
	
	@SuppressWarnings("unchecked")
	public InstanceFeatureImporter(URL url, Properties properties) throws ThinklabException {
		
		super(url, properties);
		
		tengine = 
			new PropertiesTemplateAdapter(getProperties(), "geospace." + getLayerName() + ".variable");
	}

	public void notifyFeature(String featureID, ShapeValue shape,
			String[] attributeNames, IValue[] attributeValues) throws ThinklabException {

		Hashtable<String, Object> vmap = new Hashtable<String,Object>();
		
		vmap.put("shape", shape);
		vmap.put("featureID", featureID);
		
		int i = 0;
		for (String att : attributeNames) {
			vmap.put(att, attributeValues[i++]);
		}

		ArrayList<Pair<String, Polylist>> ilists = 
			tengine.computeListsFromTemplates("geospace." + getLayerName() + ".instance.template", vmap);
		
		for (Pair<String, Polylist> t : ilists) {	
				notifyInstance(t.getSecond());
		}		
	}

	public abstract void notifyInstance (Polylist list) throws ThinklabException;
	
}
