package org.integratedmodelling.modelling.visualization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.corescience.ObservationFactory;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.time.implementations.observations.RegularTemporalGrid;
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
	RegularTemporalGrid time = null;
	Map<IConcept,IState> variables;
	Map<String,IState> auxVariables = 
		new Hashtable<String, IState>();
	ObservationContext context = null;
	HashSet<String> varnames = new HashSet<String>();
	
	/*
	 * container for variables to write
	 */
	ArrayList<Pair<String, String>> attributes;
	
	/**
	 * Add a contextualized observation and we do the rest.
	 * @param obs
	 * @throws ThinklabException 
	 */
	public void setObservation(IInstance obs) throws ThinklabException {
		
		IObservation o = ObservationFactory.getObservation(obs);
		this.context = new ObservationContext(o);
		IObservation spc = ObservationFactory.findTopology(o, Geospace.get().SubdividedSpaceObservable());
		
		if (spc == null || !(spc instanceof RasterGrid))
			throw new ThinklabUnimplementedFeatureException(
					"only raster grid data are supported in NetCDF exporter for now");

		//time  = (RasterGrid) Obs.findObservation(o, TimePlugin.GridObservable());
		space = (GridExtent) ((RasterGrid)spc).getExtent(); 
		variables = ObservationFactory.getStateMap(o);
	}
	
	/**
	 * Alternative to SetObservation, just pass a context and a map of
	 * states.
	 * @param obs
	 * @throws ThinklabException
	 */
	public void setStates(Map<IConcept, IState> states, IObservationContext context) throws ThinklabException {
		
		IExtent spc = context.getExtent(Geospace.get().SubdividedSpaceObservable());
		
		if (spc == null || !(spc instanceof GridExtent))
			throw new ThinklabUnimplementedFeatureException(
					"only raster grid data are supported in NetCDF exporter for now");

		//time  = (RasterGrid) Obs.findObservation(o, TimePlugin.GridObservable());
		space = (GridExtent)spc; 
		variables = states;
	}
	
	public void setSpaceGrid(RasterGrid grid) {
	}
	
	public void setTimeGrid() {
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
			new MemDoubleContextualizedDatasource(null, data, context);
		
		auxVariables.put(concept, st);
	}
	
	public void addRasterVariable(String concept,  double[][] data) {
		
		IState st = 
			new MemDoubleContextualizedDatasource(null, data, context);
		
		auxVariables.put(concept, st);
	}
	
	public void write(String filename) throws ThinklabException {
		
		Dimension latDim = null;
		Dimension lonDim = null;
		Dimension timDim = null;

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
			ncfile.addVariable("lat", DataType.FLOAT, new Dimension[]{latDim});
			ncfile.addVariableAttribute("lat", "units", "degrees_north");
			ncfile.addVariableAttribute("lat", "long_name", "latitude");
			/* add latitude and longitude as variables */
			ncfile.addVariable("lon", DataType.FLOAT, new Dimension[]{lonDim});
			ncfile.addVariableAttribute("lon", "units", "degrees_east");
			ncfile.addVariableAttribute("lon", "long_name", "longitude");
		}
		
		varnames.clear();
		
		for (IConcept obs : variables.keySet()) {
			
			// TODO implement the rest
			
			if (spdims.size() == 2) {
				// we have space only
				String varname = getVarname(obs);
				if (varnames.contains(varname))
					continue;
				
				ncfile.addVariable(varname, DataType.FLOAT, new Dimension[]{latDim,lonDim});

				varnames.add(varname);
				
				
				// FIXME: this crashes hard (something semantically bad is happening here)
				// add uncertainty if any
				//if (variables.get(obs).getMetadata().get(Metadata.UNCERTAINTY) != null)
				//        ncfile.addVariable(varname+"Uncertainty", DataType.FLOAT, new Dimension[]{latDim,lonDim});
				
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
		if (space != null) {
			
			ArrayDouble alat = new ArrayDouble.D1(latDim.getLength());
			Index ind1 = alat.getIndex();
			for (int i = 0; i < latDim.getLength(); i++) {
				alat.setFloat(ind1.set(i), (float)(space.getSouth() + space.getNSResolution() * i));
			}
			
			ArrayDouble alon = new ArrayDouble.D1(lonDim.getLength());
			Index ind2 = alon.getIndex();
			for (int i = 0; i < lonDim.getLength(); i++) {
				alon.setFloat(ind2.set(i), (float)(space.getWest() + space.getEWResolution() * i));
			}
			
			try {
				ncfile.write("lat", alat);
				ncfile.write("lon", alon);
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		}
		
		for (IConcept obs : variables.keySet()) {
			
			if (variables.get(obs).getTotalSize() != space.getXCells() * space.getYCells()) {
				ModellingPlugin.get().logger().error(
						"state of " + obs + " has " + variables.get(obs).getTotalSize() + 
						" multiplicity when context expects " + (space.getXCells() * space.getYCells()) + 
						": results not stored");
				continue;
			}
			
			varnames.clear();
			
			// TODO implement the rest			
			if (spdims.size() == 2) {
				
				// we have space only
				String varname = getVarname(obs);
				IState state = variables.get(obs);
				
				if (varnames.contains(varname))
					continue;
				
				varnames.add(varname);
				
				ArrayDouble data = new ArrayDouble.D2(latDim.getLength(), lonDim.getLength());
				Index ind = data.getIndex();
				double[] dd = state.getDataAsDoubles();
				
				// this can now happen for stuff like categories, eventually it will be removed
				if (dd == null)
					return;
				
				// FIXME: same deal, more Uncertainty-related badness
				//double[] uu = (double[]) state.getMetadata().get(Metadata.UNCERTAINTY);
				double[] uu = null;
				ArrayDouble unce = null;
				if (uu != null)
					unce = new ArrayDouble.D2(latDim.getLength(), lonDim.getLength());

				int i = 0;
				for (int lat = 0; lat < latDim.getLength(); lat++) {
					for (int lon = 0; lon < lonDim.getLength(); lon++) {
						Index index = ind.set(lat,lon);
						data.setFloat(index, (float)dd[i]);
						if (uu != null)
							unce.setFloat(index, (float)uu[i]);
						i++;
					}	
				}
				try {
					ncfile.write(varname, data);
					if (uu != null)
						ncfile.write(varname+"Uncertainty", unce);
				} catch (Exception e) {
					throw new ThinklabIOException(e);
				}
			}
		}
		
		for (String varname : auxVariables.keySet()) {
			
			if (spdims.size() == 2) {
			
				ArrayDouble data = new ArrayDouble.D2(latDim.getLength(), lonDim.getLength());
				Index ind = data.getIndex();
				double[] dd = auxVariables.get(varname).getDataAsDoubles();
				int i = 0;
				for (int lat = 0; lat < latDim.getLength(); lat++) {
					for (int lon = 0; lon < lonDim.getLength(); lon++) {
						data.setFloat(ind.set(lat,lon), (float)dd[i++]);
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
	private String getVarname(IConcept obs) {
		
		String ret = obs.getLocalName();
		if (obs.is("geophysics:Altitude")) {
			ret = "Altitude";
		}
		return ret;
	}

	/**
	 * add all the states of another observation. setObservation() must have been
	 * called, and all states must conform to the topology of the "master" obs.
	 */
	public void addObservation(IInstance obs) throws ThinklabException {
		IObservation o = ObservationFactory.getObservation(obs);
		space =
			(GridExtent) ((RasterGrid)
					ObservationFactory.findObservation(
							o, Geospace.get().RasterGridObservable())).getExtent();
		variables.putAll(ObservationFactory.getStateMap(o));
	}
}
