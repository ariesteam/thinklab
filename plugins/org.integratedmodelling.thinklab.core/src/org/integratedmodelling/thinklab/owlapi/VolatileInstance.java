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
package org.integratedmodelling.thinklab.owlapi;

import java.util.Collections;

import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.util.OWLEntityRemover;

/**
 * A volatile instance retracts all related axioms from the corresponding ontology
 * when it is garbage collected. It also stores its implementation locally and
 * does not save it anywhere. It should be the default implementation for
 * those created in sessions, although for now it is not used.
 * 
 * @author Ferdinando Villa
 */
public class VolatileInstance extends  Instance {

	IInstanceImplementation implementation = null;
	
	public VolatileInstance(OWLIndividual i) {
		super(i);
	}

	@Override
	protected void finalize() throws Throwable {
		
		System.out.println("Retracting " + getLocalName());
		
		/*
		 * retract all axioms from only our own ontology - which
		 * should be fine for sessions
		 */
        OWLEntityRemover remover = 
        	new OWLEntityRemover(
        			FileKnowledgeRepository.get().manager, 
        			Collections.singleton(getOWLOntology()));
        entity.accept(remover);
        
        try {
        	FileKnowledgeRepository.get().manager.applyChanges(remover.getChanges());
        } catch (OWLOntologyChangeException e) {
        	throw new ThinklabRuntimeException(e);
        }

        super.finalize();
	}
	
	


}
