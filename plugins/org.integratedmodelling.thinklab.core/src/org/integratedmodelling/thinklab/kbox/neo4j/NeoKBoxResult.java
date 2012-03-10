package org.integratedmodelling.thinklab.kbox.neo4j;

import java.util.Iterator;

import org.integratedmodelling.collections.ReadOnlyList;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;

/**
 * List to return results of a NeoKBox query. Only stores object IDs, creating
 * any object lazily.
 * 
 * @author Ferd
 *
 */
public class NeoKBoxResult extends ReadOnlyList<ISemanticObject> {

	@Override
	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ISemanticObject get(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<ISemanticObject> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
