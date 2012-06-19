package org.integratedmodelling.thinklab.modelling.datasets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabUnsupportedOperationException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataset;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.geospace.interfaces.IGridMask;
import org.integratedmodelling.thinklab.time.extents.RegularTemporalGrid;

import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;

public class NetCDFDataset implements IDataset {

	public NetCDFDataset() {
	}
	
	/**
	 * For direct API use
	 * @param context
	 * @throws ThinklabException
	 */
	public NetCDFDataset(IContext context) throws ThinklabException {
		setContext(context);
	}

	@Override
	public void setContext(IContext context) throws ThinklabException {
		
		IExtent rgrid = context.getSpace();
		if (rgrid instanceof GridExtent) {
			this.space = (GridExtent)rgrid;
		} else {
			throw new ThinklabUnsupportedOperationException(
				"only raster grid data are supported in NetCDF exporter for now");			
		}
		
		this.context = context;
	}

	@Override
	public IContext getContext() {
		return context;
	}

	@Override
	public String persist(String location) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void restore(String location) throws ThinklabException {
		// TODO Auto-generated method stub
	}

	GridExtent space         = null;
	RegularTemporalGrid time = null;
	
	Map<String,IState> auxVariables = 
	new Hashtable<String, IState>();
	IContext context = null;
	HashSet<String> varnames = new HashSet<String>();
	
	/*
	 * container for variables to write
	 */
	ArrayList<Pair<String, String>> attributes;
	
	public void write(String filename) throws ThinklabException {
		
		Dimension latDim = null;
		Dimension lonDim = null;
		Dimension timDim = null;

		HashMap<IState, double[]> dataCatalog = new HashMap<IState, double[]>();

		ArrayList<Dimension> spdims = new ArrayList<Dimension>();
		
		if (!filename.endsWith(".nc"))
			filename += ".nc";
		
		NetcdfFileWriteable ncfile = NetcdfFileWriteable.createNew(filename, false);
		
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
		
		for (IState state : context.getStates()) {
			
			// TODO implement the rest
			
			if (spdims.size() == 2) {
				// we have space only
				String varname = getVarname(state);
				if (varnames.contains(varname))
					continue;
				
				
				ncfile.addVariable(varname, DataType.FLOAT, new Dimension[]{latDim,lonDim});
				
				double[] dio = state.getDataAsDoubles();
				dataCatalog.put(state, dio);
				varnames.add(varname);

				// TODO if var is a measurement, add units attribute - this is a stupid stub
				if (varname.equals("Altitude")) {
					ncfile.addVariableAttribute("Altitude", "units", "meters");
				}
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
		
		for (IState state : context.getStates()) {
			
			if (state.getValueCount() != space.getXCells() * space.getYCells()) {
				Thinklab.get().logger().error(
						"state of " + state + " has " + state.getValueCount() + 
						" multiplicity when context expects " + (space.getXCells() * space.getYCells()) + 
						": results not stored");
				continue;
			}
			
			varnames.clear();
			
			// TODO implement the rest			
			if (spdims.size() == 2) {
				
				// we have space only
				String varname = getVarname(state);
				
				if (varnames.contains(varname))
					continue;
				
				varnames.add(varname);
				
				ArrayDouble data = new ArrayDouble.D2(latDim.getLength(), lonDim.getLength());
				Index ind = data.getIndex();
				double[] dd = dataCatalog.get(state);
				
				// this can now happen for stuff like categories, eventually it will be removed
				if (dd == null)
					continue;

				int i = 0;
				for (int lat = 0; lat < latDim.getLength(); lat++) {
					for (int lon = 0; lon < lonDim.getLength(); lon++) {
						
						Index index = ind.set(lat,lon);
						
						double val = dd[i];
						
						
						if (mask != null) {
							int[] xy = space.getXYCoordinates(i);
							if (!mask.isActive(xy[0], xy[1])) {
								val = Double.NaN;
							}
						}

						data.setFloat(index, (float)val);
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
		
		for (String varname : auxVariables.keySet()) {
			
			if (spdims.size() == 2) {
			
				ArrayDouble data = new ArrayDouble.D2(latDim.getLength(), lonDim.getLength());
				Index ind = data.getIndex();

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
	 * Extract the proper name for a state. TODO this needs a lot of work. The
	 * metadata from the model and concept should be considered.
	 * 
	 * just recognize some concepts that have special meaning for the netcdf CF convention
	 */
	private String getVarname(IState state) {
		
		IConcept obs = state.getObservable().getDirectType();
		
		String ret = obs.getLocalName() + "_" + obs.getConceptSpace();
		ret = ret.replace('.','_');
		if (obs.is("geospace.physics:Altitude")) {
			ret = "Altitude";
		}
		return ret;
	}


}
