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

	public static String getLeading(String path, char separator) {
		int n = path.lastIndexOf(separator);
		if (n > 0) {
			return path.substring(0,n);
		}
		return null;
	}

	public static String join(String[] pth, int start, char separator) {
		String ret = "";
		for (int i = start; i < pth.length; i++) 
			ret +=
				(ret.isEmpty() ? "" : ".") +
				pth[i];
		return ret;
	}
	
	

}

