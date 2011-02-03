package org.integratedmodelling.corescience.implementations.datasources;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ContextMapper;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IExtent.AggregationParameters;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IOperator;

public abstract class DefaultAbstractState implements IState {

	@Override
	public boolean isSpatiallyDistributed() {
		IExtent e = context.getSpace();
		return e != null && e.getValueCount() > 1;
	}


	@Override
	public boolean isTemporallyDistributed() {
		IExtent e = context.getTime();
		return e != null && e.getValueCount() > 1;
	}

	protected IContext context;
	protected IConcept _type;
	protected Metadata metadata = new Metadata();

	@Override
	public IState aggregate(IConcept concept) throws ThinklabException {
		
		CoreScience.PhysicalNature pn = 
			(CoreScience.PhysicalNature) this.getMetadata().get(Metadata.PHYSICAL_NATURE);
		
		// if there's nothing to aggregate, we're already aggregated in 
		// that dimension.
		if (pn == null)
			return null;

		Unit unit = 
			(Unit) this.getMetadata().get(Metadata.UNIT);

		
		IContext sourceC = this.context;
		IContext finalC = this.context;
		IState ret = null;

		if (finalC.getExtent(concept) != null) {
			
			IExtent extent = finalC.getExtent(concept);			
			AggregationParameters ap = 
				extent.getAggregationParameters(this.getObservableClass(), unit);
			
			if (ap == null)
				throw new ThinklabInternalErrorException(
						"cannot aggregate over extent - may be unimplemented, check extent implementation");

			finalC = finalC.collapse(finalC.getSpace().getObservableClass());
			ret = new MemDoubleContextualizedDatasource(
					_type, finalC.getMultiplicity(), finalC);
			
			ContextMapper cmap = new ContextMapper(sourceC, finalC);
			
			for (int  i = 0; i < finalC.getMultiplicity(); i++)
				ret.setValue(i, Double.NaN);

			for (int i = 0; i < sourceC.getMultiplicity(); i++) {
				
				double vl = this.getDoubleValue(i);
				
				if (Double.isNaN(vl))
					continue;
				
				int tind = cmap.getIndex(i);
				double val = 
					vl * 
					ap.aggregator.getAggregationFactor(i);
				
				if (Double.isNaN(ret.getDoubleValue(tind)))
					ret.setValue(tind, val);
				else 
					ret.setValue(tind, ret.getDoubleValue(tind) + val);
			}
			
			if (ap.aggregationOperator.equals(IOperator.AVG)) {
				for (int  i = 0; i < finalC.getMultiplicity(); i++)
					if (!Double.isNaN(ret.getDoubleValue(i)))
						ret.setValue(i, ret.getDoubleValue(i)/(double)finalC.getMultiplicity());
			}
			
			// unnecessary unless we loop over all dims - left here for
			// ease of extension
			sourceC = finalC;

			// transfer metadata appropriately
			ret.getMetadata().put(Metadata.UNIT, ap.aggregatedUnit);
			ret.getMetadata().put(Metadata.UNIT_SPECS, ap.aggregatedUnit.toString());
			ret.getMetadata().put(Metadata.PHYSICAL_NATURE, ap.aggregatedNature);
		}

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
