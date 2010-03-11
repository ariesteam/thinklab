package org.integratedmodelling.utils;

public class Path {

	static public String getLast(String path) {
		int n = path.lastIndexOf('/');
		String ret = path;
		if (n >= 0) {
			ret = path.substring(n+1);
		}
		return ret;
	}
	
}

