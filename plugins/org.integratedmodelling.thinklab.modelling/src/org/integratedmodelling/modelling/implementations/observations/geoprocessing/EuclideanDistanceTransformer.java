package org.integratedmodelling.modelling.implementations.observations.geoprocessing;

import org.integratedmodelling.corescience.implementations.observations.Measurement;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.internal.ContextTransformingObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Unless I find a Sextante algorithm for this, computes a 2-scan EDT on a
 * binary ranking, returning results in the requested unit (which must 
 * obviously be a length). Should be made dimensionally safe (callable on
 * any context with no error unless no space topology in context, but 1px
 * topology should be fine, and if context is vector should rasterize before
 * computing).
 * 
 * @author ferdinando.villa
 *
 */
public class EuclideanDistanceTransformer extends Measurement implements
		ContextTransformingObservation {

	@Override
	public IObservationContext getTransformedContext(IObservationContext context)
			throws ThinklabException {
		
		/*
		 * context becomes raster anyway.
		 */
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext transform(IObservationContext sourceObs, ISession session,
			IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getTransformedObservationClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
