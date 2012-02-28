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
package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.model.AgentModel;
import org.integratedmodelling.lang.model.Context;
import org.integratedmodelling.lang.model.Model;
import org.integratedmodelling.lang.model.ModelObject;
import org.integratedmodelling.lang.model.Namespace;
import org.integratedmodelling.lang.model.Scenario;
import org.integratedmodelling.lang.model.Storyline;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.owlapi.FileKnowledgeRepository;

/**
 * A class that translates the API beans output by the language parsers into
 * actual namespaces and model objects. Contains all the translation logics for
 * thinklab.
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
	 * @throws ThinklabException 
	 */
	public INamespace createNamespace(Namespace namespace) throws ThinklabException {
		
		NamespaceImpl ret = new NamespaceImpl(namespace);
		
		/*
		 * ontology first - ask for one, complain if not there
		 */
		IProject proj = namespace.getProject();
		String urlPrefix = 
				proj == null ? 
					FileKnowledgeRepository.DEFAULT_TEMP_URI : 
					proj.getOntologyNamespacePrefix();
		IOntology ont = 
				KnowledgeManager.get().getKnowledgeRepository().createOntology(namespace.getId(), urlPrefix);
		ont.define(namespace.getAxioms());

		ret._ontology = ont;
		
		for (ModelObject o : namespace.getModelObjects()) {
			ret._modelObjects.add(createModelObject(o));
		}
		
		return ret;
	}

	private IModelObject createModelObject(ModelObject o) {
		
		IModelObject ret = null;
		
		if (o instanceof Model) {
			ret = new ModelImpl((Model)o);
		} else if (o instanceof Context) {
			
		} else if (o instanceof AgentModel) {
			
		} else if (o instanceof Scenario) {
			
		} else if (o instanceof Storyline) {
			
		}
		return ret;
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
