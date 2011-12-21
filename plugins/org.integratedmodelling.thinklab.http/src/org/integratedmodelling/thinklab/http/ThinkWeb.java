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
package org.integratedmodelling.thinklab.http;

import java.io.File;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.http.application.ThinklabWebApplication;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;


/**
 * A global register for anything that the thinkcap plugins send our way. Should
 * disappear.
 */
public class ThinkWeb {

	public static final String THINKCAP_SESSION_PROPERTY = "thinkcap.session.thinkcapsession";
	public static ThinkWeb _this;
	
//	private PluginManager pluginManager = null;
	private HashMap<String, ThinklabWebApplication> applications =
		new HashMap<String, ThinklabWebApplication>();

	private File serverWebSpace = null;
	private String baseUrl = "http://127.0.0.1:8080";
	
	public static ThinkWeb get() {
		
		if (_this == null)
			_this = new ThinkWeb();
		return _this;
	}
	
	void setWebSpace(File ws) {
		ws.mkdirs();
		serverWebSpace = ws;
	}
	
	void setBaseUrl(String s) {
		baseUrl = s;
	}

	public void registerThinkcapApplication(ThinklabWebApplication app) {
		applications.put(app.getId(), app);
	}
	
	public File getWebSpace() {
		return serverWebSpace;
	}
	
	public ThinklabWebSession instrumentSession(HttpSession session) throws ThinklabException {
		
		ThinklabWebSession tlsession = new ThinklabWebSession();
		session.setAttribute(THINKCAP_SESSION_PROPERTY, tlsession);
		tlsession.initialize(session);
		
		/*
		 * TODO lookup authentication cookies and set session appropriately
		 */
		
		return tlsession;
	}
	
	/**
	 * Retrieve the thinkcap session associated with the passed http session
	 * @param session
	 * @return
	 */
	public static ThinklabWebSession getThinkcapSessionFromHttpSession(HttpSession session) {
		
		return session == null ? 
				null :
				(ThinklabWebSession)session.getAttribute(ThinkWeb.THINKCAP_SESSION_PROPERTY);
	}

	
	/**
	 * To be used in XUL scripts and actions to retrieve everything about the current Thinkcap session
	 * @param zSession
	 * @return
	 */
	static public ThinklabWebSession getThinkcapSession(Session zSession) {
		return getThinkcapSessionFromHttpSession((HttpSession) zSession.getNativeSession());
	}
	
	/**
	 * This should return the Thinkcap session handled by the calling thread.
	 * @return
	 */
	static public ThinklabWebSession getCurrentThinkcapSession() {
		return getThinkcapSession(Sessions.getCurrent());
	}

	public String getBaseUrl() {
		return baseUrl;
	}


}
