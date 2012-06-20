package org.integratedmodelling.thinklab.main;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.commandline.GraphicalTQLShell;
import org.integratedmodelling.thinklab.commandline.Shell;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * A simple command-line driven knowledge manager. Just run and type 'help'.
 * @author Ferdinando Villa
 */
public class TQLShell  {

	private static final String GRAPHICAL_SHELL_PROPERTY = "commandline.graphical.shell";
	
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

	public void startApplication() throws Exception {
		
		boolean isGraphical = 
			BooleanValue.parseBoolean(
					Thinklab.get().getProperties().getProperty(
							GRAPHICAL_SHELL_PROPERTY, "true"));
		
		if (isGraphical && GraphicsEnvironment.isHeadless())
			isGraphical = false;
		
		boolean hasScript = false;
		ISession session = null;
		for (String s : scriptFiles) {
						
			if (!hasScript && MiscUtilities.getFileName(s).startsWith("."))
				hasScript = true;
			
			Shell.runScript(s, session);
		}
		
		if (hasScript)
			return;
		
		if (isGraphical) {			
			GraphicalTQLShell shell = new GraphicalTQLShell();
			shell.startConsole();
		} else {
			Shell shell = new Shell();
			shell.startConsole();
		}
	}
	
	public static void main(String[] args) throws Exception {

		Thinklab.boot();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Thinklab.shutdown();
			}
		});
		

		TQLShell shell = new TQLShell();
		shell.initApplication(args);
		shell.startApplication();
		Thinklab.shutdown();
	}
}