package org.integratedmodelling.geospace.implementations.cmodels;

import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.LogicalConnector;
/**
 * A spatial coverage model whose subdivisions are the equal-valued partitions of a main 
 * raster coverage. The multiplicity is the total number of different values in the classification
 * coverage. Typically produced by clustering, but may have other applications.
 *
 * @author Ferdinando
 *
 */
public class ClassifiedRasterConceptualModel implements ExtentConceptualModel {

	@Override
	public IExtent getExtent() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtentMediator getExtentMediator(IExtent extent)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent mergeExtents(IExtent original, IExtent other,
			LogicalConnector connector, boolean isConstraint)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

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

}
