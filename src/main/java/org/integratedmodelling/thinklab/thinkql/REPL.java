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
package org.integratedmodelling.thinklab.thinkql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.interpreter.ModelGenerator;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.lang.IResolver;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.proxy.ModellingModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class REPL {

	private static final String USER_DEFAULT_NAMESPACE = "user";
	private InputStream input = System.in;
	private OutputStream output = System.out;
	public void run(String[] args) throws Exception {

		/*
		 * Prepare interpreter
		 */
		Injector injector = Guice.createInjector(new ModellingModule());
		ModelGenerator mg = injector.getInstance(ModelGenerator.class);
		
		PrintStream w = new PrintStream(output);
		w.println("*** Thinklab QL interpreter; enter expressions, 'exit' exits ***");

	
			String prompt = "tql> ";
			String curStat = "";
			
			for (;;) {
				
				w.print(prompt);
				String statement = readStatement().trim();
				
				if (statement == null || statement.equals("exit")) {
					w.println();
					break;
				}
				
				if (statement.isEmpty()) {
					prompt = "";
					continue;
				}

				if (statement.startsWith("/")) {
					/*
					 * TODO exec regular command
					 */
					continue;
				}

				if (!statement.endsWith(";")) {
					prompt = "> ";
					curStat += statement;
					continue;
				} else {
					curStat = statement;
				}
				
				prompt = "tql>";
				
				/*
				 * Exec; behave according to what is defined
				 */

					IResolver resolver = ((ModelManager)Thinklab.get().getModelManager()).getInteractiveResolver(input, w);
					InputStream is = new ByteArrayInputStream(curStat.getBytes());
					INamespace ns = mg.parseInNamespace(is, USER_DEFAULT_NAMESPACE, resolver);
					is.close();
					
					IModelObject obj = null; 

					if (obj instanceof IModel) {
						
						/*
						 * model using the current context; 
						 */
//						IModel model = (IModel)obj;
//						Collection<IObservation> res = ((IModel) obj).observe(context, Thinklab.get().getDefaultKbox(), session);
//						
//						if (res.size() > 0) {
//							w.println("Model can be observed in " + res.size() + " different ways");
//							
//							/*
//							 * TODO extract, run and visualize n-th observation
//							 */
//							
//						} else {
//							w.println("There is no way to observe this model in the current context.");
//						}
					}
					
					w.println(obj + " returned");

				
				curStat = "";
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
}
