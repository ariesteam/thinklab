/**
 * IRelationship.java
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


/**
 * <p>We use relationships to simplify the frame-based Concept interface. All the relationships that a class or instance
 * entertains with anything are returned as IRelationship objects. These can be queried to check if the relationship
 * is actually just the assertion of a property (like in most Concepts) or actually has a value, which can be a
 * literal (of any kind supported by the IMA), an object (instance), or a class (no matter whether we're using
 * OWL-DL or not).</p> 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * @see IConcept#getRelated()
 * @see IInstance#getRelated()
 */
public interface IRelationship {

	/**
	 * Get the property that defines the relationship. 
	 * @return a property. Never fails.
	 */
	public abstract IProperty getProperty();
	
	/**
	 * <p>Get the value that our property points to. Returns null if this is just the statement of a property.</p>
	 * @return a Value or null.
	 */
	public abstract IValue getValue();
	
    /**
     * <p>Check if relationship is with a literal. A literal is an instance of a concept that uses no formal
     * instances, such as an integer. The IMA allows arbitrarily complex literals beyond the XML Schema
     * types codified in RDF/OWL and handles their translation to/from RDF transparently.</p>
     * 
     * <p>If true, the concept field will contain the concept that the literal
     * incarnates (the same as the concept field in the literal, so not really useful, but better than null).</p>
     * 
     * @return true if literal
     */
    public abstract boolean isLiteral();

    /**
     * <p>Check if relationship is with a class. Relationships with classes are troublesome for reasoners and
     * some frameworks (notably OWL-DL) disallow them. Unfortunately they are indispensable for real work, so
     * the IMA employs a transparent trick to allow them in all frameworks. In OPAL, relationships that are 
     * subproperties of "classification" can link to a class literal which is the semantic type of the class, 
     * expressed as a string. Validation of the OPAL instances creates the appropriate support structures in the
     * ontologies so that they remain OWL-DL and reasoners are happy.</p>
     * 
     * <p>Note: classification relationships are fundamental in defining conformance between instances.</p>
     *  
     * @return true if relationship is a classification.  If so, the concept field contains the class
     * we're classifying the source concept with.
     * @see Instance.conformsTo()
     * @see ConformancePolicy
     */
    public abstract boolean isClassification();

    /**
     * <p>Check if relationship is with another instance (i.e. an object). If OWL is supported, this
     * translates to the property being an ObjectProperty.</p>
     * 
     * @return true if relationship is with an object. If true, the concept field contains the relevant Instance.
     */
    public abstract boolean isObject();

    /**
     * <p>Check if relationship has a value (either literal, object, or class).</p>
     * @return true if there is a value, false if the relationship represent a naked property statement.
     */
    public abstract boolean hasValue();

    /**
     * Return a printable description of the relationship.
     * @return descriptive string.
     */
    public abstract String toString();

    /**
     * Return the concept of the related object. Must be non-null.
     * @return
     */
	public abstract IConcept getConcept();

}