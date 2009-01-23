package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.jscience.mathematics.number.Rational;

/**
 * If a conceptual model implements ScalingConceptualModel, it declares its capability of
 * mediating values across extents. This can be accomplished by aggregating finer-scaled
 * values into coarser ones, and propagating portions of a value over portions of extents that
 * don't fit exactly.
 * 
 * @author Ferdinando Villa
 *
 */
public interface ScalingConceptualModel {

	/**
	 * Return an aggregator suitable for the given contexts and the types we represent, or null
	 * if no aggregator is required. Typically when an aggregator is requested, at least one
	 * extent has produced a nontrivial mediator, meaning that there are scaling discrepancies
	 * involving the associated observation.
	 * 
	 * @param ownContext
	 * @param ownContext
	 * @return
	 */
	public abstract IValueAggregator getAggregator(IObservationContext ownContext, 
			IObservationContext overallContext);
	
	/**
	 * Partition the passed value into what makes sense for a new extent which stands in the
	 * passed ratio with the original one. If partitioning makes no sense for this type and
	 * concept, just return the original value.
	 * 
	 * @param originalValue
	 * @param ratio
	 * @return
	 */
	public abstract IValue partition(IValue originalValue, Rational ratio);

}
