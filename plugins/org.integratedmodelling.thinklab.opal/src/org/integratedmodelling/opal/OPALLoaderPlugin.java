/**
 * OPALLoaderPlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabOPALPlugin.
 * 
 * ThinklabOPALPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabOPALPlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.opal;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.opal.profile.OPALProfileFactory;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.extensions.KnowledgeProvider;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeLoaderPlugin;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.plugin.Plugin;
import org.w3c.dom.Node;

public class OPALLoaderPlugin extends Plugin implements IKnowledgeLoaderPlugin {

	ArrayList<URL> profiles = new ArrayList<URL>();
	
	@Override
	public void load(KnowledgeManager km, File baseReadPath, File baseWritePath)
			throws ThinklabPluginException {
		
		// does not need to do anything for now - Session loader will find the plug-in 
		// from the name.

	}

	@Override
	public void unload(KnowledgeManager km) throws ThinklabPluginException {
	}

	@Override
	public void notifyResource(String name, long time, long size)
			throws ThinklabException {
		
		if (name.endsWith(".opf")) {
			profiles.add(this.exportResourceCached(name));
		}
	}

	public Collection<IInstance> loadKnowledge(URL url, ISession session, IKBox kbox) throws ThinklabException {
		
		OPALValidator val = new OPALValidator();
		return val.validate(url, session, kbox);
	}

	@Override
	public void initialize() throws ThinklabException {
	
		/*
		 * prime the profile factory with any profiles loaded from the jar. This also creates
		 * the profile factory.
		 */ 
		for (URL url : profiles) {
			OPALProfileFactory.get().readProfile(url);
		}
	}

	public boolean handlesFormat(String format) {

		if (format.equals("xml")) {
			return true;
		}
		
		boolean ret = false;
		try {
			ret = (OPALProfileFactory.get().getProfile(format, false) != null);
		} catch (ThinklabIOException e) {
		}
		return ret;
	}

	public void writeKnowledge(File outfile, String format, IInstance... instances)
			throws ThinklabException {
		
		OPALWriter.writeInstances(outfile, format, instances);
		
	}

	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}

}
