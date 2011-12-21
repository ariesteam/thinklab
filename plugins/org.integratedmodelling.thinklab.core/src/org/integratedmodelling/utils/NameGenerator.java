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
	
	static public String newID() {
		String uuid = UUID.randomUUID().toString();
		return Path.getLast(uuid, '-');
	}
	
}
