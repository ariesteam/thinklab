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
package org.integratedmodelling.thinklab;

public class Version {
	
	/**
	 * Version constants. Should be updated manually at each release.
	 */
	public static final int VERSION_MAJOR = 1;
	public static final int VERSION_MINOR = 0;
	public static final int VERSION_DETAIL = 0;
	public static final String BRANCH = "rc1";
	public static final String STATUS = "rc1";
	public static final String BUILD  = "";
	
	public String toString() {
		return
				VERSION_MAJOR + 
				"." +
				VERSION_MINOR +
				"." +
				VERSION_DETAIL +
				STATUS +
				" (" +
				BRANCH + 
				(BUILD.isEmpty() ? "" : " ") +
				BUILD + 
				")";
	}
}
