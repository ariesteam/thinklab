package org.integratedmodelling.thinklab.webapp;

import org.integratedmodelling.thinklab.http.ThinklabWebApplication;

/**
 * Utility class with simple static methods
 * @author Ferdinando Villa
 *
 */
public class TC {

	/**
	 * Thinkcap resource path: the relative URL path to a base thinkcap resource (e.g an icon) that
	 * will work across proxies. 
	 * 
	 * @param resource path to resource starting at thinkcap base dir. A leading slash is not necessary
	 *        but harmless.
	 */
	public static String url(ThinklabWebApplication app, String resource) {
		return 
			resource == null ? 
				null :
				("/" + 
				 app.getId() +
				 "/tc" + 
				 (resource.startsWith("/") ? "" : "/") +
				 resource);
	}
}
