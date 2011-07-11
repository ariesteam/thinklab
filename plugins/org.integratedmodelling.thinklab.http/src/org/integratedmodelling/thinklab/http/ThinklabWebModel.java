package org.integratedmodelling.thinklab.http;

import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.api.runtime.IUserModel;

/**
 * Helper class to build persistent MVC models for thinkcap. Any ThinkcapModel works with Portlets and portals in the view
 * package to help build clean MVC applications.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class ThinklabWebModel implements IUserModel {

	ISession tlSession = null;
	Properties properties = null;
	
	public abstract void initialize(ThinklabWebSession session) throws ThinklabException;

	public abstract void restore(String authenticatedUser) throws ThinklabException;	

	public abstract void persist(String authenticatedUser) throws ThinklabException;

	@Override
	public void initialize(ISession session) {
		tlSession = session;
	}	
	
	public ISession getSession() {
		return tlSession;
	}

	@Override
	public void setProperties(Properties uprop) {
		properties = uprop;
	}

	@Override
	public Properties getProperties() {

		if (properties == null)
			properties = new Properties();
		
		return properties;
	}
	
	@Override
	public IInstance getUserInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
