/**
 * APIOnlyKnowledgeInterface.java
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
package org.integratedmodelling.thinklab.impl;

import java.util.Map;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.interfaces.ICommandListener;
import org.integratedmodelling.thinklab.interfaces.ISessionManager;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.AlgorithmValue;

/**
 * Use this interface when the Knowledge Manager does not need any public interface, but is only accessed
 *  through the API. Supports one session related to the interface. API users can ask for how many other
 *  sessions as they want.
 * @author Ferdinando Villa
 * @author Ioannis N. Athanasiadis
 */
public class APIOnlyKnowledgeInterface implements ISessionManager, ICommandListener {

	String[] clArguments;
	ISession session;
	
    public APIOnlyKnowledgeInterface() {
    }

    public APIOnlyKnowledgeInterface(String[] arguments) {
    	// TODO do something with args
    	clArguments = arguments;
    }
    
    public void registerCommand(CommandDeclaration declaration)
            throws ThinklabException {
    }

    // TODO we do need this one. Must separate properties into their own class.
    public void importPreferences() {
    }

    // TODO we need this one.
    public void savePreferences() {
    }

    public void start() {
        // this won't relinquish control, so you can go ahead and do stuff after KM.initialize(),
    	// or derive another and put your stuff here (better)
    }


	public ISession createNewSession() throws ThinklabException {
		return new Session();
	}

	public void notifySessionDeletion(ISession session) {
		// TODO Auto-generated method stub
		
	}

	public ISession getCurrentSession() throws ThinklabException {
		
		if (session == null) {
			try {
				// create one session. This is sent back to us so it's a bit crooked. May deserve some
				// more thought, but not a big deal for now.
				session = KnowledgeManager.get().requestNewSession();
			} catch (ThinklabNoKMException e1) {
				e1.printStackTrace();
			} catch (ThinklabException e1) {
				e1.printStackTrace();
			}
		}
		return session;
	}

	public IValue execute(AlgorithmValue algorithm, ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue execute(AlgorithmValue algorithm, ISession session, Map<String, IValue> arguments) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
    
}
