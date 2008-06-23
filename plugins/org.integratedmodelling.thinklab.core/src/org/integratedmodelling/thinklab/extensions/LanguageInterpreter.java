package org.integratedmodelling.thinklab.extensions;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeProvider;
import org.integratedmodelling.thinklab.value.AlgorithmValue;

public interface LanguageInterpreter {
	
	public interface IContext {
		
		public void bind(Object object, String name);
		
	}

	public void initialize(IKnowledgeProvider km);
	
	public IValue execute(AlgorithmValue code, IContext context) throws ThinklabException;
}
