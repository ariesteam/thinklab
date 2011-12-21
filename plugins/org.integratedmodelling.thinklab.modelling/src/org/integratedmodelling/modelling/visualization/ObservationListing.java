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
package org.integratedmodelling.modelling.visualization;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.integratedmodelling.corescience.implementations.datasources.MemObjectContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.modelling.context.Context;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;

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
						" raster grid (cell area = " +
						NumberFormat.getInstance().format(((GridExtent)rgrid).getCellAreaMeters()) + 
						" m²)");
		}
		
		for (IConcept c : ((IObservationContext)context).getStateObservables()) {
			
			IState state = context.getState(c);
			
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
			if (state instanceof MemObjectContextualizedDatasource ||
				state instanceof CategoricalDistributionDatasource) {
				listCategories(state, out);
			} else {
				listHistogram(state, out);
			}
			out.println();
			
		}
		
	}

	private void listCategories(IState state,
			PrintStream out) throws ThinklabException {
		
		// throw away the result, but instantiate all metadata
		Metadata.getImageData(state);
		
		ArrayList<Pair<Object, Integer>> catalog = new ArrayList<Pair<Object,Integer>>();
		int nulls = 0;
		
		for (int i = 0; i < state.getValueCount(); i++) {

			Object o = state.getValue(i);
			
			if (o == null) {
				nulls ++;
				continue;
			}
			
			/*
			 * categorize
			 */
			boolean found = false;
			for (int j = 0; j < catalog.size(); j++) {
				if (o.equals(catalog.get(j).getFirst())) {
					found = true;
					catalog.get(j).setSecond(catalog.get(j).getSecond() + 1);
				}
			}
			if (!found)
				catalog.add(new Pair<Object, Integer>(o, 1));
		}
		
		int ndivs = 0;
		int mx = nulls;
		
		if (catalog.size() > 0) {
			
			if (catalog.get(0).getFirst() instanceof Comparable<?>) {
				Collections.sort(catalog, new Comparator<Pair<Object,Integer>>() {

					@SuppressWarnings("unchecked")
					@Override
					public int compare(Pair<Object, Integer> o1,
							Pair<Object, Integer> o2) {
						Comparable<Object> oo1 = (Comparable<Object>)o1.getFirst();
						Comparable<Object> oo2 = (Comparable<Object>)o2.getFirst();
						return oo1.compareTo(oo2);
					}					
				});
			}
			
			ndivs = catalog.size();
			for (int i = 0; i < ndivs; i++) {
				if (mx < catalog.get(i).getSecond())
					mx = catalog.get(i).getSecond();
			}
		}

		/*
		 * max len of bar to fit in 76 chars
		 */
		int wlen = "no-data".length();
		for (int i = 0; i < ndivs; i++) {
			int nx = catalog.get(i).getFirst().toString().length();
			if (wlen < nx)
				wlen = nx;
		}
		int mlen = 76 - wlen - 12;
		
		for (int i = 0; i <= ndivs; i++) {
			
			if (i == 0 || i == ndivs)
				out.println(StringUtils.repeat("-", 76));
				
			String udsc = 
				i < ndivs ? 
						catalog.get(i).getFirst().toString() :
						"no-data";	
				
			int nstars = 
				i < ndivs ?
					(int)(((double)(catalog.get(i).getSecond())/(double)mx) * (double)mlen) :
					(int)((double)(nulls)/(double)mx * (double)mlen);
		
			out.println(
				StringUtils.rightPad(udsc, wlen + 1) +
				"|" +
				StringUtils.rightPad(StringUtils.repeat("*", nstars), mlen) +
				"|" +
				StringUtils.leftPad(""+
								(i < ndivs ? catalog.get(i).getSecond() : nulls), 
								8));
		}
		
		if (state.isSpatiallyDistributed() && !state.isTemporallyDistributed()) {

			NumberFormat nf = NumberFormat.getInstance();
			IState as = state.aggregate(Geospace.get().SubdividedSpaceObservable());
			double sm = ((ArealExtent)(state.getObservationContext().getSpace())).
				getTotalAreaSquareMeters()  / 1000000.0;

			if (as != null) {
				
				double[] unc = 
					(double[]) as.getMetadata().get(Metadata.UNCERTAINTY);
							
				out.println("Aggregated over " + nf.format(sm) + " km²: " +
						nf.format(as.getDoubleValue(0)) + 
						(unc == null ? 
							"" :
							" \u00B1 " + nf.format(as.getDoubleValue(0) * unc[0] * 2)) +
						" " +
						as.getMetadata().get(Metadata.UNIT));
			}
		}
	}

	private void listHistogram(IState state, PrintStream out) throws ThinklabException {
		
		// throw away the result, but instantiate all metadata
		Metadata.getImageData(state);

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

		if ((ranks != null && ranks.size() > 0) || (nlev != null && nlev > 0 && nlev < 12)) {
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
			
			/*
			 * max len of bar to fit in 76 chars
			 */
			int wlen = "no-data".length();
			for (int i = 0; i < ndivs; i++) {
				int nx = 
					("[" + 
					  nf.format(min + step*i) + " " + 
					  nf.format(min + (step * (i+1))) +
					  "]").length();
				if (wlen < nx)
					wlen = nx;
			}
			int mlen = 76 - wlen - 12;
			
			
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
				
				int nstars = (int)(((double)(bins[i])/(double)mx) * (double)mlen);
				out.println(
						StringUtils.rightPad(udsc, wlen + 1) +
						"|" +
						StringUtils.rightPad(
								StringUtils.repeat("*", nstars), mlen) +
					    "|" +
						StringUtils.leftPad(""+bins[i], 8));
			}
		} else {	
			out.println("Min = " + min+ "; max = " + max + "; " + nans + 
						" no-data values out of " + data.length);
		}
		
		if (state.isSpatiallyDistributed() && !state.isTemporallyDistributed()) {
		
			IState as = state.aggregate(Geospace.get().SubdividedSpaceObservable());
			double sm = ((ArealExtent)(state.getObservationContext().getSpace())).
				getTotalAreaSquareMeters() / 1000000.0;

			if (as != null) {

				double[] unc = 
					(double[]) as.getMetadata().get(Metadata.UNCERTAINTY);
				
				out.println("Aggregated over " + nf.format(sm) + " km²: " +
						nf.format(as.getDoubleValue(0)) + 
						(unc == null ? 
							"" :
							" \u00B1 " + nf.format(as.getDoubleValue(0) * unc[0] * 2)) +
						" " +
						as.getMetadata().get(Metadata.UNIT));
			}
		}
	}
	
	
}
