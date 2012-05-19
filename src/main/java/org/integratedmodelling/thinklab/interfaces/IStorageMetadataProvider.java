package org.integratedmodelling.thinklab.interfaces;

import org.integratedmodelling.thinklab.api.metadata.IMetadata;

/**
 * Classes implementing this may be requested to provide metadata that
 * will be used for querying. At the moment this is used in models, which
 * will ask their datasource for metadata so they can index them spatially
 * and temporally if they implement this.
 * 
 * @author ferdinando.villa
 *
 */
public interface IStorageMetadataProvider {

	public abstract void addStorageMetadata(IMetadata metadata);
}
