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
package org.integratedmodelling.modelling.agents;

import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.geospace.literals.ShapeValue;

public class ThinkGeolocatedAgent extends ThinkAgent {

	private static final long serialVersionUID = -9031768765798228639L;
	private ShapeValue shape;
	private RasterGrid grid;
	
	public ThinkGeolocatedAgent() {
	}
	
	@Override
	public Object clone() {
		ThinkGeolocatedAgent ret = new ThinkGeolocatedAgent();
		ret.copy(this);
		return ret;
	}
	
	public ThinkGeolocatedAgent(ShapeValue sh) {
		setShape(sh);
	}

	public void setShape(ShapeValue shape) {
		this.shape = shape;
	}

	public void setGrid(RasterGrid grid) {
		this.grid = grid;
	}

	public ShapeValue getCentroid() {
		return shape.getCentroid();
	}


}
