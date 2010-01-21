package org.integratedmodelling.thinklab.interfaces.annotations;

import java.lang.annotation.*;

/**
 * Tagging a class with this will notify it to the persistence manager so that the class
 * can be created even if it's a hidden plugin class. Specifying a file extension will 
 * enable the direct creation of objects from file in the persistence manager, which will
 * recognize the class from the file extension.
 * 
 * @author Ferdinando Villa
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PersistentObject {
	public String extension() default "__NOEXT__";
}
