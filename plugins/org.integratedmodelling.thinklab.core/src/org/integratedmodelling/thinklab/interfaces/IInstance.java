/**
 * IInstance.java
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
package org.integratedmodelling.thinklab.interfaces;

import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.Polylist;

/**
 * <p>
 * Instances are the "objects" in the knowledge base. Instances can be contained
 * in the shared knowledge base (and retrieved through the knowledge manager),
 * in Sessions (where they can be added and retrieved directly) and in KBoxes
 * (where they are added and retrieved directly or through queries). In these
 * docs, the words "Instance" and "Object" are used interchangeably.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> you're not supposed to create an instance directly. Only an
 * appropriate container (e.g. a Session) can do that, either on request through
 * the API or as the result of a load operation. If created through the API, an
 * instance won't be part of the container until validated (see below).
 * </p>
 * 
 * <p>
 * Instances can have a Java "implementation" which is created by the
 * appropriate concept manager after successful validation, if a concept manager
 * with a constructor is defined for the class or one of its parents. This
 * provides the instance with arbitrary Java content that implementations can
 * use as necessary.
 * </p>
 * 
 * <p>
 * Instances are the only knowledge objects that can be created through the API.
 * It is required to validate an instance after it's been defined, or the
 * implementation will not be created. Classes implementing IInstance should be
 * able to distinguish non-validated instances and refuse to use, store, and
 * make them visible through the retrieval interface.
 * </p>
 * 
 * <p>
 * The IMA Instance definition interface provides (I believe) the simplest
 * paradigm to creating knowledge through an API. Relationships are classified
 * into three possible classes and methods are provided for each of them. OWL
 * underpinnings are dealt with transparently and OWL-DL compatibility is
 * preserved even in OWL-FULL constructs (e.g. classification relationships).
 * </p>
 * 
 * <p>
 * In addition to representing knowledge, instances are also capable of
 * executing commands and this functionality interfaces with the IMA-supported
 * expression languages in ways that depend on the implementation.
 * </p>
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * @author Ioannis N. Athanasiadis
 * @see ISession
 */
public interface IInstance extends IKnowledgeSubject, ICommandListener {

	/**
	 * Get the Java implementation, if any, created by the appropriate
	 * ConceptManager after validation.
	 * 
	 * @return the Java implementation of the instance, or null if none exists.
	 * @throws ThinklabException
	 */
	public abstract IInstanceImplementation getImplementation()
			throws ThinklabException;

	/**
	 * return the serialization of all relationships and concepts as a list,
	 * which can be validated back into a concept. Related instances are also
	 * serialized. Multiple occurrences of the same related instance are handled
	 * through the reference mechanism discussed in TODO.
	 * 
	 * @param oref
	 *            a string to use as the instance's ID. If null is passed, the
	 *            instance local name is used.
	 * @throws ThinklabException
	 */
	public abstract Polylist toList(String oref) throws ThinklabException;

	/**
	 * Like toList() but passing a preexisting table of references that have been stored
	 * previously, and must be only referenced and not defined in the resulting list. Expected
	 * to add any defined instance to the reference table with its (ID,URI) pair if the
	 * object is not already present.
	 * 
	 * @param oref
	 * @param refTable
	 * @return
	 * @throws ThinklabException
	 */
	public abstract Polylist toList(String oref,
			HashMap<String, String> refTable) throws ThinklabException;

	/**
	 * Add a relationship between the instance and a class through passed
	 * property.
	 * 
	 * @category Modifying methods
	 * @param p
	 *            a property name
	 * @param cls
	 *            a class
	 * @throws ThinklabException
	 */
	public abstract void addClassificationRelationship(String p, IConcept cls)
			throws ThinklabException;

	/**
	 * Add a relationship between the instance and a class through passed
	 * property.
	 * 
	 * @category Modifying methods
	 * @param p
	 *            a property
	 * @param cls
	 *            a class
	 * @throws ThinklabException
	 */
	public abstract void addClassificationRelationship(IProperty p, IConcept cls)
			throws ThinklabException;

	/**
	 * Add a relationship between the instance and a literal through passed
	 * property.
	 * 
	 * @category Modifying methods
	 * @param p
	 *            a property
	 * @param literal
	 *            a literal value or an Object suitable for a datatype property
	 * @throws ThinklabException
	 */
	public abstract void addLiteralRelationship(IProperty p, Object literal)
			throws ThinklabException;

	/**
	 * Add a relationship between the instance and a literal through passed
	 * property.
	 * 
	 * @category Modifying methods
	 * @param p
	 *            a property name
	 * @param literal
	 *            a literal value or an Object suitable for a datatype property
	 * @throws ThinklabException
	 */
	public abstract void addLiteralRelationship(String p, Object literal)
			throws ThinklabException;

	/**
	 * Add a relationship between the instance and another Instance through
	 * passed property.
	 * 
	 * @category Modifying methods
	 * @param p
	 *            a property
	 * @param object
	 *            an object value
	 * @throws ThinklabException
	 */
	public abstract void addObjectRelationship(IProperty p, IInstance object)
			throws ThinklabException;

	/**
	 * Add a relationship between the instance and another Instance through
	 * passed property.
	 * 
	 * @category Modifying methods
	 * @param p
	 *            a property name
	 * @param object
	 *            an object value
	 * @throws ThinklabException
	 */
	public abstract void addObjectRelationship(String p, IInstance instance)
			throws ThinklabException;

	/**
	 * <p>
	 * Validate instance according to the IMA validation criteria. Use the most
	 * sensible default for OWL validation, according to implementation.
	 * </p>
	 * 
	 * <p>
	 * Validating an instance is necessary to make it part of its container. An
	 * instance will not be retrievable or usable before it's validated.
	 * </p>
	 * 
	 * @throws ThinklabValidationException
	 *             if things are invalid.
	 * @throws ThinklabException
	 */
	public abstract void validate() throws ThinklabException;

	/**
	 * <p>
	 * Validate instance according to the IMA validation criteria, which are not
	 * OWL's and use constraints and other things on literals. Optionally
	 * validate the resulting OWL instance using the installed OWL reasoner.
	 * </p>
	 * 
	 * <p>
	 * Validating an instance is necessary to make it part of its container. An
	 * instance will not be retrievable or usable before it's validated.
	 * </p>
	 * 
	 * @param validateOWL
	 *            pass true if you want to invoke the OWL reasoner to validate
	 *            the instance after IMA validation is successful. This is
	 *            optional and its behavior and execution time will depend on
	 *            the reasoner installed.
	 * 
	 * @throws ThinklabValidationException
	 *             if things are invalid.
	 */
	public abstract void validate(boolean validateOWL) throws ThinklabException;

	/**
	 * Check if instance has been validated successfully. Should never return
	 * true unless both the underlying ontology framework and the IMA consider
	 * the instance valid. Note that a return value of false means that it has
	 * not been validated, not that it's not valid in general. Validation in the
	 * IMA may create implementations or change/normalize properties so it's a
	 * crucial operation.
	 * 
	 * @return true if instance has been validated successfully.
	 */
	public abstract boolean isValidated();

	/**
	 * Get the direct type, i.e. the concept this is a direct instance of.
	 * 
	 * @return
	 */
	public IConcept getDirectType();

	/**
	 * Get a collection of all instances that are the same thing as this is.
	 * 
	 * @return A collection, possibly empty.
	 */
	public Collection<IInstance> getEquivalentInstances();

	/**
	 * Create an instance identical to this one in the passed ontology and
	 * return it. All linked objects are cloned as well, ontologies imported as
	 * necessary.
	 * 
	 * @param ontology
	 * @return
	 * @throws ThinklabException
	 */
	public IInstance clone(IOntology session) throws ThinklabException;

	/**
	 * Return true if the passed instance conforms to this according to the
	 * passed notion of conformance.
	 * 
	 * @param otherInstance
	 * @param conformance
	 * @return
	 * @throws ThinklabException
	 */
	public boolean isConformant(IInstance otherInstance,
			IConformance conformance) throws ThinklabException;

	/**
	 * Set the implementation object manually. If an instance has an implementation before it is
	 * validated, validation will be skipped by default. 
	 * 
	 * @param second
	 * @throws ThinklabException 
	 */
	public abstract void setImplementation(IInstanceImplementation second) throws ThinklabException;

}
