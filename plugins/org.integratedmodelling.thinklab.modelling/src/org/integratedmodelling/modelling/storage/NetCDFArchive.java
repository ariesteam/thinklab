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
package org.integratedmodelling.modelling.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.interfaces.IGridMask;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.context.Context;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.time.extents.RegularTimeGridExtent;
import org.integratedmodelling.utils.Pair;

import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;

/**
 * Easily create a NetCDF file with all the data from an observation set, or add data piece by
 * piece. Not flexible and not complete.
 * 
 * @author Ferdinando
 *
 */
public class NetCDFArchive {

	GridExtent space         = null;
	RegularTimeGridExtent time = null;
	
	Map<String,IState> auxVariables = 
	new Hashtable<String, IState>();
	IContext context = null;
	HashSet<String> varnames = new HashSet<String>();
	
	/*
	 * container for variables to write
	 */
	ArrayList<Pair<String, String>> attributes;

	
	public void setContext(IContext context) throws ThinklabUnimplementedFeatureException {

		IExtent rgrid = Context.getSpace(context);
		if (rgrid instanceof GridExtent) {
			this.space = (GridExtent)rgrid;
		} else {
			throw new ThinklabUnimplementedFeatureException(
				"only raster grid data are supported in NetCDF exporter for now");			
		}
		
		this.context = context;
		
		// FIXME remove eventually
		 ((ObservationContext)context).collectStates();
		
	}
	
	/**
	 * Add another variable passing the data array directly. Must have called 
	 * setObservation first to set the context.
	 * 
	 * @param concept
	 * @param data
	 */
	public void addRasterVariable(String concept,  double[] data) {
		
		IState st = 
			new MemDoubleContextualizedDatasource(null, data, (ObservationContext) context);
		
		auxVariables.put(concept, st);
	}
	
	public void addRasterVariable(String concept,  double[][] data) {
		
		IState st = 
			new MemDoubleContextualizedDatasource(null, data, (ObservationContext)context);
		
		auxVariables.put(concept, st);
	}
	
	public void write(String filename) throws ThinklabException {
		
		Dimension latDim = null;
		Dimension lonDim = null;
		Dimension timDim = null;

		HashMap<IConcept, double[]> dataCatalog = new HashMap<IConcept, double[]>();

		ArrayList<Dimension> spdims = new ArrayList<Dimension>();
		
		if (!filename.endsWith(".nc"))
			filename += ".nc";
		
		NetcdfFileWriteable ncfile;
		try {
			ncfile = NetcdfFileWriteable.createNew(filename, false);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		/*
		 * add dimensions
		 */
		if (time != null) {
			// unimplemented for now
		}
		
		if (space != null) {
			
			latDim = ncfile.addDimension("lat", space.getYCells());
			lonDim = ncfile.addDimension("lon", space.getXCells());
			spdims.add(latDim);
			spdims.add(lonDim);

			/* add latitude and longitude as variables */
			ncfile.addVariable("lat", DataType.DOUBLE, new Dimension[]{latDim});
			ncfile.addVariableAttribute("lat", "units", "degrees_north");
			ncfile.addVariableAttribute("lat", "long_name", "latitude");
			/* add latitude and longitude as variables */
			ncfile.addVariable("lon", DataType.DOUBLE, new Dimension[]{lonDim});
			ncfile.addVariableAttribute("lon", "units", "degrees_east");
			ncfile.addVariableAttribute("lon", "long_name", "longitude");
		}
		
		varnames.clear();
		
		for (IConcept obs : ((IObservationContext)context).getStateObservables()) {
			
			// TODO implement the rest
			
			if (spdims.size() == 2) {
				// we have space only
				String varname = getVarname(obs, false);
				if (varnames.contains(varname))
					continue;
				
				IState state = context.getState(obs);
				
				// ensure that all metadata are defined. FIXME: review the logics of all this BS and ensure it's
				// done propertly and automatically.
				Metadata.rankConcepts(state);
				Metadata.getImageData(state);
				
				ncfile.addVariable(varname, DataType.FLOAT, new Dimension[]{latDim,lonDim});
				
				double zum = 0.0; double[] dio = state.getDataAsDoubles();
				if (dio != null) {
					for (int zi = 0; zi < dio.length; zi++) {
						if (!Double.isNaN(dio[zi]))
							zum += dio[zi];
					}
				
//					System.out.println("STORING " + obs + " WITH SUM " + zum);

					// do this now, so we have the uncertainty info in the metadata. Save for later. 
					// FIXME the logics of this is a little on the perverse side but the IState
					// contract does not mandate caching so for now it's good as is.
					dataCatalog.put(obs, dio);
				}
				

				varnames.add(varname);
				
				// add uncertainty if any
				double[] uu = (double[]) state.getMetadata().get(Metadata.UNCERTAINTY);
				if (uu != null) {
					ncfile.addVariable( getVarname(obs, true), DataType.FLOAT, new Dimension[]{latDim,lonDim});
				}
				
				// TODO if var is a measurement, add units attribute - this is a stupid stub
				if (varname.equals("Altitude")) {
					ncfile.addVariableAttribute("Altitude", "units", "meters");
//					ncfile.addVariableAttribute("altitude", "positive", "up");
//					ncfile.addVariableAttribute("altitude", "_CoordinateAxisType", "Height");
				}
			}
		}
		
		for (String var : auxVariables.keySet()) {
			
			// TODO implement the rest
			if (spdims.size() == 2) {
				ncfile.addVariable(var, DataType.FLOAT, new Dimension[]{latDim,lonDim});
			}
		}
		
		/*
		 * create the file before we add variables
		 */
		try {
			ncfile.create();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		/*
		 * TODO write time data
		 */
		
		/*
		 * lat and lon data if any
		 */
		IGridMask mask = null;
		
		if (space != null) {
			
			mask = space.getActivationLayer();
			
//			System.out.println("SPACE IS " + space.getNSResolution() + " " + space.getEWResolution());
			
			ArrayDouble alat = new ArrayDouble.D1(latDim.getLength());
			Index ind1 = alat.getIndex();
			double xcn = space.getSouth() + space.getNSResolution() * 0.5;
			for (int i = 0; i < latDim.getLength(); i++) {
				alat.setDouble(ind1.set(i), xcn);
				xcn += space.getNSResolution();
			}
			
			ArrayDouble alon = new ArrayDouble.D1(lonDim.getLength());
			Index ind2 = alon.getIndex();
			xcn = space.getWest() + space.getEWResolution() * 0.5;
			for (int i = 0; i < lonDim.getLength(); i++) {
				alon.setDouble(ind2.set(i), xcn);
				// use NSres instead of EWres as they may differ slightly. This will 
				// skew results if the cell is not square. This way import to ARC will
				// work.
				xcn += space.getNSResolution();
			}
			
			try {
				ncfile.write("lat", alat);
				ncfile.write("lon", alon);
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		}
		
		for (IConcept obs : ((IObservationContext)context).getStateObservables()) {
			
			if (context.getState(obs).getValueCount() != space.getXCells() * space.getYCells()) {
				ModellingPlugin.get().logger().error(
						"state of " + obs + " has " + context.getState(obs).getValueCount() + 
						" multiplicity when context expects " + (space.getXCells() * space.getYCells()) + 
						": results not stored");
				continue;
			}
			
			varnames.clear();
			
			// TODO implement the rest			
			if (spdims.size() == 2) {
				
				// we have space only
				String varname = getVarname(obs, false);
				IState state = context.getState(obs);
				
				if (varnames.contains(varname))
					continue;
				
				varnames.add(varname);
				
				ArrayDouble data = new ArrayDouble.D2(latDim.getLength(), lonDim.getLength());
				Index ind = data.getIndex();
				double[] dd = dataCatalog.get(obs);
				
				// this can now happen for stuff like categories, eventually it will be removed
				if (dd == null)
					continue;
				
				double[] uu = (double[]) state.getMetadata().get(Metadata.UNCERTAINTY);
				ArrayDouble unce = null;
				if (uu != null) {
					unce = new ArrayDouble.D2(latDim.getLength(), lonDim.getLength());
				}
					
				int i = 0;
				for (int lat = 0; lat < latDim.getLength(); lat++) {
					for (int lon = 0; lon < lonDim.getLength(); lon++) {
						
						Index index = ind.set(lat,lon);
						
						double val = dd[i];
						double uvl = uu == null ? 0 : uu[i];
						
						
						if (mask != null) {
							int[] xy = space.getXYCoordinates(i);
							if (!mask.isActive(xy[0], xy[1])) {
								val = Double.NaN;
								uvl = Double.NaN;
							}
						}

						data.setFloat(index, (float)val);
						if (uu != null)
							unce.setFloat(index, (float)uvl);
						i++;
					}	
				}
				try {
					ncfile.write(varname, data);
					if (uu != null){
						ncfile.write( getVarname(obs, true), unce);
					}
				} catch (Exception e) {
					throw new ThinklabIOException(e);
				}
			}
		}
		
		for (String varname : auxVariables.keySet()) {
			
			if (spdims.size() == 2) {
			
				ArrayDouble data = new ArrayDouble.D2(latDim.getLength(), lonDim.getLength());
				Index ind = data.getIndex();
				Metadata.getImageData(auxVariables.get(varname));
				double[] dd = auxVariables.get(varname).getDataAsDoubles();
				int i = 0;
				for (int lat = 0; lat < latDim.getLength(); lat++) {
					for (int lon = 0; lon < lonDim.getLength(); lon++) {
						
						double val = dd[i];
						if (mask != null) {
							int[] xy = space.getXYCoordinates(i);
							if (!mask.isActive(xy[0], xy[1]))
								val = Double.NaN;
						}
						
						data.setFloat(ind.set(lat,lon), (float)val);
						i++;
					}	
				}
				
				try {
					ncfile.write(varname, data);
				} catch (Exception e) {
					throw new ThinklabIOException(e);
				}
			}
		}
		
		try {
			ncfile.close();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	/*
	 * just recognize some concepts that have special meaning for the netcdf CF convention
	 */
	private String getVarname(IConcept obs, boolean isUncertainty) {
		
		String ret = obs.getAnnotation("metadata:hasOutputId");
		if (ret == null) {
			ret =  obs.getLocalName() + (isUncertainty ? "Uncertainty" : "") + "_" + obs.getConceptSpace();
			if (obs.is("geophysics:Altitude")) {
				ret = "Altitude";
			}
		} else if (isUncertainty) {
			ret += "Uncertainty";
		}
		return ret;
	}

	/**
	 * add all the states of another observation. setObservation() must have been
	 * called, and all states must conform to the topology of the "master" obs.
	 */
	public void addObservation(IObservationContext obs) throws ThinklabException {
		space = (GridExtent) Context.getSpace(obs);
		((ObservationContext)context).mergeStates(obs);
	}

}
