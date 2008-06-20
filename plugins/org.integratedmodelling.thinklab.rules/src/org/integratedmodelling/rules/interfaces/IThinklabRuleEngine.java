/**
 * IThinklabRuleEngine.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabRulePlugin.
 * 
 * ThinklabRulePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabRulePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.rules.interfaces;

import org.integratedmodelling.rules.exceptions.ThinklabRuleEngineException;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.ISession;

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
