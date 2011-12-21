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
package org.integratedmodelling.thinklab.interpreter;

import java.util.Hashtable;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.runtime.IInterpreter;
import org.integratedmodelling.thinklab.api.runtime.IInterpreterManager;
import org.integratedmodelling.thinklab.api.runtime.ISession;

public class InterpreterManager implements IInterpreterManager {

	private static IInterpreterManager _this = null;

	// binds a language type to an interpreter
	Hashtable<String, IInterpreter> interpreterFactory = new Hashtable<String, IInterpreter>();

	Hashtable<String, String> interpreterClass = new Hashtable<String, String>();
	
	// binds a session ID to an interpreter
	Hashtable<String, IInterpreter> interpreters = new Hashtable<String, IInterpreter>();
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interpreter.IInterpreterManager#getInterpreter(org.integratedmodelling.thinklab.literals.Expression)
	 */
	@Override
	public IInterpreter getInterpreter(IExpression algorithm) throws ThinklabResourceNotFoundException {
		
		class AlgMatcher implements ConceptVisitor.ConceptMatcher {

			Hashtable<String, IInterpreter> hash;

			public IInterpreter plugin = null;

			public boolean match(IConcept c) {
				plugin = hash.get(c.toString());
				return plugin != null;
			}

			public AlgMatcher(Hashtable<String, IInterpreter> h) {
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

		IInterpreter plu = matcher.plugin;
		
		if (plu == null) {
			throw new ThinklabResourceNotFoundException(
					"no language interpreter plugin installed for " + c);
		}
		
		return plu;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interpreter.IInterpreterManager#newInterpreter(java.lang.String)
	 */
	@Override
	public IInterpreter newInterpreter(String language) throws ThinklabException {
		
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
		
		IInterpreter ret = null;
		
		try {
			ret = (IInterpreter) clazz.newInstance();
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
	public void registerInterpreter(String semanticType, IInterpreter interpreter) {
		interpreters.put(semanticType, interpreter);			
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interpreter.IInterpreterManager#getInterpreter(org.integratedmodelling.thinklab.literals.Expression, org.integratedmodelling.thinklab.api.runtime.ISession)
	 */
	@Override
	public IInterpreter getInterpreter(IExpression algorithm, ISession session) throws ThinklabResourceNotFoundException {

		IInterpreter ret = interpreters.get(session.getSessionID());
		
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
	

	public static IInterpreterManager get() {

		if (_this == null) {
			_this = new InterpreterManager();
		}
		return _this;
		
	}
}
