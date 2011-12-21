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
package org.integratedmodelling.thinklab.workflow.validators;

import java.util.Map;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledgeSubject;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import com.opensymphony.workflow.WorkflowException;

/**
 * Valid if object passed as the subject argument is an instance or a subclass of the concept arg
 * @author Ferdinando
 *
 */
public class IsaValidator implements Validator {

	@Override
	public void validate(Map transientVars, Map args, PropertySet ps)
			throws InvalidInputException, WorkflowException {

		Object subj = args.get("subject");
		Object conc = args.get("concept");

		IConcept concept = null;
		IKnowledgeSubject object = null;
		
		if (conc == null) {
			throw new WorkflowException("IsaValidator: no concept parameter specified");			
		} else if (conc instanceof String) {
			try {
				concept = KnowledgeManager.get().requireConcept((String)conc);
			} catch (ThinklabException e) {
				throw new WorkflowException(e);
			}
		} else if (conc instanceof IConcept) {
			concept = (IConcept)conc;
		}

		if (subj == null) {
			throw new WorkflowException("IsaValidator: no subject parameter specified");			
		} else if (conc instanceof String) {
			object = ((ISession)(ps.getObject("thinklab.session"))).retrieveObject((String)conc);

		} else if (conc instanceof IKnowledgeSubject) {
			object = (IKnowledgeSubject)conc;
		}

		if (concept == null || object == null) 
			throw new WorkflowException("IsaValidator: errors resolving parameters");
		
		if (! object.is(concept)) {
			throw new InvalidInputException(object + " is not a " + concept);
		}

	}

}
