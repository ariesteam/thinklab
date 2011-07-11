package org.integratedmodelling.thinklab.interfaces.knowledge;

import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;


/**
 * If an instance implementation class implements IParseable, whole instances can be specified as string 
 * literals in Thinklab lists (and derived formalisms such as OPAL) using the syntax (# literal).
 * 
 * @author Ferdinando
 *
 */
public interface IParseableKnowledge {

	/**
	 * Called when the instance is being create, after the IParseable has been set as an implementation.
	 * 
	 * @param inst
	 * @param literal
	 * @return
	 * @throws ThinklabValidationException 
	 */
	public abstract void parseSpecifications(IInstance inst, String literal) throws ThinklabValidationException;
	
}
