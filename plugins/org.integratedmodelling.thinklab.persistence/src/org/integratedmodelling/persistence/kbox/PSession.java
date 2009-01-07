/**
 * PSession.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabPersistencePlugin.
 * 
 * ThinklabPersistencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabPersistencePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.persistence.kbox;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IThinklabSessionListener;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.AlgorithmValue;
import org.integratedmodelling.utils.Polylist;

public class PSession implements ISession {
	
	Logger log  = Logger.getLogger("org.integratedmodelling.persistence.kbox.PersistentSession");
	Session s;
	PersistentKBox kBox;
	Properties properties = new Properties();
	HashMap<String, Object> objects = new HashMap<String, Object>();
	

	public IInstance createObject(String name, IConcept parent)
			throws ThinklabException {
		return new PInstance(name, parent, kBox);
	}

	public IInstance createObject(String concept) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance createObject(SemanticType concept) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance createObject(String name, String concept)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance createObject(String name, SemanticType concept)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance createObject(String name, Polylist definition)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance createObject(Polylist polylist) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance createObject(IInstance ii) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteObject(String name) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	public IValue execute(AlgorithmValue algorithm,
			Map<String, IValue> arguments) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue execute(AlgorithmValue algorithm) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSessionID() {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance importObject(String kboxURI) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IInstance> listObjects() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IInstance> loadObjects(URL url) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IInstance> loadObjects(String source) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public void makePermanent(String name) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	public String makePermanent() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance requireObject(String name)
			throws ThinklabResourceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance retrieveObject(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(String file) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	public IOntology asOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addListener(IThinklabSessionListener listener) throws ThinklabException {
		throw new ThinklabException("persistent sessions do not accept listeners");
	}

	public Collection<IThinklabSessionListener> getListeners() {
		return null;
	}

	public void linkObjects(IInstance o1, IInstance o2) {
		// TODO Auto-generated method stub
		
	}

	public Properties getProperties() {
		return properties;
	}

	public Collection<String> getLocalKBoxes() {
		// TODO Auto-generated method stub
		return null;
	}

	public IKBox requireKBox(String string) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IKBox retrieveKBox(String string) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public void clearUserData(String id) {
		if (objects.containsKey(id))
			objects.remove(id);
	}

	public void registerUserData(String id, Object object) {
		objects.put(id, object);
	}

	public Object requireUserData(String id) throws ThinklabResourceNotFoundException {
		Object ret = objects.get(id);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("session: user object " + id + " not registered");
		return ret;
	}

	public Object retrieveUserData(String id) {
		return objects.get(id);
	}

	@Override
	public InputStream getDefaultInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getDefaultOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
