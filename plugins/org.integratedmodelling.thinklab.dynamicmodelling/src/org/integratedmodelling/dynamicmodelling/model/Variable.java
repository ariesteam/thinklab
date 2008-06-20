/**
 * Variable.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDynamicModellingPlugin.
 * 
 * ThinklabDynamicModellingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDynamicModellingPlugin is distributed in the hope that it will be useful,
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
 * @author    Gary W. Johnson, Jr.
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.dynamicmodelling.model;

import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;

public class Variable extends SimpleDirectedSparseVertex {
	private String name;
	private String value;
	private String units;
	private String minVal;
	private String maxVal;
	private String comment;

	public Variable(String name, String value, String units, String minVal, String maxVal, String comment) {
		this.name = name;
		this.value = value;
		this.units = units;
		this.minVal = minVal;
		this.maxVal = maxVal;
		this.comment = comment;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
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

	public void setValue(String value) {
		this.value = value;
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
}