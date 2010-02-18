package org.integratedmodelling.thinklab.interfaces.knowledge.datastructures;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.ConceptVisitor.ConceptMatcher;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * A map indexed by concepts, whose get() method will select the entry that best corresponds to the passed
 * concept using reasoning instead of equality.
 * 
 * @author Ferdinando
 *
 * @param <T>
 */
public class IntelligentMap<T> implements Map<IConcept, T> {

	Hashtable<IConcept, T> _data = new Hashtable<IConcept, T>();
	
	public T get(IConcept concept) {
		
        class Matcher<TYPE> implements ConceptVisitor.ConceptMatcher {

            Hashtable<IConcept, TYPE> coll;
            TYPE ret = null;
            
            public Matcher(Hashtable<IConcept,TYPE> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
                ret = coll.get(c);
                return(ret != null);	
            }    
        }
        
        Matcher<T> matcher = new Matcher<T>(_data);
        IConcept cms = ConceptVisitor.findMatchUpwards(matcher, concept);
        return cms == null ? null : matcher.ret;
	}
	
	public T put(IConcept concept, T data) {
		return _data.put(concept, data);
	}

	@Override
	public void clear() {
		_data.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return _data.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		return _data.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<IConcept, T>> entrySet() {
		return _data.entrySet();
	}

	@Override
	public T get(Object key) {
		return get((IConcept)key);
	}

	@Override
	public boolean isEmpty() {
		return _data.isEmpty();
	}

	@Override
	public Set<IConcept> keySet() {
		return _data.keySet();
	}

	@Override
	public void putAll(Map<? extends IConcept, ? extends T> m) {
		_data.putAll(m);
	}

	@Override
	public T remove(Object key) {
		return _data.remove(key);
	}

	@Override
	public int size() {
		return _data.size();
	}

	@Override
	public Collection<T> values() {
		return _data.values();
	}

	
}
