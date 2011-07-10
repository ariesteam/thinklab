package org.integratedmodelling.thinklab.session;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.api.runtime.IUserModel;

public class TTYUserModel implements IUserModel {

	private ISession session = null;
	private Properties properties = null;
	
	@Override
	public InputStream getInputStream() {
		return System.in;
	}

	@Override
	public PrintStream getOutputStream() {
		return System.out;
	}

	@Override
	public void initialize(ISession session) {
		this.session  = session;
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
