/**
 * KList.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.utils;

import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.lang.IList;

/**
 * Holds a PolyList and facilitates creation of objects in list representation. Nothing fancy but
 * saves time. For the way PolyList is implemented, I can't make it a list itself, so it's a 
 * little messier than it could be.
 * 
 * @author Ferdinando Villa
 *
 */
public class KList {

	IList list = null;

	public KList(String type) {
		list = PolyList.list(type);
	}

	public KList(String type, String name) {
		list = PolyList.list(type + "#" + name);
	}

	public void addLiteralProperty(IProperty p, String literal) {
		list = list.append(PolyList.list(PolyList.list(p, literal)));
	}
	
	public void addObjectProperty(IProperty p, PolyList object) {
		list = list.append(PolyList.list(PolyList.list(p, object)));
	}
		
	public void addReference(IProperty p, String id) {
		 list = list.append(PolyList.list(PolyList.list(p, PolyList.list("#" + id))));
	}
	
	public IList list() {
		return list;
	}

	public void addObjectProperty(String property, PolyList object) {
		list = list.append(PolyList.list(PolyList.list(property, object)));
	}

	public void addReference(String property, String name) {
		list = list.append(PolyList.list(PolyList.list(property, PolyList.list("#" + name))));
		
	}

	public void addLiteralProperty(String property, String literal) {
		list = list.append(PolyList.list(PolyList.list(property, literal)));
	}

	public void addElement(PolyList ccl) {
		list = list.append(PolyList.list(ccl));
	}

	public void addDescription(String comment) {
		list = list.append(PolyList.list("rdfs:comment", comment));
	}
	
	public void addLabel(String label) {
		list = list.append(PolyList.list("rdfs:label", label));
	}

}