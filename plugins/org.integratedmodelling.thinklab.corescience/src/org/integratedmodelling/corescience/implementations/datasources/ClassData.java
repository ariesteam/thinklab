package org.integratedmodelling.corescience.implementations.datasources;

import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.data.ICategoryData;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class ClassData extends IndexedContextualizedDatasourceByte<IConcept> implements
		ICategoryData {

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

}
