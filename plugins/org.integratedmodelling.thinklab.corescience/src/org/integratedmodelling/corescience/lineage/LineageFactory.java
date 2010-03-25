package org.integratedmodelling.corescience.lineage;

import org.integratedmodelling.corescience.interfaces.lineage.ILineageTraceable;
import org.jgrapht.traverse.BreadthFirstIterator;


/**
 * Methods to work with lineages.
 * 
 * @author Ferdinando
 *
 */
public class LineageFactory {
	
	static LineageTree getLineage(Object o) {
		
		LineageTree ret = null;
		if (o instanceof ILineageTraceable) {
			ret = new LineageTree();
			Object root = addLineage(ret, (ILineageTraceable) o);
			ret.setRoot((ILineageTraceable)root);
		}
		return ret;
	}

	private static Object addLineage(LineageTree ret, Object o) {

		Object v = ret.addVertex(o);
		
		if (o instanceof ILineageTraceable) {
			for (Object d : ((ILineageTraceable)o).getAncestors())
				ret.addEdge(v, addLineage(ret, d));
		}
		return v;
	}

	/**
	 * Find the first ancestor of root that is of given class using a breadth-first search.
	 * 
	 * @param root
	 * @param clasz
	 * @return
	 */
	static Object findAncestor(Object root, Class<?> clasz) {
		LineageTree tree = getLineage(root);
		return tree.find(clasz);
	}
	
}
