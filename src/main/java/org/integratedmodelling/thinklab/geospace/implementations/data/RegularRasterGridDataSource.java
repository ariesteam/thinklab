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
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;
import org.integratedmodelling.thinklab.geospace.coverage.ICoverage;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;

public abstract class RegularRasterGridDataSource implements IDataSource {

	protected ICoverage _coverage = null;
	private GridExtent _originalExtent;
	private GridExtent _finalExtent = null;
	
	public RegularRasterGridDataSource() {
	}
	
	public RegularRasterGridDataSource(ICoverage coverage,
			GridExtent gridExtent) {
		this._coverage = coverage;
	}

	public Object getValue(int index) {

		try {
			
			Object ret = _coverage.getSubdivisionValue(index, _originalExtent);
			if (! (ret instanceof Number))
				return Double.NaN;
			ret = ((Number)ret).doubleValue();
			
			double[] nd = _coverage.getNodataValue();
			if (nd != null && ret != null && (ret instanceof Double) && !Double.isNaN((Double)ret)) {
				for (double d : nd) {
					if  (((Double)ret).equals(d)) {
						ret = Double.NaN;
						break;
					}
				}
			}
			return ret;
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public IAccessor getAccessor(IContext context) throws ThinklabException {
		
		if (_coverage == null) {
			_coverage = readData();
		}
		_finalExtent = getFinalExtent(context);	
		return new RasterGridAccessor();
	}

	/*
	 * -------------------------------------------------------------------------------------------
	 * read the coverage
	 * -------------------------------------------------------------------------------------------
	 */

	/**
	 * Do whatever is needed to instantiate _coverage.
	 * @throws ThinklabException
	 */
	protected abstract ICoverage readData() throws ThinklabException; 

	
	/**
	 * Return the final grid extent implied by the context. It should also validate
	 * the context, ensuring we don't want multiplicity in domains where we cannot
	 * provide it.
	 * 
	 * @param context
	 * @return
	 */
	protected abstract GridExtent getFinalExtent(IContext context) throws ThinklabException;


	/*
	 * -------------------------------------------------------------------------------------------------
	 * simple accessor
	 * -------------------------------------------------------------------------------------------------
	 */
	class RasterGridAccessor implements ISerialAccessor {

		boolean isFirst = true;
		
		@Override
		public IConcept getStateType() {
			return Thinklab.DOUBLE;
		}

		@Override
		public Object getValue(int overallContextIndex) {
			
			if (isFirst) {
				try {
					_coverage = _coverage.requireMatch(_finalExtent, false);
				} catch (ThinklabException e) {
					throw new ThinklabRuntimeException(e);
				}
				isFirst = false;
			}
			
			return RegularRasterGridDataSource.this.getValue(overallContextIndex);
		}
		
		@Override
		public String toString() {
			return "[raster grid accessor]";
		}
		
	}
		
}
