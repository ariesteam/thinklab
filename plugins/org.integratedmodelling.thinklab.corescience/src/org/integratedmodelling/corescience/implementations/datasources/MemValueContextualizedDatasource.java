/**
 * DefaultState.java
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

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.DatasourceStateAdapter;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.Value;
import org.integratedmodelling.utils.Polylist;

public class MemValueContextualizedDatasource 
 	implements IState, IInstanceImplementation {

	private static final long serialVersionUID = -6567783706189229920L;
	private IConcept _type;
	private IValue[] data = null;
	Metadata metadata = new Metadata();
	private ObservationContext context;
	private IValue prototype = null;

	public MemValueContextualizedDatasource(IConcept type, int size, ObservationContext context) {
		_type = type;
		data = new IValue[size];
		this.context = context;
	}
	
//	@Override
//	public Object getInitialValue() {
//		return null;
//	}
//
//	@Override
//	public Object getValue(int index, Object[] parameters) {
//		return data[index];
//	}

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
		try {
			data[idx] = Value.getValueForObject(o);
			if (prototype == null && data[idx] != null)
				prototype = data[idx];			
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
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
		return "MV[" + _type + ": " /*+ Arrays.toString(data)*/ + "]";
	}
	
	@Override
	public Object getRawData() {
		return data;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		
		if (prototype == null)
			return null;
		
		if (!prototype.isNumber())
			throw new ThinklabValueConversionException("can't convert IValue into double");
		
		double[] ret = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			ret[i] = data[i].asNumber().asDouble();
		}
		return ret;
	}
	
	@Override
	public double getDoubleValue(int i) throws ThinklabValueConversionException {

		if (prototype == null)
			return Double.NaN;
		
		if (!prototype.isNumber())
			throw new ThinklabValueConversionException("can't convert IValue into double");

		return data[i].asNumber().asDouble();
	}
	
	@Override
	public Metadata getMetadata() {
		return metadata;
	}


	@Override
	public int getValueCount() {
		return data.length;
	}

//	@Override
//	public IDataSource<?> transform(IDatasourceTransformation transformation)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//		return this;
//	}
//
//	@Override
//	public void postProcess(IObservationContext context)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void preProcess(IObservationContext context)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public IConcept getObservableClass() {
		return _type;
	}

	@Override
	public ObservationContext getObservationContext() {
		return this.context;
	}


}
