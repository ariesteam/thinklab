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
package org.integratedmodelling.corescience.implementations.datasources;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.TextValue;

/**
 * A simple filter initialized with a string literal, containing a comma-separated list of
 * column names or column numbers. We make it a TextValue to use the Text validator, which
 * enables definitions through ontologies and extended literals.
 * 
 * @author UVM Affiliate
 *
 */
public class ColumnFilter extends TextValue {

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

	public IValue transform(IValue ret) {
		// TODO Auto-generated method stub
		return null;
	}
}
