package org.integratedmodelling.corescience.implementations.datasources;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.utils.Polylist;

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
public class IndexedContextualizedDatasourceLong<T> 
 	implements IState, IInstanceImplementation, IConceptualizable {

	private static final long serialVersionUID = -6567783706189229920L;
	IConcept _type;
	long[] data = null;
	private long max = 1L;
	private ObservationContext context;
	
	Metadata metadata = new Metadata();

	HashMap<T, Long> map = new HashMap<T, Long>();
	HashMap<Long, T> inverseMap = new HashMap<Long, T>();

	public IndexedContextualizedDatasourceLong(IConcept type, int size, ObservationContext context) {
		_type = type;
		data = new long[size];
		this.context = context;
	}
	
	@Override
	public Object getInitialValue() {
		return null;
	}

	@Override
	public Object getValue(int index, Object[] parameters) {
		return inverseMap.get(new Long(data[index]));
	}

	@Override
	public Object getDataAt(int offset) {
		return (offset >= 0 && offset < data.length) ? inverseMap.get(new Long(data[offset])) : null;
	}

	@Override
	public IConcept getValueType() {
		return _type;
	}
	
	@Override
	public void addValue(int idx, Object o) {
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
				Polylist.list("@", this));
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
	public Metadata getMetadata() {
		return metadata;
	}
	
	@Override
	public int getTotalSize() {
		return data.length;
	}

	@Override
	public IDataSource<?> transform(IDatasourceTransformation transformation)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void postProcess(IObservationContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preProcess(IObservationContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public IConcept getObservableClass() {
		return _type;
	}

	@Override
	public ObservationContext getObservationContext() {
		return this.context;
	}

	@Override
	public double getDoubleValue(int index)
			throws ThinklabValueConversionException {
		throw new ThinklabValueConversionException("can't convert concepts into doubles");
	}



}
