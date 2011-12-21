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

import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * The main interface that a concept must support. Note that many of the methods require that some sort of reasoner is connected, and in Jena
 * the choice of reasoner will change the results. This is
 * not optional in the IMA: implementations that do not provide these functionalities as expected are considered incomplete, and the results
 * of all ops on concepts should be the same for the same ontologies regardless of the implementation choices.
 * @author Ferdinando Villa
 */
public interface IConcept extends IKnowledgeSubject {

    /**
     * Return a collection of all the direct parent classes.
     * @return
     */
	public abstract Collection<IConcept> getParents();

    /**
     * Return a collection of all direct and indirect parent classes. Should use a reasoner if
     * installed, and only follow transitive superclass relationships if not.
     * @return
     */
	public abstract Collection<IConcept> getAllParents();
	
    /**
     * Return a collection of all direct subclasses.
     * @return all child concepts.
     */
	public abstract Collection<IConcept> getChildren();

	/**
	 * Return all properties that are  defined as having this in their domain
	 * @return
	 */
	public abstract Collection<IProperty> getDirectProperties();
	
	

	/**
	 * Return all properties that have or may have this in their domain, including their
	 * subproperties.
	 * @return
	 */
	public abstract Collection<IProperty> getProperties();

	
	/**
	 * It includes all properties inherited by its superclasses, or having undefined domains
	 * @return
	 */
	public abstract Collection<IProperty> getAllProperties();
	
	
	/**
	 * Return the range of the passed property in the context of this concept, considering
	 * restrictions.
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract Collection<IConcept> getPropertyRange(IProperty property) throws ThinklabException;
	
	/**
	 * Retrieve all direct instances.
	 * @return
	 */
	public abstract Collection<IInstance> getInstances();
	
	/**
	 * Retrieve all direct and indirect instances
	 */
	public abstract Collection<IInstance> getAllInstances();
	
	/** 
     * Return a Constraint object that translates the logical model of this concept 
     * represented by its stated restrictions. This means that the constraint returned
     * must select the type of the least general ancestor that has no restrictions on
     * inherited properties, and AND together all the restrictions on inherited properties
     * for this and all superclasses in between.
     * 
     * The OWL restrictions returned are then merged with any Thinklab-specific 
     * restrictions from this concept's annotations or its ancestors. This means that
     * the constraint for FrenchCity will select cities that are in the France 
     * polygon, if that's what we have defined.
     * 
     * This constraint can be used by a "dumb" kbox (most of them are, at least the
     * good ones) to select "inferred" classes, at least to a certain extent.
     * 
     * The minimal restriction selects the concept itself, so a restriction is never null.
     * 
     * @return a new Constraint object (never null).
	 * @throws ThinklabException 
	 * @see getRestrictions()
	 */
    public abstract Constraint getDefinition() throws ThinklabException;

    /**
     * Return a constraint with all the restrictions defined for this type and
     * its supertypes. The type in the constraint is the concept itself.
	 *
     * The minimal restriction selects the concept itself, so a restriction is never null.
	 *
     * @return
     * @throws ThinklabException
     */
    public abstract Constraint getRestrictions() throws ThinklabException;

    /**
     * The notion of an abstract concept is important where we create instances that have implementations. 
     * @return true if concept is abstract (no instances can be created).
     */
    public abstract boolean isAbstract();
    
    /**
     * returns the most specific concept which is both the concept it's called on and the other one passed.
     * @param otherConcept another concept
     * @return the least general common concept
     */
    public abstract IConcept getLeastGeneralCommonConcept(IConcept otherConcept);

    /**
     * Return the (only) parent class, or throw an exception if there's more than one parent.
     */
	public abstract IConcept getParent() throws ThinklabException;


	/** get the number of properties for this type */
	public abstract int getNumberOfProperties(String property);

	/**
	 * Get the minimum cardinality of the passed property in the domain of this
	 * concept.
	 * @param property
	 */
	public abstract int getMinCardinality(IProperty property);

	/**
	 * Get the maximum cardinality of the passed property in the domain of this
	 * concept.
	 * @param property
	 */
	public abstract int getMaxCardinality(IProperty property);

	/**
	 * 
	 * @return
	 */
	public abstract Collection<IProperty> getAnnotationProperties();
	
	
}