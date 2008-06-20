package org.integratedmodelling.thinklab.workflow.validators;

import java.util.Map;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.ISession;

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
