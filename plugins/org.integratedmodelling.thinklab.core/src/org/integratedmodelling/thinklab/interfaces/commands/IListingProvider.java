package org.integratedmodelling.thinklab.interfaces.commands;

import java.io.PrintStream;
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
	 * 
	 * @return
	 */
	public Collection<String> getListing() throws ThinklabException;
	
	/**
	 * 
	 * @param item
	 * @param out
	 */
	public void listItem(String item, PrintStream out) throws ThinklabException;
}
