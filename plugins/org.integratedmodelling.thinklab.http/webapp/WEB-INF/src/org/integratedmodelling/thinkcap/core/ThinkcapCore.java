package org.integratedmodelling.thinkcap.core;

import javax.servlet.http.HttpSession;

import org.integratedmodelling.thinkcap.Thinkcap;
import org.integratedmodelling.thinkcap.server.ThinkcapServer;
import org.java.plugin.boot.Application;
import org.java.plugin.boot.ApplicationPlugin;
import org.java.plugin.util.ExtendedProperties;

public class ThinkcapCore extends ApplicationPlugin implements Application {

	public static final String PLUGIN_ID = "org.integratedmodelling.thinkcap.webapp";
		
	@Override
	protected void doStart() throws Exception {
		
		System.out.println("CKORROOKA A KK {PHF");

	}

	@Override
	protected void doStop() throws Exception {
	}
	

	@Override
	protected Application initApplication(ExtendedProperties arg0, String[] arg1)
			throws Exception {
		
		/*
		 * load all necessary plugins according to configuration
		 */
		
		/*
		 * load a plugin that can give us a server to start later
		 */
		getManager().activatePlugin("org.integratedmodelling.thinkcap.server");

		return this;
	}

	@Override
	public void startApplication() throws Exception {
		

		
		/*
		 * set up environment and logging
		 */
		
		/*
		 * fire up jetty with thinklab in it
		 */
		((ThinkcapServer) getManager().getPlugin("org.integratedmodelling.thinkcap.server")).
			startServer(8080);
		
		/*
		 * 
		 */
	}
}