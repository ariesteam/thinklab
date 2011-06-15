package org.integratedmodelling.thinklab.interfaces.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Classes tagged with this must implement the ITransformation interface and become available through
 * the TransformationFactory to provide user-defined transformations of objects to doubles. Mostly
 * used in the corescience and modelling plugins.
 * 
 * @author Ferdinando Villa
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DataTransformation {
	public String id();
}
