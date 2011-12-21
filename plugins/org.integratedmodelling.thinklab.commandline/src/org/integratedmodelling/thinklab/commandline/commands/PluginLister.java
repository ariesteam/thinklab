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
package org.integratedmodelling.thinklab.commandline.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.PluginDescriptor;

@ListingProvider(label="plugins", itemlabel="plugin")
public class PluginLister implements IListingProvider {

	@Override
	public Collection<?> getListing() throws ThinklabException {
		
		ArrayList<String> ret = new ArrayList<String>();
		
		for (PluginDescriptor pd :
			CommandLine.get().getManager().getRegistry().getPluginDescriptors()) {

			ret.add(
				pd.getId() + 
				" (" +
				pd.getVersion() + 
				")\t" +
				(CommandLine.get().getManager().isPluginActivated(pd) ?
						"activated" :
						"inactive"));
		}
		
		return ret;
	}

	@Override
	public Collection<?> getSpecificListing(String plugin) throws ThinklabException {

		ArrayList<String> ret = new ArrayList<String>();

		Plugin plug = null;
		try {
			plug = Thinklab.get().getManager().getPlugin(plugin);
		} catch (PluginLifecycleException e) {
		}
		
		if (plug instanceof ThinklabPlugin) {
			Properties props = ((ThinklabPlugin)plug).getProperties();
			ret.add("properties for plugin " + plugin + ":");
			for (Object s : props.keySet()) {
				ret.add("  " + s + " = " + props.getProperty(s.toString()));				
			}
		} else {
			ret.add("plugin " + plugin + " does not exist");			
		}
		return ret;
	}

	@Override
	public void notifyParameter(String parameter, String value) {
	}

}
