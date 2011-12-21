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
package org.integratedmodelling.thinklab.interfaces.knowledge;

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Extends IKnowledge to define "subjects", meaning first-class objects that have a right to exist independently 
 * in a knowledge space. This includes IConcept and IInstance but not IProperty, as we see properties as connectors for 
 * KnowledgeSubject that should not exist alone.
 * 
 * KnowledgeSubject has semantic types, annotations, and relationship access methods.
 * 
 * FIXME this interface is in dire need of a better name.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract interface IKnowledgeSubject extends IKnowledge {

	/**
	 * Return the type. If this is an instance, return its direct type; if it is
	 * a type, return self. Otherwise, return whatever is appropriate.
	 * @return
	 */
	public abstract IConcept getType();
	
	/**
	 * Return a collection of all relationships of this to classes, objects, or literals.
	 * @return
	 * @throws ThinklabException TODO
	 */
	public abstract Collection<IRelationship> getRelationships() throws ThinklabException;
	
	/**
	 * Return a collection of all relationships to other classes, objects or literals connected to this via the named property.
     * @param property the semantic type of the property (versions with SemanticType and IProperty will follow)
     * @return a collection, possibly empty
     * @throws ThinklabException in various obvious cases
	 */
	public abstract Collection<IRelationship> getRelationships(String property) throws ThinklabException;

	/**
	 * Return a collection of all relationships to other classes, objects or literals connected to this via the named property
	 * or any of its subproperties.
     * @param property the semantic type of the property (versions with SemanticType and IProperty will follow)
     * @return a collection, possibly empty
     * @throws ThinklabException in various obvious cases
	 */
	public abstract Collection<IRelationship> getRelationshipsTransitive(String property) throws ThinklabException;
	
	/**
	 * Get the number of relationships in the instance that happen through the given
	 * property.
	 * @param property
	 * @return
	 */
	public abstract int getNumberOfRelationships(String property) throws ThinklabException;

	
	/**
	 * The basic function to extract the value of a property. Could have a better name but
	 * it's so basic that I like a basic name. The IValue returned will be of the proper
	 * type; if the property has more than one value on this object, the IValue will be
	 * a ListValue.
	 * 
	 * @param property
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IValue get(String property) throws ThinklabException;
	
}
