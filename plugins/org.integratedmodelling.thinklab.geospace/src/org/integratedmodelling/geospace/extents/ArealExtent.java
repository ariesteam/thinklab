/**
 * ArealExtent.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.extents;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.geospace.implementations.cmodels.SpatialConceptualModel;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;

import com.vividsolutions.jts.geom.Envelope;

public abstract class ArealExtent implements IExtent {

	SpatialConceptualModel cm = null;
	// the envelope in here is always east-west on the X axis. getDefaultEnvelope() can be used to retrieve
	// the envelope that will work with the CRS. 
	ReferencedEnvelope envelope = null;
	private CoordinateReferenceSystem crs;
	
	public ArealExtent(SpatialConceptualModel cm, CoordinateReferenceSystem crs, Envelope envelope) {
		this.cm = cm;
		this.crs = crs;
		this.envelope = new ReferencedEnvelope(envelope, crs);
	}
	
	public CoordinateReferenceSystem getCRS() {
		return crs;
	}
	
	public ExtentConceptualModel getConceptualModel() {
		return cm;
	}

	/**
	 * Get an envelope where the X axis is always east-west.
	 * @return
	 */
	public ReferencedEnvelope getNormalizedEnvelope() {
		return envelope;
	}
	
	/**
	 * Get the envelope with the axis order decided by the CRS.
	 * @return
	 */
	public ReferencedEnvelope getDefaultEnvelope() {
		
		ReferencedEnvelope ret = envelope;
		
		if (crs != null && crs.getCoordinateSystem().getAxis(0).getDirection().equals(AxisDirection.NORTH)) {
			/*
			 * swap x/y to obtain the right envelope according to the CRS
			 */
			ret = new ReferencedEnvelope(
					envelope.getMinY(), envelope.getMaxY(),
					envelope.getMinX(), envelope.getMaxX(), crs);
		} 
		
		return ret;
	}

	public double getNorth() {
		return envelope.getMaxY();
	}

	public double getWest() {
		return envelope.getMinX();
	}

	public double getSouth() {
		return envelope.getMinY();
	}

	public double getEast() {
		return envelope.getMaxX();
	}
	
	public double getEWExtent() {
		return envelope.getWidth();
	}

	public double getNSExtent() {
		return envelope.getHeight();
	}

}
