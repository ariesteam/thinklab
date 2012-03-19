package org.integratedmodelling.thinklab.kbox;

import java.util.Iterator;
import java.util.List;

import org.integratedmodelling.collections.ImmutableList;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;

/**
 * List to return results of a NeoKBox query. Only stores object IDs, creating
 * any object lazily.
 * 
 * @author Ferd
 *
 */
public class KBoxResult extends ImmutableList<ISemanticObject<?>> {

	List<Long> _results;
	IKbox      _kbox;

	class KboxIterator implements Iterator<ISemanticObject<?>> {

		int idx = 0;
		
		@Override
		public boolean hasNext() {
			return idx < _results.size();
		}

		@Override
		public ISemanticObject<?> next() {
			return get(idx++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("cannot modify read-only list");
		}
	}
	
	public KBoxResult(IKbox kbox, List<Long> res) {
		_kbox = kbox;
		_results = res;
	}

	@Override
	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ISemanticObject<?> get(int arg0) {
		try {
			return _kbox.retrieve(_results.get(arg0));
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public Iterator<ISemanticObject<?>> iterator() {
		return new KboxIterator();
	}

	@Override
	public int size() {
		return _results.size();
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
