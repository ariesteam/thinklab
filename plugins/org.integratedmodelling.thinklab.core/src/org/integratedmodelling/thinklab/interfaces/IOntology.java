/**
 * IOntology.java
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

import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabDuplicateNameException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.utils.Polylist;

/**
 * <p>The generalized ontology interface. Ontology objects are usually not accessed by the typical API user, who deals with
 * concepts and with the knowledge manager directly. Yet, we need an ontology generalization layer to provide a smooth interface
 * to most operations.</p>
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */
public interface IOntology  extends IResource {

    /**
     * Return a string identifier for an object that is guaranteed not to be in the ontology.
     * @param prefix a string prefix or null if you don't care.
     * @return unique name. Must succeed and behave properly in threaded environment.
     */
    public String getUniqueObjectName(String prefix);

    
	/**
	 * Iterate over all concepts
	 * @return an iterator over all the concepts contained in the ontology. 
	 */
	public abstract Collection<IConcept> getConcepts();

	/**
	 * Iterate over all properties
	 * @return an iterator over all the properties contained in the ontology. 
	 */
	public abstract Collection<IProperty> getProperties();

	/**
	 * Iterate over all instances
	 * @return all the instances contained in the ontology. 
	 * @throws ThinklabException 
	 */
	public abstract Collection<IInstance> getInstances() throws ThinklabException;
	
	/**
	 * Create a new concept to be defined. The concept is already in the ontology as soon as it's created.
	 * @category Modifying methods
	 * @param ID the ID of the concept
	 * @param parent the parent concept. Defaults to owl:Thing if null.
	 * @return the new Concept.
	 * TODO check if we need a validation mechanism like the one for Instances
	 * @throws ThinklabException 
	 */
	public abstract IConcept createConcept(String ID, IConcept parent) throws ThinklabException;
	
	/**
	 * Create an instance of the given concept. The instance is "loose", not linked to the ontology unless
	 * it is passed to validateInstance after being created.
	 * @category Modifying methods
	 * @param ID the instance id.
	 * @return a loose instance to define and validate.
	 * @throws ThinklabDuplicateNameException 
	 * @throws ThinklabMalformedSemanticTypeException 
	 * @throws ThinklabIOException 
	 */
	public abstract IInstance createInstance(String ID, IConcept c) throws ThinklabMalformedSemanticTypeException, ThinklabDuplicateNameException, ThinklabIOException;
	
    /**
     * Create a copy of passed instance, which may come from a different ontology.
	 * @category Modifying methods
     * @param i
     * @return
     * @throws ThinklabException
     */
    public abstract IInstance createInstance(IInstance i) throws ThinklabException;
    
    /**
     * Create an instance from a list, imposing the specified ID.
	 * @category Modifying methods
     * @param ID 
     * @param list e.g. returned by asList() from another IInstance. 
     * @return
     * @throws ThinklabException
     */
	public abstract IInstance createInstance(String ID, Polylist list) throws ThinklabException;

    /**
     * Create an instance from a list.
	 * @category Modifying methods
     * @param list
     * @return
     * @throws ThinklabException
     */
    public abstract IInstance createInstance(Polylist list) throws ThinklabException;

    
	/**
	 * Create a new property to be defined. The property is already in the ontology as soon 
	 * as it's created.
	 * @category Modifying methods
	 * @param ID the ID of the property
	 * @param range the superclass of the values to link to. If that corresponds to one
	 * 	of the base types, a standard DataProperty is created. Otherwise a new ObjectProperty
	 * 	is created and its superclass set appropriately
	 * @param parent the parent property. Must be a literal property. null can be passed.
	 * @return the new Property.
	 * TODO check if we need a validation mechanism like the one for Instances
	 * TODO hard to do this one properly unless we know the range - literals could be
	 * extended or not.
	 */
	public abstract IProperty createLiteralProperty(String ID, IConcept range, IProperty parent) 
	throws ThinklabException;
	
	/**
	 * Create a new property to be defined. The property is already in the ontology as soon 
	 * as it's created.
	 * @category Modifying methods
	 * @param ID the ID of the property
	 * @param range the superclass of what to link to
	 * @param parent the parent property. null can be passed.
	 * @return the new Property.
	 * TODO check if we need a validation mechanism like the one for Instances
	 * @throws ThinklabException 
	 */
	public abstract IProperty createObjectProperty(String ID, IConcept range, IProperty parent) throws ThinklabException;
	
	/**
	 * Create a new property to be defined. The property is already in the ontology as soon 
	 * as it's created.
	 * @category Modifying methods
	 * @param ID the ID of the property
	 * @param range the superclass of what to link to
	 * @param parent the parent property. Must be a classification property. If null is
	 * passed, it becomes a direct subproperty of tc:classificationProperty.
	 * @return the new Property.
	 * TODO check if we need a validation mechanism like the one for Instances
	 */
	public abstract IProperty createClassificationProperty(String ID, IConcept range, IProperty parent) 
	throws ThinklabException;

	/**
	 * Return a concept, or null if not found.
	 * @param ID the concept's ID
	 * @return the concept or null
	 */
	public abstract IConcept getConcept(String ID);
	
	/**
	 * Return an instance, or null if not found.
	 * @param ID the instance's ID
	 * @return the instance or null
	 */
	public abstract IInstance getInstance(String ID);
	
	/**
	 * Return a property, or null if not found.
	 * @param ID the property's ID
	 * @return the property or null
	 */
	public abstract IProperty getProperty(String ID);

	/**
	 * 
	 * @param uri
	 * @throws ThinklabException 
	 */
	public void removeInstance(String uri) throws ThinklabException;

	/**
	 * 
	 * @param os
	 */
	public void write(OutputStream os);
	
	/**
	 * The ID of the ontology concept space (its the XML namespace).
	 * @return the concept space ID. Can't fail.
	 */
	public String  getConceptSpace();

	/**
	 * Load an ontology from a specific URL. 
	 * In the second time it is called it should merge the content of the
	 * second URL in the current Ontology. 
	 * However this is not supported in protege and jena implementations. 
	 * 
	 * @param url
	 */
	public void read(URL url);


	/**
	 * 
	 */
	public String getURI();

	/**
	 * Create an equivalence statement between the two instances passed.
	 * @param o1
	 * @param o2
	 */
	public void createEquivalence(IInstance o1, IInstance o2);
	
	/**
	 * The default behavior of an ontology is to reject any new instance whose ID exists
	 * already. This function must trigger a behavior where instance IDs are renamed if
	 * names already identify other instances. 
	 */
	public abstract void allowDuplicateInstanceIDs();
	
	public abstract long getLastModificationDate();
}