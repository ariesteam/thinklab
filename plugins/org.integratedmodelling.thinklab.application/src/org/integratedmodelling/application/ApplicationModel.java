package org.integratedmodelling.application;

import org.integratedmodelling.utils.Polylist;


/**
 * A container for the state of an application. It contains a stack of objects where application
 * steps can push a result object, and callbacks to react to any change in state.
 * @author Ferdinando Villa
 *
 */
public abstract interface ApplicationModel  {

	public abstract void notifyResult(Polylist ret);

}
