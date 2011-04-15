package org.integratedmodelling.thinklab.http;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.http.application.ThinklabWebApplication;
import org.integratedmodelling.utils.MiscUtilities;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;


/*
 * A global register for anything that the thinkcap plugins send our way.
 */
public class ThinkWeb {

	public static final String THINKCAP_SESSION_PROPERTY = "thinkcap.session.thinkcapsession";
	public static ThinkWeb _this;
	
	private PluginManager pluginManager = null;
	private HashMap<String, ThinklabWebApplication> applications =
		new HashMap<String, ThinklabWebApplication>();

	private File serverWebSpace = null;
	private String baseUrl = "http://127.0.0.1:8080";
	
	public static ThinkWeb get() {
		
		if (_this == null)
			_this = new ThinkWeb();
		return _this;
	}
	
	void setPluginManager(PluginManager pm) {
		pluginManager = pm;
	}
	
	void setWebSpace(File ws) {
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
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	public ThinklabWebApplication getApplication(String appname) {
		return applications.get(appname);
	}
	
	public Collection<ThinklabWebApplication> getApplications() {
		/*
		 * TODO these would be nice sorted, as they're mostly used for display
		 */
		return applications.values();
	}
	
	public ThinklabWebSession instrumentSession(HttpSession session) throws ThinklabException {
		
		ThinklabWebSession tlsession = (ThinklabWebSession) KnowledgeManager.get().requestNewSession();
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

	public ThinklabWebApplication publishApplication(String requestURI) throws ThinklabException {

		String appName = MiscUtilities.getURLBaseName(requestURI);
		ThinklabWebApplication app = applications.get(appName);
		
		if (app == null) {
			throw new ThinklabException("no application named " + appName + " has been registered");	
		}
		
		app.publish(getWebSpace());
		
		return app;
	}
	
	/**
	 * Ensure that all plugins declaring applications are activated before the server starts.
	 * 
	 * @throws PluginLifecycleException
	 */
	public void publishApplications() throws ThinklabException {
		
		ExtensionPoint toolExtPoint = 
			pluginManager.getRegistry().
				getExtensionPoint("org.integratedmodelling.thinkcap.core", "thinkcap-application");
		
		for (Iterator<Extension> it =  toolExtPoint.getConnectedExtensions().iterator(); it.hasNext(); ) {
			Extension ext = it.next();
			try {
				pluginManager.activatePlugin(ext.getDeclaringPluginDescriptor().getId());
			} catch (Exception e) {
				throw new ThinklabPluginException(e);
			}
		}
	}

	/**
	 * Bypass ZK's rigid handling of resource finding by
	 * making sure that all plugins that declare ZK resources are activated before the
	 * server is started. Activation will make all the ZK resources available by creating
	 * appropriate jars in the WEB-INF/lib directory.
	 * 
	 * @throws PluginLifecycleException 
	 */
	public void publishZKResources() throws PluginLifecycleException {
		
		ExtensionPoint toolExtPoint = 
			pluginManager.getRegistry().
				getExtensionPoint("org.integratedmodelling.thinkcap.core", "zk");
		
		for (Iterator<Extension> it =  toolExtPoint.getConnectedExtensions().iterator(); it.hasNext(); ) {
			Extension ext = it.next();
			pluginManager.activatePlugin(ext.getDeclaringPluginDescriptor().getId());
		}
	}

}
