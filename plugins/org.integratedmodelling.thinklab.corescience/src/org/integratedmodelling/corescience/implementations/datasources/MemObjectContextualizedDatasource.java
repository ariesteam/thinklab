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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.DatasourceStateAdapter;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.literals.IndexedCategoricalDistribution;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.annotations.PersistentObject;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.storage.IPersistentObject;
import org.integratedmodelling.utils.InputSerializer;
import org.integratedmodelling.utils.OutputSerializer;
import org.integratedmodelling.utils.Polylist;

@PersistentObject()
public class MemObjectContextualizedDatasource extends DefaultAbstractState
 	implements IState, IInstanceImplementation, IPersistentObject {

	private static final long serialVersionUID = -6567783706189229920L;
	private Object[] data = null;
	private Object prototype = null;
	
	public MemObjectContextualizedDatasource(IConcept type, int size, IContext context) {
		
		_type = type;
		data = new Object[size];
		this.context = context;
		
		/*
		 * FIXME: remove?
		 * prepare to hold data of this type, determining if it's going to be a classification or what
		 */
		Metadata.rankConcepts(type,  metadata);
	}
	
	@Override
	public Object getValue(int offset) {
		Object ret = (offset >= 0 && offset < data.length) ? data[offset] : null;
		return context.isCovered(offset) ? ret : null;
	}

	@Override
	public IConcept getValueType() {
		return _type;
	}


	@Override
	public void setValue(int idx, Object o) {

		if (prototype == null)
			prototype = o;
		data[idx] = o;
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
		return "MO[" + _type + ": " /*+ Arrays.toString(data)*/ + "]";
	}
	
	@Override
	public Object getRawData() {
		return data;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
	
		if (prototype == null) {
			double[] ret = new double[data.length];
			for (int i = 0; i < data.length; i++) {
				ret[i] = Double.NaN;
			}
			return ret;			
		}
		
		if (prototype instanceof Number) {
			
			double[] ret = new double[data.length];
			for (int i = 0; i < data.length; i++) {
				Object dat = data[i];
				if (dat != null && !context.isCovered(i))
					dat = null;

				ret[i] = (dat == null ? Double.NaN : ((Number)dat).doubleValue());
			}
			return ret;
			
		} else if (prototype instanceof IndexedCategoricalDistribution) {
			
			double[] ret = new double[data.length];
			double[] unc = new double[data.length];
			
			for (int i = 0; i < data.length; i++) {

				Object dat = data[i];
				if (dat != null && !context.isCovered(i))
					dat = null;

				ret[i] = (dat == null ? Double.NaN : ((IndexedCategoricalDistribution)dat).getMean());
				unc[i] = (dat == null ? Double.NaN : ((IndexedCategoricalDistribution)dat).getUncertainty());
			}
			
			this.metadata.put(Metadata.UNCERTAINTY, unc);
			return ret;
			
			
		} else if (prototype instanceof IConcept && Metadata.getClassMappings(metadata) != null) {

			double[] ret = new double[data.length];
			HashMap<IConcept, Integer> rnk = Metadata.getClassMappings(metadata);
			for (int i = 0; i < data.length; i++) {
				Object dat = data[i];
				if (dat != null && !context.isCovered(i))
					dat = null;
				
				double dd = Double.NaN;
				
				if (dat != null && rnk != null) {
					Integer di = rnk.get((IConcept)dat);
					dd = di == null ? Double.NaN : (double)di;
				}
			
				ret[i] = (dat == null ? Double.NaN : dd);
			}
			return ret;			
		}
	
		// TODO must accommodate distributions and make histograms for Comparables
		CoreScience.get().logger().warn(_type + ": can't convert a " + 
				(prototype == null ? "null" : prototype.getClass().getCanonicalName()) + 
				" into a double");
		
		return null;
			 
	}
	
	@Override
	public double getDoubleValue(int i) throws ThinklabValueConversionException {
	
		Object dat = data[i];
		if (dat != null && !context.isCovered(i))
			dat = null;
		
		if (prototype == null)
			return Double.NaN;
		
		if (prototype instanceof Number) {
			return (dat == null? Double.NaN : ((Number)dat).doubleValue());
		} else if (prototype instanceof IConcept && Metadata.getClassMappings(metadata) != null) {
			HashMap<IConcept, Integer> rnk = Metadata.getClassMappings(metadata);
			return (dat == null ? Double.NaN : (double)(rnk.get((IConcept)dat)));
		} else if (prototype instanceof IndexedCategoricalDistribution) {
			return (dat == null ? Double.NaN : ((IndexedCategoricalDistribution)dat).getMean());
		}
	
		throw new ThinklabValueConversionException("can't convert a " + prototype.getClass() + " into a double");	 
	}

	@Override
	public int getValueCount() {
		return data.length;
	}

	@Override
	public IPersistentObject deserialize(InputStream fop)
			throws ThinklabException {
		
		InputSerializer in = new InputSerializer(fop);
		String ty = in.readString();
		
		if (ty.equals("d")) {
			MemDoubleContextualizedDatasource ds = 
				new MemDoubleContextualizedDatasource();
			return ds.deserialize(fop);
		}
		
		throw new ThinklabInternalErrorException(
				"deserialize: cannot recreate datasource of type " +
				ty + 
				" from object source: object states should not be recreated as such at this stage.");
	}
	
	@Override
	public boolean serialize(OutputStream fop) throws ThinklabException {

		IConcept c = KnowledgeManager.getConceptForObject(data[0]);
		if (c == null)
			return false;
		
		boolean ret = false;

		/*
		 * serialize as the appropriate state for the type; set ret to
		 * true.
		 */
		if (c.is(KnowledgeManager.Double())) {
			OutputSerializer out = new OutputSerializer(fop);
			out.writeString("d");
			// ACHTUNG after this, it must be in sync with MemDoubleCState.deserialize()
			out.writeString(_type.toString());
			out.writeInteger(data == null ? 0 : data.length);
			if (data != null)
				for (int i = 0; i < data.length; i++)
					out.writeDouble(data[i] == null ? Double.NaN : (Double)(data[i]));

			Metadata.serializeMetadata(metadata, fop);
			ret = true;
		}
		
		return ret;
	}
	
	@Override
	public boolean isProbabilistic() {
		// TODO Auto-generated method stub
		return prototype != null && prototype instanceof IndexedCategoricalDistribution;
	}

	@Override
	public boolean isContinuous() {
		return Metadata.isContinuous(metadata);
	}

	@Override
	public boolean isNumeric() {
		// TODO Auto-generated method stub
		return prototype != null && prototype instanceof Number;
	}

	@Override
	public boolean isCategorical() {
		return Metadata.isUnorderedClassification(metadata);
	}

	@Override
	public boolean isBoolean() {
		return Metadata.isBoolean(metadata);
	}
}
