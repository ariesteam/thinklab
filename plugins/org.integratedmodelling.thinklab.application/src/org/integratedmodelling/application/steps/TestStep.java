package org.integratedmodelling.application.steps;

import org.integratedmodelling.application.ApplicationFunctor;
import org.integratedmodelling.application.ApplicationInterpreter;
import org.integratedmodelling.application.ApplicationModel;
import org.integratedmodelling.utils.KeyValueMap;
import org.integratedmodelling.utils.Polylist;

public class TestStep implements ApplicationFunctor {

	static {
		ApplicationInterpreter.register("dio", "org.integratedmodelling.application.steps.TestStep");
	}
	
	@Override
	public Polylist run(ApplicationModel model, KeyValueMap options,
			Object... arguments) {
		// TODO Auto-generated method stub
		return null;
	}

}
