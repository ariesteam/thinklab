package org.integratedmodelling.afl;

import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.KeyValueMap;

public interface Functor {

	public abstract IValue run(StepListener model, KeyValueMap options, IValue ... arguments);
	
}
