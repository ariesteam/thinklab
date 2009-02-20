package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * A validating conceptual model declares its intention to validate anything coming from a 
 * datasource (after any mediation and aggregation) before it becomes part of the state. Validation
 * can be purely a check (raising an exception if invalid) or attempt to convert the values to
 * appropriate ones. It is typically implemented to define boundaries of validity or other criteria.
 * 
 * @author Ferdinando Villa
 *
 */
public interface ValidatingConceptualModel {

	public abstract IStateValidator getValidator(IConcept valueType);	

}
