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
package org.integratedmodelling.groovy;

import groovy.lang.GroovyShell;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.extensions.LanguageInterpreter;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeProvider;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.AlgorithmValue;
import org.integratedmodelling.thinklab.literals.Value;

public class GroovyInterpreter implements LanguageInterpreter {

	
	public IValue execute(AlgorithmValue codeval, LanguageInterpreter.IContext context) throws ThinklabException {
		
		IValue ret = null;
		
		GroovyShell shell = new GroovyShell(((GroovyContext)context).getBinding());

		Object result = null;
		
		try {
			result = shell.evaluate(codeval.toString());
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
			if (result == null)
			return ret;
		
		/* reparse result into IValue */
		if (result instanceof Value) {
			ret = (IValue)result;
		} else {
			
			try {
				ret = Value.getValueForObject(result);
			} catch (ThinklabValidationException e) {
			
				/* TODO nontrivial conversions */
				if (result instanceof IInstance) {
					
				}
			}
		}
		
		return ret;
	}
	
	public void initialize(IKnowledgeProvider km) {
		// TODO Auto-generated method stub

	}

	@Override
	public IContext getNewContext(ISession session) {
		GroovyContext ctx = new GroovyContext();
		ctx.getBinding().setVariable("session", session);
		return ctx;
	}

}
