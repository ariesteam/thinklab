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
package org.integratedmodelling.modelling.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.integratedmodelling.modelling.ModelMap;
import org.integratedmodelling.modelling.agents.ThinkAgent;
import org.integratedmodelling.modelling.annotation.Annotation;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.modelling.model.Scenario;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;

@ListingProvider(label="namespaces",itemlabel="namespace")
public class NamespaceLister implements IListingProvider {

	@Override
	public Collection<?> getListing() throws ThinklabException {
		
		ArrayList<String> ret = new ArrayList<String>();

		for (ModelMap.Entry e : ModelMap.getNamespaces()) {
			ret.add(e.toString());
		}
		Collections.sort(ret);
		return ret;
	}

	@Override
	public Collection<?> getSpecificListing(String item) throws ThinklabException {
		
		ArrayList<Object> ret = new ArrayList<Object>();

		for (IModelForm f : ModelMap.listNamespace(item)) {
			
			String prefix = "";
			if (f instanceof Scenario)
				prefix = "S";
			else if (f instanceof IModel)
				prefix = "M";
			else if (f instanceof ThinkAgent)
				prefix = "A";
			else if (f instanceof Annotation)
				prefix = "O";
			
			ret.add("   " + prefix + " " + f.getId());
		}
		
		return ret;
	}

	@Override
	public void notifyParameter(String parameter, String value) {
		// TODO Auto-generated method stub
		
	}

}
