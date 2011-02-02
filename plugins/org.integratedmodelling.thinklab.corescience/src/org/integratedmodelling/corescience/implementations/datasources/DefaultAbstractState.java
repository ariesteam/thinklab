package org.integratedmodelling.corescience.implementations.datasources;

import org.integratedmodelling.corescience.context.ContextMapper;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.observations.Measurement;
import org.integratedmodelling.corescience.implementations.observations.Measurement.PhysicalNature;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public abstract class DefaultAbstractState implements IState {

	protected IContext context;
	protected IConcept _type;
	protected Metadata metadata = new Metadata();

	@Override
	public IState aggregate(IConcept concept) throws ThinklabException {
		
		Measurement.PhysicalNature pn = 
			(PhysicalNature) this.getMetadata().get(Metadata.PHYSICAL_NATURE);
		
		Unit unit = 
			(Unit) this.getMetadata().get(Metadata.UNIT);

		// if there's nothing to aggregate, we're already aggregated in 
		// that dimension.
		if (pn == null)
			return this;
		
		IContext sourceC = this.context;
		IContext finalC = this.context;
		IState ret = null;
		
		// TODO create new unit for aggregated state w/o the dimension if the
		// physical nature is extensive. That should be done in the extent.
		// If we don't have the extent in the unit, or have no unit,
		// we shold just sum or average w/o transformation, using the phys 
		// nature as a guide.
		
		// TODO this aggregates it all - must check the types and only do this
		// ONCE. Need functions in the extent to properly implement this.
		// Extents have a natural knowledge of extensive behavior, so that's OK.
		
		if (finalC.getExtent(concept) != null) {
			
			IExtent extent = finalC.getExtent(concept);
			
			extent.getAggregationParameters(concept, unit);
			
			finalC = finalC.collapse(finalC.getSpace().getObservableClass());
			ret = new MemDoubleContextualizedDatasource(
					_type, finalC.getMultiplicity(), finalC);
			
			// TODO find appropriate conversion to eliminate extent from
			// unit.
			double convf = 1.0;
			ContextMapper cmap = new ContextMapper(sourceC, finalC);
			for (int i = 0; i < sourceC.getMultiplicity(); i++) {
				int tind = cmap.getIndex(i);
				double val = this.getDoubleValue(i) * convf;
				ret.setValue(tind, ret.getDoubleValue(tind) + val);
			}
			if (pn.equals(PhysicalNature.INTENSIVE)) {
				for (int  i = 0; i < finalC.getMultiplicity(); i++)
					ret.setValue(i, ret.getDoubleValue(i)/(double)finalC.getMultiplicity());
			}
			sourceC = finalC;
		}

		// TODO transfer metadata appropriately
		
		return ret;
	}


	@Override
	public IContext getObservationContext() {
		return this.context;
	}
	
	@Override
	public IConcept getObservableClass() {
		return _type;
	}
	
	@Override
	public Metadata getMetadata() {
		return this.metadata ;
	}

}
