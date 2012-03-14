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

import org.integratedmodelling.thinklab.api.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.modelling.IModelFactory;

import com.google.inject.AbstractModule;

/**
 * Used for dependency injection to support any external code written
 * using only thinklab-api.
 * 
 * Will bind all Thinklab managers and factories referenced in other
 * external implementations to their implementations here. All factories
 * that are singletons are proxied by delegated objects.
 * 
 * @author Ferd
 *
 */
public class ModellingModule extends AbstractModule {

	// may be null; if not, the code will belong to this project.
	IProject _project;
	
	@Override
	protected void configure() {
		bind(IModelFactory.class).to(ProxyModelFactory.class);
		bind(IKnowledgeManager.class).to(ModelKnowledgeManager.class);
	}
	
}
