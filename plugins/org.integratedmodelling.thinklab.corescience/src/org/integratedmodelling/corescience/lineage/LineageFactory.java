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
