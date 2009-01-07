/**
 * KMUtils.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabPersistencePlugin.
 * 
 * ThinklabPersistencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabPersistencePlugin is distributed in the hope that it will be useful,
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.persistence.factory;

import java.util.Collection;
import java.util.Set;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;

/**
 * A utility that traverses the KM ontologies and identifies all Concepts that are related 
 * to a given IConcept through object properties. The call is recursive.
 * 
 * @author Ioannis N. Athanasiadis
 * @since Feb 5, 2007
 * @version 0.2
 */
public class KMUtils {
	
	
	
	/**
	 * Returns all Concepts that are known to a Concept through object properties relations. Recursive method.
	 * @param concept
	 * @param associates
	 * @return
	 */
	public static Set<IConcept> getAllKnownConcepts(IConcept concept, Set<IConcept> associates){
		associates.add(concept);
		Collection<IConcept> parents = concept.getParents();
		parents.remove(KnowledgeManager.Thing());
		associates.addAll(parents);
		for(IProperty prop:concept.getProperties()){
			if(prop.isObjectProperty()){
				for(IConcept c : prop.getRange()){
					if(!associates.contains(c))
						associates.addAll(getAllKnownConcepts(c, associates));
				}
			}
		}
		return associates;
	}

	/**
	 * Returns true if the {@code concept} has at least one object property. 
	 * @param concept
	 * @return true 
	 */
	public static boolean containsObjectProperties(IConcept concept){
		boolean flag = false;
		for(IProperty p:concept.getProperties()){
			if(p.isObjectProperty()){
				flag = true;
			}
		}
		return flag;
	}
	
	
}
