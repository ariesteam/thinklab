/**
 * 
 */
package org.integratedmodelling.thinklab.interfaces.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Ferdinando
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectLoader {
	public String folder();
	public String description() default "";
}
