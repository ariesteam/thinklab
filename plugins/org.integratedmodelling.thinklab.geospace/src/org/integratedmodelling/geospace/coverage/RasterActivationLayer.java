package org.integratedmodelling.geospace.coverage;

import java.util.BitSet;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.multidimensional.MultidimensionalCursor;

/**
 * A support class that is coupled with a raster layer and tells us whether the
 * pixel at x,y belongs to the raster shape. Used by the raster conceptual model and
 * by the raster path to determine the order of iteration. 
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
		
	}

	public Pair<Integer, Integer> getCell(int index) {
		
		int x = 0;
		int y = 0;
		
		if (gaps == null) {	
			
			y = index / cursor.getDimensionSize(0);
			x = index - (y * cursor.getDimensionSize(0));
			
		} else {
			// TODO follow the gaps descriptor for n steps
		}
		
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
	
}
