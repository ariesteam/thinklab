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

import java.util.UUID;

/**
 * A utility class that knows how to create unique temporary names. Just a wrapper around Java UUIDs with
 * some recognition methods.
 * 
 * TODO not sure if these methods need synchronization.
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 *
 */
public class NameGenerator {

	static long index = 0;
	
	static public String newName() {
		return "urn:uuid:" + UUID.randomUUID();
	}
	
	static public String newName(String prefix) {
		return "urn:uuid:" + prefix + ":" + UUID.randomUUID();
	}
	
	static public boolean isGenerated(String name) {
		return name.startsWith("urn:uuid:");
	}
	
}
