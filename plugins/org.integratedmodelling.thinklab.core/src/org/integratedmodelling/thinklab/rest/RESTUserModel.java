package org.integratedmodelling.thinklab.rest;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.thinklab.authentication.AuthenticationManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.IUserModel;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.owlapi.Session;

public class RESTUserModel implements IUserModel {

	Properties properties = null;
	ISession session = null;
	
	public RESTUserModel(
			HashMap<String, String> arguments,
			Properties p, Session session) {
		this.properties = p;
		this.session = session;
	}

	@Override
	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintStream getOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(ISession session) {
		this.session = session;
	}

	@Override
	public void setProperties(Properties uprop) {
		this.properties = uprop;
	}

	@Override
	public Properties getProperties() {
		return this.properties;
	}

	@Override
	public IInstance getUserInstance() throws ThinklabException {
		
		IInstance ret = session.retrieveObject("user");
		
		if (ret == null && properties != null) {

			String user = properties.getProperty("authenticated-user");
			if (user == null)	
				return null;
			
			ret = AuthenticationManager.get().getUserInstance(user, session);
		}
		return ret;
	}

}
