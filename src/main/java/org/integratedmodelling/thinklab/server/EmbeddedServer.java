package org.integratedmodelling.thinklab.server;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.runtime.IServer;
import org.integratedmodelling.thinklab.modelling.lang.Metadata;

/**
 * The way to use thinklab as a server when it's running in the same JVM as the client. 
 * Maybe one day to be supplemented by an RMI one for fun.
 * 
 * @author Ferd
 *
 */
public class EmbeddedServer implements IServer {

	int _status;
	Metadata _metadata = new Metadata();
	
	public EmbeddedServer() {
	}
	
	@Override
	public Object executeStatement(String s) {
		
		if (_status == ERROR)
			return null;
		
		return null;
	}

	@Override
	public Object executeCommand(String command) {
		if (_status == ERROR)
			return null;
		return null;
	}

	@Override
	public long executeStatementAsynchronous(String s) {
		if (_status == ERROR)
			return 0L;
		return 0;
	}

	@Override
	public long executeCommandAsynchronous(String command) {
		if (_status == ERROR)
			return 0L;
		return 0;
	}

	@Override
	public int getStatus(long handle) {
		return _status;
	}

	@Override
	public Object getTaskResult(long handle, boolean dispose) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void shutdown() {
		Thinklab.shutdown();
		_status = OK;
	}

	@Override
	public void boot() {
		try {
			Thinklab.boot();
		} catch (ThinklabException e) {
			_status = ERROR;
		}

	}

	@Override
	public IMetadata getMetadata() {
		return _metadata;
	}

}
