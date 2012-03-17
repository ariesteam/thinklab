/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.api.runtime.IUserModel;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.ServerResource;

public class RESTManager {

	public final static String PLUGIN_ID = "org.integratedmodelling.thinklab.rest";

	HashMap<Integer, Component> _components = 
		new HashMap<Integer, Component>(); 
	HashMap<String, RestCommand> _commands = 
		new HashMap<String, RestCommand>(); 
	/*
	 * resource classes harvested from plugin code.
	 */
	HashMap<String, Class<? extends ServerResource>> _resources =
		new HashMap<String, Class<? extends ServerResource>>();
	
	int sessionCount;
	
	HashMap<String, ISession> _sessions = 
		new HashMap<String, ISession>();
	
	public static RESTManager _this = null;
	
	public static class RestCommand {

		public String[] options;
		public String[] arguments;
		public String id;
		public String description;
		
		public RestCommand(String path, String description, String argument,
				String options) {
			
			this.id = path;
			this.description = description;
			this.options = options.isEmpty() ? null : options.split(",");
			this.arguments = argument.isEmpty()? null : argument.split(",");
		}

		public Object asArray() {
			return new Object[]{id, description, arguments, options};
		}	
	}
	
	/**
	 * If query contains the ID of a valid user session, return the ISession 
	 * allocated to it, otherwise return null. 
	 * 
	 * @param hashMap
	 * @return
	 */
	public ISession getSession(String id) {
		return _sessions.get(id);
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

	public void registerService(String path, Class<? extends ServerResource> handlerClass, 
			String description, String argument, String options) {

		// TODO pass and store all further documentation.
		_resources.put(path, handlerClass);
		_commands.put(path, new RestCommand(path, description, argument, options));
		
		// update any existing servers
		for (Component p : _components.values()) {
			p.getInternalRouter().attach("/" + path, handlerClass);
		}
	}
		
	/**
	 * Start the server on specified port. Bound to "rest start" command.
	 * 
	 * @param port
	 * @throws ThinklabException
	 */
	public void start(int port) throws ThinklabException {

		if (_components.containsKey(port))
			throw new ThinklabValidationException(
					"a REST service is already running on port " + port);
		
		Component component = new Component();
		
		component.getServers().add(Protocol.HTTP, port);
		component.getDefaultHost().attach("/rest", new RESTApplication());
		
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
			throw new ThinklabValidationException(
					"no REST service running on port " + port);
		try {
			component.stop();
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);
		}
		_components.remove(port);
		
	}

	public ISession createRESTSession(final HashMap<String, String> arguments, final Properties properties) throws ThinklabException {
		
		Session ret = new Session() {
			
			@Override
			protected IUserModel createUserModel() {
				return new RESTUserModel(arguments, properties, this);
			}
			
		};
		
		synchronized (_sessions) {
			_sessions.put(ret.getID(), ret);
		}
		
		return ret;
	}

	public Collection<String> getPaths() {
		return _resources.keySet();
	}

	public Class<? extends ServerResource> getResourceForPath(String path) {
		return _resources.get(path);
	}
	
	public Collection<RestCommand> getCommandDescriptors() {
		return _commands.values();
	}
	
	public RestCommand getCommandDescriptor(String id) {
		return _commands.get(id);
	}

	public boolean allowPrivilegedLocalConnections() {
		return true;
	}
}
