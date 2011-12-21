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

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.utils.NameGenerator;

/**
 * An object that wraps an instance definition list and provides access methods and 
 * associated object that mimic the IInstance methods. Allows inspecting an instance
 * structure from its list representation without having to create the list. OPAL 
 * provides a styled translator to XML.
 * 
 * @author Ferdinando Villa
 * @deprecated there's a newer version in thinklab-api that needs to be integrated
 *
 */
public class InstanceList {

	Object[] array = null;
	
	public InstanceList(IList list) {
		array = list.array();
	}

	public String getLocalName() {

		String s = array[0].toString();
		String[] ss = s.split("#");
		
		return ss.length == 2 ? ss[1] : NameGenerator.newName("inst");
	}

	public IList asList() {
		return PolyList.fromArray(array);
	}
	
	public IConcept getDirectType() throws ThinklabException {
		String s = array[0].toString();
		String[] ss = s.split("#");		
		return KnowledgeManager.get().requireConcept(ss[0]);
	}

	public String getId() throws ThinklabException {
		String s = array[0].toString();
		String[] ss = s.split("#");		
		return ss.length > 1 ? ss[1] : null;
	}

	
	public String getLabel() {
		
		String ret = null;
		
		for (int i = 1; i < array.length; i++) {
			if (array[i] instanceof IList && 
					((IList)array[i]).first().toString().equals("rdfs:label") ) {
				ret = ((IList)array[i]).nth(1).toString();
				break;
			}	
		}
		return ret;
	}

	public IInstanceImplementation getImplementation() {

		IInstanceImplementation ret = null;
		
		for (int i = 1; i < array.length; i++) {
			if (array[i] instanceof IList && 
					((IList)array[i]).first().toString().equals("#") ) {
				ret = (IInstanceImplementation) ((IList)array[i]).nth(1);
				break;
			}	
		}
		return ret;
		
	}
	
	public String getDescription() {
		String ret = null;
		
		for (int i = 1; i < array.length; i++) {
			if (array[i] instanceof IList && 
					((IList)array[i]).first().toString().equals("rdfs:comment") ) {
				ret = ((IList)array[i]).nth(1).toString();
				break;
			}	
		}
		return ret;
	}

	public Collection<RelationshipList> getRelationships() throws ThinklabException {
		
		ArrayList<RelationshipList> ret = new ArrayList<RelationshipList>();
		
		for (int i = 1; i < array.length; i++) {
			if (array[i] instanceof IList) {
				String s = ((IList)array[i]).first().toString();
				
				if (!(s.equals("rdsf:label") || 
					  s.equals("rdfs.comment"))) {
					ret.add(new RelationshipList((IList)array[i]));
				}
				
			}
		}
		return ret;
	}

	public boolean hasLiteralContent() {

		for (int i = 1; i < array.length; i++)
			if (!(array[i] instanceof IList))
				return true;
		return false;
	}
	
	public Object getLiteralContent() {

		for (int i = 1; i < array.length; i++)
			if (!(array[i] instanceof IList))
				return array[i];
		return null;

	}

	/**
	 * Return the target of the given represented relationship assuming it's a concept or specifies one.
	 * 
	 * @param relationship
	 * @return
	 * @throws ThinklabException
	 */
	public IConcept getTargetConcept(String relationship) throws ThinklabException {

		IConcept ret = null;
		
		for (int i = 1; i < array.length; i++) {
			if (array[i] instanceof IList) {
				String s = ((IList)array[i]).first().toString();
				
				if (s.equals(relationship)) {
					
					Object o = ((IList)array[i]).nth(1);
					
					if (o instanceof IValue) {
						ret = ((IValue)o).getConcept();
					} else if (o instanceof IList) {
						/* instance specification */
						ret = resolveToConcept(((IList)o).first());
					} else {
						ret = resolveToConcept(o);
					}
				}
			}
		}
		return ret;
	}

	private IConcept resolveToConcept(Object o) throws ThinklabException {

		IConcept ret = null;

		if (o instanceof IConcept) {
			ret = (IConcept)o;
		} else {
			ret = KnowledgeManager.get().requireConcept(o.toString());
		}
		
		return ret;
	}

	public Collection<RelationshipList> getRelationships(String property) throws ThinklabException {

		ArrayList<RelationshipList> ret = new ArrayList<RelationshipList>();
		
		for (RelationshipList rel : getRelationships()) {
			if (rel.property.is(property))
				ret.add(rel);
		}
		return ret;
	}

}