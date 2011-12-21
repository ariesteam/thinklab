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
package org.integratedmodelling.thinklab.graph;

import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;

public class PropertyGraph extends KnowledgeGraph {

	Set<String> properties = new HashSet<String>();
	
	public PropertyGraph(IKnowledgeSubject root, String property) {
		
	}
	
	public PropertyGraph(IKnowledgeSubject root, String[] property) {
		
	}
	
	/**
	 * Property graph for all classes (except the root of all classes) and the given property
	 */
	public PropertyGraph(String property) {
		
	}

	/**
	 * Property graph for all classes (except the root of all classes) and the given properties
	 */
	public PropertyGraph(String[] property) {
		
	}
	
	@Override
	protected boolean followRelationship(IKnowledgeSubject source, IRelationship relationship, IConcept target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean followProperty(IConcept source, IConcept target,
			IProperty property) {
		// TODO Auto-generated method stub
		return false;
	}

}
