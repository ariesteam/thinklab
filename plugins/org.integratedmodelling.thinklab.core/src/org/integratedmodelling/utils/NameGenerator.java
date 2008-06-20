/**
 * NameGenerator.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.utils;

import java.util.Date;

/**
 * A utility class that knows how to create unique temporary names. Thread safe.
 * All methods are synchronized, which will reduce performance in heavily threaded applications. Don't use if
 * uniqueness of names is not required across threads.
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 *
 */
public class NameGenerator {

	static long index = 0;
	
	/**
	 * Generate a unique id, using the system time and a monotonically increasing index. Guaranteed never to return
	 * the same string twice. Note that the string is numeric: use the prefix version if this is unacceptable.
	 * @return a unique ID.
	 */
	static public synchronized String newName() {
		String nn = Long.toString(new Date().getTime());		
		long newidx = index++;
		return nn + "_" + Long.toString(newidx);
	}
	
	/**
	 * Generate a unique string starting with a specified prefix
	 * @param prefix
	 * @return
	 */
	static public synchronized String newName(String prefix) {
		return prefix + newName();
	}
	
}
