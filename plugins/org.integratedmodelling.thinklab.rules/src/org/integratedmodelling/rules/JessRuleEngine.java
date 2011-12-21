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
package org.integratedmodelling.rules;

//import jess.Rete;

import org.integratedmodelling.rules.exceptions.ThinklabRuleEngineException;
import org.integratedmodelling.rules.interfaces.IThinklabRuleEngine;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

//import edu.stanford.smi.protegex.owl.model.OWLModel;
//import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
//import edu.stanford.smi.protegex.owl.swrl.bridge.jess.SWRLJessBridge;

/**
 * A rule engine, with its own SWRL bridge, connected to the main owl model. Oh how do I hope that
 * this bridge stuff is reentrant.According to the documentation, several bridges can coexist but
 * they should not modify the underlying model. Seems fair enough, and if we confine any new
 * knowledge we create to the user session, we should be able to avoid conflicts.
 * 
 * @author Ferdinando Villa
 * @since 7/16/08 turned off - I don't think we'll ever care about Jess. 
 */
public class JessRuleEngine implements IThinklabRuleEngine {
	
	//SWRLJessBridge bridge = null;
	
//	JessRuleEngine(OWLModel owlModel) throws ThinklabRuleEngineException {
//		
//		/* the actual rule engine */
//		Rete rete = new Rete();
//
//		try {
//			this.bridge = new SWRLJessBridge(owlModel, rete);
//		} catch (SWRLRuleEngineBridgeException e) {
//			throw new ThinklabRuleEngineException(e);
//		} 	
//		
//	}

	public void reset() throws ThinklabRuleEngineException {
//		
//		try {
//			bridge.resetBridge();
//		} catch (SWRLRuleEngineBridgeException e) {
//			throw new ThinklabRuleEngineException(e);
//		}
	}

	public void run(IKBox kbox, ISession session) {
		// TODO Auto-generated method stub
		
	}

	public void run(IKBox kbox, ISession session, Constraint filter) {
		// TODO Auto-generated method stub
		
	}


}
