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
package org.integratedmodelling.thinklab.transformations;

import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class TransformationFactory {

	static TransformationFactory _this = new TransformationFactory();
	
	TransformationChooser _chooser = new TransformationChooser();
	
	HashMap<String, ITransformation> _transformations  = 
		new HashMap<String, ITransformation>();
	
	public ITransformation retrieveTransformation(String id, Object[] parameters) {
		return _transformations.get(id);
	}
	
	public ITransformation requireTransformation(String id, Object[] parameters) throws ThinklabResourceNotFoundException {
		ITransformation ret = _transformations.get(id);
		if (ret == null)
			throw new ThinklabResourceNotFoundException(
					"transformation factory: no transformation found for id " + id);
		return ret;
	}

	public static TransformationFactory get() {
		return _this;
	}

	public void registerTransformation(String name, ITransformation transformation) {
		_transformations.put(name, transformation);
	}

	public void loadTransformationMappings(Properties properties) throws ThinklabException {
		_chooser.load(properties);
	}

	public ITransformation getTransformation(IConcept type) throws ThinklabException {
		return _chooser.get(type);
	}
}
