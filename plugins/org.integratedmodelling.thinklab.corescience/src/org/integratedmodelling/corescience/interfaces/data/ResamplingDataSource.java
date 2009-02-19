package org.integratedmodelling.corescience.interfaces.data;

import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * An interpolating data source will be exposed to the context at handshake, so that it can
 * adjust its contents to reflect it. In order to be interpolating, it also needs to be dimensional,
 * because at least one extent must exist to allow the data to be interpolated.
 * 
 * @author Ferdinando
 *
 */
public interface ResamplingDataSource extends DimensionalDataSource {

	/*
	 * Called after dimensionality has been notified and accepted. Will interpolate as necessary. 
	 * and return self, or a new datasource if that is a more efficient or cleaner.
	 */
	public IDataSource<?> resample() throws ThinklabException;
}
