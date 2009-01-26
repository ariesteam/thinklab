package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.observation.IObservationState;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * If a conceptual model implements TransformingConceptualModel, it has the opportunity of
 * transforming the state of an observation into a data source that fits the model. The 
 * transformation can be performed in "bulk" or implemented as a wrapper. It can be used
 * to implement mediation of representation, e.g. raster <-> vector, discretization, statistical
 * sampling, or classification/clustering.
 * 
 * Such transformations are typically triggered by asking to force an arbitrary observable class to
 * a subclass that is a child of ModeledObservable. This means essentially "I can only use an observation
 * of A if it's done according to a specific conceptual model".
 * 
 * @author Ferdinando Villa
 *
 */
public interface TransformingConceptualModel {

	/**
	 * Check if a state using the passed conceptual model can be converted into this one.
	 * @param otherConceptualModel
	 * @return
	 */
	public abstract boolean canTransformFrom(IConcept otherConceptualModel);
	
	/**
	 * If canTransformFrom() has returned true for the semantic type of the passed conceptual
	 * model, this one may be called to convert the state of another observation into a datasource
	 * that fits us.
	 *  
	 * @param otherConceptualModel
	 * @param state
	 * @return
	 * @throws ThinklabValidationException if errors occur; no type checking should be done, because
	 * this method is only called if canTransformFrom has returned true.
	 */
	public abstract IDataSource transformState(IConceptualModel otherConceptualModel, IObservationState state) 
		throws ThinklabValidationException;
	
}
