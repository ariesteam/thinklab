/**
 * ThinklabAgent.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabAgentPlugin.
 * 
 * ThinklabAgentPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabAgentPlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.agents.agents;

import jade.core.Agent;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.ISession;

/**
 * A ThinklabAgent is an agent with an OWL counterpart. Retrieve agents from a kbox and they
 * will automatically register with the KM in this virtual machine. 
 * 
 * @author Ferdinando Villa
 *
 */
public class ThinklabAgent extends Agent implements IInstanceImplementation {

	private static final long serialVersionUID = -5989211712711450711L;
	ISession session = null;

	public ThinklabAgent(ISession session) {
		this.session = session;
	}
	
	protected void setup() {
	}
	
	protected void takeDown() {
		/* clear session */
	}

	public void initialize(IInstance i) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	public void validate(IInstance i) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}
}
