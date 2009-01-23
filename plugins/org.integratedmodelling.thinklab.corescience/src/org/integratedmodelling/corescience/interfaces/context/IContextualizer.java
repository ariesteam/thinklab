package org.integratedmodelling.corescience.interfaces.context;

/**
 * Testing new approach to contextualization. Used only for development at the 
 * moment.
 * 
 * It should not be passed anything but optional runtime parameters or listeners. All
 * the info to run a contextualization should be compiled in.
 * 
 * @author Ferdinando
 *
 */
public interface IContextualizer {
	
	/**
	 * TODO this will have to return the states in some way.
	 */
	public void run();
	
}
