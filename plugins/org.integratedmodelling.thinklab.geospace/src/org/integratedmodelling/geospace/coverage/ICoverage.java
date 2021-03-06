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
package org.integratedmodelling.geospace.coverage;

import java.io.File;

import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public interface ICoverage {

	/**
	 * Pop up a window or something. Only used for debugging, this is middleware, man. 
	 */
	public abstract void show();
	
	/*
	 * Write an image of the coverage to the given file in the given format.
	 */
	public abstract void writeImage(File outfile, String format) throws ThinklabIOException;

	/**
	 * Return the value associated with the n-th subdivision according to the SubdivisionOrdering associated with
	 * the coverage. The value should be what the passed conceptual model wants.
	 * 
	 * @param subdivisionOrder
	 * @return 
	 * @throws ThinklabValidationException 
	 * @throws ThinklabException 
	 */
	public abstract Object getSubdivisionValue(int subdivisionOrder, ArealExtent extent) throws ThinklabValidationException;
	
	public abstract String getSourceUrl();

	public abstract String getCoordinateReferenceSystemCode() throws ThinklabException;

	public abstract CoordinateReferenceSystem getCoordinateReferenceSystem();
	
	public abstract double getLonUpperBound();

	public abstract double getLonLowerBound();

	public abstract double getLatUpperBound();

	public abstract double getLatLowerBound();

	public abstract String getLayerName();
	
	public BoundingBox getBoundingBox();
	
	/**
	 * Return a coverage that matches the passed raster extent, warping if necessary. Return self if
	 * the match is there, a new one otherwise. If the coverage cannot be adjusted, throw an exception.
	 * 
	 * @param arealExtent the extent that this coverage needs to cover.
	 * @param allowClassChange if true, we can generate a coverage of a different class (e.g. a raster
	 *        instead of a vector); otherwise we must stay our type, and throw an exception if impossible.
	 * @return a new coverage or self. It should never return null.
	 */
	public abstract ICoverage requireMatch(ArealExtent arealExtent, boolean allowClassChange)
		throws ThinklabException;
	
	/**
	 * Write the coverage to a suitable GIS format, determined by the extension in the
	 * requested file name.
	 * 
	 * @param f file to write to.
	 * @throws ThinklabException
	 */
	public void write(File f) throws ThinklabException;
	
	/**
	 * No need to load any files until this is called.
	 * @throws ThinklabException 
	 */
	void loadData() throws ThinklabException;

	/**
	 * This should return null if a nodata value is not specified or makes no sense, and a valid
	 * double if specified (either by the original source or by a datasource).
	 * @return
	 */
	public double[] getNodataValue();

	/**
	 * 
	 */
	public abstract void setName(String covId);

}