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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * From a blog posting by Mark Space on comp.java.lang.programmer, rearranged a bit
 * 
 * @author Mark Space
 * 
 */
public class VersionString implements Comparable<VersionString> {
	
	private String version;
	
	public VersionString(String version) {
		this.version = version;
	}

	public static class Comparator implements java.util.Comparator<String> {

		public int compare(String s1, String s2) {
			if (s1 == null && s2 == null)
				return 0;
			else if (s1 == null)
				return -1;
			else if (s2 == null)
				return 1;
			String[] arr1 = s1.split("[^a-zA-Z0-9]+"), arr2 = s2
					.split("[^a-zA-Z0-9]+");
			int i1, i2, i3;
			for (int ii = 0, max = Math.min(arr1.length, arr2.length); ii <= max; ii++) {
				if (ii == arr1.length)
					return ii == arr2.length ? 0 : -1;
				else if (ii == arr2.length)
					return 1;
				try {
					i1 = Integer.parseInt(arr1[ii]);
				} catch (Exception x) {
					i1 = Integer.MAX_VALUE;
				}
				try {
					i2 = Integer.parseInt(arr2[ii]);
				} catch (Exception x) {
					i2 = Integer.MAX_VALUE;
				}
				if (i1 != i2) {
					return i1 - i2;
				}
				i3 = arr1[ii].compareTo(arr2[ii]);
				if (i3 != 0)
					return i3;
			}
			return 0;
		}

		public static void main(String[] ss) {

			String[] data = new String[] { "2.0", "1.5.1", "10.1.2.0",
					"9.0.0.0", "2.0.0.16", "1.6.0_07", "1.6.0_07-b06",
					"1.6.0_6", "1.6.0_07-b07", "1.6.0_08-a06", "5.10",
					"Generic_127127-11", "Generic_127127-13" };
			List<String> list = Arrays.asList(data);
			Collections.sort(list, new Comparator());
			for (String s : list)
				System.out.println(s);
		}
	}
	
	@Override
	public String toString() {
		return version;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new VersionString(version);
	}

	@Override
	public boolean equals(Object obj) {
		return version.equals(obj.toString());
	}

	@Override
	public int hashCode() {
		return version.hashCode();
	}

	@Override
	public int compareTo(VersionString o) {
		return new Comparator().compare(this.version, o.version);
	}

}
