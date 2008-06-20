/**
 * IProperty.java
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

import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * The interface for a Property. 
 *
 * Most of the interface is meant to check the subtype of property we're dealing with. Some of the
 * available subtypes are standard in OWL, others (such as classification) are IMA-specific and are 
 * normally figured out by checking whether the property inherits by specific upper ontology ones. 
 * 
 * Also, for now we just ignore functionality, simmetry, transitivity etc, given that the reasoner 
 * operates on the underlying OWL model. We'll see if they're needed.
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 *
 */
public interface IProperty extends IKnowledge {

    /**
     * The IMA provides ways around the fact that OWL-DL does not allow properties to have a class in their
     * range, but only an instance. A property can be made a classification property by making it a subproperty
     * of a generic "classification property" that is known to the KM. Instances of such properties will automatically
     * look for (and create if necessary) a special unique instance of the class. The target of the relationship will
     * appear as a class in the API.
     * @return true if relationship is a classification relationship.
     */
    public abstract boolean isClassification();
    
    /**
     * An equivalent of Datatype property in OWL, but extended to handle the extended literals (Reified literals)
     * that the IMA supports. Such literals can be defined in OWL as instances of a class that derives from the
     * configured ReifiedLiteral class in the base ontology, and they have a text property that links to the 
     * literal's definition. The API will create validated Values and not instances for these instances, using the
     * validator configured for the type, and I/O to ontologies will handle them transparently. 
     * @return true if literal property: either DatatypeProperty or linking to a ReifiedLiteral
     */
    public abstract boolean isLiteralProperty();
    
    /**
     * Check if this property links to an instance (object).
     * @return true if property links to objects.
     */
    public abstract boolean isObjectProperty();
    
    /**
     * 
     * @return
     */
    public abstract boolean isAnnotation();
    
    /**
     *Returns the inverse of a IProperty. Null if there is no inverse.
     * @return the inverse IProperty
     */
    public IProperty getInverseProperty();
    
    /**
     * 
     * @return
     */
    public abstract Collection<IConcept> getRange();

    /**
     * TODO domain may not be unique. It would be wonderful to ignore that for simplicity. I don't think
     * multi-domain properties are good design.
     * @return
     */
    public abstract IConcept getDomain();
    
    
    /**
     * Check if property is abstract. Duplicated here and in IConcept because it does not 
     * fit into IKnowledge.
     * @return true if property is abstract (no relationships can be created).
     */
    public abstract boolean isAbstract();
    
    /**
     * Return the (only) parent property, or throw an exception if there's more than one parent.
     */
	public abstract IProperty getParent() throws ThinklabException;

	public abstract Collection<IProperty> getParents();
	
	public abstract Collection<IProperty> getAllParents();

	public abstract Collection<IProperty> getChildren();

	public abstract Collection<IProperty> getAllChildren();

	public abstract boolean isFunctional();

}
