package org.integratedmodelling.geospace.interfaces;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.geospace.extents.GridExtent;

public interface IGridMask {

	public abstract void intersect(IGridMask other)
			throws ThinklabValidationException;

	public abstract void or(IGridMask other)
			throws ThinklabValidationException;

	public abstract Pair<Integer, Integer> getCell(int index);

	public abstract boolean isActive(int linearIndex);
	
	public abstract boolean isActive(int x, int y);

	public abstract void activate(int x, int y);

	public abstract void deactivate(int x, int y);

	public abstract int totalActiveCells();

	public abstract int nextActiveOffset(int fromOffset);

	public abstract int[] nextActiveCell(int fromX, int fromY);

	public abstract Pair<Integer, Integer> nextActiveCell(int fromOffset);

	public abstract GridExtent getGrid();
	
}