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
package org.integratedmodelling.thinklab.webapp.view;

import java.util.Iterator;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;

public class VisualProperty extends VisualKnowledge {

	IProperty property;
	
	public VisualProperty(IProperty p, TypeManager tp) {
		property = p;
	}
	
	public String getLabel()  {

		String ret = property.getLabel();
		
		if (ret == null || ret.equals("")) {

			ret = property.getLocalName();
			/* reparse camel into sentence */
			ret = checkLowerCamel(ret);
		}
		
		return ret;
	}

	
	public String getName()  {

		String ret = property.getLocalName();
		ret = checkLowerCamel(ret);
		
		return ret;
	}
	
	public String getDescription() {
		
		String ret = property.getDescription();
		if (ret == null || ret.equals(""))
			ret = "No description given";
		return ret;
	}

	public IProperty getProperty() {
		return property;
	}

	/**
	 * Return the ID of the concept that is going to be visible as the range of this property. For now
	 * simply returns the first concept in the range, or null.
	 * TODO allow disambiguation through type decorations.
	 * @return
	 */
	public IConcept getVisualizedRange() {
		// TODO Auto-generated method stub
		Iterator<IConcept> iter = property.getRange().iterator();
		return iter.hasNext() ? iter.next() : null;
	}
}
