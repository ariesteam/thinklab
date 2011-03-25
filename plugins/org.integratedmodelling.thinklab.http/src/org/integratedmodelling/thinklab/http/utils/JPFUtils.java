package org.integratedmodelling.thinklab.http.utils;

import org.java.plugin.registry.Extension;
import org.java.plugin.registry.Extension.Parameter;

public class JPFUtils {

	public static String getParameter(Extension ext, String extName, String defaultValue) {	
		String ret = getParameter(ext, extName);
		return ret == null ? defaultValue : ret;
	}

	public static String getParameter(Extension.Parameter ext, String extName, String defaultValue) {
		String ret = getParameter(ext, extName);
		return ret == null ? defaultValue : ret;
	}
	
	public static String getParameter(Extension ext, String extName) {
		Parameter prm = ext.getParameter(extName);
		return prm == null ? null : prm.valueAsString();
	}

	public static String getParameter(Extension.Parameter ext, String extName) {
		Parameter prm = ext.getSubParameter(extName);
		return prm == null ? null : prm.valueAsString();
	}
}
