/**
 * IResource.java
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
 * Basic methods that any RDF resource must implement.
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 *
 */
public interface IResource {
	
	
	/**
	 * The ID of the concept space (the first field or the semantic type). All Resources
	 * have (or ARE) a concept space.
	 * @return the concept space ID. Can't fail.
	 */
	public String       getConceptSpace();
	
	/**
	 * The fully specified URI of the resource. All resources have a URI.
	 * @return the URI. Can't fail.
	 */
	public String       getURI();
	
	/**
	 * The label associated with the resource in the default language, set in preferences.
	 * @return the label, or an empty string if not found.
	 */
	public String       getLabel();
	
	/**
	 * The description associated with the resource in the default language, set in preferences.
	 * @return the description, or an empty string if not found.
	 */
	public String       getDescription();
	
	/**
	 * The label associated with the resource in the passed language.
	 * @return the label, or an empty string if not found.
	 */
	public String       getLabel(String languageCode);

	/**
	 * The description associated with the resource in the passed language.
	 * @return the description, or an empty string if not found.
	 */
	public String  getDescription(String languageCode);
	
	/**
	 * Add a description in the default language.
	 * @category Modifying methods
	 * @param desc
	 */
	public abstract void addDescription(String desc);

	/**
	 * Add a description in the passed language.
	 * @category Modifying methods
	 * @param desc
	 * @param language
	 */
	public abstract void addDescription(String desc, String language);

	/**
	 * Add a label in the default language.
	 * @category Modifying methods
	 * @param desc
	 */
	public abstract void addLabel(String desc);

	/**
	 * Add a label in the passed language.
	 * @category Modifying methods
	 * @param desc
	 * @param language
	 */
	public abstract void addLabel(String desc, String language);

	
//	/**
//	 * Check if resource's URI equals the passed string.
//	 * @param s the URI.
//	 * @return
//	 */
//	public boolean equals(String s);
//	
//	
//	public boolean equals (IResource r);
}
