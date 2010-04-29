package org.integratedmodelling.modelling.visualization;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Map;

import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

public class ObservationListing {

	private IObservation observation = null;
	private Map<IConcept, IState> states = null;
	private boolean verbose;

	public ObservationListing(IInstance observation) throws ThinklabException {
		this.observation = ObservationFactory.getObservation(observation);
		this.states = ObservationFactory.getStateMap(this.observation);
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
			
			IState state = states.get(c);
			
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

	private void listHistogram(IState state, PrintStream out) throws ThinklabValueConversionException {
		
		double[] data = state.getDataAsDoubles();
		Double min = null; Double max = null;

		int nans = 0;
		for (int i = 0; i < data.length; i++) {
			
			if (Double.isNaN(data[i]))
				nans++;
			else {
				if (max == null) {
					max = data[i];
					min = data[i];
				} else {
					if (data[i] > max) max = data[i];
					if (data[i] < min) min = data[i];
				}
			}
		}
		
		NumberFormat nf = NumberFormat.getInstance();
		int ndivs = 10;
		if (min != null && max != null) {

			int[] bins = new int[ndivs + 1];
			double step = (max - min) / ndivs;
			for (int i = 0; i < data.length; i++) {
				
				if (Double.isNaN(data[i]))
					bins[ndivs] ++;
				else {
					int bin = (int)(((data[i] - min) / (max - min)) * ndivs);
					bins[bin] ++;
				}
			}
			
			int mx = bins[0];
			for (int i = 1; i <= ndivs; i++) {
				if (mx < bins[i])
					mx = bins[i];
			}
						
			for (int i = 0; i <= ndivs; i++) {

				if (i < ndivs) {	
					out.print(
						"[" + 
						nf.format(min + step*i) + "-" + 
						nf.format(min + (step * (i+1))) +
						"]: (" + 
						bins[i] + ")\t"); 
				} else {
					out.print("no-data: (" + bins[i] + ")\t\t");
				}
				
				int nstars = (int)((double)(bins[i])/(double)mx * 40.0);
				for (int j = 0; j < nstars; j++)
					out.print("*");
				out.print("\n");
			}
		} else {	
			out.println("Min = " + min+ "; max = " + max + "; " + nans + 
						" no-data values out of " + data.length);
		}
	}
	
	
}
