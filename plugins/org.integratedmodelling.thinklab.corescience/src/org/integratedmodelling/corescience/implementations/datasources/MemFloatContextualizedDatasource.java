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

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.DatasourceStateAdapter;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.utils.Polylist;

public class MemFloatContextualizedDatasource  extends DefaultAbstractState
 	implements IState, IInstanceImplementation {

	private static final long serialVersionUID = -6567783706189229920L;
	private float[] data = null;

	public MemFloatContextualizedDatasource(IConcept type, int size, IContext context) {
		_type = type;
		data = new float[size];
		metadata.put(Metadata.CONTINUOUS, Boolean.TRUE);
		this.context = context;
	}

	@Override
	public Object getValue(int offset) {
		return (offset >= 0 && offset < data.length) ? data[offset] : null;
	}
	
	@Override
	public IConcept getValueType() {
		return _type;
	}


	@Override
	public void setValue(int idx, Object o) {
		float d = o == null ? Float.NaN : ((Float)o);
		data[idx] = d;
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {

		return Polylist.list(
				CoreScience.CONTEXTUALIZED_DATASOURCE,
				Polylist.list("@", new DatasourceStateAdapter(this)));
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
	}

	@Override
	public void validate(IInstance i) throws ThinklabException {
	}
	
	public String toString() {
		return "MF[" + _type + ": " /*+ Arrays.toString(data)*/ + "]";
	}

	@Override
	public Object getRawData() {
		return data;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		// TODO try to convert if values are numbers
		throw new ThinklabValueConversionException("can't convert float into double");
	}
	
	@Override
	public int getValueCount() {
		return data.length;
	}

	@Override
	public double getDoubleValue(int index)
			throws ThinklabValueConversionException {
		return data[index];
	}
	

	@Override
	public boolean isProbabilistic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isContinuous() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isNumeric() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCategorical() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBoolean() {
		// TODO Auto-generated method stub
		return false;
	}


}