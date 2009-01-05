package org.integratedmodelling.clojure;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabScriptException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.extensions.Interpreter;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.Value;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.Compiler;
import clojure.lang.LineNumberingPushbackReader;
import clojure.lang.LispReader;
import clojure.lang.RT;
import clojure.lang.Var;

public class ClojureInterpreter implements Interpreter {

	InputStream input = System.in;
	OutputStream output = System.out;
	OutputStream error = System.err;
	private ISession session;
	
	@Override
	public IValue eval(Object code) throws ThinklabException {

		InputStream inp = null;
		try {
			if (code instanceof URL) {
				inp = ((URL)code).openStream();
			} else if (code instanceof File) {
				inp = new FileInputStream((File)code);
			} else {
				inp = new ByteArrayInputStream(code.toString().getBytes("UTF-8"));
			}
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);
		}
		
		LineNumberingPushbackReader rdr = new LineNumberingPushbackReader(
				new InputStreamReader(inp, RT.UTF8));
		
		Object EOF = new Object();
		Object r;
		Object ret = null;
		final Var sess  = RT.var("tl", "*session*");
		
		try {			
			Var.pushThreadBindings(RT.map(sess, this.session));			
			r = LispReader.read(rdr, false, EOF, false);
			ret = Compiler.eval(r);
		} catch (Exception e) {
			throw new ThinklabScriptException(e);
		}
		
		/*
		 * FIXME remove
		 */
		System.out.println("EXECUTED: " + code);
		
		// TODO Auto-generated method stub
		return ret == null ? null : Value.getValueForObject(ret);
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
	public void loadBindings(URL source, ClassLoader cloader) throws ThinklabException {
        try {
        	
        	/**
        	 * TODO 
        	 * needs to use context classloader - and if that doesn't work,
        	 * find out what plugin the stuff comes from and set the classloader appropriately
        	 * before calling loadFile.
        	 */ 	
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

	@Override
	public void defineTask(Class<?> taskClass) throws ThinklabException {
		
		/*
		 * Create Clojure binding for passed task.
		 */
		if (taskClass.isInterface() || Modifier.isAbstract(taskClass.getModifiers()))
			return;
		
		String fname = CamelCase.toLowerCase(MiscUtilities.getFileExtension(taskClass.getName()), '-');
		
		String clj = "(defn " + fname;
		
		ArrayList<String> set = new ArrayList<String>();
		String get = null;
		
		for (Method method : taskClass.getMethods()) {
			
			if (!method.getDeclaringClass().equals(taskClass))
				continue;
			
			if (method.getName().startsWith("set")) {
				set.add(method.getName().substring(3));
			} else if (method.getName().startsWith("get")) {
				get = method.getName().substring(3);
			}
		}
		
		/*
		 * add description: nothing for now, may want to scan annotations later
		 */
		clj += "\n\t\"\"";
		
		/*
		 * add parameters
		 */
		clj += "\n\t[";
		for (int i = 0; i < set.size(); i++) {
			clj += 
				(i > 0 ? " " : "") + 
				Character.toLowerCase(set.get(i).charAt(0)) + 
				set.get(i).substring(1);
		}
		clj += "]";
		
		/*
		 * main code: construct initialized instance
		 */
		clj += "\n\t(. (doto (new " + taskClass.getCanonicalName() + ")";
		
		/*
		 * pass parameters
		 */
		for (int i = 0; i < set.size(); i++) {
			clj += 
				"\n\t\t(.set" +
				set.get(i) + 
				" " +
				Character.toLowerCase(set.get(i).charAt(0)) + 
				set.get(i).substring(1) +
				")";
		}
		
		/* 
		 * invoke run() and close doto
		 */
		clj += "\n\t\t(.run (tl/get-session)))";
		
		/*
		 * invoke result getter on constructed object and close
		 */
		clj += "\n\tget" + get + "))";
		
		/*
		 * eval the finished method
		 */
		eval(clj);
	}
	
}
