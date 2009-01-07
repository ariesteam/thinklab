package org.integratedmodelling.thinklab.application;

import java.net.URL;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.extensions.Interpreter;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.ITask;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.interpreter.InterpreterManager;

/**
 * A task that runs a script, specified either as a 
 * @author Ferdinando
 *
 */
public class RunScript implements ITask {

	private String code = null;
	private URL codeUrl = null;
	private String language = null;
	private IValue result = null;
	
	public void setCodeUrl(Object code) {
		if (code instanceof URL)
			this.codeUrl = (URL) code;
		else 
			this.code = code.toString();
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

	
	@Override
	public void run(ISession session) throws ThinklabException {

		/*
		 * retrieve interpreter for language
		 */
		Interpreter intp = InterpreterManager.get().newInterpreter(language);
		
		intp.setSession(session);
		intp.setInput(session.getDefaultInputStream());
		intp.setOutput(session.getDefaultOutputStream());
		
		
		/*
		 * run whatever
		 */
		if (codeUrl != null) {
			result = intp.eval(codeUrl);
		} else if (code != null) {
			result = intp.eval(code);
		}

	}

	public IValue getResult() {
		return result;
	}
	
}
