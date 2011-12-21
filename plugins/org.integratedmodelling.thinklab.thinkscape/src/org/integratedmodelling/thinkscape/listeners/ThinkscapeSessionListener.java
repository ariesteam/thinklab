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
package org.integratedmodelling.thinkscape.listeners;

import org.integratedmodelling.ograph.OGraph;
import org.integratedmodelling.ograph.ONode;
import org.integratedmodelling.policy.ApplicationFrame;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.IThinklabSessionListener;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinkscape.ThinkScapeGUI;
import org.integratedmodelling.thinkscape.graph.GraphFactory;

/**
 * @author Sergey Krivov
 *
 */
public class ThinkscapeSessionListener implements IThinklabSessionListener {
	
	private ThinkScapeGUI thinkScapeGUI=null;
	 
	private GraphFactory graphFactory =null;
	
	
	public ThinkscapeSessionListener(){
		
		thinkScapeGUI= (ThinkScapeGUI)ApplicationFrame.getApplicationFrame().guiPolicy;
		thinkScapeGUI.fileReset();
		OGraph graph = thinkScapeGUI.getGraph(); 
		graphFactory = new GraphFactory(graph);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IThinklabSessionListener#objectCreated(org.integratedmodelling.thinklab.interfaces.IInstance)
	 */
	public void objectCreated(IInstance object)  throws ThinklabException {
		ONode n= graphFactory.buildInstance(object);
		thinkScapeGUI.getDisplay().setFocusNode(n);
		thinkScapeGUI.runUpdate();

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IThinklabSessionListener#objectDeleted(org.integratedmodelling.thinklab.interfaces.IInstance)
	 */
	public void objectDeleted(IInstance object)  throws ThinklabException {
		graphFactory.deleteInstance(object);
		thinkScapeGUI.runUpdate();
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IThinklabSessionListener#sessionCreated(org.integratedmodelling.thinklab.interfaces.ISession)
	 */
	public void sessionCreated(ISession session)  throws ThinklabException {
//		thinkScapeGUI= (ThinkScapeGUI)ApplicationFrame.getApplicationFrame().guiPolicy;
//		thinkScapeGUI.fileReset();
//		OGraph graph = thinkScapeGUI.getGraph(); 
//		graphFactory = new GraphFactory(graph);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IThinklabSessionListener#sessionDeleted(org.integratedmodelling.thinklab.interfaces.ISession)
	 */
	public void sessionDeleted(ISession session)  throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
