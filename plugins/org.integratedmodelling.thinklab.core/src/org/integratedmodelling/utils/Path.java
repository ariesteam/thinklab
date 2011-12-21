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

	public static String getFirst(String path, String separator) {
		int n = path.indexOf(separator);
		String ret = path;
		if (n >= 0) {
			ret = path.substring(0,n);
		}
		return ret;
	}
	
	

}

