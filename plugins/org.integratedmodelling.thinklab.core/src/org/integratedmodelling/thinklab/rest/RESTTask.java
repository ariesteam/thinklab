package org.integratedmodelling.thinklab.rest;

import org.restlet.representation.Representation;


public interface RESTTask {
	
	public abstract Representation getResult();
	
	public abstract boolean isFinished();

}
