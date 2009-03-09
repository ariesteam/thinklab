package org.integratedmodelling.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Properties;

import org.integratedmodelling.thinklab.KnowledgeManager;

/**
 * Utilities to go from names to likely resource URLs. Most of these are quite
 * specific.
 * 
 * @author Ferdinando Villa
 *
 */
public class URLUtils {
	
	/**
	 * Look for thinklab.resource.path in properties, if found scan
	 * the path to resolve the passed name as a file url. If the url is already
	 * resolved, just return it. If the path contains a http-based URL prefix
	 * just use that without checking.
	 * 
	 * @param url
	 * @param properties
	 * @return a resolved url or the original one if not resolved.
	 */
	public static String resolveUrl(String url, Properties properties) {
		
		String ret = url;
		
		if (ret.contains(":/"))
			return ret;
		
		String prop = properties.getProperty(
				KnowledgeManager.RESOURCE_PATH_PROPERTY,
				".");
		
		for (String path : prop.split(";")) {
			
			if (path.startsWith("http") && path.contains("/")) {
				ret = path + url;
				break;
			}
			
			File pth = new File(path + File.separator + url);
			
			if (pth.exists()) {
				try {
					ret = pth.toURI().toURL().toString();
					break;
				} catch (MalformedURLException e) {
				}
			}
		}
		
		return ret;
	}

}
