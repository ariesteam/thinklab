package org.integratedmodelling.thinklab.interfaces.annotations;

import java.lang.annotation.*;

/**
 * This one is used on classes implementing ITask to define the namespace that the correspondent
 * automatically generated functions will use. Of course languages that don't have namespaces will
 * not honor it, but those who do should.
 * 
 * @author Ferdinando Villa
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskNamespace {
	public String ns() default "user";
}
