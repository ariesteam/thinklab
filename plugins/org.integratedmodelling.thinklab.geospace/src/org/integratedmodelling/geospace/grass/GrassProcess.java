/**
 * GrassProcess.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of GrassWPS.
 * 
 * GrassWPS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GrassWPS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.grass;

public class GrassProcess {
	
	Process process = null;
	static String[] environment = null;
	String ID = null;
	
	static public String[] getGrassEnvironment() {
		
		if (environment == null) {
			// TODO
			// use service properties
		}
		return environment;
	}
	
	public GrassProcess(String command, String[] parameters, String[] arguments) {
		
		// generate ID
	
		// ask for environment
		
		// if an input file is given as a URL, make local copy in mapset
		
		// setup output directories
		
		// create process
	}

	public void start() {
		
	}
	
	/**
	 * Return 0 if process is terminated successfully, -1 if terminated with an 
	 * error, nonzero if still ongoing (ideally, should return the running time).
	 * @return
	 */
	public Integer getStatus() {
		return -1;
	}
	
	public void kill() {
		
	}
	
	public String getProcessID() {
		return null;
	}
	
	public String getStandardOutput() {
		return null;
	}
	
	public String getErrorOutput() {
		return null;
	}
	
	/**
	 * Return the URL of whatever output file was asked for in an output= instruction, 
	 * appropriately made available in a temporary directory.
	 * @return
	 */
	public String getOutput() {
		return null;
	}
	
	/**
	 * make sure all files generated are deleted.
	 * TODO check if we should have a destructor for this one.
	 */
	public void cleanup() {
		
	}
}
	
