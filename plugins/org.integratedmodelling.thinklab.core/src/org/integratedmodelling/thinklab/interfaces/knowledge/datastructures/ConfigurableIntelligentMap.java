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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;

/**
 * Can be used to create intelligent maps that are configured through properties and
 * return arbitrary object based on reasoner-mediated lookup. Construct them with 
 * a property prefix, e.g. thinklab.icon, and load one or more property objects that
 * contain properties like
 * 
 * thinklab.icon.observation:Observation=observation.png
 * 
 * then define the getObjectFromPropertyValue to return e.g. an Image (which is the
 * type of the implemented class) from the value (observation.png). At that point, 
 * passing any concept that is a observation:Observation will return that Image unless
 * a more specific concept was defined. 
 * 
 * @author Ferdinando Villa
 * @date Feb 18th 2010
 *
 * @param <T>
 */
public abstract class ConfigurableIntelligentMap<T> implements Map<IConcept, T> {

	String propertyPrefix = null;
	IntelligentMap<String> pmap = new IntelligentMap<String>();
	
	public ConfigurableIntelligentMap(String propertyPrefix) {
		if (!propertyPrefix.endsWith("."))
			propertyPrefix += ".";
		this.propertyPrefix = propertyPrefix;
	}
	
	protected void registerProperty(IConcept c, String value) {
		pmap.put(c, value);
	}
	
	public void load(Properties properties) throws ThinklabException {
		int ln = propertyPrefix.length();
		for (Object k : properties.keySet()) {
			if (k.toString().startsWith(propertyPrefix)) {
				String cString = k.toString().substring(ln);
				cString = cString.replaceAll("-",":");
				IConcept kconc = KnowledgeManager.get().requireConcept(cString);
				pmap.put(kconc, properties.getProperty(k.toString()));
			}
		}
	}

	@Override
	public void clear() {
		pmap.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return pmap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return pmap.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<IConcept, T>> entrySet() {
		throw new ThinklabRuntimeException("entrySet called on ConfigurableIntelligentMap");
	}

	@Override
	public T get(Object key) {
		try {
			return get((IConcept)key, (Object[])null);
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public boolean isEmpty() {
		return pmap.isEmpty();
	}

	@Override
	public Set<IConcept> keySet() {
		return pmap.keySet();
	}

	@Override
	public T put(IConcept key, T value) {
		throw new ThinklabRuntimeException("put called on ConfigurableIntelligentMap");
	}

	@Override
	public void putAll(Map<? extends IConcept, ? extends T> m) {
		throw new ThinklabRuntimeException("putAll called on ConfigurableIntelligentMap");
	}

	@Override
	public T remove(Object key) {
		throw new ThinklabRuntimeException("remove called on ConfigurableIntelligentMap");
	}

	@Override
	public int size() {
		return pmap.size();
	}

	@Override
	public Collection<T> values() {
		throw new ThinklabRuntimeException("values called on ConfigurableIntelligentMap");
	}
	
	protected abstract T getObjectFromPropertyValue(String pvalue, Object[] parameters) throws ThinklabException;

	public T get(IConcept concept) throws ThinklabException {
		String pval = pmap.get(concept);
		if (pval == null)
			return null;
		return getObjectFromPropertyValue(pval, null);
	}
	
	public T get(IConcept concept, Object ... parameters) throws ThinklabException {
		String pval = pmap.get(concept);
		if (pval == null)
			return null;
		return getObjectFromPropertyValue(pval, parameters);
	}
	
	
	
	
}
