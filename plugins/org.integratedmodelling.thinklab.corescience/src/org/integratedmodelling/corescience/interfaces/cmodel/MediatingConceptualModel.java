package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

/**
 * If a conceptual model implements MediatingConceptualModel, it declares its capability of converting objects of
 * another CM, typically of the same class but instantiated differently, into something the current CM
 * can use. This is used to implement conversion transformations, such as units of measurement, currency
 * conversion and the like.
 * 
 * @author Ferdinando Villa
 *
 */
public interface MediatingConceptualModel {

	/**
	 * Return an appropriate mediator to convert a state in a given
	 * overall context. The context adaptor should be initialized and stored to
	 * handle all states of the context.
	 * 
	 * @param overallContext
	 * @param datasource
	 * @return
	 * @throws ThinklabValidationException 
	 */
	public abstract IValueMediator getMediator(IConceptualModel conceptualModel, IObservationContext ctx) throws ThinklabException;
 
	
}
