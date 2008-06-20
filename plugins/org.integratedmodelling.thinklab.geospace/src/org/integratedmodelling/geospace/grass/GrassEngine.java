/**
 * GrassEngine.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Feb 26, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
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
 * @date      Feb 26, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.grass;

import java.io.File;

import org.integratedmodelling.geospace.GeospacePlugin;
import org.integratedmodelling.geospace.cmodel.RegularRasterModel;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * A Grass-enabled "virtual machine" that uses Grass to operate on Geospace objects.
 * 
 * @author Ferdinando
 *
 */
public class GrassEngine {

	/*
	 * GISBASE - root of grass installation
	 */
	File gisbase = null;
	
	/*
	 * GISDBASE - root of grass work area
	 */
	File   gisdbase = null;
	
	/*
	 * LOCATION_NAME - subdir of GISDBASE containing location files and mapsets
	 */
	String locationName = null;
	
	/*
	 * MAPSET - current mapset
	 */
	String mapset = null;
	
	/*
	 * GISRC - file path of gisrc text file containing following (mostly redundant) info:
	 * 
	 * GISDBASE: ...
	 * LOCATION_NAME: ...
	 * MAPSET: ...
	 * GRASS_GUI: text
	 */
	File gisrc = null;
	
	/*
	 * if this is true, synchronize temp directory creation
	 */
	private boolean isConcurrent = true;
	private File workDirectory = null;

	/**
	 * The grass engine is entirely configured through properties. If key properties are
	 * missing, or there is any problem with the runtime environment, no exception is
	 * generated. The status of the runtime should always be checked after creation using
	 * checkRunningState().
	 * 
	 * @throws ThinklabException
	 */
	public GrassEngine() throws ThinklabException {

		this.workDirectory  = new File(GeospacePlugin.get().getScratchPath() + "/grassdb");
		this.isConcurrent = isConcurrent;
	}
	
	/**
	 * Check that everything is OK to run. If 0 is returned, go ahead and enjoy. Otherwise,
	 * the following error codes could be returned:
	 * 
	 * 1. GRASS environment not properly configured.
	 * 2. GRASS environment configured but runtime engine not found.
	 * 3. Permission or other filesystem problems prevent continuation.
	 * 
	 * @return
	 */
	public int checkRunningState() {
		return 1;
	}
	
	/* create a gisrc file if not there, update otherwise */
	public void updateGisrc() {
		
	}
	
	/* create support location files (WIND, DEFAULT_WIND, etc) in PERMANENT mapset */
	public void checkLocation() {
		
	}
	
	/**
	 * Highest-level rasterization function.
	 * 
	 * @param coverage
	 * @return
	 */
	RasterCoverage rasterize(VectorCoverage coverage, RegularRasterModel rasterModel) {
		return null;
	}

	RasterCoverage vectorize(VectorCoverage coverage) {
		return null;
	}
	
}
