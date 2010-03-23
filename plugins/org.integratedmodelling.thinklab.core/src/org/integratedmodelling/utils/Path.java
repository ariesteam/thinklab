package org.integratedmodelling.utils;

public class Path {

	static public String getLast(String path, char separator) {
		int n = path.lastIndexOf(separator);
		String ret = path;
		if (n >= 0) {
			ret = path.substring(n+1);
		}
		return ret;
	}

	static public String getLast(String path) {
		return getLast(path, '/');
	}
	

}

