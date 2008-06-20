/**
 * DocumentationLoaderConstructor.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDynamicModellingPlugin.
 * 
 * ThinklabDynamicModellingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDynamicModellingPlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.dynamicmodelling.loaders;

import org.integratedmodelling.dynamicmodelling.DynamicModellingPlugin;
import org.integratedmodelling.dynamicmodelling.interfaces.IModelLoader;
import org.integratedmodelling.dynamicmodelling.interfaces.IModelLoaderConstructor;
import org.integratedmodelling.thinklab.exception.ThinklabException;

public class DocumentationLoaderConstructor implements IModelLoaderConstructor {

	public IModelLoader createModelLoader() throws ThinklabException {

		ModelDocumentationGenerator ret = new ModelDocumentationGenerator();
		ModelDocumentationGenerator.initialize(DynamicModellingPlugin.get());
		return ret;
		
	}

}
