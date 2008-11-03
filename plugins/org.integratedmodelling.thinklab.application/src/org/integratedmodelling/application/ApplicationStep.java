package org.integratedmodelling.application;

import org.integratedmodelling.utils.KeyValueMap;

public interface ApplicationStep {

	public abstract Object run(ApplicationModel model, KeyValueMap options, Object ... arguments);
	
}
