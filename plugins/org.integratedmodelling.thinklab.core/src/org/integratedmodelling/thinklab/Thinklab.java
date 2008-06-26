package org.integratedmodelling.thinklab;

import java.io.File;
import java.net.URL;

import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

/**
 * Activating this plugin means loading the knowledge manager, effectively initializing the
 * Thinklab system.
 * 
 * @author Ferdinando Villa
 *
 */
public class Thinklab extends ThinklabPlugin {

	public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.core";
	
	public static Thinklab get() {
		return _this;
	}
	
	KnowledgeManager _km = null;

	// only for this plugin, very ugly, but we need to access logging etc. before doStart() has
	// finished and the plugin has been published.
	static Thinklab _this = null;
	
	public Thinklab() {
		_this = this;
	}
	
	@Override
	protected void preStart() throws ThinklabException {
		
		/*
		 * initialize global config from plugin properties before setConfiguration() is called
		 */
		URL config = getResourceURL("core.properties");
		
		if (config != null)
			LocalConfiguration.loadProperties(config);
		
		/*
		 * TODO we need to establish what knowledge repository and session manager to
		 * use. This must work in a general fashion.
		 */
		
		if (_km == null) {
			_km = new KnowledgeManager(
					new FileKnowledgeRepository(getLoadDirectory() + File.separator + "lib"));
			_km.setPluginManager(getManager());
			_km.initialize();
		}

	}

	public IKnowledgeRepository getKnowledgeRepository() {
		return _km.getKnowledgeRepository();
	}

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unload() throws ThinklabException {

		if (_km != null) {
			_km.shutdown();
			_km = null;
		}
	}
	

}
