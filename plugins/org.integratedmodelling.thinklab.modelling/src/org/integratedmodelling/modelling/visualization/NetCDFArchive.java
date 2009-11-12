package org.integratedmodelling.modelling.visualization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.implementations.cmodels.RegularRasterModel;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
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

	RasterGrid space         = null;
	RegularTemporalGrid time = null;
	Map<IConcept,IContextualizedState> variables;
	Map<String,IContextualizedState> auxVariables = 
		new Hashtable<String, IContextualizedState>();
	
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
		
		IObservation o = Obs.getObservation(obs);
		
		IObservation spc = Obs.findObservation(o, Geospace.get().SubdividedSpaceObservable());
		
		if (spc == null || !(spc instanceof RasterGrid))
			throw new ThinklabUnimplementedFeatureException(
					"only raster grid data are supported in NetCDF exporter for now");

		//time  = (RasterGrid) Obs.findObservation(o, TimePlugin.GridObservable());
		space = (RasterGrid)spc; 
		variables = Obs.getStateMap(o);
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
		
		IContextualizedState st = 
			new MemDoubleContextualizedDatasource(null, data);
		
		auxVariables.put(concept, st);
	}
	
	public void addRasterVariable(String concept,  double[][] data) {
		
		IContextualizedState st = 
			new MemDoubleContextualizedDatasource(null, data);
		
		auxVariables.put(concept, st);
	}
	
	public void write(String filename) throws ThinklabException {
		
		Dimension latDim = null;
		Dimension lonDim = null;
		Dimension timDim = null;
		GridExtent ext   = null;

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
			
			ext = (GridExtent) ((RegularRasterModel)(space.getConceptualModel())).getExtent();
			
			latDim = ncfile.addDimension("lat", ext.getYCells());
			lonDim = ncfile.addDimension("lon", ext.getXCells());
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
		
		for (IConcept obs : variables.keySet()) {
			
			// TODO implement the rest
			
			if (spdims.size() == 2) {
				// we have space only
				String varname = getVarname(obs);
				ncfile.addVariable(varname, DataType.FLOAT, new Dimension[]{latDim,lonDim});
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
				alat.setFloat(ind1.set(i), (float)(ext.getSouth() + ext.getNSResolution() * i));
			}
			
			ArrayDouble alon = new ArrayDouble.D1(lonDim.getLength());
			Index ind2 = alon.getIndex();
			for (int i = 0; i < lonDim.getLength(); i++) {
				alon.setFloat(ind2.set(i), (float)(ext.getWest() + ext.getEWResolution() * i));
			}
			
			try {
				ncfile.write("lat", alat);
				ncfile.write("lon", alon);
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
			
		}
		
		for (IConcept obs : variables.keySet()) {
			
			// TODO implement the rest			
			if (spdims.size() == 2) {
				
				// we have space only
				String varname = getVarname(obs);
			
				ArrayDouble data = new ArrayDouble.D2(latDim.getLength(), lonDim.getLength());
				Index ind = data.getIndex();
				double[] dd = variables.get(obs).getDataAsDoubles();
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
		IObservation o = Obs.getObservation(obs);
		space = (RasterGrid) Obs.findObservation(o, Geospace.get().RasterGridObservable());
		variables.putAll(Obs.getStateMap(o));
	}
}
