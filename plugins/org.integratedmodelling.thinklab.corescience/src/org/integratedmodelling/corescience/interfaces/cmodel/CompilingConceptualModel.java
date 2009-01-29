package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.corescience.interfaces.context.IStackCompiler;

/**
 * If conceptual models in the whole observation structure being contextualized implement this one,
 * a IWorkflowCompiler will be used to return a compiled workflow that can be run. If the workflow
 * implements Serializable, it can be independently stored.
 * 
 * @author Ferdinando
 *
 */
public interface CompilingConceptualModel {

	public abstract void encodePushValue(IStackCompiler compiler);
	
}
