/**
 * AlgorithmInterpreterFactory.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.interpreter;

import java.util.Hashtable;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.extensions.Interpreter;

public class InterpreterManager {

	private static InterpreterManager AIF = null;

	// binds a language type to an interpreter
	Hashtable<String, Interpreter> interpreterFactory = new Hashtable<String, Interpreter>();

	Hashtable<String, String> interpreterClass = new Hashtable<String, String>();
	
	// binds a session ID to an interpreter
	Hashtable<String, Interpreter> interpreters = new Hashtable<String, Interpreter>();
	
	private Interpreter getInterpreter(IValue algorithm) throws ThinklabResourceNotFoundException {
		
		class AlgMatcher implements ConceptVisitor.ConceptMatcher {

			Hashtable<String, Interpreter> hash;

			public Interpreter plugin = null;

			public boolean match(IConcept c) {
				plugin = hash.get(c.toString());
				return plugin != null;
			}

			public AlgMatcher(Hashtable<String, Interpreter> h) {
				hash = h;
			}
		}
		
		IConcept c = algorithm.getConcept();

		AlgMatcher matcher = new AlgMatcher(interpreterFactory);
		IConcept cc = ConceptVisitor.findMatchUpwards(matcher, c);

		if (cc == null) {
			throw new ThinklabResourceNotFoundException(
					"no language interpreter can be identified for " + c);
		}

		Interpreter plu = matcher.plugin;
		
		if (plu == null) {
			throw new ThinklabResourceNotFoundException(
					"no language interpreter plugin installed for " + c);
		}
		
		return plu;
	}
	
	public Interpreter newInterpreter(String language) throws ThinklabException {
		
		String iclass = interpreterClass.get(language);
		
		if (iclass == null)
			throw new ThinklabValidationException(
					"no interpreter registered for language " + language);
		
		Class<?> clazz = null;
		
		try {
			clazz = Class.forName(iclass);
		} catch (ClassNotFoundException e) {
			throw new ThinklabValidationException(e);
		}
		
		Interpreter ret = null;
		
		try {
			ret = (Interpreter) clazz.newInstance();
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		return ret;
	}
	
	public void registerInterpreter(String language, String interpreterClass) {
		this.interpreterClass.put(language, interpreterClass);			
	}
	
	/**
	 * Bind an algorithm concept to a plugin ID. The plugin must be an InterpreterPlugin and
	 * is used to generate the interpreter for an algorithm of this class.
	 * @param semanticType The class of the algorithm (language interpreted).
	 * @param pluginID the name of the InterpreterPlugin that handles it.
	 */
	public void registerInterpreter(String semanticType, Interpreter interpreter) {
		interpreters.put(semanticType, interpreter);			
	}
	
	/**
	 * Retrieve interpreter for given algorithm, using interpreter registry and class
	 * of algorithm. The same interpreter may be returned for the same session, as it is
	 * a Thinklab requirement that operations in the same session are synchronized.
	 * The IValue containing the algorithm as a string. May have been
	 * validated or not. The specific IConcept linked to the string will be used to
	 * select the interpreter.
	 * @param algorithm 
	 * @param session
	 * @return
	 */
	public Interpreter getInterpreter(IValue algorithm,
			ISession session) throws ThinklabResourceNotFoundException {

		Interpreter ret = interpreters.get(session.getSessionID());
		
		if (ret != null)
			return ret;
		
		ret = getInterpreter(algorithm);

		if (ret == null)  {
			throw new ThinklabResourceNotFoundException(
					"interpreter creation for " + algorithm.getConcept() + " failed");
		}

		interpreters.put(session.getSessionID(), ret);
		
		return ret;
	}
	
	/**
	 * Call this if you want the interpreter to be renewed within the same
	 * session.
	 * 
	 * @param session
	 */
	public void deleteInterpreter(ISession session) {
		
		if (interpreters.containsKey(session.getSessionID())) {
			interpreters.remove(session.getSessionID());
		}
	}

	public static InterpreterManager get() {

		if (AIF == null) {
			AIF = new InterpreterManager();
		}
		return AIF;
		
	}
}
