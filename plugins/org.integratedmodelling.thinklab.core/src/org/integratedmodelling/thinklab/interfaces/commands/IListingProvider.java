package org.integratedmodelling.thinklab.interfaces.commands;

import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;

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
