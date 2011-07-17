package org.integratedmodelling.utils;

import java.util.Collection;
import java.util.Iterator;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;

/**
 * Pass a collection (e.g. a clojure list of :kw value pairs) and retrieve pairs of key/value with the key 
 * converted to a string and the leading colon removed. Also performs minimal error checking and is null-tolerant.
 * 
 * @author Ferdinando
 *
 */
public class OptionListIterator implements Iterator<Pair<String, Object>> {

	Iterator<?> _it = null;
	
	public OptionListIterator(Object o) {
		if (o != null)
			_it = ((Collection<?>)o).iterator();
	}
	
	@Override
	public boolean hasNext() {
		return _it == null ? false : _it.hasNext();
	}

	@Override
	public Pair<String, Object> next() {
		String key = _it.next().toString();
		Object val = _it.next();
		if (!key.startsWith(":"))
			throw new ThinklabRuntimeException("keyword list improperly formatted: key is not a clojure keyword");
		key = key.substring(1);
		return new Pair<String, Object>(key, val);
	}

	@Override
	public void remove() {
		_it.remove();
		_it.remove();
	}

}
