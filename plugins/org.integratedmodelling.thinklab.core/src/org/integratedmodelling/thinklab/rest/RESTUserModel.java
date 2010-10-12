package org.integratedmodelling.thinklab.rest;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.IUserModel;

public class RESTUserModel implements IUserModel {

	Properties properties = null;
	ISession session = null;
	
	public RESTUserModel(
			HashMap<String, String> arguments,
			Properties p) {
		this.properties = p;
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

}
