package org.integratedmodelling.thinklab.exceptions;

import org.integratedmodelling.exceptions.ThinklabException;

public class ThinklabCircularDependencyException extends ThinklabException {

	private static final long serialVersionUID = 5840329595400463869L;

	public ThinklabCircularDependencyException() {
		super();
	}

	public ThinklabCircularDependencyException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ThinklabCircularDependencyException(String arg0) {
		super(arg0);
	}

	public ThinklabCircularDependencyException(Throwable arg0) {
		super(arg0);
	}

}
