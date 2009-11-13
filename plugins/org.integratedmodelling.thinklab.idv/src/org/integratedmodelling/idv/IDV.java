package org.integratedmodelling.idv;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;

import ucar.unidata.idv.DefaultIdv;

/**
 * Interfaces to IDV functionalities
 * 
 * @author Ferdinando
 *
 */
public class IDV {

	/**
	 * Just run it
	 * @throws ThinklabIOException
	 */
	public static void run(String[] args) throws ThinklabIOException {
		try {
			DefaultIdv.main(args);
		} catch (Exception e) {
			throw new ThinklabIOException("error launching IDV");
		}	
	}
	
	/**
	 * Visualize given datasource
	 * @param datasource
	 * @throws ThinklabIOException
	 */
	public static void visualize(String datasource) throws ThinklabIOException {
		
		/*
		 * trivial for now - should keep the instance and load incrementally
		 */
		String[] args = {DefaultIdv.ARG_DATA, datasource};
		
		try {
			DefaultIdv.main(args);
		} catch (Exception e) {
			throw new ThinklabIOException("error launching IDV on " + datasource);
		}
	}
	
}
