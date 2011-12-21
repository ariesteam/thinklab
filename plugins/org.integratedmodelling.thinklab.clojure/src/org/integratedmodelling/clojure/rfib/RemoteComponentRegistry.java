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

package org.integratedmodelling.clojure.rfib;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteComponentRegistry
{
    private ConcurrentHashMap<String,RemoteComponent> registry;

    public RemoteComponentRegistry()
    {
	registry = new ConcurrentHashMap<String,RemoteComponent>();
    }

    public void addComponent(String id, RemoteComponent comp)
    {
	registry.put(id, comp);
    }

    public void dropComponent(String id)
    {
	registry.remove(id);
    }

    public Set listComponents(String id)
    {
	return registry.keySet();
    }

    public RemoteComponent getComponent(String id)
    {
	return registry.get(id);
    }

    public void clearRegistry()
    {
	registry.clear();
    }
}
