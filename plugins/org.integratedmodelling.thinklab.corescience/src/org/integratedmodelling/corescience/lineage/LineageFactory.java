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
package org.integratedmodelling.corescience.lineage;

import org.integratedmodelling.corescience.interfaces.lineage.ILineageTraceable;


/**
 * Methods to work with lineages.
 * 
 * @author Ferdinando
 *
 */
public class LineageFactory {
	
	public static LineageTree getLineage(Object o) {
		
		LineageTree ret = null;
		if (o instanceof ILineageTraceable) {
			ret = new LineageTree();
			ret.setRoot((ILineageTraceable)addLineage(ret, (ILineageTraceable) o));
		}
		return ret;
	}

	private static Object addLineage(LineageTree ret, Object o) {

		ret.addVertex(o);
				
		if (o instanceof ILineageTraceable) {
			for (Object d : ((ILineageTraceable)o).getAncestors())
				ret.addEdge(o, addLineage(ret, d));
		}
		return o;
	}

	/**
	 * Find the first ancestor of root that is of given class using a breadth-first search.
	 * 
	 * @param root
	 * @param clasz
	 * @return
	 */
	public static Object findAncestor(Object root, Class<?> clasz) {
		LineageTree tree = getLineage(root);
		return tree.find(clasz);
	}
	
}
