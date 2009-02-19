package org.integratedmodelling.thinklab.extensions;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeProvider;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.AlgorithmValue;

/**
 * @deprecated
 */
public interface LanguageInterpreter {
	
	public interface IContext {
		
		public void bind(Object object, String name);
		
	}

	public void initialize(IKnowledgeProvider km);
	
	public IValue execute(AlgorithmValue code, IContext context) throws ThinklabException;

	IContext getNewContext(ISession session);
}
