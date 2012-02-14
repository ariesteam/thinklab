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
package org.integratedmodelling.thinklab.proxy;

import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.model.Namespace;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;
import org.integratedmodelling.thinklab.api.modelling.units.IUnit;
import org.integratedmodelling.thinklab.modelling.model.ModelManager;

public class ProxyModelFactory implements IModelFactory {

	@Override
	public IUnit parseUnit(String unit) throws ThinklabValidationException {
		return ModelManager.get().parseUnit(unit);
	}

	@Override
	public IModelObject clone(IModelObject o, INamespace namespace) {
		return ModelManager.get().clone(o, namespace);
	}

	@Override
	public void processNamespace(Namespace namespace) {
		ModelManager.get().processNamespace(namespace);
	}
}
