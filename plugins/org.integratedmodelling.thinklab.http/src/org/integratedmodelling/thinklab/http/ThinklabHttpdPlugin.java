/*
 * 
 */
package org.integratedmodelling.thinklab.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.http.application.ThinklabWebApplication;
import org.integratedmodelling.thinklab.http.extensions.WebApplication;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.MiscUtilities;
import org.java.plugin.PluginLifecycleException;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;

public class ThinklabHttpdPlugin extends ThinklabPlugin {

	public class WebApplicationHandler implements AnnotatedClassHandler {

		@Override
		public void process(Annotation annotation, Class<?> cls, ThinklabPlugin plugin)
				throws ThinklabException {
			
			
			ThinklabWebApplication webapp = null;
			WebApplication wdesc = (WebApplication) annotation;

			try {
				webapp = (ThinklabWebApplication) cls.newInstance();
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}
			
			webapp.initialize(plugin, wdesc);
			
			/*
			 * store webapp with plugin. Do not publish until started.
			 */
			applications.put(wdesc.name(), webapp);

			logger().info(
					"registered web application " + wdesc.name() +
					" from plugin " + plugin.getDescriptor().getId());

		}

	}

	private Server server = null;
	
	private HashMap<Integer, Server> servers = 
		new HashMap<Integer, Server>();
	
	private HashMap<String, ThinklabWebApplication> applications = 
		new HashMap<String, ThinklabWebApplication>();
	
	private int minPort = 8060, maxPort = 8079;
	
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

	public void stopServer(int port) {

		if (servers.get(port) != null)
			try {
				log.info("stopping Jetty server on port " + port);
				servers.get(port).stop();
			} catch (Exception e) {
				throw new ThinklabRuntimeException(e);
			} finally {
				servers.remove(port);
			}
	}

	public ThinklabWebApplication publishApplication(String name, Server server) 
		throws ThinklabException {

		ThinklabWebApplication webapp = applications.get(name);
		
		if (webapp == null)
			throw new ThinklabResourceNotFoundException(
					"application " + name + " has not been registered");
		
		webapp.publish(ThinkWeb.get().getWebSpace(), server);
		
		return webapp;
	}
	
	public Server startServer(String host, int port) throws ThinklabException {
		
		if (port < 0) {
			for (int pp = minPort; pp <= maxPort; pp++) {
				if (servers.get(pp) == null) {
					port = pp;
					break;
				}
			}
		}
		
		if (port /* still */ < 0) {
			throw new ThinklabResourceNotFoundException(
					"no port available in range " + minPort + "-" + maxPort +
					"; please use the -p switch");
		}
		
		log.info("starting Jetty server on " + host + ":" + port);
		
		/*
		 * do all preparatory chores before starting the server
		 */
		ThinkWeb.get().setBaseUrl("http://" + host + ":" + port);
		
		if (server != null)
			throw new ThinklabException("thinkcap server is already active");
		
		Server serv = new Server(); 
		SelectChannelConnector connector = new SelectChannelConnector(); 
		connector.setHost(host);
		connector.setPort(port); 
		serv.setConnectors (new Connector[]{connector}); 
		
		WebAppContext wah = new WebAppContext(); 
		wah.setContextPath("/"); 
		wah.setWar(ThinkWeb.get().getWebSpace().toString()); 
		
		ClassLoader cl = this.getClass().getClassLoader(); 
		WebAppClassLoader wacl;
		try {
			wacl = new WebAppClassLoader(cl, wah);
			wah.setClassLoader(wacl); 
			serv.addHandler(wah); 
			serv.setStopAtShutdown(true);
			serv.start();		
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);
		} 
		
		servers.put(port, serv);
		
		return serv;
	}

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		
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

		/*
		 * set up things to register webapps through annotations
		 */
		registerAnnotatedClass(
				ThinklabWebApplication.class, WebApplication.class, 
				"webapps", new WebApplicationHandler());
		
	}

	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	public ThinklabWebApplication getApplicationForURL(String requestURI) {
		String appName = MiscUtilities.getURLBaseName(requestURI);
		return applications.get(appName);
	}
}
