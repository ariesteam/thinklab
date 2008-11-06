package org.integratedmodelling.afl;

import org.integratedmodelling.utils.KeyValueMap;
import org.integratedmodelling.utils.Polylist;

public interface Functor {

	public abstract Polylist run(StepListener model, KeyValueMap options, Object ... arguments);
	
}
