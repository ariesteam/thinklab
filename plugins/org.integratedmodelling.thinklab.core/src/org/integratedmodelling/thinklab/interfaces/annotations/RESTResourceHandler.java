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
public @interface RESTResourceHandler {
	public String id();
	public String description();
	public String arguments() default "";
	public String options() default "";
	public String[] mimeTypes() default {};
}
