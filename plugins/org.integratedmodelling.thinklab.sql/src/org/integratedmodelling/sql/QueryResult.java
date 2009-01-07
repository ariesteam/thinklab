/**
 * QueryResult.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabSQLPlugin.
 * 
 * ThinklabSQLPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabSQLPlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.BooleanValue;

/**
 * Copies and holds the results of an SQL query for as long as you need. Returned on 
 * request by SQLServer.query(). It's obviously expensive to use this on large queries,
 * but also a lot more convenient, elegant and synchronizable than going through the 
 * mess of catch clauses and allocations every time you need a query - particularly if
 * you need nested ones. It also provides a quick and easy bridge to Thinklab IValue
 * validation of literals.
 * 
 * Use your own judgment.
 *  
 * @author Ferdinando Villa
 *
 */
public class QueryResult extends ArrayList<ArrayList<String>> {

	private static final long serialVersionUID = 5541226844896674514L;

	protected QueryResult(ResultSet rset) throws ThinklabStorageException {
		
		if (rset != null) {
			int nc;
			try {
				nc = rset.getMetaData().getColumnCount();
		
				if (rset.first()) {

					for (; !rset.isAfterLast(); rset.next()) {	
						ArrayList<String> row = new ArrayList<String>();
				
						for (int i = 1; i <= nc; i++)
							row.add(rset.getString(i));
				
						add(row);
					}
				}
			} catch (SQLException e) {
				throw new ThinklabStorageException(e);
			}
		}
	}
	
	public int nRows() {
		return size();
	}
	
	public int nColumns() {
		return size() == 0 ? 0 : get(0).size();
	}
	
	public String get(int row, int column) {
		return get(row).get(column);
	}
	
	public String getString(int row, int column) {
		return get(row).get(column);
	}

	public int getInt(int row, int column) {
		return Integer.valueOf(get(row,column));
	}

	public long getLong(int row, int column) {
		return Long.valueOf(get(row,column));
	}
	
	public float getFloat(int row, int column) {
		return Float.valueOf(get(row,column));
	}
	
	public double getDouble(int row, int column) {
		return Double.valueOf(get(row,column));
	}
	
	public boolean getBoolean(int row, int column) {
		return BooleanValue.parseBoolean(get(row,column));
	}
	
	public IValue getValue(int row, int column, IConcept concept) throws ThinklabException {
		return KnowledgeManager.get().validateLiteral(concept, get(row,column), null);
	}

	public IValue getValue(int row, int column, String concept) throws ThinklabException {
		return KnowledgeManager.get().validateLiteral(
				KnowledgeManager.get().requireConcept(concept), get(row,column), null);
	}

}
