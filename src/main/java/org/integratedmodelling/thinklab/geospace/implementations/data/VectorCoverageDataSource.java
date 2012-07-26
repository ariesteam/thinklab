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
package org.integratedmodelling.thinklab.geospace.implementations.data;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabUnsupportedOperationException;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.geospace.coverage.ICoverage;
import org.integratedmodelling.thinklab.geospace.extents.ArealExtent;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;

/**
 * @author Ferdinando
 */
public abstract class VectorCoverageDataSource implements IDataSource {

	protected ICoverage coverage = null;
	
	/**
	 * This must ensure that coverage contains a valid vector coverage.
	 * 
	 * @throws ThinklabException
	 */
	protected abstract void initialize() throws ThinklabException;
	
//	public Object getValue(int index, Object[] parameters) {
//		
//		Object ret = null;
//		try {
//			ret = coverage.getSubdivisionValue(
//					index, 
//					gridExtent == null ? shapeExtent : gridExtent);
//			
//			if (this.transformation != null && ret != null && !(ret instanceof Double && Double.isNaN((Double)ret)) ) {
//				HashMap<String, Object> parms = new HashMap<String, Object>();
//				parms.put("self", ret);
//				ret = this.transformation.eval(parms);
//			}
//			
//			/*
//			 * TODO if there's any table linked, use the result as
//			 * a key into it.
//			 */
//			
//		} catch (ThinklabException e) {
//			throw new ThinklabRuntimeException(e);
//		}
//		return ret;
//	}

	@Override
	public IAccessor getAccessor(IContext context) throws ThinklabException {

		initialize();
		
		if (context.getSpace() instanceof GridExtent) {
			ICoverage cov = coverage.requireMatch((ArealExtent) context.getSpace(), true);
			RegularRasterGridDataSource ds = new RegularRasterGridDataSource(cov, (GridExtent) context.getSpace()) {
				
				@Override
				protected ICoverage readData() throws ThinklabException {
					return _coverage;
				}
				
				@Override
				protected GridExtent getFinalExtent(IContext context)
						throws ThinklabException {
					return _finalExtent;
				}
			};
			
			return ds.getAccessor(context);
			
		} else {
			
			/*
			 * TODO accessor for vectors - may need to do the monster conversion if
			 * the context's shapes are different
			 */
			throw new ThinklabUnsupportedOperationException("vector accessors not there yet, please be patient");
		}
	}

}
