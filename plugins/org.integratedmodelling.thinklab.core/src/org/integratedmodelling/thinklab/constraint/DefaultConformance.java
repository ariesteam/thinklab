package org.integratedmodelling.thinklab.constraint;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.IValue;

/**
 * Default conformance policy will select same concepts for class types and classification properties, 
 * and will ignore all literal properties.
 * 
 * @author Ferdinando
 *
 */
public class DefaultConformance extends DefaultAbstractConformance {

	@Override
	public IConcept getMatchingConcept(IConcept concept) {
		return concept;
	}

	@Override
	public Restriction setConformance(IProperty property, IConcept extent) {
		return new Restriction(property, extent);
	}

	@Override
	public Restriction setConformance(IProperty property, IValue extent) {
		// by default, literals are not part of conformance checking.
		return null;
	}
}
