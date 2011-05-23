package org.integratedmodelling.thinklab.rest;


public abstract class RESTTask extends Thread {
	
	private volatile boolean isException = false;
	private String error = null;
	
	protected abstract void execute() throws Exception;
	protected abstract void cleanup();
	
	public abstract ResultHolder getResult();
	
	@Override
	public void run() {

		try {
			execute();
		} catch (Exception e) {
			error = e.getMessage();
			isException = true;
		} finally {
			cleanup();
		}
	}
	
	public boolean error() {
		return isException;
	}

}
