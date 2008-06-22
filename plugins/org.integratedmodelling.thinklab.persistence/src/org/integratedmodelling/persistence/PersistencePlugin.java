/**
 * PersistencePlugin.java
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
package org.integratedmodelling.persistence;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.persistence.shell.Generate;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.IKBoxPlugin;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.plugin.Plugin;
import org.w3c.dom.Node;

/**
 * The Persistence Storage Plugin provides with the facilities for generating and accessing KBoxes
 * through an ontology-object-relational mapping.
 * 
 * The current version allows the generation of source code in Java using Enterprise
 * Java Beans 3.0, with persistence supported through hibernate.
 * 
 *  
 * @author Ioannis N. Athanasiadis,  Dalle Molle Institute for Artificial Intelligence, USI/SUPSI
 * @version 0.2
 * @since Feb 5, 2007
 */
public class PersistencePlugin extends Plugin implements IKBoxPlugin{
	public static String pluginname = "Persistence"; //Just in case we rename it
	
	public static IProperty ABSTRACT_PERSISTENCY_PROPERTY;
	public static IProperty FACTORY_PERSISTENCY_PROPERTY;
	public static IProperty LONG_TEXT_PROPERTY;
	
	@Override
	public void initialize() throws ThinklabException {
		// register new command!
		new Generate().install(KnowledgeManager.get());
		ABSTRACT_PERSISTENCY_PROPERTY = KnowledgeManager.KM.requireProperty("persistence:abstract");
		FACTORY_PERSISTENCY_PROPERTY = KnowledgeManager.KM.requireProperty("persistence:factory");
		LONG_TEXT_PROPERTY = KnowledgeManager.KM.requireProperty("persistence:text");
	}

	@Override
	public void load(KnowledgeManager km, File baseReadPath, File baseWritePath) throws ThinklabPluginException {
		/*
		 * TODO substitute with extension point declaration
		 */
		KBoxManager.get().registerKBoxProtocol("hbm", this);
	}

	@Override
	public void notifyResource(String name, long time, long size) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload(KnowledgeManager km) throws ThinklabPluginException {
		// TODO Auto-generated method stub
		
	}

	public IKBox createKBoxFromURL(URI url) throws ThinklabStorageException {
		// TODO Auto-generated method stub
		return null;
	}

	public IKBox createKBox(String protocol, String dataUri,
			Properties properties) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}

}
