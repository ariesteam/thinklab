package org.integratedmodelling.utils.collections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;


/**
 * A normal Java Map, but with Set behavior (objects can be in no more than once) and iterates its keys like
 * a Stack, plus a method to move existing objects to the top. Basically a golem collection.
 * 
 * @author Ferdinando Villa
 *
 * @param <T>
 */
public class StackMap<K,T> {

	private static final long serialVersionUID = 1988080719023977747L;
	private HashMap<K,T> _map = new HashMap<K,T>();
	private Stack<K>     _stk = new Stack<K>();
	
	public synchronized T pop() {
		K key = _stk.pop();
		return _map.remove(key);
	}

	public boolean containsKey(K key) {
		return _map.containsKey(key);
	}
	
	public synchronized T push(K key, T val) {
		if (containsKey(key)) {
			remove(key);
		}
		_map.put(key, val);
		_stk.push(key);
		return val;
	}

	public void clear() {
		_stk.clear();
		_map.clear();
	}

	public boolean remove(K key) {
		_map.remove(key);
		return _stk.remove(key);
	}

	public void toTop(K key) {
		if (_map.containsKey(key)) {
			_stk.remove(key);
			_stk.push(key);
		}
	}
	
	public Iterator<K> iterator() {
		return _stk.iterator();
	}
	
	public int size() {
		return _stk.size();
	}
	
	public K get(int i) {
		return _stk.get(i);
	}
	
	public T get(K key) {
		return _map.get(key);
	}
	
	public T top() {
		return _map.get(_stk.peek());
	}
}
