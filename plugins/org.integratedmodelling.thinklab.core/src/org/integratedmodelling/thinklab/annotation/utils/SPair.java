package org.integratedmodelling.thinklab.annotation.utils;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;

@Concept(NS.PAIR)
public class SPair {

	@Property(NS.HAS_FIRST_FIELD)
	Object first;
	@Property(NS.HAS_SECOND_FIELD)
	Object second;
}
