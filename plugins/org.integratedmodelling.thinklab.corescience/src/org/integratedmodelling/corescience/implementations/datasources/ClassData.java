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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.data.ICategoryData;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IPersistentObject;
import org.integratedmodelling.utils.Pair;

public class ClassData extends IndexedContextualizedDatasourceInt<IConcept> implements
		ICategoryData, IPersistentObject {
	
	HashMap<IConcept, Integer> ranks = null;
	
	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		
		double[] ret = new double[this.data.length];
		
		for (int i = 0; i < this.data.length; i++) {
			
			IConcept c = (IConcept) getValue(i);
			if (c == null) {
				ret[i] = Double.NaN;
			} else if (ranks == null) {
				ret[i] = (double)data[i];
			} else {
				Object o = ranks.get(c);
				ret[i] = o == null ? Double.NaN : ((Number)o).doubleValue();
			}
		}
		
		return ret;
	}

	public ClassData(IConcept type, int size, ArrayList<Pair<GeneralClassifier, IConcept>> classifiers,
			IContext context) throws ThinklabValidationException {
		
		super(type, size, context);
		
		IConcept[] rnk = null;

		if (classifiers != null) {
			/*
			 * remap the values to ranks and determine how to rewire the input
			 * if necessary, use classifiers instead of lexicographic order to
			 * infer the appropriate concept order
			 */
			ArrayList<GeneralClassifier> cls = new ArrayList<GeneralClassifier>();
			ArrayList<IConcept> con = new ArrayList<IConcept>();
			for (Pair<GeneralClassifier, IConcept> op : classifiers) {
				cls.add(op.getFirst());
				con.add(op.getSecond());
			}

			Pair<double[], IConcept[]> pd = Metadata
					.computeDistributionBreakpoints(type, cls, con);
			if (pd != null) {
				if (pd.getSecond()[0] != null) {
					rnk = pd.getSecond();
				}
			}
		}
		
		if (rnk == null) {	
			this.ranks = Metadata.rankConcepts(_type, metadata);
		} else {
			this.ranks = Metadata.rankConcepts(_type, rnk, metadata);
		}

		if (ranks == null) {
			throw new ThinklabRuntimeException("internal: classdata: cannot determine classification from type " + _type);
		}
		
		// preload indexes so that we use these rankings and we have the whole
		// set of classes even if the data do not contain all of them
		for (Map.Entry<IConcept, Integer> e : ranks.entrySet()) {
			this.map.put(e.getKey(), e.getValue());
			this.inverseMap.put(e.getValue(), e.getKey());
		}
	}

	@Override
	public Collection<IConcept> getAllCategories() {
		return map.keySet();
	}

	@Override
	public IConcept getCategory(int n) {
		return (IConcept) getValue(n);
	}

	@Override
	public IConcept getConceptSpace() {
		return _type;
	}

	@Override
	public IConcept[] getData() {
		IConcept[] ret = new IConcept[data.length];
		for (int i = 0; i < data.length; i++) {
			ret[i] = getCategory(i);
		}
		return ret;
	}

	public String toString() {
		return "CD[" + _type + " {" + map + "}: " /*+ Arrays.toString(data)*/ + "]";
	}

	@Override
	public IPersistentObject deserialize(InputStream fop) throws ThinklabException {
		
		Metadata.MetadataDeserializer in = new Metadata.MetadataDeserializer(fop);
		_type = KnowledgeManager.get().requireConcept(in.readString());
		map = in.readRankings();
		// just reconstruct this one
		for (IConcept c : map.keySet())
			inverseMap.put(map.get(c), c);
		data = in.readIntegers();
		metadata = Metadata.deserializeMetadata(fop);
		return this;
	}

	@Override
	public boolean serialize(OutputStream fop) throws ThinklabException {
		
		Metadata.MetadataSerializer out = new Metadata.MetadataSerializer(fop);
		out.writeString(_type.toString());
		out.writeRankings(map);
		out.writeIntegers(data);
		Metadata.serializeMetadata(metadata, fop);
		return true;
	}

	@Override
	public boolean isProbabilistic() {
		return false;
	}

	@Override
	public boolean isContinuous() {
		return false;
	}

	@Override
	public boolean isNumeric() {
		return false;
	}

	@Override
	public boolean isCategorical() {
		return true;
	}

	@Override
	public boolean isBoolean() {
		return Metadata.isBoolean(metadata);
	}

}
