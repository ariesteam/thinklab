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
package org.integratedmodelling.thinkscape;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class ThinkscapePlugin extends ThinklabPlugin  {

	static final public String PLUGIN_ID = "org.integratedmodelling.thinklab.thinkscape";
	
	public ThinkscapePlugin() {
		// TODO Auto-generated constructor stub
	}

	public static ThinkscapePlugin get() {
		return (ThinkscapePlugin) getPlugin(PLUGIN_ID);
	}

	@Override
	public void load(KnowledgeManager km) throws ThinklabPluginException {
	}

	@Override
	public void unload() throws ThinklabPluginException {
	}

	
}
