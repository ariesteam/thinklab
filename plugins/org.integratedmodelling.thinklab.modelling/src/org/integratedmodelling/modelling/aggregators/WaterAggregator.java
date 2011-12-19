package org.integratedmodelling.modelling.aggregators;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.modelling.context.Context;
import org.integratedmodelling.modelling.interfaces.IStateAggregator;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.Aggregator;

/**
 * Specifically meant to aggregate volumes of water expressed in mm; will return
 * total cubic meters of water across area. NOTE: will aggregate across space AND
 * time and won't work on non-spatial data.
 * 
 * @author Ferd
 *
 */
@Aggregator(id="water-mm", concept="representation:Volume")
public class WaterAggregator implements IStateAggregator {

	@Override
	public double aggregate(IState state, IContext context) throws ThinklabException {
		double area;
		
		IExtent rgrid = Context.getSpace(context);
		if (rgrid instanceof GridExtent) {
			area = ((GridExtent)rgrid).getCellAreaMeters();
		} else {
			return 0.0;
		}
		
		double ret = 0.0;
		
		for (double d : state.getDataAsDoubles()) {
			if (Double.isNaN(d) || d <= 0.0)
				continue;
			
			ret += (d * 0.001 * area);
		}
		
		return ret;
	}

}
