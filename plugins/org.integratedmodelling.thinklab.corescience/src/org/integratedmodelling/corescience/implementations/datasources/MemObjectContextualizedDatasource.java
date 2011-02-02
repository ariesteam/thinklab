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
		return (offset >= 0 && offset < data.length) ? data[offset] : null;
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
				ret[i] = (data[i] == null ? Double.NaN : ((Number)data[i]).doubleValue());
			}
			return ret;
			
		} else if (prototype instanceof IndexedCategoricalDistribution) {
			
			double[] ret = new double[data.length];
			for (int i = 0; i < data.length; i++) {
				ret[i] = (data[i] == null ? Double.NaN : ((IndexedCategoricalDistribution)data[i]).getMean());
			}
			return ret;
			
			
		} else if (prototype instanceof IConcept && Metadata.getClassMappings(metadata) != null) {

			double[] ret = new double[data.length];
			HashMap<IConcept, Integer> rnk = Metadata.getClassMappings(metadata);
			for (int i = 0; i < data.length; i++) {
				Object o = data[i];
				ret[i] = (o == null ? Double.NaN : (double)(rnk.get((IConcept)o)));
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
	
		if (prototype instanceof Number) {
			return (data[i] == null ? Double.NaN : ((Number)data[i]).doubleValue());
		} else if (prototype instanceof IConcept && Metadata.getClassMappings(metadata) != null) {
			HashMap<IConcept, Integer> rnk = Metadata.getClassMappings(metadata);
			return (data[i] == null ? Double.NaN : (double)(rnk.get((IConcept)data[i])));
		}
	
		// TODO must accommodate distributions 
		
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
}
