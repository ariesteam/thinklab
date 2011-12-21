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
package org.integratedmodelling.thinklab.modelling.model;

import org.integratedmodelling.lang.model.Namespace;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;

/**
 * A class that translates the API beans output by the language parsers into
 * actual namespaces and model objects.
 * 
 * @author Ferd
 *
 */
public class ModelAdapter {


	/**
	 * The main entry point.
	 * 
	 * @param namespace
	 * @return
	 */
	public INamespace createNamespace(Namespace namespace) {
		return null;
	}

	/**
	 * Assume the namespace has been incrementally modified and just parse the last
	 * model object defined in it. Used only in interactive sessions when statements
	 * are evaluated one by one.
	 * 
	 * @param evaluate
	 * @return
	 */
	public IModelObject createModelObject(Namespace evaluate) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
