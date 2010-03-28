package org.integratedmodelling.clojure;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabScriptException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.extensions.Interpreter;
import org.integratedmodelling.thinklab.interfaces.annotations.TaskNamespace;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.Value;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;

import clojure.lang.Compiler;
import clojure.lang.DynamicClassLoader;
import clojure.lang.LineNumberingPushbackReader;
import clojure.lang.LispReader;
import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public class ClojureInterpreter implements Interpreter {

	InputStream input = System.in;
	OutputStream output = System.out;
	OutputStream error = System.err;
	private ISession session;
	
	private synchronized Symbol newGlobalSymbol(String ns) {
		return Symbol.intern(ns);
	}
	
	public IValue evalInNamespace(Object code, String namespace) throws ThinklabException {
		
		Object ret = evalRaw(code, namespace, null);
		return ret == null ? null : Value.getValueForObject(ret);
	}

	@Override
	public IValue eval(Object code, HashMap<String, Object> args)
			throws ThinklabException {
		
		return Value.getValueForObject(
				evalRaw(code, session == null ? "user" : session.getSessionID(), args));
	}
	
	private synchronized void addRTClasspath(URL[] urls) throws ThinklabInternalErrorException {
		for (URL url : urls) {
			try {
				RT.addURL(url);
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}
		}
	}
	
	@Override
	public void addClasspath(URL[] urls) throws ThinklabException {
		addRTClasspath(urls);
	}
	
	@Override
	public void loadBindings(URL source, ClassLoader cloader) throws ThinklabException {
		
        try {
        	
        	DynamicClassLoader cl = null;
        	if (cloader != null) {
        		cl = RT.ROOT_CLASSLOADER;
        		RT.ROOT_CLASSLOADER = new DynamicClassLoader(cloader);
        	}
        		
			Compiler.loadFile(Escape.fromURL(source.getFile().toString()));
			
			if (cloader != null) {
				RT.ROOT_CLASSLOADER = cl;
			}
			
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
	public void defineTask(Class<?> taskClass, ClassLoader cloader) throws ThinklabException {
		
		/*
		 * FIXME this should be the ID of the declaring plugin by default
		 */
		String ns = "plugin";
		
		/*
		 * Create Clojure binding for passed task.
		 */
		if (taskClass.isInterface() || Modifier.isAbstract(taskClass.getModifiers()))
			return;
		
		/*
		 * lookup namespace if any
		 */
		for (Annotation annotation : taskClass.getAnnotations()) {
			if (annotation instanceof TaskNamespace) {
				ns = ((TaskNamespace)annotation).ns();
			}
		}
		
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
		 * FIXME
		 * sort the get() methods for predictability of the parameter order. Eventually
		 * we may want to use a map for an argument, instead of N args.
		 */
		Collections.sort(set);
		
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
		 * eval the finished method in given namespace
		 */
    	
    	DynamicClassLoader cl = null;
    	if (cloader != null) {
    		cl = RT.ROOT_CLASSLOADER;
    		RT.ROOT_CLASSLOADER = new DynamicClassLoader(cloader);
    	}
    	
		evalInNamespace(clj, ns);
		
		if (cloader != null) {
			RT.ROOT_CLASSLOADER = cl;
		}
	}

	@Override
	public IValue eval(Object code) throws ThinklabException {   
    	return evalInNamespace(code, session == null ? "user" : session.getSessionID());    	
	}

	public Object evalRaw(Object code, String namespace, HashMap<String, Object> args) throws ThinklabException {
		
		if (namespace == null)
			namespace = "user";
		
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
		
		final Symbol TL = Symbol.intern("tl");
		final Symbol CLOJURE = Symbol.intern("clojure.core");

		final Var refer = RT.var("clojure.core", "refer");
		final Var ns = RT.var("clojure.core", "*ns*");
		final Var star1 = RT.var("clojure.core", "*1");
		final Var star2 = RT.var("clojure.core", "*2");
		final Var star3 = RT.var("clojure.core", "*3");
		final Var stare = RT.var("clojure.core", "*e");
		final Var sess  = RT.var("tl", "*session*");

		final Namespace CUSTOM_NS = 
				Namespace.findOrCreate(newGlobalSymbol(namespace));		

		Object ret = null;
		
		try {

			Var.pushThreadBindings(
				RT.map(
					//RT.USE_CONTEXT_CLASSLOADER, RT.T, 
					ns, CUSTOM_NS, 
					star1, null,
					star2, null, 
					star3, null, 
					stare, null, 
					sess, this.session));

			if (args != null)
				for (String arg : args.keySet()) {
					final Var vz = RT.var(CUSTOM_NS.toString(), arg);
					Var.pushThreadBindings(RT.map(vz, args.get(arg)));
				}
			
			refer.invoke(CLOJURE);
			refer.invoke(TL);
			
			LineNumberingPushbackReader rdr = new LineNumberingPushbackReader(
					new InputStreamReader(inp, RT.UTF8));
			
			Object EOF = new Object();

			for (;;) {
				
				try {
					Object r = LispReader.read(rdr, false, EOF, false);
					if (r == EOF) {
						break;
					}
					ret = Compiler.eval(r);
					star3.set(star2.get());
					star2.set(star1.get());
					star1.set(ret);
					
				} catch (Exception e) {
					stare.set(e);
					throw e;
				}
			}
		} catch (Exception e) {
			throw new ThinklabScriptException(e);
		} finally {
			Var.popThreadBindings();
		}
		
		/*
		 * FIXME remove
		 */
		// System.out.println("EXECUTED: [" + namespace + "] " + code);
		
		// TODO Auto-generated method stub
		return ret;
	}
	
}
