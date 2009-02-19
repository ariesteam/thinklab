/**
 * MetadataFactory.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Apr 25, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabMetadataPlugin.
 * 
 * ThinklabMetadataPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabMetadataPlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.metadata;

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.time.literals.TimeValue;
import org.integratedmodelling.utils.Polylist;

/**
 * A helper class with methods to simplify accessing and creating standard
 * metadata.
 * 
 * creator
 * contributor
 * date
 * format
 * identifier
 * language
 * publisher
 * rights
 * source
 * subject
 * title
 * type
 * 
 * TODO implement it. Use codified relationships from plugin, loaded from
 * properties, defaulted to dublincore.
 * 
 * @author Ferdinando Villa
 */
public class MetadataFactory {

	private static final String SOURCE_CITATIONS_PROPERTY = "metadata:hasSourceCitation";
	
	private static final String IMAGE_URL_PROPERTY = "metadata:hasImageUrl";

	/**
	 * Adds the default metadata.
	 * 
	 * @param instance the instance
	 * 
	 * @return the i instance
	 */
	public static IInstance addDefaultMetadata(IInstance instance) {
		return instance;
	}
	
	/**
	 * Adds the default metadata.
	 * 
	 * @param instance the instance
	 * 
	 * @return the polylist
	 */
	public static Polylist addDefaultMetadata(Polylist instance) {
		return instance;
	}
	
	/**
	 * Adds the creation date.
	 * 
	 * @param instance the instance
	 * 
	 * @return the polylist
	 */
	public static Polylist addCreationDate(Polylist instance) {
		return instance;
	}
	
	/**
	 * Adds the creation date.
	 * 
	 * @param instance the instance
	 * @param time the time
	 * 
	 * @return the polylist
	 */
	public static Polylist addCreationDate(Polylist instance, TimeValue time) {
		return instance;
	}

	/**
	 * Adds the creator.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addCreator(Polylist instance, String value) {
		return instance;
	}
	
	/**
	 * Adds the contributor.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addContributor(Polylist instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the format.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addFormat(Polylist instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the identifier.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addIdentifier(Polylist instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the language.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addLanguage(Polylist instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the publisher.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addPublisher(Polylist instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the rights.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addRights(Polylist instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the source.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addSource(Polylist instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the subject.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addSubject(Polylist instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the title.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addTitle(Polylist instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the type.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the polylist
	 */
	public static Polylist addType(Polylist instance, String value) {
		return instance;
	}

	/**
	 * Adds the creation date.
	 * 
	 * @param instance the instance
	 * 
	 * @return the i instance
	 */
	public static IInstance addCreationDate(IInstance instance) {
		return instance;
	}
	
	/**
	 * Adds the creation date.
	 * 
	 * @param instance the instance
	 * @param time the time
	 * 
	 * @return the i instance
	 */
	public static IInstance addCreationDate(IInstance instance, TimeValue time) {
		return instance;
	}

	/**
	 * Adds the creator.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addCreator(IInstance instance, String value) {
		return instance;
	}
	
	/**
	 * Adds the contributor.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addContributor(IInstance instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the format.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addFormat(IInstance instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the identifier.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addIdentifier(IInstance instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the language.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addLanguage(IInstance instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the publisher.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addPublisher(IInstance instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the rights.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addRights(IInstance instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the source.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addSource(IInstance instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the subject.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addSubject(IInstance instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the title.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addTitle(IInstance instance, String value) {
		return instance;
	}	
	
	/**
	 * Adds the type.
	 * 
	 * @param instance the instance
	 * @param value the value
	 * 
	 * @return the i instance
	 */
	public static IInstance addType(IInstance instance, String value) {
		return instance;
	}

	
	public static String[] getContributor(IInstance instance) {
		return null;
	}
	
	public static String getCreationDate(IInstance instance) {
		return null;
	}
	
	public static String[] getCreator(IInstance instance) {
		return null;
	}
	
	public static String getFormat(IInstance instance) {
		return null;
	}
	
	public static String getIdentifier(IInstance instance) {
		return null;
	}
	
	public static String getLanguage(IInstance instance) {
		return null;
	}

	public static String getPublisher(IInstance instance) {
		return null;
	}
	
	public static String getRights(IInstance instance) {
		return null;
	}
	
	public static String getSource(IInstance instance) {
		return null;
	}
	
	public static String getSubject(IInstance instance) {
		return null;
	}

	public static String getTitle(IInstance instance) {
		return null;
	}
	
	public static String getType(IInstance instance) {
		return null;
	}
	
	public static String getImageUrl(IInstance instance) {
		IValue ret = null;
		try {
			ret = instance.get(IMAGE_URL_PROPERTY);
		} catch (ThinklabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret == null ? "" : ret.toString();
	}
	
	public static String[] getSourceCitations(IInstance instance) {
		
		Collection<IRelationship> rels = null;
		try {
			rels = instance.getRelationships(SOURCE_CITATIONS_PROPERTY);
		} catch (ThinklabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] ret = new String[rels.size()];
		int i = 0;
		for (IRelationship r : rels) {
			ret[i++] = r.getValue().toString();
		}
		return ret;
	}











}
