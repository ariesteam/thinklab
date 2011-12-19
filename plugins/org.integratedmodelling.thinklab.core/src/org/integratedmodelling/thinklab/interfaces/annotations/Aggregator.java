package org.integratedmodelling.thinklab.interfaces.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Classes tagged with this must implement the IStateAggregator interface. Use to provide custom
 * aggregation on model results. See modelling plugin for examples.
 * 
 * @author Ferdinando Villa
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Aggregator {
	public String id();
	public String concept();
}
