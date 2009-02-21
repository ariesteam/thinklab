package org.integratedmodelling.geospace.implementations.cmodels;

import org.integratedmodelling.corescience.implementations.datasources.MemValueContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.TransformingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * A spatial coverage model whose subdivisions are the equal-valued partitions of a main classification
 * raster coverage. The multiplicity is the total number of different values in the classification
 * coverage. Useful to cut out raster datasets based on attribute maps (e.g. land use) or to give
 * semantic sense to clustering algorithms.
 * 
 * @author Ferdinando
 *
 */
public class ClusteredRasterModel implements IConceptualModel, TransformingConceptualModel {

	@Override
	public IStateAccessor getStateAccessor(IConcept stateType,
			IObservationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validate(IObservation observation)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IInstance transformObservation(IInstance inst)
			throws ThinklabException {
		
		IObservation obs = (IObservation) inst.getImplementation();
		IObservationContext cm = obs.getObservationContext();
		
		if (cm.getDimension(Geospace.get().SpaceObservable()) == null) {
			throw new ThinklabValidationException(
					"clustering model: cannot use non-spatial observations");
		}
		
		/* get all spatial dependencies of obs and set their states as cluster */
		
		
		/* process the results of clustering into own datasource */
//		MemValueContextualizedDatasource ds = new MemValueContextualizedDatasource();
		
		
		/* build final transformed observation */
		
		
		return null;
	}


}
