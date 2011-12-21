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

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.utils.Polylist;

public class RelationshipList {

	IProperty property;
	ValueList value;
	
	public RelationshipList(Polylist polylist) throws ThinklabException {
		
		Object o = polylist.first();
		if (o instanceof IProperty)
			property = (IProperty)o;
		else
			property = KnowledgeManager.get().requireProperty(o.toString());
		
		value = new ValueList(polylist.second());
		
	}

	public IKnowledge getProperty() {
		return property;
	}

	public boolean isObject() {
		return value.isObject();
	}

	public ValueList getValue() {
		return value;
	}

	public boolean isClassification() {
		return value.isConcept();
	}

}
