package org.integratedmodelling.thinklab.modelling.interfaces;

import org.integratedmodelling.thinklab.api.modelling.IState;

public interface IModifiableState extends IState {

	public abstract void setValue(int index, Object value);
}
