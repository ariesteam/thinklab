package org.integratedmodelling.modelling.visualization;

import java.io.PrintStream;
import java.util.Map;

import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

public class ObservationListing {

	private IObservation observation = null;
	private Map<IConcept, IContextualizedState> states = null;
	private boolean verbose;

	public ObservationListing(IInstance observation) throws ThinklabException {
		this.observation = Obs.getObservation(observation);
		this.states = Obs.getStateMap(this.observation);
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public void dump(PrintStream out) throws ThinklabException {
		
		RasterGrid rgrid = null;
		if (ObservationFactory.isRaster(observation)) {
			rgrid = ObservationFactory.getRasterGrid(observation);
			out.println("Spatially distributed on a " + 
					rgrid.getRows() + " by " + rgrid.getColumns() + 
					" raster grid");
		}
		
		for (IConcept c : states.keySet()) {
			
			IContextualizedState state = states.get(c);
			
			/*
			 * 
			 */
			out.println("Listing state of observable " + c + " (" + state + ")");
			
			/*
			 * list datasource
			 */
			if (verbose) {
				// TODO
			}
			
			/*
			 * compute histogram
			 */
			listHistogram(state, out);
			
		}
		
	}

	private void listHistogram(IContextualizedState state, PrintStream out) throws ThinklabValueConversionException {
		
		double[] data = state.getDataAsDoubles();
		double min = data[0]; double max = data[0];
		int nans = 0;
		for (int i = 0; i < data.length; i++) {
			
			if (Double.isNaN(data[i]))
				nans++;
			else {				
				if (data[i] > max) max = data[i];
				if (data[i] < min) min = data[i];
			}
		}
		
		out.println("\tmin = " + min+ "; max = " + max + "; " + nans + " NaN values out of " + data.length);
		
	}
	
	
}
