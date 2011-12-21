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
package org.integratedmodelling.dynamicmodelling.model;

import java.util.ArrayList;
import java.util.Collection;

import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;

public class Stock extends SimpleDirectedSparseVertex {
	private String name;
	private String state;
	private String units;
	private String minVal;
	private String maxVal;
	private String comment;
	
	public Stock(String name, String state, String units) {
		this.name = name;
		this.state = state;
		this.units = units;
	}

	public String getName() {
		return this.name;
	}

	public String getState() {
		return this.state;
	}

	public String getUnits() {
		return this.units;
	}

	public String getMinVal() {
		return this.minVal;
	}

	public String getMaxVal() {
		return this.maxVal;
	}

	public String getComment() {
		return this.comment;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public void setMinVal(String minVal) {
		this.minVal = minVal;
	}

	public void setMaxVal(String maxVal) {
		this.maxVal = maxVal;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Collection<Flow> getInflows() {
		
		ArrayList<Flow> ret = new ArrayList<Flow>();
		
		for (Object edge : getInEdges()) {
			if (edge instanceof FlowEdge) {
				ret.add((Flow)((FlowEdge)edge).getSource());
			}
		}
		return ret;
	}
	
	public Collection<Flow> getOutflows() {
		
		ArrayList<Flow> ret = new ArrayList<Flow>();
		
		for (Object edge : getOutEdges()) {
			if (edge instanceof FlowEdge) {
				ret.add((Flow)((FlowEdge)edge).getDest());
			}
		}
		return ret;
	}
}