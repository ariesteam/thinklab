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
package org.integratedmodelling.utils.instancelist;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.lang.IList;

/**
 * @deprecated there's a newer version in thinklab-api that needs to be integrated
 */
public class ValueList {

	String sValue = null;
	IList lValue = null;
	IConcept cValue = null;
	
	public ValueList(Object o) throws ThinklabException {
		
		if (o instanceof IList) {
			lValue = (IList)o;
		} else if (o instanceof IConcept) {
			cValue = (IConcept)o;
		} else {
			if (SemanticType.validate(o.toString())) {
				cValue = KnowledgeManager.get().retrieveConcept(o.toString());
			}
			if (cValue == null) {
				sValue = o.toString();
			}
		}
	}

	public InstanceList asInstanceList() {
		return new InstanceList(lValue);
	}
	
	public String asString() {

		String ret = sValue;
		if (ret == null && cValue != null) {
			ret = cValue.toString();
		}
		return ret;
	}
	
	public boolean isObject() {
		return lValue != null;
	}

	public boolean isConcept() {
		// TODO Auto-generated method stub
		return cValue != null;
	}

}