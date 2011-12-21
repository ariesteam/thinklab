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
package org.integratedmodelling.rules.interfaces;

import org.integratedmodelling.rules.exceptions.ThinklabRuleEngineException;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

public interface IThinklabRuleEngine {

	/**
	 * Reset the rule engine, deleting all rules and 
	 * @throws ThinklabRuleEngineException
	 */
	public abstract void reset() throws ThinklabRuleEngineException;

	/**
	 * Run all the rules in a kbox; set results in passed session.
	 * @param kbox
	 * @param session
	 */
	public abstract void run(IKBox kbox, ISession session);
	
	/**
	 * Run all the rules in a kbox, pre-screened through a constraint to determine if the
	 * rules apply. This way we can use an efficient, DB-based kbox architecture to ensure
	 * that no useless rules are triggered.
	 * 
	 * @param kbox
	 * @param session
	 * @param filter
	 */
	public abstract void run(IKBox kbox, ISession session, Constraint filter);
}
