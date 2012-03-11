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
package org.integratedmodelling.utils.image;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.ConfigurableIntelligentMap;

/**
 * Maps concepts to colormaps. The modelling plugin has one of these and an API + commands to access it.
 * 
 * @author Ferdinando
 *
 */
public class ColormapChooser extends ConfigurableIntelligentMap<ColorMap> {

	public ColormapChooser(String propertyPrefix) {
		super(propertyPrefix);
	}

	@Override
	protected ColorMap getObjectFromPropertyValue(String pvalue, Object[] parameters) throws ThinklabException {
	
		if (parameters == null || parameters.length == 0 || !(parameters[0] instanceof Integer)) {
			throw new ThinklabValidationException("ColormapChooser: must pass the number of levels");
		}
		
		int levels = (Integer)parameters[0];
		Boolean isz = parameters.length > 1 ? (Boolean)parameters[1] : null;
		
		ColorMap ret = null;
		String[] pdefs = pvalue.split(","); 
		       
		for (String pdef : pdefs) {
			
			pdef = pdef.trim();
			String[] ppd = pdef.split("\\s+");
			
			String cname = ppd[0] + "(";
			
			for (int i = 1; i < ppd.length; i++) {
				cname += (ppd[i] + (i == (ppd.length-1) ? "" : ","));
			}
			cname += ")";
			
			ret = ColorMap.getColormap(cname, levels, isz);
			if (ret != null)
				break;
		}
		       
		return ret;
	}


}
