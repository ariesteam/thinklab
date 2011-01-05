package org.integratedmodelling.modelling.visualization;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.modelling.context.Context;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class ObservationListing {

	private boolean verbose;
	private IContext context;

	public ObservationListing(IContext context) throws ThinklabException {
		this.context = context;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public void dump(PrintStream out) throws ThinklabException {
		
		IExtent rgrid = Context.getSpace(context);
		if (rgrid instanceof GridExtent) {
			out.println("Spatially distributed on a " + 
						((GridExtent)rgrid).getXCells() + " by " + ((GridExtent)rgrid).getYCells() + 
						" raster grid");
		}
		
		for (IConcept c : ((IObservationContext)context).getStateObservables()) {
			
			IState state = context.getState(c);
			// throw away the result, but instantiate all metadata
			Metadata.getImageData(state);
			
			/*
			 * 
			 */
			out.println(StringUtils.repeat("-", 76));
			out.println(c);
			
			/*
			 * list datasource
			 */
			if (verbose) {
				out.println(""+state);
			}
						
			/*
			 * compute histogram
			 */
			listHistogram(state, out);
			out.println();
			
		}
		
	}

	private void listHistogram(IState state, PrintStream out) throws ThinklabValueConversionException {
		
		double[] data = state.getDataAsDoubles();
		
		if (data == null)
			return;
		
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
		Integer nlev = (Integer)state.getMetadata().get(Metadata.IMAGE_LEVELS);
		HashMap<IConcept, Integer> ranks = Metadata.getClassMappings(state.getMetadata());

		if (ranks != null || (nlev != null && nlev < 12)) {
			ndivs = nlev;
		}
		
		if (min != null && max != null) {

			int[] bins = new int[ndivs + 1];
			double step = (max - min) / ndivs;
			for (int i = 0; i < data.length; i++) {
				
				if (Double.isNaN(data[i]))
					bins[ndivs] ++;
				else {
					int bin = (int)(((data[i] - min) / (max - min)) * (ndivs-1));
					bins[bin] ++;
				}
			}
			
			int mx = bins[0];
			for (int i = 1; i <= ndivs; i++) {
				if (mx < bins[i])
					mx = bins[i];
			}
			
			for (int i = 0; i <= ndivs; i++) {

				if (i == 0 || i == ndivs)
					out.println(StringUtils.repeat("-", 76));
				
				String udsc = 
					i < ndivs ? 
						"[" + 
							nf.format(min + step*i) + " " + 
							nf.format(min + (step * (i+1))) +
							"]" :
						"no-data";	
				
				int nstars = (int)((double)(bins[i])/(double)mx * 40.0);
				out.println(
						StringUtils.rightPad(udsc, 26) +
						"|" +
						StringUtils.rightPad(
								StringUtils.repeat("*", nstars), 40) +
					    "|" +
						StringUtils.leftPad(""+bins[i], 8));
			}
		} else {	
			out.println("Min = " + min+ "; max = " + max + "; " + nans + 
						" no-data values out of " + data.length);
		}
	}
	
	
}
