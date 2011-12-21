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
package org.integratedmodelling.persistence;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

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
public class PersistencePlugin extends ThinklabPlugin {
	
	public static String PLUGIN_ID = "org.integratedmodelling.thinklab.persistence"; //Just in case we rename it
	
	public static IProperty ABSTRACT_PERSISTENCY_PROPERTY;
	public static IProperty FACTORY_PERSISTENCY_PROPERTY;
	public static IProperty LONG_TEXT_PROPERTY;
	
	public static PersistencePlugin get() {
		return (PersistencePlugin) getPlugin(PLUGIN_ID);
	}
	
	@Override
	public void load(KnowledgeManager km) throws ThinklabException {

		// TODO move 
		// new Generate().install(KnowledgeManager.get());
		ABSTRACT_PERSISTENCY_PROPERTY = km.requireProperty("persistence:abstract");
		FACTORY_PERSISTENCY_PROPERTY = km.requireProperty("persistence:factory");
		LONG_TEXT_PROPERTY = km.requireProperty("persistence:text");

		/*
		 * TODO substitute with extension point declaration
		 */
		//KBoxManager.get().registerKBoxProtocol("hbm", this);
	}


	@Override
	public void unload() throws ThinklabPluginException {
		// TODO Auto-generated method stub
		
	}

}
