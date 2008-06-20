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
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IExtentConceptualModel;
import org.integratedmodelling.geospace.cmodel.SpatialConceptualModel;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public abstract class ArealExtent implements IExtent {

	SpatialConceptualModel cm = null;
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
	
	public IExtentConceptualModel getConceptualModel() {
		return cm;
	}

	public ReferencedEnvelope getEnvelope() {
		return envelope;
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
