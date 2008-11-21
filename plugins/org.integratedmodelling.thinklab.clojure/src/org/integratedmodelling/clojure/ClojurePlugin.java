package org.integratedmodelling.clojure;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

import clojure.lang.RT;

public class ClojurePlugin extends ThinklabPlugin {

	static final String PLUGIN_ID = "org.integratedmodelling.thinklab.clojure";
	
	public static ClojurePlugin get() {
		return (ClojurePlugin) getPlugin(PLUGIN_ID );
	}

	
	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
				
		try {			
			logger().info("initializing Clojure runtime");
			RT.loadResourceScript("thinklab.clj");			
			logger().info("Clojure initialized successfully");
		} catch (Exception e) {
			throw new ThinklabPluginException(e);
		}
	}

	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
