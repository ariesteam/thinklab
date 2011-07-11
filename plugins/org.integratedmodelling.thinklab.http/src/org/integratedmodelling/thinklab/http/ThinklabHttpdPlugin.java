/*
 * 
 */
package org.integratedmodelling.thinklab.http;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.http.application.ThinklabWebApplication;
import org.integratedmodelling.thinklab.http.extensions.WebApplication;
import org.integratedmodelling.thinklab.http.utils.FileOps;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.MiscUtilities;
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

			logger().info("registered web application " + wdesc.name() +
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
		return (ThinklabHttpdPlugin) getPlugin(PLUGIN_ID);
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
				
		ThinkWeb.get().setWebSpace(new File(getScratchPath() + "/web"));

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

	/**
	 * ensure that all web resources are published under the main webspace.
	 * @throws ThinklabException 
	 */
	public void publishCommonResources() throws ThinklabException {

		File destination = ThinkWeb.get().getWebSpace();
		File source = 
			new File(getLoadDirectory() + File.separator + "webapp");
		
		logger().info("caching common web resources to web space: " + destination);
		
		int nres = 0;
		try {
			nres = FileOps.copyFilesCached(source, destination, null);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}

		logger().info("copied " + nres + " resource files");
		
	}
}
