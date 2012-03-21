package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.lang.parsing.IPropertyDefinition;

@Concept(NS.PROPERTY_DEFINITION)
public class PropertyObject extends ModelObject<PropertyObject> implements IPropertyDefinition {

	@Override
	public PropertyObject demote() {
		return this;
	}

}
