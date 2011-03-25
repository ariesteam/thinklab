package org.integratedmodelling.thinklab.http;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;


public class DefaultThinklabWebModel extends ThinklabWebModel {

	Properties properties = null;
	
	@Override
	public void initialize(ThinklabWebSession session) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public void persist(String authenticatedUser) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public void restore(String authenticatedUser) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream getInputStream() {
		return null;
	}

	@Override
	public PrintStream getOutputStream() {
		return null;
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

}
