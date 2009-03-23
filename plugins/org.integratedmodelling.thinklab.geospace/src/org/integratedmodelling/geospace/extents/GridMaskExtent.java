package org.integratedmodelling.geospace.extents;

import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.geospace.coverage.RasterActivationLayer;
import org.integratedmodelling.geospace.literals.CoverageValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * An extent that enumerates different possible states of an observable distributed over a grid.
 * The total granularity is the number of different states. Each granule is a spatially distributed
 * mask that only selects one of the possible values. The granule has a numerosity and retains a 
 * connection to the mask but retains no spatial information other than the amount of cells and the
 * x/y dimensions, but there is no requirement of contiguity.
 * 
 * @author Ferdinando
 *
 */
public class GridMaskExtent implements IExtent {

	private int[] mask;
	private int xDivs;
	private int yDivs;
	private int nClasses;
	private int[] classValues;
	private RasterActivationLayer activationLayer;
	private ExtentConceptualModel cm;

	public GridMaskExtent(ExtentConceptualModel cm, int[] maskLayer, int xDivs, int yDivs, int nClasses, int[] classValues) {
		this.mask = maskLayer;
		this.xDivs = xDivs;
		this.yDivs = yDivs;
		this.nClasses = nClasses;
		this.classValues = classValues;
		this.cm = cm;
	}
	
	/**
	 * Set activation layer optionally. It will cause the inactive cells to not count in the
	 * total. 
	 * 
	 * @param aLayer
	 */
	public void setActivationLayer(RasterActivationLayer aLayer) {

		this.activationLayer = aLayer;
		/*
		 * mask the mask with -1 values if the activation layer has at least
		 * one clear bit
		 * TODO should use nextClearBit in the cycle to be faster. 
		 */
		if (!(aLayer.nextClearBit(0) < 0))
			for (int i = 0; i < mask.length; i++) {
				if (!aLayer.get(i))
					mask[i] = -1;
			}	
	}
	
	@Override
	public ExtentConceptualModel getConceptualModel() {
		return cm;
	}

	@Override
	public IValue getFullExtentValue() {

		return new CoverageValue(mask, xDivs, yDivs, null, 
				(activationLayer == null ? xDivs*yDivs : activationLayer.totalActiveCells()));
	}

	@Override
	public IValue getState(int granule) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTotalGranularity() {
		// TODO Auto-generated method stub
		return nClasses;
	}

	@Override
	public IExtent getExtent(int granule) {
		// TODO Auto-generated method stub
		return null;
	}

}
