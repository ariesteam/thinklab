package org.integratedmodelling.modelling.implementations.observations.geoprocessing;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IState;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.rasterize.euclideanDistance.EuclideanDistanceAlgorithm;

/**
 *
 */
public class EuclideanDistanceTransformer extends SextanteAlgorithmTransformer {

	@Override
	protected GeoAlgorithm getParameterizedAlgorithm() {

		EuclideanDistanceAlgorithm alg = new EuclideanDistanceAlgorithm();
		
		return alg;
	}

	@Override
	protected String getResultID() {
		return EuclideanDistanceAlgorithm.RESULT;
	}

	@Override
	protected IState createOutputState(ObservationContext context) {
		return 
			new MemDoubleContextualizedDatasource(
					getObservableClass(), context.getMultiplicity(), context);
	}

}
