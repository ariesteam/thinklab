package org.integratedmodelling.thinklab;

import java.net.URL;
import java.util.Iterator;

import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
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
public class Thinklab extends Plugin {

	KnowledgeManager _km = null;
	
	@Override
	protected void doStart() throws Exception {
		
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

	@Override
	protected void doStop() throws Exception {
		
		if (_km != null) {
			_km.shutdown();
			_km = null;
		}
	}
	
	public IKnowledgeRepository getKnowledgeRepository() {
		return _km.getKnowledgeRepository();
	}
	

}
