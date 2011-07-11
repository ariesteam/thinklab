package org.integratedmodelling.thinklab.webapp.listeners;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.http.ThinkWeb;
import org.integratedmodelling.thinklab.http.ThinklabWebSession;
import org.integratedmodelling.thinklab.http.application.ThinklabWebApplication;

public class SessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent sessionEvent) {
		/*
		 * register session with Thinkcap
		 */
		try {
			ThinkWeb.get().instrumentSession(sessionEvent.getSession());
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		
		/*
		 * get the model - if any - and call persistence hookups.
		 */
		ThinklabWebSession sess = 
			ThinkWeb.getThinkcapSessionFromHttpSession(sessionEvent.getSession());		

		ThinklabWebApplication app = sess.getApplication();
		if (app != null)
			app.notifyUserDisconnected(sess);

		if (sess.getUserModel() != null ) {
			
			/*
			 * TODO notify user model 
			 */
		}
	}

}
