package org.integratedmodelling.thinklab.application;

import java.net.URL;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.extensions.Interpreter;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.ITask;
import org.integratedmodelling.thinklab.interfaces.applications.IUserModel;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
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
	
	public void setCode(Object code) {
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

		IUserModel userModel = session.getUserModel();
		
		/*
		 * retrieve interpreter for language
		 */
		Interpreter intp = InterpreterManager.get().newInterpreter(language);
		
		intp.setSession(session);
		
		if (userModel != null) {
			intp.setInput(userModel.getDefaultInputStream());
			intp.setOutput(userModel.getDefaultOutputStream());
		}
		
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
