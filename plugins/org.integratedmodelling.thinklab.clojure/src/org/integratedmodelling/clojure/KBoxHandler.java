package org.integratedmodelling.clojure;

import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

/**
 * Interacts with a kbox on behalf of a with-kbox form.
 * 
 * @author Ferdinando Villa
 *
 */
public class KBoxHandler {

	IKBox kbox = null;
		
	public void setKbox(Object kbox, Object options) {
		
		/*
		 * input could be anything that points to a kbox
		 */
	}
	
	public void addKnowledge(Object kbox, Object options) {
		
		/*
		 * just ignore anything we don't know what to do with
		 */
	}
		
	public IKBox getKbox() {
		return null;
	}
}


