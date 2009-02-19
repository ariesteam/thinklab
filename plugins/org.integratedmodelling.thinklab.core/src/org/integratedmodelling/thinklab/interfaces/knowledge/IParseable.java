package org.integratedmodelling.thinklab.interfaces.knowledge;

import org.integratedmodelling.utils.Polylist;

/**
 * If an instance implementation class implements IParseable, whole instances can be specified as string 
 * literals in Thinklab lists (and derived formalisms such as OPAL) using the syntax (# literal).
 * 
 * @author Ferdinando
 *
 */
public interface IParseable {

	/**
	 * Called when the instance is being create, after the IParseable has been set as an implementation.
	 * 
	 * @param inst
	 * @param literal
	 * @return
	 */
	public abstract void parseSpecifications(IInstance inst, String literal);
	
}
