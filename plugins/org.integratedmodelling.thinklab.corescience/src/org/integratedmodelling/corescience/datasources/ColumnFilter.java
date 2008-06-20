/**
 * ColumnFilter.java
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
package org.integratedmodelling.corescience.datasources;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.value.TextValue;

/**
 * A simple filter initialized with a string literal, containing a comma-separated list of
 * column names or column numbers. We make it a TextValue to use the Text validator, which
 * enables definitions through ontologies and extended literals.
 * 
 * @author UVM Affiliate
 *
 */
public class ColumnFilter extends TextValue implements org.integratedmodelling.corescience.interfaces.IDataFilter {

	String columns[] = null;
	
	public boolean isColumnNumber(int i) {
		boolean ret = true;
		try {
			Integer.decode(columns[i]);
		} catch (NumberFormatException e) {
			ret = false;
		}
		return ret;
	}
	
	public boolean hasHeaders() {

		boolean ret = false;
		for (int i = 0; i < nColumns(); i++) {
			if ( (ret = !isColumnNumber(i)))
				break;
		}
		return ret;
	}
	
	public ColumnFilter(String s) throws ThinklabException {
		super(s);
		columns = s.split(",");
	}
	
	public int nColumns() {
		return columns.length;
	}

	public String columnName(int i) {
		return columns[i];
	}
	
	public int columnNumber(int i) {
		return Integer.decode(columns[i]);
	}
}
