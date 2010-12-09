package org.integratedmodelling.corescience.interfaces;

/**
 * A tag interface that can be used to denote Observation types that accept no-data
 * points in their dependencies states. It only specifies the default for the type; 
 * the :accepts-nodata model option can override it (see modelling plugin).
 * 
 * @author ferdinando.villa
 *
 */
public interface IPartiallySpecifiableObservation {

}
