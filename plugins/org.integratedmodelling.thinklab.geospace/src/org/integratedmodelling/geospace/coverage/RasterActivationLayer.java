package org.integratedmodelling.geospace.coverage;

import java.util.BitSet;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.interfaces.IGridMask;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A support class that is coupled with a raster layer and tells us whether the
 * pixel at x,y belongs to the raster shape. Basically a mask, used by the raster 
 * conceptual model and by the raster path to determine the order of iteration. 
 * 
 * Uses X,Y indexing - not row, column.
 * 
 * @author Ferdinando Villa
 *
 */
public class RasterActivationLayer extends BitSet implements IGridMask {

	private static final long serialVersionUID = 2831346054544907423L;
	private int active;

	// nothing for now
	Object gaps = null;
	private CoordinateReferenceSystem crs;
	private GridExtent grid;
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.IGridMask#intersect(org.integratedmodelling.geospace.coverage.RasterActivationLayer)
	 */
	public void intersect(IGridMask other) throws ThinklabValidationException {
		this.and((RasterActivationLayer)other);
		active = this.cardinality();
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.IGridMask#or(org.integratedmodelling.geospace.coverage.IGridMask)
	 */
	public void or(IGridMask other) throws ThinklabValidationException {
		this.or(other);
		active = this.cardinality();
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.IGridMask#getCell(int)
	 */
	public Pair<Integer, Integer> getCell(int index) {
		
		int[] xy = grid.getXYCoordinates(index);
		return new Pair<Integer, Integer>(xy[0], xy[1]);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.IGridMask#isActive(int, int)
	 */
	public boolean isActive(int x, int y) {
		return get(grid.getIndex(x,y));
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.IGridMask#activate(int, int)
	 */
	public void activate(int x, int y) {
		if (!isActive(x,y))
			active++;
		set(grid.getIndex(x,y),true);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.IGridMask#deactivate(int, int)
	 */
	public void deactivate(int x, int y) {
		if (isActive(x,y))
			active--;
		set(grid.getIndex(x,y),false);
	}
	
	public RasterActivationLayer(int x, int y, GridExtent grid) {
		super(x*y);
		active = grid.getValueCount();
		this.grid = grid;
		// set all bits to true
		and(this);
	}

	public RasterActivationLayer(int x, int y, boolean isActive, GridExtent grid) {
		
		super(x*y);
		active = 0;
		this.grid = grid;
		
		// set all bits to true
		if (isActive) {
			and(this);
			active = grid.getValueCount();
		} else {
			xor(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.IGridMask#totalActiveCells()
	 */
	public int totalActiveCells() {
		return active;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.IGridMask#nextActiveOffset(int)
	 */
	public int nextActiveOffset(int fromOffset) {
		return nextSetBit(fromOffset);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.IGridMask#nextActiveCell(int, int)
	 */
	public int[] nextActiveCell(int fromX, int fromY) {
		
		int ofs = nextSetBit(grid.getIndex(fromX,fromY));
		
		if (ofs == -1) 
			return null;
		
		return grid.getXYCoordinates(ofs);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.IGridMask#nextActiveCell(int)
	 */
	public Pair<Integer, Integer> nextActiveCell(int fromOffset) {
		
		int ofs = nextSetBit(fromOffset);
		
		if (ofs == -1) 
			return null;
		
		int[] xy = grid.getXYCoordinates(ofs);
		return new Pair<Integer, Integer>(xy[0], xy[1]);
	}
	
	public void setCRS(CoordinateReferenceSystem crs) {
		this.crs = crs;
	}
	
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return this.crs;
	}

	@Override
	public GridExtent getGrid() {
		return this.grid;
	}

	@Override
	public boolean isActive(int linearIndex) {
		int[] xy = grid.getXYCoordinates(linearIndex);
		return isActive(xy[0], xy[1]);
	}
}
