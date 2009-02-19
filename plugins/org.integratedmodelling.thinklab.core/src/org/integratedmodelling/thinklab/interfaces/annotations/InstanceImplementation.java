package org.integratedmodelling.thinklab.interfaces.annotations;

import java.lang.annotation.*;

/**
 * Classes that are in the package <plugin package>.implementations will be scanned to collect these annotations;
 * if any is found, the annotated class becomes the Java implementation for instances of the given concept.
 * 
 * @author Ferdinando Villa
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface InstanceImplementation {
	public String concept();
}
