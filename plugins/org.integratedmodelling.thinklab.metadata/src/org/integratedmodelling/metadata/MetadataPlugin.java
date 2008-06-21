/**
 * MetadataPlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabMetadataPlugin.
 * 
 * ThinklabMetadataPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabMetadataPlugin is distributed in the hope that it will be useful,
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
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.metadata;

import java.io.File;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.extensions.KnowledgeProvider;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.w3c.dom.Node;

public class MetadataPlugin extends ThinklabPlugin {

	static final String ID = "Metadata";
    
	public static MetadataPlugin get() {
		return (MetadataPlugin) getPlugin(ID );
	}
	
    public void load(KnowledgeManager km, File baseReadPath, File baseWritePath)
            throws ThinklabPluginException {
    }

    public void unload(KnowledgeManager km) throws ThinklabPluginException {
        // TODO Auto-generated method stub

    }

	public void initialize() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyResource(String name, long time, long size) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}

}
