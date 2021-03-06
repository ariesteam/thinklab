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
package org.integratedmodelling.utils;

import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;

/**
 * Holds a polylist and facilitates creation of objects in list representation. Nothing fancy but
 * saves time. For the way polylist is implemented, I can't make it a list itself, so it's a 
 * little messier than it could be.
 * 
 * @author Ferdinando Villa
 *
 */
public class KList {

	Polylist list = null;

	public KList(String type) {
		list = Polylist.list(type);
	}

	public KList(String type, String name) {
		list = Polylist.list(type + "#" + name);
	}

	public void addLiteralProperty(IProperty p, String literal) {
		list = list.append(Polylist.list(Polylist.list(p, literal)));
	}
	
	public void addObjectProperty(IProperty p, Polylist object) {
		list = list.append(Polylist.list(Polylist.list(p, object)));
	}
		
	public void addReference(IProperty p, String id) {
		 list = list.append(Polylist.list(Polylist.list(p, Polylist.list("#" + id))));
	}
	
	public Polylist list() {
		return list;
	}

	public void addObjectProperty(String property, Polylist object) {
		list = list.append(Polylist.list(Polylist.list(property, object)));
	}

	public void addReference(String property, String name) {
		list = list.append(Polylist.list(Polylist.list(property, Polylist.list("#" + name))));
		
	}

	public void addLiteralProperty(String property, String literal) {
		list = list.append(Polylist.list(Polylist.list(property, literal)));
	}

	public void addElement(Polylist ccl) {
		list = list.append(Polylist.list(ccl));
	}

	public void addDescription(String comment) {
		list = list.append(Polylist.list("rdfs:comment", comment));
	}
	
	public void addLabel(String label) {
		list = list.append(Polylist.list("rdfs:label", label));
	}

}