package org.integratedmodelling.corescience.implementations.datasources;

import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.corescience.interfaces.data.ICategoryData;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class ClassData extends IndexedContextualizedDatasourceByte<IConcept> implements
		ICategoryData {
	
	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		
		double[] ret = new double[this.data.length];
		HashMap<IConcept, Integer> ranks = Metadata.rankConcepts(_type);
		
		for (int i = 0; i < this.data.length; i++) {
			
			IConcept c = (IConcept) getValue(i, null);
			if (c == null) {
				ret[i] = Double.NaN;
			} else if (ranks == null) {
				ret[i] = (double)data[i];
			} else {
				ret[i] = (double)ranks.get(c);
			}
		}
		
		return ret;
	}

	public ClassData(IConcept type, int size) {
		super(type, size);
	}

	@Override
	public Collection<IConcept> getAllCategories() {
		return map.keySet();
	}

	@Override
	public IConcept getCategory(int n) {
		return (IConcept) getValue(n, null);
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

}
