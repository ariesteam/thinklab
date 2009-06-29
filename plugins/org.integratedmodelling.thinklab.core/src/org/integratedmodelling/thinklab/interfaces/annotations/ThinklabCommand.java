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
public @interface ThinklabCommand {
	public String name();
	public String description() default "Undocumented command";
	public String returnType() default "";
	public String argumentNames() default "";
	public String argumentTypes() default "";
	public String argumentDescriptions() default "";
	public String optionalArgumentNames() default "";
	public String optionalArgumentTypes() default "";
	public String optionalArgumentDescriptions() default "";
	public String optionalArgumentDefaultValues() default "";
	public String optionNames() default "";
	public String optionLongNames() default "";
	public String optionDescriptions() default "";
	public String optionTypes() default "";
	public String optionArgumentLabels() default "";
}
