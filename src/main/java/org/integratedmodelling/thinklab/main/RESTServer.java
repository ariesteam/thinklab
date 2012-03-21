package org.integratedmodelling.thinklab.main;

import java.io.File;
import java.util.ArrayList;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.commandline.Shell;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * A simple command-line driven knowledge manager. Just run and type 'help'.
 * @author Ferdinando Villa
 */
public class RESTServer  {

	private ArrayList<String> scriptFiles = 
		new ArrayList<String>();
	
	public ISession session;
	
	protected void initApplication(String[] args) {

//		scriptFiles.addAll(LocalConfiguration.getProfileScripts());
		
		for (String s : args) {
			
			// FIXME?
			// temporary fix - the first arg in cl is the main class name, but
			// that doesn't apply when running through the JVM API so just 
			// starting from 1 would require some inelegant CL hacks.
			if (s.startsWith("org.") && !new File(s).exists())
				continue;
			
			scriptFiles.add(s);
		}
	}

	public void startServer(int port) throws Exception {
		
		boolean hasScript = false;
		ISession session = null;
		for (String s : scriptFiles) {
			
			if (session == null)
				session = new Session();
			
			if (!hasScript && MiscUtilities.getFileName(s).startsWith("."))
				hasScript = true;
			
			Shell.runScript(s, session);
		}
		
		
		RESTManager.get().start(port);
			
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {

		Thinklab.boot();
		
		String ept = System.getenv("THINKLAB_REST_PORT");
		if (ept == null)
			ept = "8182";
		
		/*
		 * TODO process command line options
		 */
		
		final int port = Integer.parseInt(ept);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					RESTManager.get().stop(port);
				} catch (ThinklabException e) {
					throw new ThinklabRuntimeException(e);
				}
				Thinklab.shutdown();
			}
		});
		
		RESTServer shell = new RESTServer();
		shell.initApplication(args);
		shell.startServer(port);
	}
}