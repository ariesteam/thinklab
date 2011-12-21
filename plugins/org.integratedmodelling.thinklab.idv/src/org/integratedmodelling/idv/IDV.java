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
