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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.interpreter.ModelGenerator;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IResolver;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.api.runtime.IUserModel;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.modelling.lang.Context;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.thinklab.proxy.ModellingModule;
import org.json.JSONObject;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class RESTUserModel implements IUserModel {

	Properties properties = null;
	ISession session = null;
	
	public RESTUserModel(
			HashMap<String, String> arguments,
			Properties p, Session session) {
		this.properties = p;
		this.session = session;
	}

	@Override
	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintStream getOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(ISession session) {
		this.session = session;
	}

	@Override
	public void setProperties(Properties uprop) {
		this.properties = uprop;
	}

	@Override
	public Properties getProperties() {
		return this.properties;
	}

	@Override
	public ISemanticObject<?> getUser() throws ThinklabException {

		return null;
		
//		IInstance ret = session.retrieveObject("user");
//		
//		if (ret == null && properties != null) {
//
//			String user = properties.getProperty("authenticated-user");
//			if (user == null)	
//				return null;
//			
//			ret = AuthenticationManager.get().getUserInstance(user, session);
//		}
//		return ret;
	}

	/*
	 * We have one resolver, model generator, and current context instance per user. Maybe it should be
	 * in the session, but we have a 1-1 user-session relationship; this could eventually
	 * become relevant if we end up having collaborative modeling sessions, where switching
	 * from the user resolver to the session resolver would give users their own sandboxes.
	 */
	
	ModelGenerator _mg = null;
	IResolver _resolver = null;
	IContext _currentContext = new Context();
	
	public ModelGenerator getModelGenerator() {

		if (_mg == null) {
			Injector injector = Guice.createInjector(new ModellingModule());
			_mg = injector.getInstance(ModelGenerator.class);
		}
		return _mg;
	}

	public IResolver getResolver() {
		if (_resolver == null) {
			_resolver = 
					((ModelManager)Thinklab.get().getModelManager()).
						getInteractiveResolver(session.getInputStream(), session.getOutputStream());
		}
		return _resolver;
	}
	
	public void mergeContext(IContext ctx) throws ThinklabException {
		_currentContext.merge(ctx);
	}
	
	public IContext getCurrentContext() {
		return _currentContext;
	}
}
