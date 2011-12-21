/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.clojure;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.runtime.IInterpreter;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.utils.MiscUtilities;

import clojure.lang.Compiler;
import clojure.lang.LineNumberingPushbackReader;
import clojure.lang.LispReader;
import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public class ClojureInterpreter implements IInterpreter {

	InputStream input = System.in;
	OutputStream output = System.out;
	OutputStream error = System.err;
	private ISession session;
	
	private URL currentSource = null;
	
	public interface FormListener {
		public abstract void onFormEvaluated(Object retval, String formCode);
	}
 	
	public Object evalInNamespace(Object code, String namespace) throws ThinklabException {
		
		return evalRaw(code, namespace, null);
	}

	@Override
	public Object eval(Object code, HashMap<String, Object> args)
			throws ThinklabException {
		
		return evalRaw(code, session == null ? "user" : session.getSessionWorkspace(), args);
	}
	
	
	/**
	 * Returns whatever URL is being read while executing loadBindings, which
	 * is synchronized. 
	 * 
	 * @return
	 */
	public URL getCurrentSource() {
		return currentSource;
	}
	
	
	private synchronized Symbol newGlobalSymbol(String ns) {
		return Symbol.intern(ns);
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

		File mf = MiscUtilities.resolveUrlToFile(source.toString());
		ClassLoader clsl = Thread.currentThread().getContextClassLoader();

        try {
        	if (cloader != null) {
        		Thread.currentThread().setContextClassLoader(cloader);
        	}
			Compiler.loadFile(mf.toString());
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		} finally {
			if (cloader != null) {
				Thread.currentThread().setContextClassLoader(clsl);
			}
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
	public Object eval(Object code) throws ThinklabException {   
    	return evalInNamespace(code, session == null ? "user" : session.getSessionWorkspace());    	
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
			throw new ThinklabValidationException(e);
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
