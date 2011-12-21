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
package org.integratedmodelling.thinklab.interfaces.commands;

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * These provide a collection to be listed with the list command. In order
 * to work, it must be tagged with the ListingProvider annotation and have an
 * empty constructor.
 * 
 * The label field of ListingProvider will select the listing provider and call
 * getListing() on it. The objects returned should have a sensible toString() method.
 * 
 * If the ListingProvider annotation defines the itemlabel field, then a command
 * "list itemlabel itemname" will call listItem(itemname, out).
 * 
 * @author Ferdinando Villa
 *
 */
public interface IListingProvider {
	
	/**
	 * If list command is specifying parameters, 
	 * @param parameter
	 * @param value
	 */
	public void notifyParameter(String parameter, String value);
	
	/**
	 * Respond to the plural form of the list object, if any. Objects returned are
	 * supposed to be printable, although they may have different roles too.
	 * 
	 * @return
	 */
	public Collection<?> getListing() throws ThinklabException;
	
	/**
	 * Respond to the singular form of the list object, if any. Return type as per
	 * getListing().
	 * 
	 * @param item
	 */
	public Collection<?> getSpecificListing(String item) throws ThinklabException;
}
