package org.integratedmodelling.thinklab.annotation.utils;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;

@Concept(NS.KEY_VALUE_PAIR)
public class SKeyValue {
	
	@Property(NS.HAS_FIRST_FIELD)
	public String key;
	@Property(NS.HAS_SECOND_FIELD)
	public Object value;

}
