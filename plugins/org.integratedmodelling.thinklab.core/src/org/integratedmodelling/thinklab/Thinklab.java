package org.integratedmodelling.thinklab;

import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.java.plugin.Plugin;

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
		
		if (_km == null) {
			_km = new KnowledgeManager(new FileKnowledgeRepository());
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
