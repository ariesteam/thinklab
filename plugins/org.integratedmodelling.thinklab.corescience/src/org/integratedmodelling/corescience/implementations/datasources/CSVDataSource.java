/**
 * CSVDataSource.java
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
package org.integratedmodelling.corescience.implementations.datasources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.exceptions.ThinklabInconsistentDataSourceException;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.ExcelCSVParser;
import com.Ostermiller.util.LabeledCSVParser;

/**
 * A DataSource that interfaces to a comma-delimited ascii data stream. The 
 * column[s] indicates as the URL fragment should represent one observation. If
 * the URL fragment is a number, assume we don't have any column headers and
 * interpret as column number (1-based). If not, assume we have headers and
 * read in column with that header.
 * 
 * Handles "normal" and Excel CSV streams from any URL. Understands column
 * filters (one or more columns) where column numbers and headings can be 
 * mixed if desired (the presence of at least one header means that all columns
 * have a header). Transformation filters are applied in sequence when the 
 * data are retrieved.
 * 
 * TODO this should be made abstract and contain all methods to retrieve mappings and
 * transformations. Concrete implementations should define CSVTable (with named columns containing
 * stuff), CSVMatrix2D, CSVMatrix3D and anything else appropriate.
 * 
 * @author Ferdinando Villa
 * @since October, 2006
 *
 */
public class CSVDataSource implements IDataSource<Object>, IInstanceImplementation {

	String url = null;
	boolean isExcel = false;
	ArrayList<ColumnFilter> filters = new ArrayList<ColumnFilter>();
	ArrayList<String> data = new ArrayList<String>();
	int nrows = -1;
	int ncols = -1;
		
	HashMap<String,Object> metadata = new HashMap<String,Object>();

	public void initialize(IInstance i) throws ThinklabException {
		
		// FIXME use class tree
		isExcel = i.is(KnowledgeManager.get().requireConcept(CoreScience.EXCEL_CSV_DATASOURCE));
		
		/* retrieve URI from instance */
		url = i.get(CoreScience.HAS_SOURCE_URI).toString();
		
		System.out.println("URL: " + url);
		
		/* see if there is a filter associated. We only understand literal column ids. */
		Collection<IRelationship> frels = i.getRelationships(CoreScience.HAS_FILTER);
		
		for (IRelationship r : frels) {

			Object theFilter = null;
			
			/* can be the literal if literal, or the implementation if object. Check. */
			if (r.isLiteral()) 
				theFilter = r.getValue();
			else if (r.isObject()) {
				theFilter = r.getValue().asObjectReference().getObject().getImplementation();
			}
			
			if (theFilter != null)
				filters.add((ColumnFilter) theFilter);
			
		}
		
		/* call datasource initializer */
		initialize(url, filters);
	}


	public void initialize(String url, Collection<ColumnFilter> filters) throws ThinklabInconsistentDataSourceException, ThinklabIOException {
		
		ColumnFilter cfilter = null;
		
		/*
		 * Start with the column filter, if any. We silently ignore multiple ones.
		 */
		for (ColumnFilter f : filters) {
			if (f instanceof ColumnFilter) {
				cfilter = (ColumnFilter) f;
				break;
			}
		}
	
		
		InputStream input = null;
		
		/*
		 * Open url, obtain reader
		 */
		try {

			URL source = new URL(url);
			input = source.openStream();
		
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		
		CSVParse parser = isExcel ? new ExcelCSVParser(input) : new CSVParser(input);
		
		/*
		 * read headers only if we have a column filter that mentions headers
		 */
		boolean readHeaders = (cfilter != null && cfilter.hasHeaders());
		
		try {
			if (readHeaders) {
				parser = new LabeledCSVParser(parser);
			}
			
			ncols = 0;
			nrows = 0;
			
			/* read all data in row order. TODO check if this is feasible or we need more
			 * input structure analysis in order to match contexts. My guess is that we are
			 * mostly reading one observation, i.e. oe ncolumn or number, so problems will
			 * show up only with more sophisticated usage (which will have more sophisticated
			 * semantics). 
			 */
			String[] row = null;
			
			while ((row = parser.getLine()) != null) {
			
				nrows ++;
				
				/* get string values */
				if (cfilter != null) {
					for (int i = 0; i < cfilter.nColumns(); i++) {
						if (cfilter.isColumnNumber(i)) {
							data.add(row[cfilter.columnNumber(i) - 1]);
						} else {
							data.add(((LabeledCSVParser)parser).getValueByLabel(cfilter.columnName(i)));
						}
					}
				} else {
					for (int i = 0; i < row.length; i++) {
						data.add(row[i]);
					}
				}
				
				if (ncols < 0) {
					ncols = cfilter == null ? row.length : cfilter.nColumns();
				}
			}
			
			parser.close();
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		
	}

	// FIXME merge with the real one below
	public Object getValue(int idx, IConcept concept) throws ThinklabException {

		IValue ret = KnowledgeManager.get().validateLiteral(concept,data.get(idx));
		
		/*
		 * If any filter is a transformation, we apply them all to the value in the
		 * sequence they come in. 
		 * 
		 * FIXME this far the sequence comes from RDF, which means there is no 
		 * reliable sequence unless we use messy RDF lists. Which at this time we
		 * don't. So cascading transformations is unreliable.
		 */
		for (ColumnFilter f : filters) {
			ret = f.transform(ret);
		}
		
		return ret;
	}


	public IConcept getValueType() {
		// FIXME this should return a configured type
		return KnowledgeManager.LiteralValue();
	}

	@Override
	public Object getValue(int index, Object[] parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getInitialValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataSource<?> transform(IDatasourceTransformation transformation)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public void validate(IInstance i) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void postProcess(IObservationContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void preProcess(IObservationContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<String, Object> getMetadata() {
		return metadata;
	}
}
