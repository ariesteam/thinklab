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
package org.integratedmodelling.thinklab.plugin;

import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;



/**
 * A specialized JPF plugin to support extension of the knowledge manager.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class ThinklabPlugin 
{	
	abstract protected void load() throws ThinklabException;
	
	abstract protected void unload() throws ThinklabException;
	
	protected abstract String getPluginBaseName();
	
	public abstract Properties getProperties();
	
	/**
	 * 
	 * @return
	 * @deprecated
	 */
	public String getKnowledgeDirectory() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
