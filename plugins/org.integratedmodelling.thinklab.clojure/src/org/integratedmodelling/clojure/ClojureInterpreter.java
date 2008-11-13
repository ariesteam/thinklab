package org.integratedmodelling.clojure;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.extensions.Interpreter;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.Compiler;

public class ClojureInterpreter implements Interpreter {

	InputStream input = System.in;
	OutputStream output = System.out;
	OutputStream error = System.err;
	private ISession session;
	
	@Override
	public IValue eval(Object code) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue eval(Object code, Object... args) throws ThinklabException {
		
		if (code instanceof String) {
			
		} else if (code instanceof Polylist) {
			
		} else {
			
		}
		
		return null;
	}

	@Override
	public IValue eval(Object code, HashMap<String, Object> args)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue eval(URL source) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadBindings(URL source) throws ThinklabException {
        try {
			Compiler.loadFile(Escape.fromURL(source.getFile().toString()));
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
	}

	@Override
	public void setError(OutputStream input) {
		this.error = input;
	}

	@Override
	public void setInput(InputStream input) {
		this.input = input;
	}

	@Override
	public void setOutput(OutputStream input) {
		this.output = input;
	}

	@Override
	public void setSession(ISession session) {
		this.session = session;
	}

}
