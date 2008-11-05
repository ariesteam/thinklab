package org.integratedmodelling.application;

import org.integratedmodelling.utils.KeyValueMap;
import org.integratedmodelling.utils.Polylist;

public interface ApplicationFunctor {

	public abstract Polylist run(ApplicationModel model, KeyValueMap options, Object ... arguments);
	
}
