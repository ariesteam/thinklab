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
