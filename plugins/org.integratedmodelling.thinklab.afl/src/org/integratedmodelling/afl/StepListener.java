package org.integratedmodelling.afl;

import org.integratedmodelling.utils.Polylist;


/**
 * A container for the state of an application. It contains a stack of objects where application
 * steps can push a result object, and callbacks to react to any change in state.
 * @author Ferdinando Villa
 *
 */
public abstract interface StepListener  {

	public abstract void notifyResult(Polylist ret);

	
}
