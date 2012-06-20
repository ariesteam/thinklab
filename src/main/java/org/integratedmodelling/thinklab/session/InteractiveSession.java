package org.integratedmodelling.thinklab.session;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.api.runtime.IUserModel;
import org.integratedmodelling.thinklab.owlapi.Session;

public class InteractiveSession extends Session {

	private InputStream _input;
	private PrintStream _output;

	public InteractiveSession(InputStream input, PrintStream output) {
		_input = input;
		_output = output;
	}
	
	@Override
	protected IUserModel createUserModel() {

		return new IUserModel() {
			
			@Override
			public void setProperties(Properties uprop) {
				InteractiveSession.this.getProperties().putAll(uprop);
			}
			
			@Override
			public void initialize(ISession session) {
			}
			
			@Override
			public ISemanticObject<?> getUser() throws ThinklabException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Properties getProperties() {
				return InteractiveSession.this.getProperties();
			}
			
			@Override
			public PrintStream getOutputStream() {
				return _output;
			}
			
			@Override
			public InputStream getInputStream() {
				return _input;
			}
		};
	}

}
