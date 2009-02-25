package org.integratedmodelling.geospace.implementations.cmodels;

import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.GridMaskExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
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
@InstanceImplementation(concept=Geospace.GRID_CLASSIFICATION_MODEL)
public class ClassifiedRasterConceptualModel implements ExtentConceptualModel {

	GridMaskExtent totalExtent = null;
	
	public ClassifiedRasterConceptualModel(int[] maskLayer, int xDivs, int yDivs, int nClasses, int[] classValues) {

		totalExtent = new GridMaskExtent(this, maskLayer, xDivs, yDivs, nClasses, classValues);
	}
	
	@Override
	public IExtent getExtent() throws ThinklabException {
		return totalExtent;
	}

	@Override
	public IExtentMediator getExtentMediator(IExtent extent)
			throws ThinklabException {
		return null;
	}

	@Override
	public IExtent mergeExtents(IExtent original, IExtent other,
			LogicalConnector connector, boolean isConstraint)
			throws ThinklabException {
		return null;
	}

	@Override
	public IStateAccessor getStateAccessor(IConcept stateType,
			IObservationContext context) {
		return null;
	}

	@Override
	public IConcept getStateType() {
		return Geospace.get().GridClassifier();
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
	}

	@Override
	public void validate(IObservation observation)
			throws ThinklabValidationException {
	}

}
