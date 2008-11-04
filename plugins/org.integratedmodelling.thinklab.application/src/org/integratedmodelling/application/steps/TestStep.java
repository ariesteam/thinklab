package org.integratedmodelling.application.steps;

import org.integratedmodelling.application.ApplicationManager;
import org.integratedmodelling.application.ApplicationModel;
import org.integratedmodelling.application.ApplicationStep;
import org.integratedmodelling.utils.KeyValueMap;

public class TestStep implements ApplicationStep {

	static {
		ApplicationManager.register("dio", "org.integratedmodelling.application.steps.ApplicationManager");
	}
	
	@Override
	public Object run(ApplicationModel model, KeyValueMap options,
			Object... arguments) {
		// TODO Auto-generated method stub
		return null;
	}

}
