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
package org.integratedmodelling.thinklab.geospace.interfaces;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;

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

	public abstract void invert();
	
}