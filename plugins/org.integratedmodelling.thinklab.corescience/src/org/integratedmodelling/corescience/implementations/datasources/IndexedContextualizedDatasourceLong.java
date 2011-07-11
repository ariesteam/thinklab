package org.integratedmodelling.corescience.implementations.datasources;

import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.DatasourceStateAdapter;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IInstanceImplementation;

/**
 * A generic datasource that will contain multiple copies of hashable objects. Objects are 
 * stored once, and an integer is used as an index for storage. Indexes are generated
 * automatically unless permissible objects are passed to the constructor.
 * 
 * The Byte version uses bytes as a classifier, so it must not be used for anything that
 * can have more than 255 distinct objects. Typically this is good for classifications.
 * Use the Int and Long version as required.
 * 
 * @author Ferdinando
 *
 * @param <ObjectType>
 */
public class IndexedContextualizedDatasourceLong<T>  extends DefaultAbstractState
 	implements IState, IInstanceImplementation, IConceptualizable {

	private static final long serialVersionUID = -6567783706189229920L;
	long[] data = null;
	private long max = 1L;
	
	HashMap<T, Long> map = new HashMap<T, Long>();
	HashMap<Long, T> inverseMap = new HashMap<Long, T>();

	public IndexedContextualizedDatasourceLong(IConcept type, int size, IContext context) {
		_type = type;
		data = new long[size];
		this.context = context;
	}

	@Override
	public Object getValue(int offset) {
		return (offset >= 0 && offset < data.length) ? inverseMap.get(new Long(data[offset])) : null;
	}

	@Override
	public IConcept getValueType() {
		return _type;
	}
	
	@Override
	public void setValue(int idx, Object o) {
		data[idx] = getIndex((T)o);
	}

	private long getIndex(T o) {
		if (o == null)
			return 0;
		Long i = (Long) map.get(o);
		if (i == null) {
			map.put(o, (i = new Long(max++)));
			inverseMap.put(i,o);
		}
		return i;
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
		return "ICDL[" + _type + " {" + map + "}: " /*+ Arrays.toString(data)*/ + "]";
	}

	@Override
	public Object getRawData() {
		return data;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		throw new ThinklabValueConversionException("can't convert concepts into doubles");
	}

	@Override
	public int getValueCount() {
		return data.length;
	}
	

	@Override
	public double getDoubleValue(int index)
			throws ThinklabValueConversionException {
		throw new ThinklabValueConversionException("can't convert concepts into doubles");
	}



}
