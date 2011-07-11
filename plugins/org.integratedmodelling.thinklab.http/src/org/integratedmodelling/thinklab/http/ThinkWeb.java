package org.integratedmodelling.thinklab.http;

import java.io.File;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.integratedmodelling.exceptions.ThinklabException;
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
