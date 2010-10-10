package org.integratedmodelling.thinklab.rest;

import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.rest.interfaces.IRESTHandler;
import org.integratedmodelling.utils.KeyValueMap;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class RESTManager {

	public final static String PLUGIN_ID = "org.integratedmodelling.thinklab.rest";

	HashMap<Integer, Component> _components = 
		new HashMap<Integer, Component>(); 

	public static RESTManager _this = null;
	
	/**
	 * If query contains the ID of a valid user session, return the ISession 
	 * allocated to it, otherwise return null. 
	 * 
	 * @param query
	 * @return
	 */
	public ISession getSessionForCommand(KeyValueMap query) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ISession initiateSession() {
		return null;
	}

	public static RESTManager get() {
		
		if (_this == null)
			_this = new RESTManager();
		
		return _this;
	}

	public HashMap<Integer, Component> getComponents() {
		return _components;
	}

	public void registerService(String path, Class<?> handlerClass) {

		// TODO use reflection to scan the methods and their annotation; use annotations to attribute the
		// allowed return MIME types
		
	}
	
	/**
	 * Start the server on specified port. Bound to "rest start" command.
	 * 
	 * @param port
	 * @throws ThinklabException
	 */
	public void start(int port) throws ThinklabException {

		if (_components.containsKey(port))
			throw new ThinklabInappropriateOperationException(
					"a REST service is already running on port " + port);
		
		Component component = new Component();
		
		component.getServers().add(Protocol.HTTP, port);
		component.getDefaultHost().attach("/rest", new RestApplication());

		/*
		 * TODO attach all registered services
		 */
		
		try {
			component.start();
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);
		}
		_components.put(port, component);

	}
	
	/**
	 * Stop the server on specified port. Bound to "rest stop" command.
	 * 
	 * @param port
	 * @throws ThinklabException
	 */
	public void stop(int port) throws ThinklabException {
	
		Component component = _components.get(port);
		if (component == null) 
			throw new ThinklabInappropriateOperationException(
					"no REST service running on port " + port);
		try {
			component.stop();
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);
		}
		_components.remove(port);
		
	}
}
