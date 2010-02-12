package org.integratedmodelling.thinklab.interfaces.annotations;

import java.lang.annotation.*;

/**
 * Tagging a class with this will notify the command system that the class provides a
 * new type of items that can be listed with the list command. The class should implement
 * IListingProvider. The itemlabel field will enable listing of a specific element if
 * prefixed by itemlabel, e.g. "list locations / list location france".
 * 
 * @author Ferdinando Villa
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ListingProvider {
	public String label();
	public String itemlabel() default "";
}
