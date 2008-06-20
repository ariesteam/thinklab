/**
 * JessRuleEngine.java
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
package org.integratedmodelling.rules;

import jess.Rete;

import org.integratedmodelling.rules.exceptions.ThinklabRuleEngineException;
import org.integratedmodelling.rules.interfaces.IThinklabRuleEngine;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.ISession;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.SWRLJessBridge;

/**
 * A rule engine, with its own SWRL bridge, connected to the main owl model. Oh how do I hope that
 * this bridge stuff is reentrant.According to the documentation, several bridges can coexist but
 * they should not modify the underlying model. Seems fair enough, and if we confine any new
 * knowledge we create to the user session, we should be able to avoid conflicts.
 * 
 * @author Ferdinando Villa
 *
 */
public class JessRuleEngine implements IThinklabRuleEngine {
	
	SWRLJessBridge bridge = null;
	
	JessRuleEngine(OWLModel owlModel) throws ThinklabRuleEngineException {
		
		/* the actual rule engine */
		Rete rete = new Rete();

		try {
			this.bridge = new SWRLJessBridge(owlModel, rete);
		} catch (SWRLRuleEngineBridgeException e) {
			throw new ThinklabRuleEngineException(e);
		} 	
		
	}

	public void reset() throws ThinklabRuleEngineException {
		
		try {
			bridge.resetBridge();
		} catch (SWRLRuleEngineBridgeException e) {
			throw new ThinklabRuleEngineException(e);
		}
	}

	public void run(IKBox kbox, ISession session) {
		// TODO Auto-generated method stub
		
	}

	public void run(IKBox kbox, ISession session, Constraint filter) {
		// TODO Auto-generated method stub
		
	}


}
