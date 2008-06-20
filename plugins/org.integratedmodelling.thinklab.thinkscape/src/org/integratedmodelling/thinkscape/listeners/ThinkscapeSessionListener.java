/**
 * 
 */
package org.integratedmodelling.thinkscape.listeners;

import org.integratedmodelling.ograph.OGraph;
import org.integratedmodelling.ograph.ONode;
import org.integratedmodelling.policy.ApplicationFrame;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IThinklabSessionListener;
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
