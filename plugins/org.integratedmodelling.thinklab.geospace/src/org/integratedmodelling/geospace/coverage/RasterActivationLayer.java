package org.integratedmodelling.geospace.coverage;

import java.util.BitSet;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.multidimensional.MultidimensionalCursor;

/**
 * A support class that is coupled with a raster layer and tells us whether the
 * pixel at x,y belongs to the raster shape. Basically a mask, used by the raster 
 * conceptual model and by the raster path to determine the order of iteration. 
 * @author Ferdinando Villa
 *
 */
public class RasterActivationLayer extends BitSet {

	private static final long serialVersionUID = 2831346054544907423L;
	private int active;
	MultidimensionalCursor cursor = new MultidimensionalCursor(MultidimensionalCursor.StorageOrdering.C);

	// nothing for now
	Object gaps = null;
	
	public void intersect(RasterActivationLayer other) throws ThinklabValidationException {
		this.and(other);
		active = this.cardinality();
	}

	public void or(RasterActivationLayer other) throws ThinklabValidationException {
		this.or(other);
		active = this.cardinality();
	}
	
	public Pair<Integer, Integer> getCell(int index) {
		
		int x = index / cursor.getDimensionSize(0);
		int y = index - (x * cursor.getDimensionSize(0));
		
		return new Pair<Integer, Integer>(x, y);
	}
	
	public boolean isActive(int x, int y) {
		return get(cursor.getElementOffset(x,y));
	}
	
	public void activate(int x, int y) {
		if (!isActive(x,y))
			active++;
		set(x,y,true);
	}
	
	public void deactivate(int x, int y) {
		if (isActive(x,y))
			active--;
		set(x,y,false);
	}
	
	public RasterActivationLayer(int x, int y) {
		
		super(x*y);
		cursor.defineDimensions(x,y);
		active = cursor.getMultiplicity();
		
		// set all bits to true
		and(this);
	}

	public RasterActivationLayer(int x, int y, boolean isActive) {
		
		super(x*y);
		cursor.defineDimensions(x,y);
		active = 0;
		
		// set all bits to true
		if (isActive) {
			and(this);
			active = cursor.getMultiplicity();
		} else {
			xor(this);
		}
	}

	public int totalActiveCells() {
		return active;
	}
	
	public int nextActiveOffset(int fromOffset) {
		return nextSetBit(fromOffset);
	}
	
	public Pair<Integer, Integer> nextActiveCell(int fromX, int fromY) {
		
		int ofs = nextSetBit(cursor.getElementOffset(fromX,fromY));
		
		if (ofs == -1) 
			return null;
		
		int x = ofs / cursor.getDimensionSize(0);
		int y = ofs - (x * cursor.getDimensionSize(0));
		
		return new Pair<Integer, Integer>(x, y);
	}

	public Pair<Integer, Integer> nextActiveCell(int fromOffset) {
		
		int ofs = nextSetBit(fromOffset);
		
		if (ofs == -1) 
			return null;
		
		int x = ofs / cursor.getDimensionSize(0);
		int y = ofs - (x * cursor.getDimensionSize(0));
		
		return new Pair<Integer, Integer>(x, y);
	}

}
