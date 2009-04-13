package org.integratedmodelling.corescience.literals;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class GeneralClassifier {

	GeneralClassifier[] classifiers = null;
	
	public IConcept classify(Object o) {
		
		IConcept ret = null;
		
		if (classifiers != null) {
			for (GeneralClassifier cl : classifiers) {
				if ( (ret = cl.classify(o)) != null)
					break;
			}
		} else {
			
			/*
			 * 
			 */
		}
		
		return ret;
	}
	
}
