package org.integratedmodelling.thinklab;

import java.net.URL;
import java.util.Iterator;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.java.plugin.Plugin;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.Extension.Parameter;

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
		return (Thinklab)getPlugin(PLUGIN_ID);
	}
	
	KnowledgeManager _km = null;
	
	@Override
	protected void preStart() throws ThinklabException {
		
		/*
		 * TODO we need to establish what knowledge repository and session manager to
		 * use. This must work in a general fashion.
		 */
		
		if (_km == null) {
			_km = new KnowledgeManager(new FileKnowledgeRepository());
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
