package org.integratedmodelling.corescience.interfaces;

import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;

public interface IDataSource<T> {
	
	/**
	 * All datasources must report to the conceptual model what kind of
	 * value they are going to return.
	 * 
	 * @return
	 */
	public abstract IConcept getValueType();

	/**
	 * Datasources may have an initial value before anything is computed or extracted. This value, if
	 * not null, is used to initialize state storage before contextualization. If the initial value
	 * makes sense for the datasource, return it here. Otherwise just return null.
	 * 
	 * @return
	 */
	public abstract T getInitialValue();
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public abstract T getValue(int index, Object[] parameters);
	
	/**
	 * This callback gets called before any process() command is called. It's a good place to load
	 * data, check the context and prepare for processing.
	 * 
	 * @param context
	 * @throws ThinklabException
	 */
	public abstract void preProcess(IObservationContext context) throws ThinklabException;

	/**
	 * This callback gets called after any process() command is called but before the first
	 * values are extracted. It's a good place to load data after transformations. Datasource
	 * must be ready to serve data in the given context after this is called.
	 * 
	 * @param context
	 * @throws ThinklabException
	 */
	public abstract void postProcess(IObservationContext context) throws ThinklabException;

	/**
	 * Process the passed transformation created by the extents and
	 * return the transformed datasource.
	 * 
	 * @param transformation
	 * @return
	 * @throws ThinklabException if the transformation cannot be handled.
	 */
	public abstract IDataSource<?> transform(IDatasourceTransformation transformation)
		throws ThinklabException;
}
