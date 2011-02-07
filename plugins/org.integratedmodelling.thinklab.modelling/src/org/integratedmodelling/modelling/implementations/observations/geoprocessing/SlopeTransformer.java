package org.integratedmodelling.modelling.implementations.observations.geoprocessing;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.morphometry.slope.SlopeAlgorithm;

/**
 *
 */
@InstanceImplementation(concept="modeltypes:SlopeAlgorithm")
public class SlopeTransformer extends SextanteAlgorithmTransformer {

	public int method = 0;
	public int units = 0;
	
	@Override
	protected GeoAlgorithm getParameterizedAlgorithm() {

		SlopeAlgorithm alg = new SlopeAlgorithm();
		
		return alg;
	}

	@Override
	protected String getResultID() {
		return SlopeAlgorithm.SLOPE;
	}

	@Override
	protected IState createOutputState(ObservationContext context) {
		return 
			new MemDoubleContextualizedDatasource(
					getObservableClass(), context.getMultiplicity(), context);
	}

}
