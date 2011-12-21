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
package org.integratedmodelling.thinklab.interfaces.knowledge.datastructures;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;

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
