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
package org.integratedmodelling.geospace.feature;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;
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
