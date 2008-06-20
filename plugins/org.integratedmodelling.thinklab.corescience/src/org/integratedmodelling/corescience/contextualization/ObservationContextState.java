/**
 * ObservationContextState.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.corescience.contextualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.integratedmodelling.corescience.interfaces.IObservationContextState;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.Pair;

/**
 * Bare implementation based on hash map. IValues for dependencies can be added
 * to the container by activation records or mediators. 
 * 
 * In the interest of efficiency, no checks are done to ensure that indexes are 
 * initialized before use or values returned are actually there, so it must be used 
 * correctly and consistently. 
 * 
 * @author Ferdinando Villa
 *
 */
public class ObservationContextState extends HashMap<String, Pair<IValue, IUncertainty>> implements IObservationContextState{

	private static final long serialVersionUID = -1484076872514765528L;
	
	ArrayList<String> idx = new ArrayList<String>();
	int[] indexes = null;
	boolean[] changed = null;
	long linearIndex;
	
	public void defineDimension(IConcept c) {
		idx.add(c.toString());
	}
	
	public void set(String id, IValue value) {
		super.put(id, new Pair<IValue, IUncertainty>(value, null));
	}

	public void set(String id, IValue value, IUncertainty uncertainty) {
		super.put(id, new Pair<IValue, IUncertainty>(value, uncertainty));
	}
	
	public void setIndexes(int[] indexes, boolean[] changed, long l) {
		this.indexes = indexes;
		this.changed = changed;
		this.linearIndex = l;
	}
	
	public int getIndex(int dimensionIndex) {
		// TODO Auto-generated method stub
		return indexes[dimensionIndex];
	}

	public int getIndex(String dimensionID) {
		// TODO Auto-generated method stub
		return indexes[idx.indexOf(dimensionID)];
	}

	public IValue getValue(int dimensionIndex) {
		// TODO Auto-generated method stub
		return super.get(idx.get(dimensionIndex)).getFirst();
	}

	public IValue getValue(String dimensionID) {
		return super.get(dimensionID).getFirst();
	}

	public String toString() {
		
		String ret = "[";
		
		if (indexes != null) {
			for (int i = 0; i < indexes.length; i++)
				ret += (i == 0 ? "" : ",") + indexes[i];
		}
		
		if (size() > 0) {		
			
			if (ret.length() > 1)
				ret += " ";
			
			Iterator printMap = entrySet().iterator();
			while (printMap.hasNext()) {
				Map.Entry cellId = (Map.Entry)printMap.next();
				ret += "(" + cellId.getKey() + " = " + cellId.getValue() + ")";
			}
		}
		
		return ret + "]";
	}

	public int[] getIndexes() {
		return indexes;
	}

	public IUncertainty getUncertainty(String dimensionID) {
		return super.get(dimensionID).getSecond();
	}

	// return whether the given extent has changed state
	public boolean hasChanged(int i) {
		return changed[i];
	}

	public long getLinearIndex() {
		return linearIndex;
	}
	
}
