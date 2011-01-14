package org.integratedmodelling.geospace.kmeans;

/**
 * Exception thrown when insufficient memory is available to
 * perform an operation.  Designed to be throw before doing 
 * something that would cause a <code>java.lang.OutOfMemoryError</code>.
 */
public class InsufficientMemoryException extends Exception {

    /**
     * Constructor.
     * 
     * @param message an explanatory message.
     */
    public InsufficientMemoryException(String message) {
        super(message);
    }
    
    /**
     * Default constructor.
     */
    public InsufficientMemoryException() {}
    
}
