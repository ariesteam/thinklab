/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
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
