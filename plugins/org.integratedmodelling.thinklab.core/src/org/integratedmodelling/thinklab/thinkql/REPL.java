package org.integratedmodelling.thinklab.thinkql;

/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Common Public License 1.0 (http://opensource.org/licenses/cpl.php)
 *   which can be found in the file CPL.TXT at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Oct 18, 2007 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.interpreter.ModelGenerator;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.modelling.IAnnotation;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservationList;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.modelling.context.Context;
import org.integratedmodelling.thinklab.proxy.ModellingModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class REPL {

	private InputStream input = System.in;
	private OutputStream output = System.out;
	private ISession session = null;

	private IContext context = new Context();
	
	public void run(String[] args) throws Exception {

		/*
		 * Prepare interpreter
		 */
		Injector injector = Guice.createInjector(new ModellingModule());
		ModelGenerator mg = injector.getInstance(ModelGenerator.class);
		
		PrintWriter w = new PrintWriter(new OutputStreamWriter(output));
		w.println("*** Thinklab QL interpreter; enter expressions, 'exit' exits ***");

		try {
	
			for (;;) {
				w.write("tql> ");
				w.flush();
				String statement = readStatement().trim();
				
				if (statement == null || statement.equals("exit")) {
					w.write("\n");
					w.flush();
					break;
				}
				
				if (statement.isEmpty())
					continue;

				/*
				 * Exec; behave according to what is defined
				 */
				try {
					IModelObject obj = mg.evaluate(statement);

					if (obj instanceof IAnnotation) {
						/*
						 * add to current context
						 */
					} else if (obj instanceof IModel) {
						/*
						 * model using the current context; 
						 */
						IModel model = (IModel)obj;
						IObservationList res = ((IModel) obj).observe(context, Thinklab.get().getDefaultKbox(), session);
						
						if (res.size() > 0) {
							w.println("Model can be observed in " + res.size() + " different ways");
							this.context = res.resolve(0);
						} else {
							w.println("There is no way to observe this model in the current context.");
						}
					}
					
					w.println(obj + " returned");
				} catch (ThinklabException e) {
					w.println("*** ThinkQL error: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			w.println("*** ThinkQL error: " + e.getMessage());
		} finally {
		}
	}

	public String readStatement() {

		StringBuffer buff = new StringBuffer();

		while (true) {
			int ch;
			try {
				ch = input.read();
			} catch (IOException e) {
				return null;
			}
			buff.append((char)ch);
			if (ch == ';') {
				break;
			}
		}
		
		return buff.toString();
	}
	
	public void setInput(InputStream inputStream) {
		this.input = inputStream;
	}

	public void setOutput(OutputStream outputStream) {
		this.output = outputStream;
	}

	public void setSession(ISession session) {
		this.session = session;
	}
}
