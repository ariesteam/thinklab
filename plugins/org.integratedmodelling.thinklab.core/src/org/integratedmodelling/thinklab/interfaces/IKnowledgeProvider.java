/**
 * IKnowledgeProvider.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Apr 25, 2008
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
 * @date      Apr 25, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.interfaces;

import java.util.Collection;

import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Tentative interface for the knowledge read/check operations of the knowledge manager.
 * Will become the main interface for a client KM that connects to a server KM.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IKnowledgeProvider {

	public abstract IConcept getRootConcept();

	public abstract IConcept getBooleanType()
			throws ThinklabValidationException;

	public abstract IConcept getDoubleType() throws ThinklabValidationException;

	public abstract IConcept getFloatType() throws ThinklabValidationException;

	public abstract IConcept getIntegerType()
			throws ThinklabValidationException;

	public abstract IConcept getLongType() throws ThinklabValidationException;

	public abstract IConcept getNumberType() throws ThinklabValidationException;

	public abstract IConcept getTextType() throws ThinklabValidationException;

	public abstract String getURIFromConceptSpace(String cs)
			throws ThinklabResourceNotFoundException;

	public abstract String getConceptSpaceFromURI(String cs)
			throws ThinklabResourceNotFoundException;

	/**
	 * Return concept from string representing semantic type. Concept must exist.
	 * @param id the semantic type
	 * @return the concept.
	 * @throws ThinklabMalformedSemanticTypeException if semantic type is malformed
	 * @throws ThinklabResourceNotFoundException if concept not found.
	 */
	public abstract IConcept requireConcept(String id)
			throws ThinklabMalformedSemanticTypeException,
			ThinklabResourceNotFoundException;

	/**
	 * Return property from string representing semantic type. Property must exist.
	 * @param id the semantic type
	 * @return the property
	 * @throws ThinklabMalformedSemanticTypeException if semantic type is malformed
	 * @throws ThinklabResourceNotFoundException if property not found
	 */
	public abstract IProperty requireProperty(String id)
			throws ThinklabMalformedSemanticTypeException,
			ThinklabResourceNotFoundException;

	/**
	 * Return instance from string representing semantic type. Instance must exist.
	 * @param id the semantic type
	 * @return the instance
	 * @throws ThinklabMalformedSemanticTypeException if semantic type is malformed
	 * @throws ThinklabResourceNotFoundException if instance not found
	 */
	public abstract IInstance requireInstance(String id)
			throws ThinklabMalformedSemanticTypeException,
			ThinklabResourceNotFoundException;

	public abstract IConcept getConceptFromURI(String uri);

	public abstract IProperty getPropertyFromURI(String uri);

	public abstract IKnowledgeSubject retrieveInstance(String resultID)
			throws ThinklabMalformedSemanticTypeException;

	/**
	 * Retrieve property from string ID.
	 * @param prop
	 * @return
	 * @throws ThinklabMalformedSemanticTypeException
	 */
	public abstract IProperty retrieveProperty(String prop)
			throws ThinklabMalformedSemanticTypeException;

	/**
	 * Retrieve concept from string ID.
	 * @param prop
	 * @return
	 * @throws ThinklabMalformedSemanticTypeException
	 */
	public abstract IConcept retrieveConcept(String prop)
			throws ThinklabMalformedSemanticTypeException;

	/**
	 * Retrieve object from passed uri and create it in the passed session.
	 * @param uri
	 * @param session
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IInstance getInstanceFromURI(String uri, ISession session)
			throws ThinklabException;

	public abstract IConcept getLeastGeneralCommonConcept(
			SemanticType semanticType, SemanticType otherConcept)
			throws ThinklabResourceNotFoundException;

	/**
	 * Return the least general ancestor of both concepts passed, or null if there is none.
	 * @param concept1
	 * @param c
	 * @return
	 */
	public abstract IConcept getLeastGeneralCommonConcept(IConcept concept1,
			IConcept c);

	/**
	 * Return the least general common concept in a collection of concept, or null if there
	 * is none.
	 * @param cc
	 * @return
	 */
	public abstract IConcept getLeastGeneralCommonConcept(
			Collection<IConcept> cc);

	/**
	 * Find the concept manager that can validate a literal of the passed concept and validate the passed
	 * string into the proper literal.
	 * @param c A concept to validate to
	 * @param literal a literal representing an instance of that concept
	 * @return a Value containing the concept
	 * @throws ThinklabValidationException
	 */
	public abstract IValue validateLiteral(IConcept c, String literal,
			IOntology ontology) throws ThinklabValidationException;
	
	public abstract void blacklistProperty(String semType);

	public abstract void blacklistConcept(String semType);

	public abstract boolean isConceptBlacklisted(String c);

	public abstract boolean isPropertyBlacklisted(String c);

}