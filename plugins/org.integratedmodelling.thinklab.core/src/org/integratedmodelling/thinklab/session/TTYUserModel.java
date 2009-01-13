package org.integratedmodelling.thinklab.session;

import java.io.InputStream;
import java.io.PrintStream;

import org.integratedmodelling.thinklab.interfaces.applications.IUserModel;

public class TTYUserModel implements IUserModel {

	@Override
	public InputStream getInputStream() {
		return System.in;
	}

	@Override
	public PrintStream getOutputStream() {
		return System.out;
	}

}
