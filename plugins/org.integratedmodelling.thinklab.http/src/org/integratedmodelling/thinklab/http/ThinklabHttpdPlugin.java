/*
 * 
 */
package org.integratedmodelling.thinklab.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.java.plugin.PluginLifecycleException;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;

public class ThinklabHttpdPlugin extends ThinklabPlugin {

	private Server server = null;
	
	public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.http";
	
	public static ThinklabHttpdPlugin get() {
		
		ThinklabHttpdPlugin ret = null;
		try {
			ret = (ThinklabHttpdPlugin) ThinkWeb.get().getPluginManager().getPlugin(PLUGIN_ID);
		} catch (PluginLifecycleException e) {
			// screw it
		}
		return ret;
	}
	
	public Log logger() {
		return log;
	}
	

	public void stopServer() {

		if (server != null)
			try {
				log.info("stopping Jetty server");
				server.stop();
			} catch (Exception e) {
				throw new ThinklabRuntimeException(e);
			} finally {
				server = null;
			}
	}

	public void startServer(String host, int port) throws ThinklabException {
		
		log.info("starting Jetty server on " + host + ":" + port);
		
		/*
		 * do all preparatory chores before starting the server
		 */
		ThinkWeb.get().setBaseUrl("http://" + host + ":" + port);
		
		if (server != null)
			throw new ThinklabException("thinkcap server is already active");
		
		server = new Server(); 
		SelectChannelConnector connector = new SelectChannelConnector(); 
		connector.setHost(host);
		connector.setPort(port); 
		server.setConnectors (new Connector[]{connector}); 
		
		WebAppContext wah = new WebAppContext(); 
		wah.setContextPath("/"); 
		wah.setWar(ThinkWeb.get().getWebSpace().toString()); 
		
		ClassLoader cl = this.getClass().getClassLoader(); 
		WebAppClassLoader wacl;
		try {
			wacl = new WebAppClassLoader(cl, wah);
			wah.setClassLoader(wacl); 
			server.addHandler(wah); 
			server.setStopAtShutdown(true);
			server.start();
//			server.join();
			
		} catch (Exception e) {
			
			throw new ThinklabInternalErrorException(e);
		} 
	}

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		
		// TODO Auto-generated method stub
		/*
		 * we don't go very far without this, so do it anyway
		 */
		requirePlugin("org.integratedmodelling.thinklab.core");
		
		/*
		 * tell thinklab to generate ThinkcapSessions
		 */
		KnowledgeManager.get().setSessionManager(new ThinklabWebSessionManager());
		
		ThinkWeb.get().setPluginManager(getManager());
		
		/*
		 * recover path of webapp in plugin dir. FIXME there must be a better way, and
		 * if not, at least put this in a method.
		 */
		String lf = getDescriptor().getLocation().getFile();
		try {
			lf = URLDecoder.decode(lf.substring(0, lf.lastIndexOf("/")), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ThinklabValidationException(e);
		}
		
		ThinkWeb.get().setWebSpace(new File(lf + "/webapp"));

	}

	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
}
