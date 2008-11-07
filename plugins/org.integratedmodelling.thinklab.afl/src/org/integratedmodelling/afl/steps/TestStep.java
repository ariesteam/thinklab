package org.integratedmodelling.afl.steps;

import org.integratedmodelling.afl.Functor;
import org.integratedmodelling.afl.Interpreter;
import org.integratedmodelling.afl.StepListener;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.KeyValueMap;

public class TestStep implements Functor {

	static {
		Interpreter.registerFunctor("test", "org.integratedmodelling.application.steps.TestStep");
	}
	
	@Override
	public IValue run(StepListener model, KeyValueMap options, IValue... arguments) {
		// TODO Auto-generated method stub
		return null;
	}

}
