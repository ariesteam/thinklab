package org.integratedmodelling.afl.steps;

import org.integratedmodelling.afl.Functor;
import org.integratedmodelling.afl.Interpreter;
import org.integratedmodelling.afl.StepListener;
import org.integratedmodelling.utils.KeyValueMap;
import org.integratedmodelling.utils.Polylist;

public class TestStep implements Functor {

	static {
		Interpreter.registerFunctor("test", "org.integratedmodelling.application.steps.TestStep");
	}
	
	@Override
	public Polylist run(StepListener model, KeyValueMap options,
			Object... arguments) {
		// TODO Auto-generated method stub
		return null;
	}

}
