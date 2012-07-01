package org.integratedmodelling.thinklab.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Hashtable;

import org.integratedmodelling.interpreter.ModelGenerator;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.lang.IResolver;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.api.runtime.IServer;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.modelling.lang.Metadata;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.thinklab.proxy.ModellingModule;
import org.integratedmodelling.thinklab.session.InteractiveSession;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The way to use thinklab as a server when it's running in the same JVM as the client. 
 * Maybe one day to be supplemented by an RMI one for fun. Embedded server does not 
 * require any authentication.
 * 
 * @author Ferd
 *
 */
public class EmbeddedServer implements IServer {

	private static final String USER_DEFAULT_NAMESPACE = "user";
	
	Metadata _metadata = new Metadata();
	
	private InputStream _input;
	private PrintStream _output;
	private ISession _sess;
	private boolean _isActive = false;
	
	private volatile long _nextId = 3L;
	private synchronized long nextId() {
		return _nextId++;
	}
	
	private final CResult OK_RESULT = new CResult(OK, null, null, null, null);

	CResult _status = new CResult(SCHEDULED, null, null, null, null);
	
	/*
	 * we store stuff in here
	 */
	Hashtable<Long, Result> _results = new Hashtable<Long, IServer.Result>();
	
	ModelGenerator _mg = null;
	IResolver _resolver = null;
	
	public EmbeddedServer() {
	}
	
	ModelGenerator getModelGenerator() {

		if (_mg == null) {
			Injector injector = Guice.createInjector(new ModellingModule());
			_mg = injector.getInstance(ModelGenerator.class);
		}
		return _mg;
	}

	IResolver getResolver() {
		if (_resolver == null) {
			_resolver = 
					((ModelManager)Thinklab.get().getModelManager()).getInteractiveResolver(_input, _output);
		}
		return _resolver;
	}
	public EmbeddedServer(InputStream input, PrintStream output) {
		_input = input;
		_output = output;
	}
	
	@Override
	public Result executeStatement(String s) {
		
		if (_status._s == ERROR)
			return null;
		
		CResult ret = new CResult(ERROR, s, null, null, null);
		
		try {
			ret._s = SCHEDULED;
			InputStream is = new ByteArrayInputStream(s.getBytes());
			getModelGenerator().parseInNamespace(is, USER_DEFAULT_NAMESPACE, getResolver());
			is.close();
			ret._s = OK;
			ret._r = getResolver().getLastProcessedObject(); 
		} catch (Exception e) {
			ret._e = e;
		} finally {
		}	
		
		return ret;
	}

	@Override
	public Result executeCommand(String command) {
		
		CResult result = new CResult(ERROR, command, null, null, null);
		
		try {
			Command cmd = CommandParser.parse(command);
			Object o = CommandManager.get().submitCommand(cmd, getSession());
			result._s = OK;
			result._r = o;
		} catch (Exception e) {
			result._s = ERROR;
			result._e = e;
		}
		
		return result;

	}

	@Override
	public long executeStatementAsynchronous(final String s) {
		
		final long id = nextId();
		_results.put(id, new CResult(SCHEDULED, s, null, null, null));

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				_results.put(id, executeStatement(s));
			}
			
		});
		
		t.start();
		
		return t.getId();
	}

	@Override
	public long executeCommandAsynchronous(final String command) {
		
		final long id = nextId();
		_results.put(id, new CResult(SCHEDULED, command, null, null, null));

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				_results.put(id, executeCommand(command));
			}
		});
		
		t.start();
		
		return t.getId();
	}

	@Override
	public int getStatus(long handle) {
		Result r = _results.get(handle);
		if (r == null)
			return ERROR;
		return r.getStatus();
	}

	@Override
	public Result getTaskResult(long handle, boolean dispose) {
		Result r = _results.get(handle);
		if (dispose)
			_results.remove(handle);
		return r;
	}

	@Override
	public Result shutdown() {
		Thinklab.shutdown();
		_isActive = false;
		return OK_RESULT;
	}

	@Override
	public Result boot() {

		try {
			Thinklab.boot();
			Runtime runtime = Runtime.getRuntime();
			_metadata.put(TOTAL_MEMORY_MB, runtime.totalMemory()/1048576);
			_metadata.put(FREE_MEMORY_MB, runtime.freeMemory()/1048576);
			_status = OK_RESULT;
			_isActive = true;
		} catch (Exception e) {
			_status = new CResult(ERROR, null, null, null, e);
		}
		
		return _status;
	}

	@Override
	public IMetadata getMetadata() {
		return _metadata;
	}

	private ISession getSession() {
	
		if (_sess == null) {
			if (_input != null || _output != null) {
				_sess = new InteractiveSession(_input, _output);
			} else {
				_sess = new Session();
			}
		}
		return _sess;
	}


	@Override
	public boolean isActive() {
		return _isActive;
	}

	@Override
	public Result authenticate(Object... authInfo) {
		return OK_RESULT;
	}

	@Override
	public Result deploy(IProject p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result undeploy(IProject p) {
		// TODO Auto-generated method stub
		return null;
	}

	// obvious things last
	
	public static class CResult implements Result {

		private int _s;
		private String _c;
		private Object _r;
		private String _o;
		private Throwable _e;

		public CResult(int status, String command, Object result, String output, Throwable exception) {
			_s = status; 
			_c = command;
			_r = result;
			_o = output;
			_e = exception;
		}
		
		@Override
		public int getStatus() {
			return _s;
		}

		@Override
		public String getCommand() {
			return _c;
		}

		public Object getResult() {
			return _r;
		}
		
		@Override
		public String getOutput() {
			return _o;
		}

		@Override
		public Throwable getException() {
			return _e;
		}
		
	}




}
