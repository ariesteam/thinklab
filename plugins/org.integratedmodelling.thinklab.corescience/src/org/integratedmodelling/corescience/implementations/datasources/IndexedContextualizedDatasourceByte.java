package org.integratedmodelling.corescience.implementations.datasources;

import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.DatasourceStateAdapter;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
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
 * @deprecated
 */
public class IndexedContextualizedDatasourceByte<T> extends DefaultAbstractState
 	implements IState, IInstanceImplementation, IConceptualizable {

	private static final long serialVersionUID = -6567783706189229920L;
	byte[] data = null;
	private byte max = 1;
	
	
	HashMap<T, Integer> map = new HashMap<T, Integer>();
	HashMap<Integer, T> inverseMap = new HashMap<Integer, T>();

	public IndexedContextualizedDatasourceByte(IConcept type, int size, IContext ctx) {
		_type = type;
		data = new byte[size];
		this.context = ctx;
	}

	@Override
	public Object getValue(int offset) {
		return (offset >= 0 && offset < data.length) ? inverseMap.get(new Integer(data[offset])) : null;
	}

	@Override
	public IConcept getValueType() {
		return _type;
	}

	@Override
	public void setValue(int idx, Object o) {
		data[idx] = getIndex((T)o);
	}

	private byte getIndex(T o) {
		if (o == null)
			return 0;
		Integer i = (Integer) map.get(o);
		if (i == null) {
			map.put(o, (i = new Integer(max++)));
			inverseMap.put(i,o);
		}
		return (byte)(int)i;
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
		return "ICDB[" + _type + " {" + map + "}: " /*+ Arrays.toString(data)*/ + "]";
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
	public int getValueCount() {
		return data.length;
	}
//
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
//	public void preProcess(IObservationContext ctx)
//			throws ThinklabException {
//	}


	@Override
	public double getDoubleValue(int index)
			throws ThinklabValueConversionException {
		throw new ThinklabValueConversionException("can't convert concepts into doubles");
	}


}
