package org.integratedmodelling.thinklab.annotation.internal;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;

@Concept(NS.KEY_VALUE_PAIR)
public class SKeyValue {
	
	public SKeyValue() {}
	
	public SKeyValue(Object key2, Object value2) {
		key = key2;
		value = value2;
	}
	
	@Property(NS.HAS_FIRST_FIELD)
	public Object key;
	@Property(NS.HAS_SECOND_FIELD)
	public Object value;

}
